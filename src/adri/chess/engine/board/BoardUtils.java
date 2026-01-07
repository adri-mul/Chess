package adri.chess.engine.board;

import adri.chess.engine.ChessColor;

public class BoardUtils {

    public static final int NUM_TILES = 64;
    public static final int NUM_RANKS = 8;
    public static final int NUM_FILES = 8;

    private BoardUtils() {
        throw new RuntimeException("Cannot instantiate");
    }

    public static boolean validTileCoordinate(int[] tileCoord) {
        final int coordRank = tileCoord[0];
        final int coordFile = tileCoord[1];
    
        return (coordRank >= 0 && coordRank < 8) && (coordFile >= 0 && coordFile < 8);
    }

    public static boolean isBlockedTile(ChessColor thisColor, Tile destinationTile) {
        if (destinationTile.hasPiece()) {
            return thisColor == destinationTile.getPiece().getPieceColor();
        }
        return false;
    }
}
