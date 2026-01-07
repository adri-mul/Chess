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

public class Bishop extends Piece {
    
    public Bishop(int[] piecePos, ChessColor pieceColor) {
        super(PieceType.BISHOP, piecePos, pieceColor);
    }

    @Override
    public String toString() {
        return PieceType.BISHOP.toString();
    }

    @Override
    public Piece movePiece(Move move) {
        return new Bishop(move.getDestinationCoord(), pieceColor);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        int[] candidateDestinationCoord = new int[2];
        final List<Move> legalMoves = new ArrayList<>();
        
        // bottomright legal moves
        for (int i = 1; i < 8; i++) {
            candidateDestinationCoord[0] = this.piecePos[0] + i;
            candidateDestinationCoord[1] = this.piecePos[1] + i;

            if (!BoardUtils.validTileCoordinate(candidateDestinationCoord) || 
            BoardUtils.isBlockedTile(this.pieceColor, board.getTile(candidateDestinationCoord))) {
                break;
            } else {
                addLegalMove(board, legalMoves, candidateDestinationCoord);
            } if (board.getTile(candidateDestinationCoord).hasPiece()) {
                break;
            }
        }

        // topleft legal moves
        for (int i = 1; this.piecePos[0] - i >= 0 && this.piecePos[1] - i >= 0; i++) {
            candidateDestinationCoord[0] = this.piecePos[0] - i;
            candidateDestinationCoord[1] = this.piecePos[1] - i;

            if (!BoardUtils.validTileCoordinate(candidateDestinationCoord) || 
            BoardUtils.isBlockedTile(this.pieceColor, board.getTile(candidateDestinationCoord))) {
                break;
            } else {
                addLegalMove(board, legalMoves, candidateDestinationCoord);
            } if (board.getTile(candidateDestinationCoord).hasPiece()) {
                break;
            }
        }

        // bottomleft legal moves
        for (int i = 1; this.piecePos[0] - i >= 0 && this.piecePos[1] + i < 8; i++) {
            candidateDestinationCoord[0] = this.piecePos[0] - i;
            candidateDestinationCoord[1] = this.piecePos[1] + i;

            if (!BoardUtils.validTileCoordinate(candidateDestinationCoord) || 
            BoardUtils.isBlockedTile(this.pieceColor, board.getTile(candidateDestinationCoord))) {
                break;
            } else {
                addLegalMove(board, legalMoves, candidateDestinationCoord);
            } if (board.getTile(candidateDestinationCoord).hasPiece()) {
                break;
            }
        }

        // topright legal moves
        for (int i = 1; this.piecePos[0] + i < 8 && this.piecePos[1] - i >= 0; i++) {
            candidateDestinationCoord[0] = this.piecePos[0] + i;
            candidateDestinationCoord[1] = this.piecePos[1] - i;

            if (!BoardUtils.validTileCoordinate(candidateDestinationCoord) || 
            BoardUtils.isBlockedTile(this.pieceColor, board.getTile(candidateDestinationCoord))) {
                break;
            } else {
                addLegalMove(board, legalMoves, candidateDestinationCoord);
            } if (board.getTile(candidateDestinationCoord).hasPiece()) {
                break;
            }
        }

        
        return Collections.unmodifiableList(legalMoves);
    }

    // method to add a legal move to list of legal moves
    private void addLegalMove(Board board, List<Move> legalMoves, int[] coord) {
        if (board.getTile(coord).hasPiece()) {
            Piece pieceAtTile = board.getTile(coord).getPiece();
            ChessColor pieceColor = pieceAtTile.getPieceColor();
            if (this.pieceColor != pieceColor) {
                legalMoves.add(new CaptureMove(board, this, coord.clone(), pieceAtTile));
            }

        } else {
            legalMoves.add(new Move(board, this, coord.clone()));
        }
    }

    

}
