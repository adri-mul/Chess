package adri.chess.engine.board;

import adri.chess.engine.ChessColor;
import adri.chess.engine.pieces.King;
import adri.chess.engine.pieces.Piece;

public class BoardUtils {

    public static final int NUM_TILES = 64;
    public static final int NUM_RANKS = 8;
    public static final int NUM_FILES = 8;

    // Zobrist table
    public static long[][] ZOBRIST_PIECES = new long[12][64];
    public static long ZOBRIST_SIDE;
    public static long[] ZOBRIST_CASTLING = new long[4];
    public static long[] ZOBRIST_ENPASSANT = new long[8];


    private BoardUtils() {
        throw new RuntimeException("Cannot instantiate BoardUtils");
    }

    public static int tileCoordToInt(int[] coord) {
        return coord[0] * 8 + coord[1];
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

    // Generated with AI
    public void initZobrist() {
        java.util.Random rng = new java.util.Random(20240115);

        for (int p = 0; p < 12; p++) {
            for (int sq = 0; sq < 64; sq++) {
                ZOBRIST_PIECES[p][sq] = rng.nextLong();
            }
        }

        ZOBRIST_SIDE = rng.nextLong();

        for (int i = 0; i < 4; i++) {
            ZOBRIST_CASTLING[i] = rng.nextLong();
        }

        for (int f = 0; f < 8; f++) {
            ZOBRIST_ENPASSANT[f] = rng.nextLong();
        }

    }

    public static int pieceIndex(Piece piece) {
        if (piece == null) return -1;

        int type = switch (piece.getPieceType()) {
            case PAWN -> 0;
            case KNIGHT -> 1;
            case BISHOP -> 2;
            case ROOK -> 3;
            case QUEEN -> 4;
            case KING -> 5;
        };

        return piece.getPieceColor().isWhite() ? type : type + 6;
    }

    public static long computeFullHash(Board board) {
        long hash = 0;
        for (int rank = 0; rank < NUM_RANKS; rank++) {
            for (int file = 0; file < NUM_FILES; file++) {
                Piece piece = board.getTile(new int[]{rank, file}).getPiece();
                if (piece != null) {
                    int square = rank * 8 + file;
                    hash ^= ZOBRIST_PIECES[pieceIndex(piece)][square];
                }
            }
        }

        if (board.getCurrentPlayer().getColor().isWhite()) {
            hash ^= ZOBRIST_SIDE;
        }

        // Castling
        King currPlayerKing = board.getCurrentPlayer().getPlayerKing();
        King oppPlayerKing = board.getCurrentPlayer().getOpponent().getPlayerKing();
        if (currPlayerKing.isKingSideCastleAllowed()) {
            hash ^= ZOBRIST_CASTLING[0];
        }
        if (currPlayerKing.isQueenSideCastleAllowed()) {
            hash ^= ZOBRIST_CASTLING[1];
        }
        if (oppPlayerKing.isKingSideCastleAllowed()) {
            hash ^= ZOBRIST_CASTLING[2];
        }
        if (oppPlayerKing.isQueenSideCastleAllowed()) {
            hash ^= ZOBRIST_CASTLING[3];
        }

        //TODO en passant
        return hash;
    }

    public static long updateHash(
        long hash,
        Move move
        //boolean[] oldCastling, // [wK, wQ, bK, bQ]
        /* boolean[] newCastling */)
    {
        Board board = move.board; // board BEFORE the move

        Piece moved = move.movedPiece;

        int movedIdx = pieceIndex(moved);

        // 1. XOR out the piece from its old square
        hash ^= ZOBRIST_PIECES[movedIdx][tileCoordToInt(move.getCurrentCoord())];

        // 2. XOR in the piece on its new square
        hash ^= ZOBRIST_PIECES[movedIdx][tileCoordToInt(move.getDestinationCoord())];

        // 3. Handle captures
        Piece captured = board.getTile(move.destinationCoord).getPiece();
        if (captured != null) {
            int captureIdx = pieceIndex(captured);
            hash ^= ZOBRIST_PIECES[captureIdx][tileCoordToInt(move.getDestinationCoord())];
        }

        // 4. Promotions
        if (move.isPromotion()) {
            int pawnIdx = movedIdx;
            int promoIdx = pieceIndex(move.getMovedPiece().getPromotionPiece());

            // remove pawn at destination
            hash ^= ZOBRIST_PIECES[pawnIdx][tileCoordToInt(move.getDestinationCoord())];
            // add promoted piece
            hash ^= ZOBRIST_PIECES[promoIdx][tileCoordToInt(move.getDestinationCoord())];
        }

        // TODO I'll implement this later, rn this is a bit too much to deal with for testing
        /*// 5. Castling rights changes
        for (int i = 0; i < 4; i++) {
            if (oldCastling[i] != newCastling[i]) {
                hash ^= ZOBRIST_CASTLING[i];
            }
        }*/

        // Side to move flips
        hash ^= ZOBRIST_SIDE;
        System.out.println(hash);
        return hash;
    }
}
