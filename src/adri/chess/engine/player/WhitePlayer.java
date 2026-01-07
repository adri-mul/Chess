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

public class WhitePlayer extends Player {

    public WhitePlayer(final Board board, final Collection<Move> whiteBaseLegalMoves, final Collection<Move> blackBaseLegalMoves) {
        super(board, whiteBaseLegalMoves, blackBaseLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public ChessColor getColor() {
        return ChessColor.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.getBlackPlayer();
    }

    @Override
    public Collection<Move> calculateKingCastles(final Collection<Move> legalMoves, final Collection<Move> opponentLegalMoves) {
        final List<Move> kingCastles = new ArrayList<>();
        if (getPlayerKing().isFirstMove() && !this.isInCheck()) {
            // white king-side castle
            if (!this.board.getTile(new int[]{7, 6}).hasPiece() && !this.board.getTile(new int[]{7, 5}).hasPiece()) {
                final Tile rookTile = this.board.getTile(new int[] {7, 7});
                if (rookTile.hasPiece() && rookTile.getPiece().getPieceType().isRook() && rookTile.getPiece().isFirstMove()) {
                    final Rook castleRook = (Rook) rookTile.getPiece();
                    final int[] newKingPos = new int[]{7, 6};
                    final int[] newCastleRookPos = new int[]{7, 5};
                    if (calculateAttackMovesOnTile(new int[]{7, 6}, opponentLegalMoves).isEmpty() &&
                        calculateAttackMovesOnTile(new int[]{7, 5}, opponentLegalMoves).isEmpty()) {
                        kingCastles.add(new KingSideCastle(board, playerKing, newKingPos, castleRook, rookTile.getTileCoord(), newCastleRookPos));
                    }
                }
            } if (!this.board.getTile(new int[]{7, 1}).hasPiece() && 
                  !this.board.getTile(new int[]{7, 2}).hasPiece() && 
                  !this.board.getTile(new int[]{7, 3}).hasPiece()) {
                    final Tile rookTile = this.board.getTile(new int[]{7, 0});
                    if (rookTile.hasPiece() && rookTile.getPiece().getPieceType().isRook() && rookTile.getPiece().isFirstMove()) {
                        final Rook castleRook = (Rook) rookTile.getPiece();
                        final int[] newKingPos = new int[]{7, 2};
                        final int[] newCastleRookPos = new int[]{7, 3};
                        if (calculateAttackMovesOnTile(new int[]{7, 1}, opponentLegalMoves).isEmpty() &&
                            calculateAttackMovesOnTile(new int[]{7, 2}, opponentLegalMoves).isEmpty() &&
                            calculateAttackMovesOnTile(new int[]{7, 3}, opponentLegalMoves).isEmpty()) {
                            kingCastles.add(new QueenSideCastle(board, playerKing, newKingPos, castleRook, rookTile.getTileCoord(), newCastleRookPos));
                        }
                    }
            }
        }

        return Collections.unmodifiableList(kingCastles);
    }

}
