package adri.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import adri.chess.engine.ChessColor;
import adri.chess.engine.board.Board;
import adri.chess.engine.board.BoardUtils;
import adri.chess.engine.board.Move;
import adri.chess.engine.board.Move.EnPassantCapture;
import adri.chess.engine.board.Move.PawnCapture;
import adri.chess.engine.board.Move.PawnJump;
import adri.chess.engine.board.Move.PawnMove;
import adri.chess.engine.board.Move.PawnPromotion;

public class Pawn extends Piece{

    private static final int[][] CANDIDATE_LEGAL_MOVES = {{1, 0}, {2, 0}, {1, 1}, {1, -1}};

    public Pawn(int[] piecePos, ChessColor pieceColor) {
        super(PieceType.PAWN, piecePos, pieceColor);
    }

    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }

    @Override
    public Pawn movePiece(Move move) {
        return new Pawn(move.getDestinationCoord(), pieceColor);
    }
    
    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        int[] candidateDestinationCoord = new int[2];
        List<Move> legalMoves = new ArrayList<>();

        for (int[] currentCandidateOffset: CANDIDATE_LEGAL_MOVES) {
            candidateDestinationCoord[0] = this.piecePos[0] + (this.getPieceColor().getDirection() * currentCandidateOffset[0]);
            candidateDestinationCoord[1] = this.piecePos[1] + currentCandidateOffset[1];

            if (!BoardUtils.validTileCoordinate(candidateDestinationCoord)) {
                continue;
            }

            if (currentCandidateOffset.equals(CANDIDATE_LEGAL_MOVES[0]) && !board.getTile(candidateDestinationCoord).hasPiece()) {
                if (this.pieceColor.isPromotionSquare(candidateDestinationCoord)) {
                    legalMoves.add(new PawnPromotion( new PawnMove(board, this, candidateDestinationCoord.clone()) ));
                } else {
                    legalMoves.add(new PawnMove(board, this, candidateDestinationCoord.clone()));
                }
            } else if (currentCandidateOffset.equals(CANDIDATE_LEGAL_MOVES[1])) {

                // check if pawn is white and on second rank, or black and on seventh rank
                if (this.getPieceColor().isWhite() && this.piecePos[0] == 6) {
                    // tile jumped over
                    final int[] jumpedTile = {this.piecePos[0] - 1, this.piecePos[1]};
                    if (!board.getTile(jumpedTile).hasPiece() && !board.getTile(candidateDestinationCoord).hasPiece()) {
                        legalMoves.add(new PawnJump(board, this, candidateDestinationCoord.clone()));
                    }
                } else if (this.getPieceColor().isBlack() && this.piecePos[0] == 1) {
                    final int[] jumpedTile = {this.piecePos[0] + 1, this.piecePos[1]};
                    if (!board.getTile(jumpedTile).hasPiece() && !board.getTile(candidateDestinationCoord).hasPiece()) {
                        legalMoves.add(new PawnJump(board, this, candidateDestinationCoord.clone()));
                    }
                }

            // handles captures
            } else if (currentCandidateOffset[0] == CANDIDATE_LEGAL_MOVES[2][0] && 
                       currentCandidateOffset[1] == CANDIDATE_LEGAL_MOVES[2][1]) {
                if (board.getTile(candidateDestinationCoord).hasPiece()) {
                    final Piece attackedPiece = board.getTile(candidateDestinationCoord).getPiece();
                    if (this.pieceColor != attackedPiece.pieceColor) {
                        if (this.pieceColor.isPromotionSquare(candidateDestinationCoord)) {
                            legalMoves.add(new PawnPromotion( new PawnCapture(board, this, candidateDestinationCoord.clone(), attackedPiece) ));
                        } else {
                            legalMoves.add(new PawnCapture(board, this, candidateDestinationCoord.clone(), attackedPiece));
                        }
                    }
                // En passant moves
                } else if (board.getJumpedPawn() != null) {
                    //check pawn on left
                    if (board.getJumpedPawn().getPiecePos()[0] == this.piecePos[0] && board.getJumpedPawn().getPiecePos()[1] == 
                        this.piecePos[1] + this.pieceColor.getOppositeDirection()) {
                        final Piece attackedPiece = board.getJumpedPawn();
                        if (this.pieceColor != attackedPiece.getPieceColor()) {
                            if (this.pieceColor.isPromotionSquare(candidateDestinationCoord)) {
                                legalMoves.add(new PawnPromotion( new EnPassantCapture(board, this, candidateDestinationCoord.clone(), attackedPiece) ));
                            } else {
                                legalMoves.add(new EnPassantCapture(board, this, candidateDestinationCoord.clone(), attackedPiece));
                            }
                        }
                    }
                }
            } else if (currentCandidateOffset.equals(CANDIDATE_LEGAL_MOVES[3])) {
                if (board.getTile(candidateDestinationCoord).hasPiece()) {
                    final Piece attackedPiece = board.getTile(candidateDestinationCoord).getPiece();
                    if (this.pieceColor != attackedPiece.pieceColor) {
                        if (this.pieceColor.isPromotionSquare(candidateDestinationCoord)) {
                            legalMoves.add(new PawnPromotion( new PawnCapture(board, this, candidateDestinationCoord.clone(), attackedPiece) ));
                        } else {
                            legalMoves.add(new PawnCapture(board, this, candidateDestinationCoord.clone(), attackedPiece));
                        }
                    }
                // En passant moves
                } else if (board.getJumpedPawn() != null) {
                    // check pawn on right
                    if (board.getJumpedPawn().getPiecePos()[0] == this.piecePos[0] && board.getJumpedPawn().getPiecePos()[1] == 
                        this.piecePos[1] + this.pieceColor.getDirection()) {
                        final Piece attackedPiece = board.getJumpedPawn();
                        if (this.pieceColor != attackedPiece.getPieceColor()) {
                            if (this.pieceColor.isPromotionSquare(candidateDestinationCoord)) {
                                legalMoves.add(new PawnPromotion( new EnPassantCapture(board, this, candidateDestinationCoord.clone(), attackedPiece) ));
                            } else {
                                legalMoves.add(new EnPassantCapture(board, this, candidateDestinationCoord.clone(), attackedPiece));
                            }
                        }
                    } 
                }
            }
        }

        return Collections.unmodifiableList(legalMoves);
    }

    
    
}
