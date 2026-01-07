package adri.chess.engine.pieces;

import adri.chess.engine.ChessColor;
import adri.chess.engine.board.Board;
import adri.chess.engine.board.BoardUtils;
import adri.chess.engine.board.Move;
import adri.chess.engine.board.Move.CaptureMove;
import adri.chess.engine.board.Tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Collections;

public class Knight extends Piece {

    private static final int[][] CANDIDATE_LEGAL_MOVES = {{-1, -2}, {1, -2}, {2, -1}, {2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}}; // possible squares starting from west, clockwise

    public Knight(int[] piecePos, ChessColor pieceColor) {
        super(PieceType.KNIGHT, piecePos, pieceColor);
    }

    @Override
    public String toString() {
        return PieceType.KNIGHT.toString();
    }

    @Override
    public Piece movePiece(Move move) {
        return new Knight(move.getDestinationCoord(), pieceColor);
    }
    
    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        int[] candidateDestinationCoord = new int[2]; // set to {0, 0}
        List<Move> legalMoves = new ArrayList<Move>();

        for (int[] currentCandidate: CANDIDATE_LEGAL_MOVES) {

            candidateDestinationCoord[0] = this.piecePos[0] + currentCandidate[0];
            candidateDestinationCoord[1] = this.piecePos[1] + currentCandidate[1];

            

            // check if candidate move is within board bounds
            if (BoardUtils.validTileCoordinate(candidateDestinationCoord)) {
                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoord);

                if (!candidateDestinationTile.hasPiece()) {
                    legalMoves.add(new Move(board, this, candidateDestinationCoord.clone())); // placeholder
                } else {
                    final Piece pieceAtTile = candidateDestinationTile.getPiece();
                    final ChessColor pieceColor = pieceAtTile.getPieceColor();

                    // check if knight's color is different than other piece
                    if (this.pieceColor != pieceColor) {
                        legalMoves.add(new CaptureMove(board, this, candidateDestinationCoord.clone(), pieceAtTile)); // placeholder
                    }
                }
            }
        }

        return Collections.unmodifiableList(legalMoves);
    }

    

}
