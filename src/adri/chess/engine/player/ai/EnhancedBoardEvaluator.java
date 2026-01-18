package adri.chess.engine.player.ai;

import java.util.ArrayList;
import java.util.List;

import adri.chess.engine.board.Board;
import adri.chess.engine.board.BoardUtils;
import adri.chess.engine.board.Move;
import adri.chess.engine.pieces.Piece;
import adri.chess.engine.player.Player;

public class EnhancedBoardEvaluator implements BoardEvaluator {
    private static final int CHECK_BONUS = 100;
    private static final int CHECK_MATE_BONUS = 100000;
    //private static final int DEPTH_BONUS = 100;
    private static final int CASTLE_BONUS = 60;
    private static final int STALE_MATE_PENALTY = -1000;

    private List<Long> history = new ArrayList<>();
    private long currHash;

    @Override
    public int evaluate(final Board board) {
        return /* staleMatePenalty(board) + */  // position is equal if in stalemate (also discourages repetitions)
               materialValue(board.getCurrentPlayer()) + 
               checkValue(board) + checkMateValue(board) + 
               boardControlValue(board) + 
               gameOverPenalty(board) +
               castleValue(board);
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

    public void logBoardHistory(Board board, Move move) {
        if (history.size() == 0) {
            currHash = BoardUtils.computeFullHash(board);
            history.add(currHash);
        } else {
            currHash = BoardUtils.updateHash(currHash, move);
            history.add(currHash);
        }
    } 

    private int checkValue(Board board) {
        return board.getCurrentPlayer().getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    private int checkMateValue(Board board) {
        return board.getCurrentPlayer().getOpponent().isInCheckMate() ? CHECK_MATE_BONUS : 0;
    }

    private int staleMatePenalty(Board board) {
        return (board.getCurrentPlayer().isInStaleMate() || isThreeFoldRepetition(history, currHash)) ? STALE_MATE_PENALTY : 0;
    }

    private int gameOverPenalty(Board board) {
        return board.getCurrentPlayer().isInCheckMate() ? -CHECK_MATE_BONUS : 0;
    }

    private int boardControlValue(Board board) {
        return board.getCurrentPlayer().getLegalMoves().size();
    }

    private int castleValue(Board board) {
        return board.getCurrentPlayer().isCastled() ? CASTLE_BONUS : 0;
    }

    private boolean isThreeFoldRepetition(List<Long> history, long currHash) {
        int count = 0;
        for (int i = history.size() - 1; i >= 0; i--) {
            if (history.get(i) == currHash) {
                count++;
                if (count >= 3) return true;
            }
        }

        return false;
    }
}
