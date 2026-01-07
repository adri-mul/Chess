package adri.chess.engine.pieces;

import java.util.Collection;
import adri.chess.engine.ChessColor;
import adri.chess.engine.board.Move;
import adri.chess.engine.board.Board;

// class to represent a piece
public abstract class Piece {
    
    protected final PieceType pieceType;
    protected final int[] piecePos;
    protected final ChessColor pieceColor;
    private final int cachedHashCode;
    private final boolean isFirstMove;
    private static Piece promotePiece;
    //protected Collection<Move> legalMoves = calculateLegalMoves(board);

    protected Piece(PieceType pieceType, int[] piecePos, ChessColor pieceColor) {
        this.pieceType = pieceType;
        this.piecePos = piecePos;
        this.pieceColor = pieceColor;
        this.cachedHashCode = computeHashCode();
        this.isFirstMove = true;
    }

    protected Piece(PieceType pieceType, int[] piecePos, ChessColor pieceColor, boolean isFirstMove) {
        this.pieceType = pieceType;
        this.piecePos = piecePos;
        this.pieceColor = pieceColor;
        this.cachedHashCode = computeHashCode();
        this.isFirstMove = isFirstMove;
    }

    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    private int computeHashCode() {
        int result = pieceType.hashCode();
        result = 31 * result + pieceColor.hashCode();
        result = 31 * result + piecePos[0]*8 + piecePos[1];
        result = 31 * result + (isFirstMove ? 1: 0);
        return result;
    }

    public Piece getPromotionPiece() {
        return promotePiece;
    }

    public static void setPromotionPiece(Piece piece) {
        promotePiece = piece;
    }

    // method to calculate possible moves for the piece given the board position
    public abstract Collection<Move> calculateLegalMoves(Board board);
    public abstract String toString();

    public ChessColor getPieceColor() {
        return pieceColor;
    }

    public int[] getPiecePos() {
        return this.piecePos;
    }

    public PieceType getPieceType() {
        return this.pieceType;
    }

    public abstract Piece movePiece(Move move);

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Piece)) {
            return false;
        }
        Piece otherPiece = (Piece) other;
        return (this.pieceType == otherPiece.pieceType);
        
    }

    @Override
    public int hashCode() {
        return this.cachedHashCode;
    }

    
    public boolean fullEquals(Piece otherPiece) {
        if (this.piecePos[0] == otherPiece.piecePos[0] && this.piecePos[1] == otherPiece.piecePos[1]) {
            if (this.pieceType == otherPiece.pieceType && this.getPieceColor() == otherPiece.getPieceColor()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasSameColor(Piece otherPiece) {
        return this.pieceColor == otherPiece.pieceColor;
    }

    public int getPieceValue() {
        return this.pieceType.getPieceValue();
    }

    public enum PieceType {

        PAWN("P", 100) {
            @Override
            public boolean isKing() {
                return false;
            }
            @Override
            public boolean isRook() {
                return false;
            }
            @Override
            public String fileString() {
                return "pawn";
            }
        },
        KNIGHT("N", 300) {
            @Override
            public boolean isKing() {
                return false;
            }
            @Override
            public boolean isRook() {
                return false;
            }
            @Override
            public String fileString() {
                return "knight";
            }
        },
        BISHOP("B", 350) {
            @Override
            public boolean isKing() {
                return false;
            }
            @Override
            public boolean isRook() {
                return false;
            }
            @Override
            public String fileString() {
                return "bishop";
            }
        },
        ROOK("R", 500) {
            @Override
            public boolean isKing() {
                return false;
            }
            @Override
            public boolean isRook() {
                return true;
            }
            @Override
            public String fileString() {
                return "rook";
            }
        },
        QUEEN("Q", 900) {
            @Override
            public boolean isKing() {
                return false;
            }
            @Override
            public boolean isRook() {
                return false;
            }
            @Override
            public String fileString() {
                return "queen";
            }
        },
        KING("K", Integer.MAX_VALUE) {
            @Override
            public boolean isKing() {
                return true;
            }
            @Override
            public boolean isRook() {
                return false;
            }
            @Override
            public String fileString() {
                return "king";
            }
        };

        private String pieceName;
        private int pieceValue;

        PieceType(final String pieceName, final int pieceValue) {
            this.pieceName = pieceName;
            this.pieceValue = pieceValue;
        }

        @Override
        public String toString() {
            return this.pieceName;
        }
        public int getPieceValue() {
            return this.pieceValue;
        }

        public abstract boolean isKing();
        public abstract String fileString();
        public abstract boolean isRook();
        
    }


}
