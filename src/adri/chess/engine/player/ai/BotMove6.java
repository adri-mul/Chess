package adri.chess.engine.player.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import adri.chess.engine.board.Board;
import adri.chess.engine.board.BoardUtils;
import adri.chess.engine.board.Move;
import adri.chess.engine.pieces.Piece;
import adri.chess.engine.player.MoveUpdate;

public class BotMove6 implements Bot {

    private static final int MAX_MOVES = 218; // max moves in a chess position
    private static final int HISTORY_BONUS = 1000; // bonus for moves with good history
    
    private final BoardEvaluator boardEvaluator;
    private int numPositions;
    private Move lastMove;
    private Move move;
    private int[] moveScores;
    private long thinkTime; // in ms
    private boolean searchCancelled;
    private int[][] historyTable; // track good moves: [from][to] -> score

    public BotMove6() {
        this.boardEvaluator = new EnhancedBoardEvaluator();
        this.numPositions = 0;
        this.lastMove = Move.NULL_MOVE; // to ward off many repetitions
        this.move = Move.NULL_MOVE;
        this.moveScores = new int[MAX_MOVES];
        this.thinkTime = 1000; // in ms
        this.historyTable = new int[64][64]; // 64 squares from, 64 squares to
    }

    public Move execute(Board board, final int maxDepth) {
        final long startTime = System.currentTimeMillis();
        final long endTime = startTime + this.thinkTime;
        this.searchCancelled = false;
        this.numPositions = 0;

        // Reset history table for new search
        // for (int i = 0; i < 64; i++) {
        //     for (int j = 0; j < 64; j++) {
        //         historyTable[i][j] = 0;
        //     }
        // }

        Move bestMove = Move.NULL_MOVE;
        int bestEvaluation = Integer.MIN_VALUE + 1;

        if (maxDepth <= 0) {
            // If caller passes zero or negative depth, do a single shallow evaluation and keep the board unchanged.
            this.move = bestMove;
            this.boardEvaluator.logBoardHistory(board, this.move);
            printDebugInfo(startTime, bestEvaluation, numPositions);
            return bestMove;
        }

        for (int depth = 1; depth <= maxDepth; depth++) {
            if (System.currentTimeMillis() >= endTime) {
                this.searchCancelled = true;
                break;
            }

            int alpha = Integer.MIN_VALUE + 1; // -infinity + 1 ( overflows :( )
            int beta = Integer.MAX_VALUE - 1; // +infinity - 1 ( overflows :( )
            Move iterationBestMove = Move.NULL_MOVE;
            int iterationBestEvaluation = Integer.MIN_VALUE + 1;

            List<Move> moves = new ArrayList<>(board.getCurrentPlayer().getLegalMoves());
            // Use previous iteration's best move as the preferred (principal variation) move
            orderMoves(moves, board, bestMove);
            for (int i = 0; i < moves.size(); i++) {
                if (System.currentTimeMillis() >= endTime) {
                    this.searchCancelled = true;
                    break;
                }

                Move move = pickBest(moves, i);
                MoveUpdate moveUpdate = board.getCurrentPlayer().playMove(move);
                if (moveUpdate.getMoveStatus().isDone()) {
                    numPositions++;
                    int evaluation = -search(moveUpdate.getBoard(), depth - 1, -beta, -alpha, endTime);

                    if (evaluation > iterationBestEvaluation) {
                        iterationBestEvaluation = evaluation;
                        iterationBestMove = move;
                    }

                    if (evaluation > alpha) {
                        alpha = evaluation;
                        iterationBestEvaluation = evaluation;
                        iterationBestMove = move;
                    }
                }
            }

            if (!this.searchCancelled && iterationBestMove != Move.NULL_MOVE) {
                bestMove = iterationBestMove;
                bestEvaluation = iterationBestEvaluation;
            }

            if (this.searchCancelled) {
                this.boardEvaluator.logBoardHistory(board, this.move);
                printDebugInfo(startTime, bestEvaluation, numPositions);
                return bestMove;
            }
        }

        this.move = bestMove;
        // log the move using zobrist
        this.boardEvaluator.logBoardHistory(board, this.move);
        printDebugInfo(startTime, bestEvaluation, numPositions);
        return bestMove;
    }

    // Updated to allow a preferred move (from previous iteration) to be boosted
    public void orderMoves(List<Move> moves, Board board, Move preferredMove) {
        int i = 0;
        for (Move move : moves) {
            moveScores[i] = scoreMove(move, board, 0);
            // If this move matches the preferred move from the previous iterative-deepening iteration,
            // give it a very large bonus so it gets examined first (improves alpha-beta pruning).
            if (preferredMove != Move.NULL_MOVE) {
                boolean sameMove = false;
                try {
                    sameMove = move.equals(preferredMove);
                } catch (Exception ignored) {
                }
                if (!sameMove) {
                    sameMove = move.getCurrentCoord() == preferredMove.getCurrentCoord() &&
                               move.getDestinationCoord() == preferredMove.getDestinationCoord();
                }
                if (sameMove) {
                    moveScores[i] += 1_000_000;
                }
            }
            i++;
        }

    }

    
    // won't be using this
    public int search(final Board board, final int depth) {
        throw new RuntimeException("No implementation for minimax search");
    }

    public int search(final Board board, final int depth, int alpha, int beta, final long endTime) {
        if (System.currentTimeMillis() >= endTime) {
            this.searchCancelled = true;
            return quiescenceSearch(board, alpha, beta);
            // return this.boardEvaluator.evaluate(board);
        }

        if (depth == 0 || isGameOver(board)) {
            // return quiescenceSearch(board, alpha, beta);
            return this.boardEvaluator.evaluate(board);
        }

        List<Move> moves = new ArrayList<>(board.getCurrentPlayer().getLegalMoves());
        // In recursive searches we don't have a PV from previous full-iteration at this node,
        // so pass NULL_MOVE to use history heuristic only.
        orderMoves(moves, board, Move.NULL_MOVE);
        for (int i = 0; i < moves.size(); i++) {
            final Move move = pickBest(moves, i);
            final MoveUpdate moveUpdate = board.getCurrentPlayer().playMove(move);

            if (moveUpdate.getMoveStatus().isDone()) {
                numPositions++;
                int evaluation = -search(moveUpdate.getBoard(), depth - 1, -beta, -alpha, endTime);

                if (evaluation >= beta) {
                    // Beta cutoff - record this move in history
                    historyTable[BoardUtils.tileCoordToInt(move.getCurrentCoord())][BoardUtils.tileCoordToInt(move.getDestinationCoord())] += HISTORY_BONUS;
                    return beta;
                }

                alpha = Math.max(alpha, evaluation);
            }
        }
        return alpha;
    }

    // this avoids the horizon effect, where the search stops on a capture, but maybe the next move you lose a piece
    public int quiescenceSearch(final Board board, int alpha, int beta) {
        int evaluation = this.boardEvaluator.evaluate(board);
        if (evaluation >= beta) {
            return beta;
        }

        alpha = Math.max(alpha, evaluation);

        List<Move> captures = new ArrayList<>(board.getCurrentPlayer().getCaptureMoves());
        orderMoves(captures, board, Move.NULL_MOVE);

        for (int i = 0; i < captures.size(); i++) {
            Move move = pickBest(captures, i);
            MoveUpdate update = board.getCurrentPlayer().playMove(move);

            if (update.getMoveStatus().isDone()) {
                numPositions++;
                evaluation = -quiescenceSearch(update.getBoard(), -beta, -alpha);

                if (evaluation >= beta) {
                    return beta;
                }
                alpha = Math.max(alpha, evaluation);
            }
        }

        return alpha;
    }
    
    public Move getLastMove() {
        return lastMove;
    }

    public Move getMove() {
        return move;
    }

    public void setLastMove(Move lastMove) {
        this.lastMove = lastMove;
    }
    
    private int scoreMove(final Move move, final Board board, int depth) {
        int moveScore = 0;
        MoveUpdate moveUpdate = board.getCurrentPlayer().playMove(move);
        if (moveUpdate.getMoveStatus().isDone()) {
            Piece movedPiece = move.getMovedPiece();
            Piece capturedPiece = move.getCapturedPiece();

            // Capturing a high value piece with a lower value piece is rewarded
            if (capturedPiece != null) {
                moveScore = 10 * (capturedPiece.getPieceValue() - movedPiece.getPieceValue());
            }
            
            // Promotions of pawns are rewarded
            if (move.isPromotion()) {
                moveScore += move.getMovedPiece().getPromotionPiece().getPieceValue();
            }
            
            // Add history heuristic bonus
            moveScore += historyTable[BoardUtils.tileCoordToInt(move.getCurrentCoord())][BoardUtils.tileCoordToInt(move.getDestinationCoord())];

            // Moving pieces into danger is penalized
            if (board.getCurrentPlayer().calculateAttackMovesOnTile(move.getDestinationCoord(), moveUpdate.getBoard().getCurrentPlayer().getLegalMoves()).size() > 0) {
                moveScore -= move.getMovedPiece().getPieceValue();
            }

        }

        return moveScore;
    }

    private Move pickBest(List<Move> moves, final int startIndex) {
        int bestIndex = startIndex;
        int bestScore = this.moveScores[startIndex];

        for (int i = startIndex + 1; i < moves.size(); i++) {
            if (moveScores[i] > bestScore) {
                bestScore = moveScores[i];
                bestIndex = i;
            }
        }

        // Swap best to beginning of list
        Collections.swap(moves, startIndex, bestIndex);
        // Swap scores as well
        int temp = moveScores[startIndex];
        moveScores[startIndex] = moveScores[bestIndex];
        moveScores[bestIndex] = temp;

        return moves.get(startIndex);
    }

    private static boolean isGameOver(Board board) {
        return board.getCurrentPlayer().isInCheckMate() ||
               board.getCurrentPlayer().isInStaleMate();
    }

    public void printDebugInfo(long startTime, int alpha, int numPositions) {
        System.out.println("Bot 6:");
        System.out.println("Current Board Hash: " + boardEvaluator.getCurrHash());
        System.out.println("Executed in " + (System.currentTimeMillis() - startTime) + "ms");
        System.out.println("Searched over " + numPositions + " positions");
        System.out.println("Best evaluation was " + alpha + "\n");
    }
    
}
