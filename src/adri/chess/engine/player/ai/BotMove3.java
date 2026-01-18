package adri.chess.engine.player.ai;


//import java.util.Collection;

import adri.chess.engine.board.Board;
import adri.chess.engine.board.Move;
//import adri.chess.engine.pieces.Piece;
import adri.chess.engine.player.MoveUpdate;
//import adri.chess.engine.player.Player;

public class BotMove3 implements Bot { // With alpha-beta pruning
    
    private final BoardEvaluator boardEvaluator;
    private int numPositions;
    private long thinkTime; // in ms
    private boolean searchCancelled;

    public BotMove3() {
        this.boardEvaluator = new StandardBoardEvaluator();
        this.numPositions = 0;
        this.thinkTime = 500; // in ms
    }

    public Move execute(Board board, final int depth) {
        final long startTime = System.currentTimeMillis();
        final long endTime = startTime + this.thinkTime;
        searchCancelled = false;

        Move bestMove = null;
        int alpha = Integer.MIN_VALUE + 1; // -infinity + 1 ( overflows :( )
        int beta = Integer.MAX_VALUE - 1; // +infinity - 1 ( overflows :( )
        
        if (searchCancelled ) {
            System.out.println("Bot3:");
            System.out.println("Executed in " + (System.currentTimeMillis() - startTime) + "ms");
            System.out.println("Searched over " + numPositions + " positions");
            System.out.println("Best evaluation was " + alpha + "\n");
            return bestMove;
        } else {
            // Already calculates first depth
            for (Move move : board.getCurrentPlayer().getLegalMoves()) {
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
            System.out.println("Bot3:");
            System.out.println("Executed in " + (System.currentTimeMillis() - startTime) + "ms");
            System.out.println("Searched over " + numPositions + " positions");
            System.out.println("Best evaluation was " + alpha + "\n");
            return bestMove;
        }
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

    /*public void orderMoves(final Player player, Collection<Move> moves) {
        for (Move move : moves) {
            MoveUpdate moveUpdate = player.playMove(move);
            if (moveUpdate.getMoveStatus().isDone()) {

                int moveScoreGuess = 0;
                Piece movedPieceType = move.getMovedPiece();
                Piece capturedPieceType = move.getCapturedPiece();

                // Captures of a high value piece by a low value one is a good idea
                if (capturedPieceType != null) {
                    moveScoreGuess = 10 * capturedPieceType.getPieceValue() - movedPieceType.getPieceValue();
                }

                // Pawn promotion
                if (move.isPromotion()) {
                    moveScoreGuess += move.getMovedPiece().getPromotionPiece().getPieceValue();
                }

                // Penalize moving pieces into danger
                if (player.calculateAttackMovesOnTile(move.getDestinationCoord(), moveUpdate.getBoard().getCurrentPlayer().getLegalMoves()).size() > 0) {
                    moveScoreGuess -= moveUpdate.getMove().getMovedPiece().getPieceValue();
                }
            }
            
        }
    }*/

    private static boolean isGameOver(Board board) {
        return board.getCurrentPlayer().isInCheckMate() ||
               board.getCurrentPlayer().isInStaleMate();
    }


}
