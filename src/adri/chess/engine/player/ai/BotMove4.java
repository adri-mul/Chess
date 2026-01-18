package adri.chess.engine.player.ai;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import adri.chess.engine.board.Board;
import adri.chess.engine.board.Move;
import adri.chess.engine.pieces.Piece;
import adri.chess.engine.player.MoveUpdate;

public class BotMove4 implements Bot { // Alpha-beta heuristic Minimax search with better search algorithm and move ordering (to increase alpha-beta heuristic search efficiency)
    private static final int MAX_MOVES = 218; // max moves in a chess position
    
    private final BoardEvaluator boardEvaluator;
    private int numPositions;
    private Move lastMove;
    private Move move;
    private int[] moveScores;
    private long thinkTime; // in ms
    private boolean searchCancelled;

    public BotMove4() {
        this.boardEvaluator = new EnhancedBoardEvaluator();
        this.numPositions = 0;
        this.lastMove = Move.NULL_MOVE; // to ward off many repetitions
        this.move = Move.NULL_MOVE;
        this.moveScores = new int[MAX_MOVES];
        this.thinkTime = 1000; // in ms
    }

    public Move execute(Board board, final int depth) {
        final long startTime = System.currentTimeMillis();
        final long endTime = startTime + this.thinkTime;
        this.searchCancelled = false;

        Move bestMove = null;
        int alpha = Integer.MIN_VALUE + 1; // -infinity + 1 ( overflows :( )
        int beta = Integer.MAX_VALUE - 1; // +infinity - 1 ( overflows :( )
        
        if (searchCancelled) { // time up for search
            System.out.println("Bot 4:");
            System.out.println("Executed in " + (System.currentTimeMillis() - startTime) + "ms");
            System.out.println("Searched over " + numPositions + " positions");
            System.out.println("Best evaluation was " + alpha + "\n");
            this.move = bestMove;
            // log the move using zobrist
            this.boardEvaluator.logBoardHistory(board, this.move);
            return bestMove;
        } else {
            List<Move> moves = new ArrayList<>(board.getCurrentPlayer().getLegalMoves());
            orderMoves(board);
            for (int i = 0; i < moves.size(); i++) {
                Move move = pickBest(moves, i);
                MoveUpdate moveUpdate = board.getCurrentPlayer().playMove(move);
                if (moveUpdate.getMoveStatus().isDone()) {
                    numPositions++;
                    int evaluation = -search(moveUpdate.getBoard(), depth - 1, -beta, -alpha, endTime);
                    if (bestMove == null) {
                        bestMove = move;
                    }
                    
                    if (evaluation > alpha) {
                        alpha = evaluation;
                        bestMove = move;
                    } // Don't do anything for beta
                }
            }
            // Already calculates first depth
            System.out.println("Bot 4:");
            System.out.println("Executed in " + (System.currentTimeMillis() - startTime) + "ms");
            System.out.println("Searched over " + numPositions + " positions");
            System.out.println("Best evaluation was " + alpha + "\n");
            this.move = bestMove;
            // log the move using zobrist
            this.boardEvaluator.logBoardHistory(board, this.move);
            return bestMove;
        }
        
    }

    public void orderMoves(Board board) {
        List<Move> moves = new ArrayList<>(board.getCurrentPlayer().getLegalMoves());
        int i = 0;
        for (Move move : moves) {
            moveScores[i] = scoreMove(move, board, 0);
            i++;
        }
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

    // won't be using this
    public int search(final Board board, final int depth) {
        throw new RuntimeException("No implementation for minimax search");
    }

    public int search(final Board board, final int depth, int alpha, int beta, final long endTime) {
        if (System.currentTimeMillis() >= endTime) {
            this.searchCancelled = true;
            return this.boardEvaluator.evaluate(board);
        }

        if (depth == 0 || isGameOver(board)) {
            return this.boardEvaluator.evaluate(board);
        }

        for (final Move move : board.getCurrentPlayer().getLegalMoves()) {
            final MoveUpdate moveUpdate = board.getCurrentPlayer().playMove(move);

            if (moveUpdate.getMoveStatus().isDone()) {
                numPositions++;
                int evaluation = -search(moveUpdate.getBoard(), depth - 1, -beta, -alpha, endTime);

                if (evaluation >= beta) {
                    return beta;
                }

                alpha = Math.max(alpha, evaluation);
            }
        }
        return alpha;
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
}
