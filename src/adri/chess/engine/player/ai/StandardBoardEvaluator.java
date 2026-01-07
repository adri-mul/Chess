package adri.chess.engine.player.ai;

import adri.chess.engine.board.Board;
import adri.chess.engine.pieces.Piece;
import adri.chess.engine.player.Player;

public class StandardBoardEvaluator implements BoardEvaluator {
    
    private static final int CHECK_BONUS = 100;
    private static final int CHECK_MATE_BONUS = 100000;
    //private static final int DEPTH_BONUS = 100;
    //private static final int CASTLE_BONUS = 60;

    @Override
    public int evaluate(final Board board) {
        int advantage = materialValue(board.getCurrentPlayer());
        return advantage + checkValue(board) + checkMateValue(board) + boardControlValue(board) + gameOverPenalty(board);
    }

    private static int materialValue(final Player player) {
        int pieceValueScore = 0;
        int enemyValueScore = 0;
        for (final Piece piece : player.getActivePieces())  {
            pieceValueScore += piece.getPieceValue();
        }
        for (final Piece piece : player.getOpponent().getActivePieces()) {
            enemyValueScore += piece.getPieceValue();
        }
        return pieceValueScore - enemyValueScore;
    }

    private static int checkValue(Board board) {
        return board.getCurrentPlayer().getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    private static int checkMateValue(Board board) {
        return board.getCurrentPlayer().getOpponent().isInCheckMate() ? CHECK_MATE_BONUS : 0;
    }

    private static int gameOverPenalty(Board board) {
        return board.getCurrentPlayer().isInCheckMate() ? -CHECK_MATE_BONUS : 0;
    }

    private static int boardControlValue(Board board) {
        return board.getCurrentPlayer().getLegalMoves().size();
    }

    @Override
    public int evaluate(Board board, BotMove4 bot) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'evaluate'");
    }

}
