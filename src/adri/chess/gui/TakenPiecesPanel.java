package adri.chess.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import adri.chess.engine.board.Move;
import adri.chess.engine.pieces.Piece;
import adri.chess.gui.Table.MoveLog;

public class TakenPiecesPanel extends JPanel {
    private final JPanel blackPanel;
    private final JPanel whitePanel;
    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);
    private static final Color PANEL_COLOR = Color.decode("0xFDF5E6");
    private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(60, 80);

    public TakenPiecesPanel() {
        super(new BorderLayout());
        this.setBackground(Color.decode("0xFDF5E6"));
        this.setBorder(PANEL_BORDER);
        this.blackPanel = new JPanel(new GridLayout(8, 2));
        this.whitePanel = new JPanel(new GridLayout(8, 2));
        this.blackPanel.setBackground(PANEL_COLOR);
        this.whitePanel.setBackground(PANEL_COLOR);
        this.add(this.blackPanel, BorderLayout.NORTH);
        this.add(this.whitePanel, BorderLayout.SOUTH);
        this.setPreferredSize(TAKEN_PIECES_DIMENSION);
    }

    public void clear() {
        this.blackPanel.removeAll();
        this.whitePanel.removeAll();
    }

    public void drawTakenPieces(final MoveLog moveLog) {
        this.blackPanel.removeAll();
        this.whitePanel.removeAll();

        final List<Piece> whiteTakenPieces = new ArrayList<>();
        final List<Piece> blackTakenPieces = new ArrayList<>();

        for (final Move move : moveLog.getMoves()) {
            if (move.isCapture()) {
                final Piece takenPiece = move.getCapturedPiece();
                if (takenPiece.getPieceColor().isBlack()) {
                    whiteTakenPieces.add(takenPiece);
                } else if (takenPiece.getPieceColor().isWhite()) {
                    blackTakenPieces.add(takenPiece);
                } else {
                    throw new RuntimeException("Error in piece color");
                }
            }
        }

        Collections.sort(whiteTakenPieces, new Comparator<Piece>() {
            @Override
            public int compare(Piece p1, Piece p2) {
                return Integer.compare(p1.getPieceValue(), p2.getPieceValue());
            }
        });
        Collections.sort(blackTakenPieces, new Comparator<Piece>() {
            @Override
            public int compare(Piece p1, Piece p2) {
                return Integer.compare(p1.getPieceValue(), p2.getPieceValue());
            }
        });

        drawPieces(whiteTakenPieces);
        drawPieces(blackTakenPieces);

        validate();
    }

    private void drawPieces(List<Piece> takenPieces) {
        for (final Piece takenPiece : takenPieces) {
            try {
                BufferedImage image = ImageIO.read( new File("src/adri/chess/images/" + 
                                                        takenPiece.getPieceColor().fileString() + 
                                                        takenPiece.getPieceType().fileString() + 
                                                        ".png") );
                ImageIcon imgIcon = new ImageIcon(image);
                JLabel imgLable = new JLabel(imgIcon);
                if (takenPiece.getPieceColor().isBlack()) {
                    this.whitePanel.add(imgLable);
                } else {
                    this.blackPanel.add(imgLable);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}