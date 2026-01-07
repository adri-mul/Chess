package adri.chess.engine.board;
// import pieces
import adri.chess.engine.pieces.Piece;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// abstract class to represent a chess tile
public abstract class Tile {

    protected final int[] tileCoord;

    // set map of all possible positions of empty tiles
    private static final Map<Integer, EmptyTile> EMPTY_TILES = createEmptyTiles();

    private static Map<Integer, EmptyTile> createEmptyTiles() {
        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();

        // loop to add empty tiles
        for (int rank = 8; rank >= 0; rank--) {
            for (int file = 0; file < 8; file++) {
                int[] coord = {rank, file};
                emptyTileMap.put((rank * 8 + file), new EmptyTile(coord));
            }
        }
        return Collections.unmodifiableMap(emptyTileMap);
    }

    public static Tile createTile(int[] tileCoord, Piece piece) {
        //System.out.println(EMPTY_TILES.get(tileCoord[0] * 8 + tileCoord[1]));
        return piece != null ? new OccupiedTile(tileCoord, piece): EMPTY_TILES.get(tileCoord[0] * 8 + tileCoord[1]);
    }

    private Tile(int[] tileCoord) {
        this.tileCoord = tileCoord;
    }

    public int[] getTileCoord() {
        return tileCoord;
    }
    
    public abstract boolean hasPiece();
    public abstract Piece getPiece();
    public abstract String toString();


    public static final class EmptyTile extends Tile {

        private EmptyTile(int[] tileCoord) {
            super(tileCoord);
        }

        @Override
        public boolean hasPiece() {
            return false;
        }

        @Override
        public Piece getPiece() {
            return null;
        }

        @Override
        public String toString() {
            return "-";
        }

    }


    public static final class OccupiedTile extends Tile {
        private final Piece piece;

        private OccupiedTile(int[] tileCoord, Piece piece) {
            super(tileCoord);
            this.piece = piece;
        }

        @Override
        public boolean hasPiece() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return piece;
        }

        @Override
        public String toString() {
            return getPiece().getPieceColor().isWhite() ? getPiece().toString() : getPiece().toString().toLowerCase();
        }
        
    }

}