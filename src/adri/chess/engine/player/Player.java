package adri.chess.engine.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import adri.chess.engine.ChessColor;
import adri.chess.engine.board.Board;
import adri.chess.engine.board.Move;
import adri.chess.engine.pieces.King;
import adri.chess.engine.pieces.Piece;

public abstract class Player {

    protected final Board board;
    protected final King playerKing;
    protected Collection<Move> legalMoves;
    private final boolean isInCheck;

    protected Player(Board board, Collection<Move> legalMoves, Collection<Move> opponentMoves) {
        this.board = board;
        this.playerKing = establishKing();
        this.legalMoves = legalMoves;
        this.isInCheck = !this.calculateAttackMovesOnTile(this.playerKing.getPiecePos(), opponentMoves).isEmpty();
        this.legalMoves = Stream.concat((this.legalMoves.stream()), calculateKingCastles(legalMoves, opponentMoves).stream()).toList();
    }

    public Collection<Move> calculateAttackMovesOnTile(int[] piecePos, Collection<Move> opponentMoves) {
        List<Move> attackMoves = new ArrayList<>();
        for (Move move : opponentMoves) {
            if (move.isTarget(piecePos)) {
                attackMoves.add(move);
            }
        }
        return Collections.unmodifiableList(attackMoves);
    }

    private King establishKing() {
        for (Piece piece : getActivePieces()) {
            if (piece.getPieceType().isKing()) {
                return (King) piece;
            }
        }
        throw new RuntimeException("King could not be found; error in the board");
    }

    public King getPlayerKing() {
        return playerKing;
    }

    public boolean isMoveLegal(Move move) {
        return this.legalMoves.contains(move);
    }

    public Collection<Move> getLegalMoves() {
        return this.legalMoves;
    }

    public boolean isInCheck() {
        //this.isInCheck = !Player.calculateAttackMovesOnTile(this.playerKing.getPiecePos(), opponentMoves).isEmpty();
        return this.isInCheck;
    }

    public boolean isInCheckMate() {
        return this.isInCheck && !hasEscapeMove();
    }

    public boolean isInStaleMate() {
        return ( (this.legalMoves.isEmpty() || !hasEscapeMove()) && !this.isInCheck) ||
                // check if only kings are battling. Kings are guaranteed to be the last piece, bc they can't be captured
                (this.getActivePieces().size() == 1 && this.getOpponent().getActivePieces().size() == 1);
    }

    protected boolean hasEscapeMove() {
        
        for (Move move : this.legalMoves) {
            final MoveUpdate update = playMove(move);
            if (update.getMoveStatus().isDone()) {
                return true;
            }
        }
        return false;
    }

    public boolean isCastled() {
        return false;
    }

    public boolean isKingSideCastleAllowed() {
        return this.playerKing.isKingSideCastleAllowed();
    }

    public boolean isQueenSideCastleAllowed() {
        return this.playerKing.isQueenSideCastleAllowed();
    }

    public MoveUpdate playMove(Move move) {
        if (!isMoveLegal(move)) {
            return new MoveUpdate(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }
        Board updateBoard = move.execute();
        Collection<Move> attacksOnKing = calculateAttacksOnKing(move, updateBoard);
        

        if (!attacksOnKing.isEmpty()) {
            //this.legalMoves.remove(move);
            return new MoveUpdate(this.board, move, MoveStatus.PLAYER_IN_CHECK);
        } else {
            return new MoveUpdate(updateBoard, move, MoveStatus.DONE);
        }

    }

    public Collection<Move> calculateAttacksOnKing(Move move, Board board) {
        // calculate attacks on king
        final Collection<Move> attacksOnKing = 
        calculateAttackMovesOnTile(board.getCurrentPlayer().getOpponent().getPlayerKing().getPiecePos(), 
                                   board.getCurrentPlayer().getLegalMoves());
        return attacksOnKing;
    }

    
    public abstract Collection<Piece> getActivePieces();
    public abstract ChessColor getColor();
    public abstract Player getOpponent();
    public abstract Collection<Move> calculateKingCastles(Collection<Move> legalMoves, Collection<Move> opponentLegalMoves);
}
