package adri.chess.engine.player.ai;

import adri.chess.engine.board.Board;
import adri.chess.engine.board.Move;
import adri.chess.engine.player.MoveUpdate;

// Uses MixMax strat
public class BotMove {

    private final BoardEvaluator boardEvaluator;

    public BotMove() {
        this.boardEvaluator = new StandardBoardEvaluator();
    }

    public Move execute(Board board, int depth) {
        final long startTime = System.currentTimeMillis();

        Move bestMove = null;
        int highestValue = Integer.MIN_VALUE;
        int lowestValue = Integer.MAX_VALUE;
        int currentValue;

        System.out.println(board.getCurrentPlayer() + "thinking with depth = " + depth);
        //int numMoves = board.getCurrentPlayer().getLegalMoves().size();
        for (final Move move : board.getCurrentPlayer().getLegalMoves()) {
            final MoveUpdate moveUpdate = board.getCurrentPlayer().playMove(move);
            if (moveUpdate.getMoveStatus().isDone()) {
                currentValue = board.getCurrentPlayer().getColor().isWhite() ?
                    max(moveUpdate.getBoard(), depth - 1) :
                    min(moveUpdate.getBoard(), depth - 1);
                
                if (board.getCurrentPlayer().getColor().isWhite() && currentValue >= highestValue) {
                    highestValue = currentValue;
                    bestMove = move;
                } else if (board.getCurrentPlayer().getColor().isBlack() && currentValue <= lowestValue) {
                    lowestValue = currentValue;
                    bestMove = move;
                }
            }
        }
        final long executeTime = System.currentTimeMillis() - startTime;
        System.out.println("Search took " + executeTime + "ms");

        return bestMove;
    }

    public int min(final Board board, final int depth) {
        if (depth == 0 || isEndGameScenario(board)) {
            return this.boardEvaluator.evaluate(board);
        }
        int lowestValue = Integer.MAX_VALUE;
        for (final Move move : board.getCurrentPlayer().getLegalMoves()) {
            final MoveUpdate moveUpdate = board.getCurrentPlayer().playMove(move);
            if (moveUpdate.getMoveStatus().isDone()) {
                final int currentValue = max(moveUpdate.getBoard(), depth - 1);
                if (currentValue <= lowestValue) {
                    lowestValue = currentValue;
                }
            }
        }

        return lowestValue;
    }

    public int max(final Board board, final int depth) {
        if (depth == 0 || isEndGameScenario(board)) {
            return this.boardEvaluator.evaluate(board);
        }
        int highestValue = Integer.MIN_VALUE;
        for (final Move move : board.getCurrentPlayer().getLegalMoves()) {
            final MoveUpdate moveUpdate = board.getCurrentPlayer().playMove(move);
            if (moveUpdate.getMoveStatus().isDone()) {
                final int currentValue = min(moveUpdate.getBoard(), depth - 1);
                if (currentValue >= highestValue) {
                    highestValue = currentValue;
                }
            }
        }

        return highestValue;
    }    

    private static boolean isEndGameScenario(final Board board) {
        return board.getCurrentPlayer().isInCheckMate() ||
               board.getCurrentPlayer().isInStaleMate();
    }

    @Override
    public String toString() {
        return "Bot";
    }
}
