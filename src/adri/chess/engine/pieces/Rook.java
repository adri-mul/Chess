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

public class Rook extends Piece {

    public Rook(int[] piecePos, ChessColor pieceColor) {
        super(PieceType.ROOK, piecePos, pieceColor, true);
    }

    public Rook(int[] piecePos, ChessColor pieceColor, boolean isFirstMove) {
        super(PieceType.ROOK, piecePos, pieceColor, isFirstMove);
    }

    public boolean isFirstMove() {
        return super.isFirstMove();
    }

    @Override
    public String toString() {
        return PieceType.ROOK.toString();
    }

    @Override
    public Rook movePiece(Move move) {
        return new Rook(move.getDestinationCoord(), pieceColor, false);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        int[] candidateDestinationCoord = new int[2];
        List<Move> legalMoves = new ArrayList<>();
        
        // calculate moves forwards
        for (int i = this.piecePos[0] + 1; i < 8; i++) {
            candidateDestinationCoord[0] = i;
            candidateDestinationCoord[1] = this.piecePos[1];

            if (!BoardUtils.validTileCoordinate(candidateDestinationCoord) || 
            BoardUtils.isBlockedTile(this.pieceColor, board.getTile(candidateDestinationCoord))) {
                break;
            } else {
                addLegalMove(board, legalMoves, candidateDestinationCoord);
            } if (board.getTile(candidateDestinationCoord).hasPiece()) {
                break;
            }
        }

        // calculate moves backwards
        for (int i = this.piecePos[0] - 1; i >= 0; i--) {
            candidateDestinationCoord[0] = i;
            candidateDestinationCoord[1] = this.piecePos[1];

            if (!BoardUtils.validTileCoordinate(candidateDestinationCoord) || 
            BoardUtils.isBlockedTile(this.pieceColor, board.getTile(candidateDestinationCoord))) {
                break;
            } else {
                addLegalMove(board, legalMoves, candidateDestinationCoord);
            } if (board.getTile(candidateDestinationCoord).hasPiece()) {
                break;
            }
        }

        // calculate moves left
        for (int i = this.piecePos[1] - 1; i >= 0; i--) {
            candidateDestinationCoord[0] = this.piecePos[0];
            candidateDestinationCoord[1] = i;

            if (!BoardUtils.validTileCoordinate(candidateDestinationCoord) || 
            BoardUtils.isBlockedTile(this.pieceColor, board.getTile(candidateDestinationCoord))) {
                break;
            } else {
                addLegalMove(board, legalMoves, candidateDestinationCoord);
            } if (board.getTile(candidateDestinationCoord).hasPiece()) {
                break;
            }
        }

        // calculate moves right
        for (int i = this.piecePos[1] + 1; i < 8; i++) {
            candidateDestinationCoord[0] = this.piecePos[0];
            candidateDestinationCoord[1] = i;

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
