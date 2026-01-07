package adri.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import adri.chess.engine.ChessColor;
import adri.chess.engine.board.Board;
import adri.chess.engine.board.BoardUtils;
import adri.chess.engine.board.Move;
import adri.chess.engine.board.Move.CaptureMove;
import adri.chess.engine.board.Tile;

public class King extends Piece {

    private static final int[][] CANDIDATE_LEGAL_MOVES = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}}; // starting west, going clockwise
    private boolean isCastled;
    private boolean kingSideCastleAllowed;
    private boolean queenSideCastleAllowed;
    
    public King(int[] piecePos, ChessColor pieceColor, final boolean kingSideCastleAllowed, final boolean queenSideCastleAllowed) {
        super(PieceType.KING, piecePos, pieceColor, true);
        this.kingSideCastleAllowed = kingSideCastleAllowed;
        this.queenSideCastleAllowed = queenSideCastleAllowed;
    }

    public King(int[] piecePos, ChessColor pieceColor, boolean isFirstMove, final boolean kingSideCastleAllowed, final boolean queenSideCastleAllowed) {
        super(PieceType.KING, piecePos, pieceColor, isFirstMove);
        this.kingSideCastleAllowed = kingSideCastleAllowed;
        this.queenSideCastleAllowed = queenSideCastleAllowed;
    }

    public boolean isCastled() {
        return this.isCastled;
    }

    public boolean isKingSideCastleAllowed() {
        return this.kingSideCastleAllowed;
    }

    public boolean isQueenSideCastleAllowed() {
        return this.queenSideCastleAllowed;
    }

    public boolean isFirstMove() {
        return super.isFirstMove();
    }

    @Override
    public String toString() {
        return PieceType.KING.toString();
    }

    @Override
    public King movePiece(Move move) {
        return new King(move.getDestinationCoord(), pieceColor, false, false);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        int[] candidateDestinationCoord = new int[2];
        final List<Move> legalMoves = new ArrayList<>();

        for (int[] currentCandidateOffset: CANDIDATE_LEGAL_MOVES) {
            candidateDestinationCoord[0] = this.piecePos[0] + currentCandidateOffset[0];
            candidateDestinationCoord[1] = this.piecePos[1] + currentCandidateOffset[1];

            if (BoardUtils.validTileCoordinate(candidateDestinationCoord)) {
                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoord);
                if (!candidateDestinationTile.hasPiece()) {
                    legalMoves.add(new Move(board, this, candidateDestinationCoord.clone()));
                } else {
                    final Piece pieceAtTile = candidateDestinationTile.getPiece();
                    final ChessColor pieceColor = pieceAtTile.pieceColor;
                    if (this.pieceColor != pieceColor) {
                        legalMoves.add(new CaptureMove(board, this, candidateDestinationCoord.clone(), pieceAtTile));
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    
    
}
