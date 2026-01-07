package adri.chess.engine.player.ai;

//import java.util.Collection;

import adri.chess.engine.board.Board;
import adri.chess.engine.board.Move;
//import adri.chess.engine.pieces.Piece;
import adri.chess.engine.player.MoveUpdate;
//import adri.chess.engine.player.Player;

public class BotMove2 implements Bot {
    
    private final BoardEvaluator boardEvaluator;
    private int numPositions;

    public BotMove2() {
        this.boardEvaluator = new StandardBoardEvaluator();
        this.numPositions = 0;
    }

    public Move execute(Board board, final int depth) {
        final long startTime = System.currentTimeMillis();

        Move bestMove = null;
        int bestEval = Integer.MIN_VALUE;
        
        // Already calculates first depth
        for (Move move : board.getCurrentPlayer().getLegalMoves()) {
            MoveUpdate moveUpdate = board.getCurrentPlayer().playMove(move);
            if (moveUpdate.getMoveStatus().isDone()) {
                numPositions++;
                int evaluation = -search(moveUpdate.getBoard(), depth - 1);
                if (evaluation > bestEval) {
                    bestEval = evaluation;
                    bestMove = move;
                }
            }
        }
        System.out.println("Executed in " + (System.currentTimeMillis() - startTime) + "ms");
        System.out.println("Searched over " + numPositions + " positions");
        System.out.println("Best evaluation was " + bestEval + "\n");
        return bestMove;
    }

    public int search(final Board board, final int depth) {
        if (depth == 0 || isGameOver(board)) {
            return this.boardEvaluator.evaluate(board);
        }
        int bestEval = Integer.MIN_VALUE;
        //int searches = 0;
        for (final Move move : board.getCurrentPlayer().getLegalMoves()) {
            final MoveUpdate moveUpdate = board.getCurrentPlayer().playMove(move);
            if (moveUpdate.getMoveStatus().isDone()) {
                numPositions++;
                int evaluation = -search(moveUpdate.getBoard(), depth - 1);
                bestEval = Math.max(evaluation, bestEval);

            }
        }
        return bestEval;
    }

    private static boolean isGameOver(Board board) {
        return board.getCurrentPlayer().isInCheckMate() ||
               board.getCurrentPlayer().isInStaleMate();
    }
}
