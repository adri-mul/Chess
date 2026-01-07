package adri.chess.engine.board;

import adri.chess.engine.board.Board.Builder;
import adri.chess.engine.pieces.Pawn;
import adri.chess.engine.pieces.Piece;
import adri.chess.engine.pieces.Rook;;

public class Move {
    
    protected final Board board;
    protected final Piece movedPiece;
    protected final int[] destinationCoord;
    public int score;

    public static final Move NULL_MOVE = new NullMove();

    public Move(Board board, Piece movedPiece, int[] destinationCoord) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoord = destinationCoord;
    }

    public boolean isTarget(int[] piecePos) {
        // checks if legal moves has a move to the piecePos
        if ( this.equals(piecePos) ) {
            return true;
        }
        return false;
    }

    public Board getBoard() {
        return this.board;
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    public int[] getCurrentCoord() {
        return this.movedPiece.getPiecePos();
    }

    public int[] getDestinationCoord() {
        return this.destinationCoord;
    }

    public boolean equals(int[] otherPos) {
        if (this.destinationCoord[0] == otherPos[0] && this.destinationCoord[1] == otherPos[1]) {
            return true;
        }
        return false;
    }

    public Board execute() {
        final Builder builder = new Builder();

        for (Piece piece : this.board.getCurrentPlayer().getActivePieces()) {
            if (!this.movedPiece.fullEquals(piece)) {
                builder.setPiece(piece);
            }
        }

        for (Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces()) {
            builder.setPiece(piece);
        }

        // Move the piece moved
        builder.setPiece(getMovedPiece().movePiece(this));
        builder.setJumpedPawn(null);
        builder.setCurrentColor(this.board.getCurrentPlayer().getOpponent().getColor());
        return builder.build();
    }

    public boolean isCapture() {
        return false;
    }

    public boolean isCastlingMove() {
        return false;
    }

    public boolean isPromotion() {
        return false;
    }

    public Piece getCapturedPiece() {
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.destinationCoord[0]*8 + this.destinationCoord[1];
        result = prime * result + this.movedPiece.hashCode();

        return result;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof Move)) {
            return false;
        }
        final Move otherMove = (Move) other;
        return getDestinationCoord()[0] == otherMove.getDestinationCoord()[0] && 
               getMovedPiece().equals(otherMove.getMovedPiece()) && 
               getDestinationCoord()[1] == otherMove.getDestinationCoord()[1];
    }


    // Algebraic chess notation for moves. i.e. Ne4 -> Knight to e4
    @Override
    public String toString() {
        switch (this.getDestinationCoord()[1]) {
            case 0:
                return this.getMovedPiece().toString() + "a" + (8-this.getDestinationCoord()[0]);
            case 1:
                return this.getMovedPiece().toString() + "b" + (8-this.getDestinationCoord()[0]);
            case 2:
                return this.getMovedPiece().toString() + "c" + (8-this.getDestinationCoord()[0]);
            case 3:
                return this.getMovedPiece().toString() + "d" + (8-this.getDestinationCoord()[0]);
            case 4:
                return this.getMovedPiece().toString() + "e" + (8-this.getDestinationCoord()[0]);
            case 5:
                return this.getMovedPiece().toString() + "f" + (8-this.getDestinationCoord()[0]);
            case 6:
                return this.getMovedPiece().toString() + "g" + (8-this.getDestinationCoord()[0]);
            case 7:
                return this.getMovedPiece().toString() + "h" + (8-this.getDestinationCoord()[0]);
            default:
                return null;
        }
    }

    public static class PawnPromotion extends Move {

        final Move decoratedMove;
        final Pawn promotedPawn;

        public PawnPromotion(final Move decoratedMove) {
            super(decoratedMove.getBoard(), decoratedMove.getMovedPiece(), decoratedMove.getDestinationCoord());
            this.decoratedMove = decoratedMove;
            this.promotedPawn = (Pawn) decoratedMove.getMovedPiece();
        }

        @Override
        public int hashCode() {
            return decoratedMove.hashCode() + (31 * promotedPawn.hashCode());
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof PawnPromotion && this.decoratedMove.equals(other);
        }

        @Override
        public Board execute() {
            Board updateBoard = this.decoratedMove.execute();
            final Builder builder = new Builder();
            for (final Piece piece : updateBoard.getCurrentPlayer().getActivePieces()) {
                if (!(this.promotedPawn.fullEquals(piece))) {
                    builder.setPiece(piece);
                }
            }

            for (final Piece piece : updateBoard.getCurrentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }

            // gets the promoted piece instead of a pawn, and moves it
            builder.setPiece(this.promotedPawn.getPromotionPiece().movePiece(this.decoratedMove));
            // don't do getOpponent because decoratedMove execute() already does
            builder.setCurrentColor(updateBoard.getCurrentPlayer().getColor());
            return builder.build();
        }

        @Override
        public boolean isCapture() {
            return this.decoratedMove.isCapture();
        }

        @Override
        public Piece getCapturedPiece() {
            return this.decoratedMove.getCapturedPiece();
        }

        @Override
        public String toString() {
            return this.decoratedMove.toString() + "=Q";
        }

        @Override
        public boolean isPromotion() {
            return true;
        }
        
    }

    public static class CaptureMove extends Move {

        private final Piece capturedPiece;

        public CaptureMove(final Board board, Piece movedPiece, final int[] destinationCoord, Piece capturedPiece) {
            super(board, movedPiece, destinationCoord);
            this.capturedPiece = capturedPiece;
        }

        @Override
        public String toString() {
            switch (this.getDestinationCoord()[1]) {
                case 0: 
                    return this.getMovedPiece().toString() + "xa" + (8-this.getDestinationCoord()[0]);
                case 1: 
                    return this.getMovedPiece().toString() + "xb" + (8-this.getDestinationCoord()[0]);
                case 2: 
                    return this.getMovedPiece().toString() + "xc" + (8-this.getDestinationCoord()[0]);
                case 3: 
                    return this.getMovedPiece().toString() + "xd" + (8-this.getDestinationCoord()[0]);
                case 4: 
                    return this.getMovedPiece().toString() + "xe" + (8-this.getDestinationCoord()[0]);
                case 5: 
                    return this.getMovedPiece().toString() + "xf" + (8-this.getDestinationCoord()[0]);
                case 6: 
                    return this.getMovedPiece().toString() + "xg" + (8-this.getDestinationCoord()[0]);
                case 7: 
                    return this.getMovedPiece().toString() + "xh" + (8-this.getDestinationCoord()[0]);
                default:
                    return null;
            }
        }

        @Override
        public int hashCode() {
            return super.hashCode() + capturedPiece.hashCode(); 
        }

        @Override
        public boolean equals(final Object other) {
            if (!(other instanceof CaptureMove)) {
                return false;
            }
            final CaptureMove otherCapture= (CaptureMove) other;
            return super.equals(otherCapture) && getCapturedPiece().fullEquals(otherCapture.getCapturedPiece());
        }

        @Override
        public Board execute() {
            return super.execute();
        }

        @Override
        public boolean isCapture() {
            return true;
        }

        @Override
        public boolean isCastlingMove() {
            return false;
        }

        @Override
        public Piece getCapturedPiece() {
            return this.capturedPiece;
        }
    }



    public static final class PawnMove extends Move {
        public PawnMove(final Board board, Piece movedPiece, final int[] destinationCoord) {
            super(board, movedPiece, destinationCoord);
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof PawnMove && super.equals(other);
        }

        @Override
        public Board execute() {
            return super.execute();
        }

        @Override
        public String toString() {
            switch (this.getDestinationCoord()[1]) {
                case 0:
                    return "a" + (8-this.getDestinationCoord()[0]);
                case 1:
                    return "b" + (8-this.getDestinationCoord()[0]);
                case 2:
                    return "c" + (8-this.getDestinationCoord()[0]);
                case 3:
                    return "d" + (8-this.getDestinationCoord()[0]);
                case 4:
                    return "e" + (8-this.getDestinationCoord()[0]);
                case 5:
                    return "f" + (8-this.getDestinationCoord()[0]);
                case 6:
                    return "g" + (8-this.getDestinationCoord()[0]);
                case 7:
                    return "h" + (8-this.getDestinationCoord()[0]);
                default:
                    return null;
            }
        }
    }



    public static class PawnCapture extends CaptureMove {
        public PawnCapture(final Board board, Piece movedPiece, final int[] destinationCoord, Piece capturedPiece) {
            super(board, movedPiece, destinationCoord, capturedPiece);
        }

        @Override
        public Board execute() {
            return super.execute();
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof PawnCapture && super.equals(other);
        }

        @Override
        public String toString() {
            switch (this.getCurrentCoord()[1]) {
                case 0:
                    return "a" + super.toString().substring(1);
                case 1:
                    return "b" + super.toString().substring(1);
                case 2:
                    return "c" + super.toString().substring(1);
                case 3:
                    return "d" + super.toString().substring(1);
                case 4:
                    return "e" + super.toString().substring(1);
                case 5:
                    return "f" + super.toString().substring(1);
                case 6:
                    return "g" + super.toString().substring(1);
                case 7:
                    return "h" + super.toString().substring(1);
                default:
                    return null;
            }
        }
    }



    public static final class EnPassantCapture extends PawnCapture {
        public EnPassantCapture(final Board board, Piece movedPiece, final int[] destinationCoord, Piece capturedPiece) {
            super(board, movedPiece, destinationCoord, capturedPiece);
        }

        @Override
        public String toString() {
            return super.toString() + " ep";
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof EnPassantCapture && super.equals(other);
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (Piece piece : this.board.getCurrentPlayer().getActivePieces()) {
                if (!(this.movedPiece.fullEquals(piece))) {
                    builder.setPiece(piece);
                }
            }

            for (Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces()) {
                if (!(piece.fullEquals(this.getCapturedPiece()))) {
                    builder.setPiece(piece);
                }
            }

            builder.setPiece(getMovedPiece().movePiece(this));
            builder.setJumpedPawn(null);
            builder.setEnPassantPawn((Pawn) this.getMovedPiece());
            builder.setCurrentColor(this.board.getCurrentPlayer().getOpponent().getColor());
            return builder.build();
        }
    }



    public static final class PawnJump extends Move {
        public PawnJump(final Board board, Piece movedPiece, final int[] destinationCoord) {
            super(board, movedPiece, destinationCoord);
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (final Piece piece : this.board.getCurrentPlayer().getActivePieces()) {
                if (!piece.fullEquals(this.getMovedPiece())) {
                    builder.setPiece(piece);
                }
            }

            for (final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }

            final Pawn movedPawn = (Pawn) this.getMovedPiece().movePiece(this);
            builder.setPiece(movedPawn);
            builder.setJumpedPawn(movedPawn);
            builder.setEnPassantPawn(null);
            builder.setCurrentColor(this.board.getCurrentPlayer().getOpponent().getColor());
            return builder.build();
        }

        @Override
        public String toString() {
            switch (this.getDestinationCoord()[1]) {
                case 0:
                    return "a" + (8-this.getDestinationCoord()[0]);
                case 1:
                    return "b" + (8-this.getDestinationCoord()[0]);
                case 2:
                    return "c" + (8-this.getDestinationCoord()[0]);
                case 3:
                    return "d" + (8-this.getDestinationCoord()[0]);
                case 4:
                    return "e" + (8-this.getDestinationCoord()[0]);
                case 5:
                    return "f" + (8-this.getDestinationCoord()[0]);
                case 6:
                    return "g" + (8-this.getDestinationCoord()[0]);
                case 7:
                    return "h" + (8-this.getDestinationCoord()[0]);
                default:
                    return null;
            }
        }
    }



    public abstract static class Castle extends Move {
        protected final Rook castleRook;
        protected final int[] castleRookPos;
        protected final int[] castleRookDestination;
        
        public Castle(final Board board, Piece movedPiece, final int[] destinationCoord, 
                      final Rook castleRook, final int[] castleRookPos, final int[] castleRookDestination) {
            super(board, movedPiece, destinationCoord);
            this.castleRook = castleRook;
            this.castleRookPos = castleRookPos;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook() {
            return this.castleRook;
        }

        @Override
        public boolean isCastlingMove() {
            return true;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.castleRook.hashCode();
            result = prime * result + this.castleRookDestination[0] * 8 + this.castleRookDestination[1];
            return result;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Castle && super.equals(other) && this.castleRook.equals( ((Castle) other).getCastleRook() );
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (final Piece piece : this.board.getCurrentPlayer().getActivePieces()) {
                if (!(piece.fullEquals(this.castleRook)) && !(piece.fullEquals(this.getMovedPiece()))) {
                    builder.setPiece(piece);
                }
            }

            for (final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }

            builder.removePiece(this.castleRookPos);
            builder.removePiece(this.getMovedPiece());
            builder.setPiece(this.getMovedPiece().movePiece(this));
            builder.setPiece(new Rook(this.castleRookDestination, this.board.getCurrentPlayer().getColor(), false));
            builder.setJumpedPawn(null);
            builder.setEnPassantPawn(null);
            builder.setCurrentColor(this.board.getCurrentPlayer().getOpponent().getColor());
            return builder.build();
        }
    }



    public static final class KingSideCastle extends Castle {
        public KingSideCastle(final Board board, Piece movedPiece, final int[] destinationCoord,
                              final Rook castleRook, final int[] castleRookPos, final int[] castleRookDestination) {
            super(board, movedPiece, destinationCoord, castleRook, castleRookPos, castleRookDestination);
        }

        // super implements correct equals

        @Override
        public String toString() {
            return "O-O";
        }
    }



    public static final class QueenSideCastle extends Castle {
        public QueenSideCastle(final Board board, Piece movedPiece, final int[] destinationCoord,
                               final Rook castleRook, final int[] castleRookPos, final int[] castleRookDestination) {
            super(board, movedPiece, destinationCoord, castleRook, castleRookPos, castleRookDestination);
        }

        // super implements correct equals

        @Override
        public String toString() {
            return "O-O-O";
        }
    }



    public static final class NullMove extends Move {
        public NullMove() {
            super(null, null, new int[]{-1, -1});
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Cannot execute move of type 'NullMove'");
        }
    }

    public static class MoveFactory {
        private MoveFactory() {
            throw new RuntimeException("Cannot instantiate me!");
        }

        public static Move createMove(Board board, final int[] currentCoord, final int[] destinationCoord) {
            for (Move move : board.getCurrentPlayer().getLegalMoves()) {
                if (move.getCurrentCoord()[0] == currentCoord[0] && move.getCurrentCoord()[1] == currentCoord[1]) {
                    if (move.getDestinationCoord()[0] == destinationCoord[0] && move.getDestinationCoord()[1] == destinationCoord[1]) {
                        return move;
                    }
                }
            }
            return NULL_MOVE;
        }
    }

}
