package adri.chess.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Color;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import static javax.swing.SwingUtilities.isRightMouseButton;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.invokeLater;

import adri.chess.engine.ChessColor;
import adri.chess.engine.board.Board;
import adri.chess.engine.board.BoardUtils;
import adri.chess.engine.board.Move;
import adri.chess.engine.board.Tile;
import adri.chess.engine.board.Move.EnPassantCapture;
import adri.chess.engine.board.Move.MoveFactory;
import adri.chess.engine.pieces.King;
import adri.chess.engine.pieces.Piece;
import adri.chess.engine.pieces.Queen;
import adri.chess.engine.pieces.Piece.PieceType;
import adri.chess.engine.player.MoveStatus;
import adri.chess.engine.player.MoveUpdate;
import adri.chess.engine.player.Player;
import adri.chess.engine.player.ai.BotMove;
import adri.chess.engine.player.ai.BotMove2;
import adri.chess.engine.player.ai.BotMove3;
import adri.chess.engine.player.ai.BotMove4;

public class Table {
    // constants
    private static final Dimension OUTER_FRAME_DIMENTION = new Dimension(680, 600);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);
    private static final String imageFolderPath = "src/adri/chess/images/";
    private static final int DEPTH = 4;

    // game parts
    private Board chessBoard;
    private final JFrame gameFrame;
    private BoardPanel boardPanel;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;

    // internal parts
    private final MoveLog moveLog;
    private Tile selectTile;
    private Tile destinationTile;
    private Piece playerMovedPiece;
    private static BoardDirection boardDirection;
    private static boolean doHighlight;
    public boolean isBlackBot;
    public boolean isWhiteBot;
    private boolean flipBoardOnTurn;
    private int simStep;

    public Table() {
        this.gameFrame = new JFrame("Adrian Chess");
        this.gameFrame.setLayout(new BorderLayout());
        final JMenuBar menuBar = createMenuBar();
        this.gameFrame.setJMenuBar(menuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENTION);
        this.chessBoard = Board.createStandardBoard();
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        boardDirection = BoardDirection.REGULAR;
        //this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.gameFrame.setVisible(true);
        doHighlight = true;
        this.isBlackBot = false;
        this.isWhiteBot = false;
        this.flipBoardOnTurn = false;
        this.simStep = 0;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(creatGameMenu());
        menuBar.add(createOptionsMenu());
        menuBar.add(createSimulationMenu());
        return menuBar;
    }

    public void whiteBotPlay(int totalStep) {
        if (chessBoard.getCurrentPlayer().getColor().equals(ChessColor.WHITE)) {
            Object[] moves = chessBoard.getCurrentPlayer().getLegalMoves().toArray();
            MoveUpdate whiteBotUpdate;
            Move randMove;

            // redo move if it is not valid
            do {
                randMove = (Move) moves[(int) (Math.random() * moves.length)];
                whiteBotUpdate = chessBoard.getCurrentPlayer().playMove(randMove);
                //System.out.println(blackBotUpdate.getMoveStatus());
            } while (whiteBotUpdate.getMoveStatus() != MoveStatus.DONE);
            // update board with player move
            final Move move = randMove;
            chessBoard = chessBoard.getCurrentPlayer().playMove(move).getBoard();
            //System.out.println(chessBoard);
            invokeLater(new Runnable() {
                @Override
                public void run() {
                    moveLog.addMove(move);
                    Piece.setPromotionPiece(new Queen(move.getDestinationCoord(), chessBoard.getCurrentPlayer().getColor()));
                    boardPanel.drawBoard(chessBoard, false, true);
                }
            });
        }
        if (isBlackBot && !chessBoard.getCurrentPlayer().isInCheckMate() && !chessBoard.getCurrentPlayer().isInStaleMate()) {
            invokeLater(new Runnable() {
                @Override
                public void run() {
                    blackBotPlay(totalStep);
                }
            });
        // start game
        } else if (isBlackBot && simStep < totalStep && (chessBoard.getCurrentPlayer().isInCheckMate() || chessBoard.getCurrentPlayer().isInStaleMate()) ) {
            
            invokeLater(new Runnable() {
                @Override
                public void run() {
                    simStep++;
                    System.out.println(simStep);
                    chessBoard = Board.createStandardBoard();
                    moveLog.getMoves().clear();
                    gameHistoryPanel.clearTable();
                    takenPiecesPanel.clear();
                    Table.boardDirection = boardDirection.getRegular(boardPanel.boardTiles);
                    whiteBotPlay(totalStep);
                }
            });
        }
    }

    public void botPlay(int depth) {
        BotMove generator = new BotMove();
        Move generatedMove = generator.execute(chessBoard, depth);
        chessBoard = chessBoard.getCurrentPlayer().playMove(generatedMove).getBoard();
        
        invokeLater(new Runnable() {
                @Override
                public void run() {
                    moveLog.addMove(generatedMove);
                    Piece.setPromotionPiece(new Queen(generatedMove.getDestinationCoord(), chessBoard.getCurrentPlayer().getColor()));
                    boardPanel.drawBoard(chessBoard, false, true);
                }
        });
    }

    public void botPlay2(int depth) {
        BotMove2 generator = new BotMove2();
        Move generatedMove = generator.execute(chessBoard, depth);
        chessBoard = chessBoard.getCurrentPlayer().playMove(generatedMove).getBoard();
        
        invokeLater(new Runnable() {
                @Override
                public void run() {
                    moveLog.addMove(generatedMove);
                    Piece.setPromotionPiece(new Queen(generatedMove.getDestinationCoord(), chessBoard.getCurrentPlayer().getColor()));
                    boardPanel.drawBoard(chessBoard, false, true);

                    // Call the opponent bot
                    if (isWhiteBot) {
                        invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                if (!chessBoard.getCurrentPlayer().isInCheckMate() && !chessBoard.getCurrentPlayer().getOpponent().isInCheckMate()) {
                                        botPlay3(depth);
                                }
                            }
                        });
                    }
                }
        });
        
    }

    public void botPlay3(int depth) {
        BotMove3 generator = new BotMove3();
        Move generatedMove = generator.execute(chessBoard, depth);
        chessBoard = chessBoard.getCurrentPlayer().playMove(generatedMove).getBoard();
        
        invokeLater(new Runnable() {
                @Override
                public void run() {
                    moveLog.addMove(generatedMove);
                    Piece.setPromotionPiece(new Queen(generatedMove.getDestinationCoord(), chessBoard.getCurrentPlayer().getColor()));
                    boardPanel.drawBoard(chessBoard, false, true);

                    if (isBlackBot) {
                        invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                if (!chessBoard.getCurrentPlayer().isInCheckMate() && !chessBoard.getCurrentPlayer().getOpponent().isInCheckMate()) {
                                    blackBotPlay(1);
                                    //botPlay4(depth);
                                }
                            }
                        });
                    }
                }
        });
        
    }

    public void botPlay4(int depth) {
        BotMove4 generator = new BotMove4();
        Move generatedMove = generator.execute(chessBoard, depth);
        generator.setLastMove(generatedMove);
        chessBoard = chessBoard.getCurrentPlayer().playMove(generatedMove).getBoard();
        
        invokeLater(new Runnable() {
                @Override
                public void run() {
                    moveLog.addMove(generatedMove);
                    Piece.setPromotionPiece(new Queen(generatedMove.getDestinationCoord(), chessBoard.getCurrentPlayer().getColor()));
                    boardPanel.drawBoard(chessBoard, false, true);

                    if (isBlackBot) {
                        invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                if (!chessBoard.getCurrentPlayer().isInCheckMate() && !chessBoard.getCurrentPlayer().getOpponent().isInCheckMate()) {
                                    blackBotPlay(1);
                                    //botPlay3(depth);
                                }
                            }
                        });
                    }
                }
        });
        
    }


    public void blackBotPlay(int totalStep) {
        if (chessBoard.getCurrentPlayer().getColor().equals(ChessColor.BLACK)) {
            Object[] moves = chessBoard.getCurrentPlayer().getLegalMoves().toArray();
            MoveUpdate blackBotUpdate;
            Move randMove;

            // redo move if it is not valid
            do {
                randMove = (Move) moves[(int) (Math.random() * moves.length)];
                blackBotUpdate = chessBoard.getCurrentPlayer().playMove(randMove);
                //System.out.println(blackBotUpdate.getMoveStatus());
            } while (blackBotUpdate.getMoveStatus() != MoveStatus.DONE);
            // update board with player move
            final Move move = randMove;
            chessBoard = chessBoard.getCurrentPlayer().playMove(move).getBoard();
            //gameHistoryPanel.appendMoveHistory(chessBoard, moveLog);
            //System.out.println(chessBoard);
            invokeLater(new Runnable() {
                @Override
                public void run() {
                    moveLog.addMove(move);
                    Piece.setPromotionPiece(new Queen(move.getDestinationCoord(), chessBoard.getCurrentPlayer().getColor()));
                    boardPanel.drawBoard(chessBoard, false, true);

                    // Call the opponent bot (and handle number of simulations)
                    if (isWhiteBot && !chessBoard.getCurrentPlayer().isInCheckMate() && !chessBoard.getCurrentPlayer().isInStaleMate()) {
                        invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                //whiteBotPlay(totalStep);
                                botPlay3(DEPTH);
                            }
                        });
                    } else if (simStep < totalStep) {
                        invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                simStep++;
                                chessBoard = Board.createStandardBoard();
                                moveLog.getMoves().clear();
                                gameHistoryPanel.clearTable();
                                takenPiecesPanel.clear();
                                Table.boardDirection = boardDirection.getRegular(boardPanel.boardTiles);
                                whiteBotPlay(totalStep);
                            }
                        });
                    }
                }
            });
        }
    }

    public static BoardDirection getBoardDirection() {
        return boardDirection;
    }

    public Board getBoard() {
        return this.chessBoard;
    }

    public BoardPanel getBoardPanel() {
        return this.boardPanel;
    }
    
    private JMenu creatGameMenu() {
        final JMenu gameMenu = new JMenu("Game");
        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Open PGN"); 
                }
        });
        gameMenu.add(openPGN);
                
        gameMenu.add(createPlayMenu());
        JMenuItem restartMenuItem = new JMenuItem("Reset Position");
        restartMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chessBoard = Board.createStandardBoard();
                moveLog.clear();
                gameHistoryPanel.clearTable();
                //gameHistoryPanel.updateMoveHistory(chessBoard, moveLog);
                takenPiecesPanel.clear();
                boardPanel.drawBoard(chessBoard, doHighlight, true);
            }
        });
        gameMenu.add(restartMenuItem);

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        gameMenu.add(exitMenuItem);

        return gameMenu;
    }

    private JMenu createPlayMenu() {
        // play menu
        JMenu playMenu = new JMenu("Play");
        JMenuItem playerVsPlayer = new JMenuItem("Player v. Player");
        playerVsPlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isBlackBot = false;
                isWhiteBot = false;
                moveLog.getMoves().clear();
                gameHistoryPanel.clearTable();
                takenPiecesPanel.clear();
                chessBoard = Board.createStandardBoard();
                Table.boardDirection = boardDirection.getRegular(boardPanel.boardTiles);
                boardPanel.drawBoard(chessBoard, doHighlight, false);
            }
        });
        playMenu.add(playerVsPlayer);

        JMenuItem playerVsBot = new JMenuItem("Player v. Black Bot");
        playerVsBot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isBlackBot = true;
                isWhiteBot = false;
                gameHistoryPanel.clearTable();
                takenPiecesPanel.clear();
                moveLog.getMoves().clear();
                chessBoard = Board.createStandardBoard();
                Table.boardDirection = boardDirection.getRegular(boardPanel.boardTiles);
                boardPanel.drawBoard(chessBoard, doHighlight, false);
            }
        });
        playMenu.add(playerVsBot);

        JMenuItem botVsPlayer = new JMenuItem("Player v. White Bot");
        botVsPlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isBlackBot = false;
                isWhiteBot = true;
                gameHistoryPanel.clearTable();
                takenPiecesPanel.clear();
                moveLog.getMoves().clear();
                chessBoard = Board.createStandardBoard();
                Table.boardDirection = boardDirection.getFlipped(boardPanel.boardTiles);
                simStep = 1;
                whiteBotPlay(1);
                //botPlay2(DEPTH);
                boardPanel.drawBoard(chessBoard, doHighlight, true);
            }
        });
        playMenu.add(botVsPlayer);

        JMenuItem botVsBot = new JMenuItem("Bot v. Bot");
        botVsBot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isBlackBot = true;
                isWhiteBot = true;
                moveLog.getMoves().clear();
                takenPiecesPanel.clear();
                gameHistoryPanel.clearTable();
                chessBoard = Board.createStandardBoard();
                Table.boardDirection = boardDirection.getRegular(boardPanel.boardTiles);
                doHighlight = false;
                simStep = 1;
                //whiteBotPlay(1);
                botPlay3(DEPTH);
                //botPlay2(DEPTH);
                boardPanel.drawBoard(chessBoard, doHighlight, true);
            }
        });
        playMenu.add(botVsBot);

        return playMenu;
    }

    private JMenu createOptionsMenu() {
        JMenu optionsMenu = new JMenu("Options");
        JMenuItem flipBoard = new JMenuItem("Flip Board");
        flipBoard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardDirection = boardDirection.oppositeDirection(boardPanel.boardTiles);
                boardPanel.drawBoard(chessBoard, doHighlight, false);
            }
        });
        optionsMenu.add(flipBoard);

        JMenuItem showLegalMoveItem = new JMenuItem(doHighlight ? "Hide Legal Moves": "Show Legal Moves");
        showLegalMoveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!doHighlight) {
                    doHighlight = true;
                    showLegalMoveItem.setText("Hide Legal Moves");
                } else {
                    doHighlight = false;
                    showLegalMoveItem.setText("Show Legal Moves");
                }
            }
        });
        optionsMenu.add(showLegalMoveItem);

        JCheckBoxMenuItem flipBoardOnTurnItem = new JCheckBoxMenuItem("Flip Board on Turn");
        flipBoardOnTurnItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flipBoardOnTurn = !flipBoardOnTurn;
            }
        });
        optionsMenu.add(flipBoardOnTurnItem);
        return optionsMenu;
    }

    private JMenu createSimulationMenu() {
        JMenu simulationMenu = new JMenu("Simulation");
        JMenuItem play2 = new JMenuItem("Play x2");
        play2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isBlackBot = true;
                isWhiteBot = true;
                moveLog.getMoves().clear();
                gameHistoryPanel.clearTable();
                chessBoard = Board.createStandardBoard();
                Table.boardDirection = boardDirection.getRegular(boardPanel.boardTiles);
                simStep = 0;
                whiteBotPlay(2);              
            }
        });
        simulationMenu.add(play2);

        JMenuItem play10 = new JMenuItem("Play x10");
        play10.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isBlackBot = true;
                isWhiteBot = true;
                moveLog.getMoves().clear();
                gameHistoryPanel.clearTable();
                chessBoard = Board.createStandardBoard();
                Table.boardDirection = boardDirection.getRegular(boardPanel.boardTiles);
                simStep = 0;
                whiteBotPlay(10);
            }
        });
        simulationMenu.add(play10);

        JMenuItem play50 = new JMenuItem("Play x50");
        play50.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isBlackBot = true;
                isWhiteBot = true;
                moveLog.getMoves().clear();
                gameHistoryPanel.clearTable();
                chessBoard = Board.createStandardBoard();
                Table.boardDirection = boardDirection.getRegular(boardPanel.boardTiles);
                simStep = 0;
                whiteBotPlay(50);
            }
        });
        simulationMenu.add(play50);

        return simulationMenu;
    }

    public class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles;
        
        BoardPanel() {
            super (new GridLayout(8, 8));
            this.boardTiles = new ArrayList<>();
            // layout tiles
            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final TilePanel tilePanel = new TilePanel(this, new int[]{i/8, i%8});
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        public void drawBoard(Board board, boolean highlight, boolean updateMoveTable) {
            removeAll();
            for (TilePanel tilePanel : boardTiles) {
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            if (highlight) {
                highlightLegalMoves(board);
            }
            if (updateMoveTable) {
                //System.out.println(Arrays.toString(moveLog.getMoves().toArray()));
                gameHistoryPanel.updateMoveHistory(board, moveLog);
            }
            //TODO takenPiecesPanel
            //takenPiecesPanel.drawTakenPieces(moveLog);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            validate();
            repaint();
        }

        public TilePanel getTilePanel(int[] coord) {
            for (TilePanel tp : boardTiles) {
                if (tp.tileId[0] == coord[0] && tp.tileId[1] == coord[1]) {
                    return tp;
                }
            }
            return null;
        }
    }

    public void highlightLegalMoves(Board board) {
        // adds dot to tiles that piece can move to
        //
        // if piece can capture, it will set the
        // background to a different color
        if (playerMovedPiece != null) {
            for (Move move : pieceLegalMoves(board)) {
                TilePanel tilePanel = boardPanel.getTilePanel(move.getDestinationCoord());
                if (board.getCurrentPlayer().playMove(move).getMoveStatus().isDone()) {
                    if (!board.getTile(move.getDestinationCoord()).hasPiece() && !(move instanceof EnPassantCapture)) {
                        try {
                            BufferedImage image = ImageIO.read(new File(imageFolderPath + "other/dot.png"));
                            JLabel legalMoveLabel = new JLabel(new ImageIcon(image));
                            legalMoveLabel.setHorizontalAlignment(JLabel.RIGHT);
                            legalMoveLabel.setVerticalAlignment(JLabel.BOTTOM);
                            tilePanel.add(legalMoveLabel);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        tilePanel.setBackground(TilePanel.captureColor);
                    }
                }
            }
        }
    }

    private Collection<Move> pieceLegalMoves(Board board) {
        if (playerMovedPiece != null && playerMovedPiece.getPieceColor() == board.getCurrentPlayer().getColor()) {
            // Handle castle moves independently for kings
            if (playerMovedPiece instanceof King) {
                Player player = board.getCurrentPlayer();
                Player opponent = player.getOpponent();
                return Stream.concat((playerMovedPiece.calculateLegalMoves(board).stream()), 
                                     player.calculateKingCastles(player.getLegalMoves(), 
                                                                 opponent.getLegalMoves()).stream()).toList();
            }
            return playerMovedPiece.calculateLegalMoves(board);
        }
        return Collections.emptyList();
    } 

    public enum BoardDirection {
        REGULAR {

            @Override
            BoardDirection oppositeDirection(List<TilePanel> boardTiles) {
                Collections.reverse(boardTiles);
                return FLIPPED;
            }

            @Override
            public boolean isRegular() {
                return true;
            }

            @Override
            public BoardDirection getFlipped(List<TilePanel> boardTiles) {
                if (isRegular()) {
                    oppositeDirection(boardTiles);
                }
                return FLIPPED;
            }

            @Override
            public BoardDirection getRegular(List<TilePanel> boardTiles) {
                if (!isRegular()) {
                    oppositeDirection(boardTiles);
                }
                return REGULAR;
            }

        },

        FLIPPED {

            @Override
            BoardDirection oppositeDirection(List<TilePanel> boardTiles) {
                Collections.reverse(boardTiles);
                return REGULAR;
            }

            @Override
            public boolean isRegular() {
                return false;
            }

            @Override
            public BoardDirection getFlipped(List<TilePanel> boardTiles) {
                if (isRegular()) {
                    oppositeDirection(boardTiles);
                }
                return FLIPPED;
            }

            @Override
            public BoardDirection getRegular(List<TilePanel> boardTiles) {
                if (!isRegular()) {
                    oppositeDirection(boardTiles);
                }
                return REGULAR;
            }

        };

        abstract BoardDirection oppositeDirection(List<TilePanel> boardTiles);
        public abstract BoardDirection getFlipped(List<TilePanel> boardTiles);

        public abstract BoardDirection getRegular(List<TilePanel> boardTiles);
        
        public abstract boolean isRegular();
    }

    public static class MoveLog {
        private List<Move> moves;

        MoveLog() {
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves() {
            return this.moves;
        }

        public void addMove(final Move move) {
            this.moves.add(move);
        }

        public int size() {
            return this.moves.size();
        }

        public void clear() {
            this.moves.clear();
        }

        public Move removeMove(int index) {
            return this.moves.remove(index);
        }

        public void removeMove(Move move) {
            this.moves.remove(move);
        }
    }

    private class TilePanel extends JPanel {
        private final int[] tileId;
        private final Color lightTileColor = new Color(210, 180, 140, 150);
        private final Color darkTileColor = new Color(92, 64, 51);
        private final Color selectTileColor = new Color(114, 47, 55);
        public static final Color captureColor = new Color(200, 0, 200);

        TilePanel(BoardPanel boardPanel, final int[] tilePos) {
            super(new GridBagLayout());
            this.tileId = tilePos;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            setPieceImage(chessBoard);

            addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isRightMouseButton(e)) {
                        selectTile = null;
                        destinationTile = null;
                        playerMovedPiece = null;
                        invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                boardPanel.drawBoard(chessBoard, doHighlight, false);
                            }
                        });
                    } else if (isLeftMouseButton(e)) {
                        // if selecting new tile
                        if (selectTile == null) {
                            selectTile = chessBoard.getTile(tileId);
                            if (selectTile.hasPiece() && selectTile.getPiece().getPieceColor().equals(chessBoard.getCurrentPlayer().getColor())) {
                                playerMovedPiece = selectTile.getPiece();
                                invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        boardPanel.drawBoard(chessBoard, doHighlight, false);
                                    }
                                });
                            } else {
                                selectTile = null;
                                destinationTile = null;
                                playerMovedPiece = null;
                            }
                        } else {
                            // if a tile is already selected
                            destinationTile = chessBoard.getTile(tileId);
                            Move move = MoveFactory.createMove(chessBoard, selectTile.getTileCoord(), destinationTile.getTileCoord());
                            //System.out.println(move);
                            MoveUpdate update = chessBoard.getCurrentPlayer().playMove(move);
                            if (update.getMoveStatus().isDone()) {
                                //TODO implement popup for choosing a piece
                                /*if (move.getMovedPiece() instanceof Pawn) {
                                    if (move.getDestinationCoord()[0] == 0) {
                                        gameFrame.add(createPieceChoicePopup(chessBoard.getCurrentPlayer().getOpponent().getColor()));
                                        validate();
                                        repaint();
                                    }
                                }*/
                                chessBoard = update.getBoard();
                                moveLog.addMove(move);
                                // run the drawBoard if move is successful
                                invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Piece.setPromotionPiece(new Queen(move.getDestinationCoord(), chessBoard.getCurrentPlayer().getColor()));
                                        boardPanel.drawBoard(chessBoard, doHighlight, true);
                                        selectTile = null;
                                        destinationTile = null;
                                        playerMovedPiece = null;
                                    }
                                });
                                
                                // Run the bot response
                                invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isBlackBot && !chessBoard.getCurrentPlayer().isInCheckMate()) {
                                            //blackBotPlay(1);
                                            botPlay4(DEPTH);
                                        // white bot
                                        } if (isWhiteBot && !chessBoard.getCurrentPlayer().isInCheckMate()) {
                                            //whiteBotPlay(simStep);
                                            botPlay3(DEPTH);
                                        }
                                    }
                                });
                                
                                // code to flip the board depending on player
                                if (flipBoardOnTurn) {
                                    if (chessBoard.getCurrentPlayer().getColor() == ChessColor.BLACK) {
                                        boardDirection = getBoardDirection().getFlipped(boardPanel.boardTiles);
                                    } else {
                                        boardDirection = getBoardDirection().getRegular(boardPanel.boardTiles);
                                    }
                                }
                            } else {
                                selectTile = null;
                                destinationTile = null;
                                playerMovedPiece = null;
                                selectTile = chessBoard.getTile(tileId);
                                if (selectTile.hasPiece() && selectTile.getPiece().getPieceColor().equals(chessBoard.getCurrentPlayer().getColor())) {
                                    playerMovedPiece = selectTile.getPiece();
                                    // run drawBoard if there is a selected tile
                                    invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (destinationTile != null || selectTile != null) {
                                                boardPanel.drawBoard(chessBoard, doHighlight, false);
                                            }
                                            destinationTile = null;
                                        }
                                    });
                                } else {
                                    selectTile = null;
                                }
                            }
                            
                            
                        }
                    }
                }


                private JPopupMenu createPieceChoicePopup(ChessColor color) {
                    JPopupMenu pieceChoicePopup = new JPopupMenu();
                    try {
                        for (PieceType pieceType : new PieceType[]{PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT}) {
                            //System.out.println(imageFolderPath + piece.getPieceColor().toString() + piece.toString() + ".png");
                            BufferedImage image = ImageIO.read( new File(imageFolderPath + 
                                                                color.fileString() + 
                                                                pieceType.fileString() + 
                                                                ".png") );
                            JLabel imgLable = new JLabel(new ImageIcon(image));
                            imgLable.setHorizontalAlignment(JLabel.CENTER);
                            imgLable.setVerticalAlignment(JLabel.CENTER);
                            pieceChoicePopup.add(imgLable);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                       
                    return pieceChoicePopup;
                }

                
                @Override
                public void mousePressed(MouseEvent e) {
                    /*if (selectTile == null) {
                        selectTile = chessBoard.getTile(tileId);
                        if (selectTile.hasPiece() && selectTile.getPiece().getPieceColor().equals(chessBoard.getCurrentPlayer().getColor())) {
                            playerMovedPiece = selectTile.getPiece();
                        } else {
                            selectTile = null;
                        }
                    }*/
                   // if selecting new tile
                        if (selectTile == null) {
                            selectTile = chessBoard.getTile(tileId);
                            if (selectTile.hasPiece() && selectTile.getPiece().getPieceColor().equals(chessBoard.getCurrentPlayer().getColor())) {
                                playerMovedPiece = selectTile.getPiece();
                                invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        boardPanel.drawBoard(chessBoard, doHighlight, false);
                                    }
                                });
                            } else {
                                selectTile = null;
                                destinationTile = null;
                                playerMovedPiece = null;
                            }
                        } else {
                            // if a tile is already selected
                            destinationTile = chessBoard.getTile(tileId);
                            Move move = MoveFactory.createMove(chessBoard, selectTile.getTileCoord(), destinationTile.getTileCoord());
                            //System.out.println(move);
                            MoveUpdate update = chessBoard.getCurrentPlayer().playMove(move);
                            if (update.getMoveStatus().isDone()) {
                                //TODO implement popup for choosing a piece
                                /*if (move.getMovedPiece() instanceof Pawn) {
                                    if (move.getDestinationCoord()[0] == 0) {
                                        gameFrame.add(createPieceChoicePopup(chessBoard.getCurrentPlayer().getOpponent().getColor()));
                                        validate();
                                        repaint();
                                    }
                                }*/
                                chessBoard = update.getBoard();
                                moveLog.addMove(move);
                                // run the drawBoard if move is successful
                                invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Piece.setPromotionPiece(new Queen(move.getDestinationCoord(), chessBoard.getCurrentPlayer().getColor()));
                                        boardPanel.drawBoard(chessBoard, doHighlight, true);
                                        selectTile = null;
                                        destinationTile = null;
                                        playerMovedPiece = null;
                                        
                                        invokeLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Run the bots
                                                if (isBlackBot && !chessBoard.getCurrentPlayer().isInCheckMate()) {
                                                        //blackBotPlay(1);
                                                        botPlay4(DEPTH);
                                                
                                                } if (isWhiteBot && !chessBoard.getCurrentPlayer().isInCheckMate()) {
                                                        //whiteBotPlay(DEPTH);
                                                        botPlay3(DEPTH);
                                                }
                                            }
                                        });
                                    }
                                });
                                
                                
                                // code to flip the board depending on player
                                if (flipBoardOnTurn) {
                                    if (chessBoard.getCurrentPlayer().getColor() == ChessColor.BLACK) {
                                        boardDirection = getBoardDirection().getFlipped(boardPanel.boardTiles);
                                    } else {
                                        boardDirection = getBoardDirection().getRegular(boardPanel.boardTiles);
                                    }
                                }
                            } else {
                                selectTile = null;
                                destinationTile = null;
                                playerMovedPiece = null;
                                selectTile = chessBoard.getTile(tileId);
                                if (selectTile.hasPiece() && selectTile.getPiece().getPieceColor().equals(chessBoard.getCurrentPlayer().getColor())) {
                                    playerMovedPiece = selectTile.getPiece();
                                    // run drawBoard if there is a selected tile
                                    invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (destinationTile != null || selectTile != null) {
                                                boardPanel.drawBoard(chessBoard, doHighlight, false);
                                            }
                                            destinationTile = null;
                                        }
                                    });
                                } else {
                                    selectTile = null;
                                }
                            }
                            
                            
                        }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    /*if (selectTile != null) {
                        destinationTile = chessBoard.getTile(tileId);
                        Move move = MoveFactory.createMove(chessBoard, selectTile.getTileCoord(), destinationTile.getTileCoord());
                        MoveUpdate update = chessBoard.getCurrentPlayer().playMove(move);
                        if (update.getMoveStatus().isDone()) {
                            chessBoard = update.getBoard();
                            //TODO add move to move log
                        }
                        selectTile = null;
                        destinationTile = null;
                        playerMovedPiece = null;
                        
                        invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                boardPanel.drawBoard(chessBoard);
                            }
                        });
                    }*/
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    
                }
                
            });

            validate();
        }

        public void drawTile(Board board) {
            setPieceImage(board);
            assignTileColor();
            validate();
            repaint();
        }

        private void setPieceImage(Board board) {
            this.removeAll();
            if (board.getTile(this.tileId).hasPiece()) {
                final Piece piece = board.getTile(tileId).getPiece();
                try {
                    //System.out.println(imageFolderPath + piece.getPieceColor().fileString() + piece.getPieceType().fileString() + ".png");
                    BufferedImage image = ImageIO.read( new File(imageFolderPath + 
                                                        piece.getPieceColor().fileString() + 
                                                        piece.getPieceType().fileString() + 
                                                        ".png") );
                    JLabel imgLable = new JLabel(new ImageIcon(image));
                    imgLable.setHorizontalAlignment(JLabel.CENTER);
                    imgLable.setVerticalAlignment(JLabel.CENTER);
                    add(imgLable);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void assignTileColor() {
            // if even row, starting from row 0
            if (this.tileId[0] % 2 == 0) {
                setBackground(this.tileId[1] % 2 == 0 ? lightTileColor: darkTileColor);
            }  else {
                setBackground(this.tileId[1] % 2 == 1 ? lightTileColor: darkTileColor);
            }

            if (selectTile != null && 
                this.tileId[0] == selectTile.getTileCoord()[0] && 
                this.tileId[1] == selectTile.getTileCoord()[1] &&
                playerMovedPiece.getPieceColor().equals(chessBoard.getCurrentPlayer().getColor())) {
                setBackground(selectTileColor);
            }
            
        }

        

    }
}
