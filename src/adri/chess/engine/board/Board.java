package adri.chess.engine.board;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import adri.chess.engine.ChessColor;
import adri.chess.engine.pieces.Bishop;
import adri.chess.engine.pieces.King;
import adri.chess.engine.pieces.Knight;
import adri.chess.engine.pieces.Pawn;
import adri.chess.engine.pieces.Piece;
import adri.chess.engine.pieces.Queen;
import adri.chess.engine.pieces.Rook;
import adri.chess.engine.player.BlackPlayer;
import adri.chess.engine.player.Player;
import adri.chess.engine.player.WhitePlayer;

public class Board {

    private final Tile[][] gameBoard;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private Player currentPlayer;

    private final Pawn jumpedPawn;
    private final Pawn enPassantPawn;

    private Board(final Builder builder) {
        this.gameBoard = createGameBoard();
        this.whitePieces = calculateActivePieces(this.gameBoard, ChessColor.WHITE);
        this.blackPieces = calculateActivePieces(this.gameBoard, ChessColor.BLACK);

        this.jumpedPawn = Builder.jumpedPawn;
        this.enPassantPawn = Builder.enPassantPawn;

        final Collection<Move> whiteBaseLegalMoves = calculatePlayerLegalMoves(this.whitePieces);
        final Collection<Move> blackBaseLegalMoves = calculatePlayerLegalMoves(this.blackPieces);

        this.whitePlayer = new WhitePlayer(this, whiteBaseLegalMoves, blackBaseLegalMoves);
        this.blackPlayer = new BlackPlayer(this, blackBaseLegalMoves, whiteBaseLegalMoves);

        this.currentPlayer = builder.currentColor.choosePlayer(this.whitePlayer, this.blackPlayer);
    }

    public Pawn getJumpedPawn() {
        return this.jumpedPawn;
    }

    public Pawn getEnPassantPawn() {
        return this.enPassantPawn;
    }

    public Collection<Piece> getBlackPieces() {
        return this.blackPieces;
    }

    public Collection<Piece> getWhitePieces() {
        return this.whitePieces;
    }

    public Player getWhitePlayer() {
        return this.whitePlayer;
    }

    public Player getBlackPlayer() {
        return this.blackPlayer;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (int rank = 0; rank < BoardUtils.NUM_RANKS; rank++) {
            for (int file = 0; file < BoardUtils.NUM_FILES; file++) {
                String tileText = this.gameBoard[rank][file].toString();
                builder.append(String.format("%3s", tileText));
                if (file == BoardUtils.NUM_FILES - 1) {
                    builder.append("\n");
                }
            }
        }
        return builder.toString();
    }


    private Collection<Move> calculatePlayerLegalMoves(Collection<Piece> pieces) {
        final List<Move> legalMoves = new ArrayList<>();
        
        for (Piece piece : pieces) {
            Collection<Move> pieceMoves = piece.calculateLegalMoves(this);
            legalMoves.addAll(pieceMoves);
        }

        return Collections.unmodifiableList(legalMoves);
    }

    private static Collection<Piece> calculateActivePieces(final Tile[][] gameBoard, ChessColor color) {
        List<Piece> activePieces = new ArrayList<>();

        for (Tile[] rank : gameBoard) {
            for (Tile tile : rank) {
                if (tile.hasPiece()) {
                    if (color == tile.getPiece().getPieceColor()) {
                        activePieces.add(tile.getPiece());
                    }
                }
            }
        }
        return Collections.unmodifiableList(activePieces);
    }

    public Tile getTile(int[] tileCoord) {
        return gameBoard[tileCoord[0]][tileCoord[1]];
    }

    private static Tile[][] createGameBoard() {
        Tile[][] tiles = new Tile[BoardUtils.NUM_RANKS][BoardUtils.NUM_FILES];
        for (int rank = 0; rank < BoardUtils.NUM_RANKS; rank++) {
            for (int file = 0; file < BoardUtils.NUM_FILES; file++) {
                int[] newPos = {rank, file};
                tiles[rank][file] = Tile.createTile(newPos, Builder.boardConfigMap.get(rank * 8 + file));
            }
        }
        
        return tiles;
    }

    public Iterable<Move> getMoves() {
        Stream<Move> allMoves = Stream.concat(this.whitePlayer.getLegalMoves().stream(), this.blackPlayer.getLegalMoves().stream());
        return Collections.unmodifiableList(allMoves.toList());
    }

    public static Board createStandardBoard() {
        final Builder builder = new Builder();
        // black pieces
        builder.setPiece(new Rook(new int[]{0, 7}, ChessColor.BLACK));
        builder.setPiece(new Knight(new int[]{0, 6}, ChessColor.BLACK));
        builder.setPiece(new Bishop(new int[]{0, 5}, ChessColor.BLACK));
        builder.setPiece(new King(new int[]{0, 4}, ChessColor.BLACK, true, true));
        builder.setPiece(new Queen(new int[]{0, 3}, ChessColor.BLACK));
        builder.setPiece(new Bishop(new int[]{0, 2}, ChessColor.BLACK));
        builder.setPiece(new Knight(new int[]{0, 1}, ChessColor.BLACK));
        builder.setPiece(new Rook(new int[]{0, 0}, ChessColor.BLACK));
        // black pawns
        builder.setPiece(new Pawn(new int[]{1, 7}, ChessColor.BLACK));
        builder.setPiece(new Pawn(new int[]{1, 6}, ChessColor.BLACK));
        builder.setPiece(new Pawn(new int[]{1, 6}, ChessColor.BLACK));
        builder.setPiece(new Pawn(new int[]{1, 5}, ChessColor.BLACK));
        builder.setPiece(new Pawn(new int[]{1, 4}, ChessColor.BLACK));
        builder.setPiece(new Pawn(new int[]{1, 3}, ChessColor.BLACK));
        builder.setPiece(new Pawn(new int[]{1, 2}, ChessColor.BLACK));
        builder.setPiece(new Pawn(new int[]{1, 1}, ChessColor.BLACK));
        builder.setPiece(new Pawn(new int[]{1, 0}, ChessColor.BLACK));

        // white pieces
        builder.setPiece(new Rook(new int[]{7, 7}, ChessColor.WHITE));
        builder.setPiece(new Knight(new int[]{7, 6}, ChessColor.WHITE));
        builder.setPiece(new Bishop(new int[]{7, 5}, ChessColor.WHITE));
        builder.setPiece(new King(new int[]{7, 4}, ChessColor.WHITE, true, true));
        builder.setPiece(new Queen(new int[]{7, 3}, ChessColor.WHITE));
        builder.setPiece(new Bishop(new int[]{7, 2}, ChessColor.WHITE));
        builder.setPiece(new Knight(new int[]{7, 1}, ChessColor.WHITE));
        builder.setPiece(new Rook(new int[]{7, 0}, ChessColor.WHITE));
        // white pawns
        builder.setPiece(new Pawn(new int[]{6, 7}, ChessColor.WHITE));
        builder.setPiece(new Pawn(new int[]{6, 6}, ChessColor.WHITE));
        builder.setPiece(new Pawn(new int[]{6, 5}, ChessColor.WHITE));
        builder.setPiece(new Pawn(new int[]{6, 4}, ChessColor.WHITE));
        builder.setPiece(new Pawn(new int[]{6, 3}, ChessColor.WHITE));
        builder.setPiece(new Pawn(new int[]{6, 2}, ChessColor.WHITE));
        builder.setPiece(new Pawn(new int[]{6, 1}, ChessColor.WHITE));
        builder.setPiece(new Pawn(new int[]{6, 0}, ChessColor.WHITE));

        builder.setCurrentColor(ChessColor.WHITE);
        return builder.build();
    }

    public static Board createCustomBoard() {
        final Builder builder = new Builder();
        // black pieces
        builder.setPiece(new Rook(new int[]{0, 7}, ChessColor.BLACK));
        builder.setPiece(new Knight(new int[]{0, 6}, ChessColor.BLACK));
        builder.setPiece(new Bishop(new int[]{0, 5}, ChessColor.BLACK));
        builder.setPiece(new King(new int[]{4, 3}, ChessColor.BLACK, false, false));
        builder.setPiece(new Queen(new int[]{0, 3}, ChessColor.BLACK));
        builder.setPiece(new Bishop(new int[]{0, 2}, ChessColor.BLACK));
        builder.setPiece(new Knight(new int[]{0, 1}, ChessColor.BLACK));
        builder.setPiece(new Rook(new int[]{0, 0}, ChessColor.BLACK));
        // black pawns
        builder.setPiece(new Pawn(new int[]{1, 7}, ChessColor.BLACK));
        builder.setPiece(new Pawn(new int[]{1, 6}, ChessColor.BLACK));
        builder.setPiece(new Pawn(new int[]{1, 6}, ChessColor.BLACK));
        builder.setPiece(new Pawn(new int[]{1, 5}, ChessColor.BLACK));
        builder.setPiece(new Pawn(new int[]{1, 4}, ChessColor.BLACK));
        builder.setPiece(new Pawn(new int[]{1, 3}, ChessColor.BLACK));
        builder.setPiece(new Pawn(new int[]{1, 2}, ChessColor.BLACK));
        builder.setPiece(new Pawn(new int[]{1, 1}, ChessColor.BLACK));
        builder.setPiece(new Pawn(new int[]{1, 0}, ChessColor.BLACK));

        // white pieces
        builder.setPiece(new Rook(new int[]{7, 7}, ChessColor.WHITE));
        //builder.setPiece(new Knight(new int[]{7, 6}, ChessColor.WHITE));
        //builder.setPiece(new Bishop(new int[]{7, 5}, ChessColor.WHITE));
        builder.setPiece(new King(new int[]{7, 4}, ChessColor.WHITE, false, false));
        builder.setPiece(new Queen(new int[]{7, 3}, ChessColor.WHITE));
        builder.setPiece(new Bishop(new int[]{7, 2}, ChessColor.WHITE));
        builder.setPiece(new Knight(new int[]{7, 1}, ChessColor.WHITE));
        builder.setPiece(new Rook(new int[]{7, 0}, ChessColor.WHITE));
        // white pawns
        builder.setPiece(new Pawn(new int[]{6, 7}, ChessColor.WHITE));
        builder.setPiece(new Pawn(new int[]{6, 6}, ChessColor.WHITE));
        builder.setPiece(new Pawn(new int[]{6, 5}, ChessColor.WHITE));
        builder.setPiece(new Pawn(new int[]{6, 4}, ChessColor.WHITE));
        builder.setPiece(new Pawn(new int[]{6, 3}, ChessColor.WHITE));
        builder.setPiece(new Pawn(new int[]{6, 2}, ChessColor.WHITE));
        builder.setPiece(new Pawn(new int[]{6, 1}, ChessColor.WHITE));
        builder.setPiece(new Pawn(new int[]{6, 0}, ChessColor.WHITE));

        builder.setCurrentColor(ChessColor.WHITE);
        return builder.build();
    }

    public static Board createEmptyBoard() {
        final Builder builder = new Builder();
        builder.setCurrentColor(ChessColor.WHITE);
        return builder.build();
    }

    public static class Builder {

        // Map tile id to piece on tile
        private static Map<Integer, Piece> boardConfigMap;
        private ChessColor currentColor;
        private static Pawn jumpedPawn;
        private static Pawn enPassantPawn;

        public Builder() {
            boardConfigMap = new HashMap<>();
        }

        public Builder setPiece(Piece piece) {
            boardConfigMap.put(piece.getPiecePos()[0] * 8 + piece.getPiecePos()[1], piece);
            return this;
        }

        public Builder setPiece(Piece piece, int[] pos) {
            boardConfigMap.put(pos[0] * 8 + pos[1], piece);
            return this;
        }

        public Builder removePiece(Piece piece) {
            boardConfigMap.remove(piece.getPiecePos()[0]*8 + piece.getPiecePos()[1]);
            return this;
        }

        public Builder removePiece(int[] pos) {
            boardConfigMap.remove(pos[0]*8 + pos[1]);
            return this;
        }

        public Pawn getJumpedPawn() {
            return jumpedPawn;
        }

        public Pawn getEnPassantPawn() {
            return enPassantPawn;
        }

        public Builder setCurrentColor(ChessColor color) {
            this.currentColor = color;
            return this;
        }

        public void setJumpedPawn(Pawn movedPawn) {
            jumpedPawn = movedPawn;
        }

        public void setEnPassantPawn(Pawn movedPawn) {
            enPassantPawn = movedPawn;
        }

        public Board build() {
            return new Board(this);
        }
    }
}
