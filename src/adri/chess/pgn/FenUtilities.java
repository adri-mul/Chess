package adri.chess.pgn;

import adri.chess.engine.board.Board;
import adri.chess.engine.board.BoardUtils;
import adri.chess.engine.pieces.Pawn;

public class FenUtilities {
    private FenUtilities() {
        throw new RuntimeException("Not instantiable");
    }

    public static Board createCameFromFEN(final String fenString) {
        return null;
    }

    public static String createFENFromGame(final Board board) {
        return calculateBoardText(board) + " " + 
               calculateCurrentPlayerText(board) + " " + 
               calculateCastleText(board) + " " + 
               calculateEnPassantSquare(board) + " 0 1";
    }

    private static String calculateEnPassantSquare(final Board board) {
        //TODO implement enpassant FEN format
        /*final Pawn enPassantPawn = board.getEnPassantPawn();
        if (enPassantPawn != null) {
            int[] enPassantPos = enPassantPawn.getPiecePos();
            return BoardUtils.getPositionAtCoordinate(enPassantPos[0]*8 + enPassantPos[1] + 
                                                      8*enPassantPawn.getPieceColor().getOppositeDirection());
        }*/
        return "-";
    }

    private static String calculateCastleText(final Board board) {
        final StringBuilder builder = new StringBuilder();
        if (board.getWhitePlayer().isKingSideCastleAllowed()) {
            builder.append("K");
        }
        if (board.getWhitePlayer().isQueenSideCastleAllowed()) {
            builder.append("Q");
        }
        if (board.getBlackPlayer().isKingSideCastleAllowed()) {
            builder.append("k");
        }
        if (board.getBlackPlayer().isQueenSideCastleAllowed()) {
            builder.append("q");
        }
        final String result = builder.toString();
        return result.isEmpty() ? "-" : result;
    }

    private static String calculateCurrentPlayerText(final Board board) {
        return board.getCurrentPlayer().toString().substring(0, 1).toLowerCase();
    }

    private static String calculateBoardText(final Board board) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < BoardUtils.NUM_RANKS; i++) {
            for (int j = 0; i < BoardUtils.NUM_FILES; j++) {
                final String tileText = board.getTile(new int[]{1, 2}).toString();
                builder.append(tileText);
            }
        }

        builder.insert(8, "/");
        builder.insert(17, "/");
        builder.insert(26, "/");
        builder.insert(35, "/");
        builder.insert(44, "/");
        builder.insert(53, "/");
        builder.insert(62, "/");

        return builder.toString().replaceAll("---------", "8")
                                 .replaceAll("-------", "7")
                                 .replaceAll("------", "6")
                                 .replaceAll("-----", "5")
                                 .replaceAll("----", "4")
                                 .replaceAll("---", "3")
                                 .replaceAll("--", "2")
                                 .replaceAll("-", "1");
    }
}
