package adri.chess.engine.player.ai;

import java.util.ArrayList;
import java.util.List;

import adri.chess.engine.board.Board;
import adri.chess.engine.board.BoardUtils;
import adri.chess.engine.board.Move;
import adri.chess.engine.pieces.Piece;
import adri.chess.engine.player.Player;

// TODO make a board evaluator that aids simple king endgames
public class EnhancedBoardEvaluator implements BoardEvaluator {
    private static final int CHECK_BONUS = 100;
    private static final int CHECK_MATE_BONUS = 100000;
    //private static final int DEPTH_BONUS = 100;
    private static final int CASTLE_BONUS = 60;

    private ArrayList<Long> history;
    private long currHash;

    public EnhancedBoardEvaluator() {
        history = new ArrayList<>();
    }

    @Override
    public int evaluate(final Board board) {
        return isBoardInStaleMate(board) ? 0:   // position is equal if in stalemate (also discourages repetitions)
               materialValue(board.getCurrentPlayer()) + 
               checkValue(board) + checkMateValue(board) + 
               boardControlValue(board) + 
               checkMatePenalty(board) +
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
            System.out.println(currHash);
            history.add(currHash);
        }
    } 

    private int checkValue(Board board) {
        return board.getCurrentPlayer().getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    private int checkMateValue(Board board) {
        return board.getCurrentPlayer().getOpponent().isInCheckMate() ? CHECK_MATE_BONUS : 0;
    }

    private boolean isBoardInStaleMate(Board board) {
        return (board.getCurrentPlayer().isInStaleMate() || isThreeFoldRepetition(history, currHash));
    }

    private int checkMatePenalty(Board board) {
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
                if (count >= 2) return true;
            }
        }

        return false;
    }

    // debug
    public long getCurrHash() {
        return currHash;
    }
}
