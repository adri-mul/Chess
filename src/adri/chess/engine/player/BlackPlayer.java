package adri.chess.engine.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import adri.chess.engine.ChessColor;
import adri.chess.engine.board.Board;
import adri.chess.engine.board.Move;
import adri.chess.engine.board.Move.KingSideCastle;
import adri.chess.engine.board.Move.QueenSideCastle;
import adri.chess.engine.board.Tile;
import adri.chess.engine.pieces.Piece;
import adri.chess.engine.pieces.Rook;

public class BlackPlayer extends Player {

    public BlackPlayer(final Board board, final Collection<Move> blackBaseLegalMoves, final Collection<Move> whiteBaseLegalMoves) {
        super(board, blackBaseLegalMoves, whiteBaseLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public ChessColor getColor() {
        return ChessColor.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.getWhitePlayer();
    }

    @Override
    public Collection<Move> calculateKingCastles(Collection<Move> legalMoves, Collection<Move> opponentLegalMoves) {
        final List<Move> kingCastles = new ArrayList<>();
        if (getPlayerKing().isFirstMove() && !this.isInCheck()) {
            // black king-side castle
            if (!this.board.getTile(new int[]{0, 5}).hasPiece() && !this.board.getTile(new int[]{0, 6}).hasPiece()) {
                final Tile rookTile = this.board.getTile(new int[] {0, 7});
                if (rookTile.hasPiece() && rookTile.getPiece().getPieceType().isRook() && rookTile.getPiece().isFirstMove()) {
                    final Rook castleRook = (Rook) rookTile.getPiece();
                    final int[] newKingPos = new int[]{0, 6};
                    final int[] newCastleRookPos = new int[]{0, 5};
                    if (calculateAttackMovesOnTile(new int[]{0, 5}, opponentLegalMoves).isEmpty() &&
                        calculateAttackMovesOnTile(new int[]{0, 6}, opponentLegalMoves).isEmpty()){
                        kingCastles.add(new KingSideCastle(board, playerKing, newKingPos, castleRook, rookTile.getTileCoord(), newCastleRookPos));
                    }
                }
            } if (!this.board.getTile(new int[]{0, 1}).hasPiece() && 
                  !this.board.getTile(new int[]{0, 2}).hasPiece() && 
                  !this.board.getTile(new int[]{0, 3}).hasPiece()) {
                    final Tile rookTile = this.board.getTile(new int[]{0, 0});
                    if (rookTile.hasPiece() && rookTile.getPiece().getPieceType().isRook() && rookTile.getPiece().isFirstMove()) {
                        final Rook castleRook = (Rook) rookTile.getPiece();
                        final int[] newKingPos = new int[]{0, 2};
                        final int[] newCastleRookPos = new int[]{0, 3};
                        if (calculateAttackMovesOnTile(new int[]{0, 1}, opponentLegalMoves).isEmpty() &&
                            calculateAttackMovesOnTile(new int[]{0, 2}, opponentLegalMoves).isEmpty() && 
                            calculateAttackMovesOnTile(new int[]{0, 3}, opponentLegalMoves).isEmpty()){
                        kingCastles.add(new QueenSideCastle(board, playerKing, newKingPos, castleRook, rookTile.getTileCoord(), newCastleRookPos));
                    }
                    }
            }
        }

        return Collections.unmodifiableList(kingCastles);
    }

}
