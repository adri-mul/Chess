package adri.chess.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import adri.chess.engine.board.Board;
import adri.chess.engine.board.Move;
import adri.chess.gui.Table.MoveLog;

import java.util.ArrayList;
import java.util.List;


public class GameHistoryPanel extends JPanel {
    private final DataModel model;
    private final JScrollPane scrollPane;
    private final JTable table;

    private static final Dimension HISTORY_PANEL_SIZE = new Dimension(100, 400);

    public GameHistoryPanel() {
        this.setLayout(new BorderLayout());
        this.model = new DataModel();
        this.table = new JTable(model);
        table.setRowHeight(15);
        this.scrollPane = new JScrollPane(table);
        this.scrollPane.setColumnHeaderView(table.getTableHeader());
        this.scrollPane.setPreferredSize(HISTORY_PANEL_SIZE);
        this.add(scrollPane, BorderLayout.CENTER);
        this.setVisible(true);
    }

    public DataModel getModel() {
        return this.model;
    }

    // create all the moves
    public void updateMoveHistory(Board board, MoveLog moveHistory) {
        /*int currentRow = 0;
        this.model.clear();
        for (Move move : moveHistory.getMoves()) {
            if (move.getMovedPiece().getPieceColor().isWhite()) {
                currentRow++;
                this.model.setValueAt(move.toString(), currentRow, 0);
            } else if (move.getMovedPiece().getPieceColor().isBlack()) {
                this.model.setValueAt(move.toString(), currentRow, 1);
            }
        }*/

        if (moveHistory.getMoves().size() > 0) {
            Move lastMove = moveHistory.getMoves().get(moveHistory.size()-1);
            if (lastMove.getMovedPiece().getPieceColor().isWhite()) {
                //currentRow++;
                //System.out.println(lastMove.toString() + calculateCheckAndCheckMate(board));
                this.model.setValueAt(lastMove.toString() + calculateCheckAndCheckMate(board), model.values.size(), 0);
            } else if (lastMove.getMovedPiece().getPieceColor().isBlack()) {
                //System.out.println(lastMove.toString() + calculateCheckAndCheckMate(board));
                this.model.setValueAt(lastMove.toString() + calculateCheckAndCheckMate(board), model.values.size()-1, 1);
            }
        }

        final JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    public void clearTable() {
        this.model.clear();
    }

    private String calculateCheckAndCheckMate(Board board) {
        if (board.getCurrentPlayer().isInCheckMate()) {
            return "#";
        } else if (board.getCurrentPlayer().isInCheck()) {
            return "+";
        }
        return "";
    }

    private class DataModel extends DefaultTableModel {
        private List<Row> values;
        private static final String[] HEADER_STRINGS = {"White", "Black"};

        public DataModel() {
            this.values = new ArrayList<>();
        }

        @SuppressWarnings("unused")
        public List<Row> getValues() {
            return this.values;
        }

        public void clear() {
            this.values.clear();
            this.setRowCount(0);
        }

        @Override
        public int getRowCount() {
            if (this.values == null) {
                return 0;
            } else {
                return this.values.size();
            }
        }

        @Override
        public int getColumnCount() {
            return HEADER_STRINGS.length;
        }

        @Override
        public Object getValueAt(final int row, final int column) {
            final Row currentRow = this.values.get(row);
            if (column == 0) {
                return currentRow.getWhiteMove();
            } else if (column == 1) {
                return currentRow.getBlackMove();
            }
            return null;
        }

        @Override
        public void setValueAt(final Object value, final int row, final int column) {
            Row currentRow = new Row();

            if (column == 0) {
                currentRow.setWhiteMove((String) value);
                
                fireTableRowsInserted(row, row);
                fireTableCellUpdated(row, column);
                values.add(currentRow);
            } else if (column == 1) {
                if (values.size() > row && row > -1) {
                    currentRow = values.get(row);
                }

                currentRow.setBlackMove((String) value);
                //this.values.add(currentRow);
                fireTableCellUpdated(row, column);
            }
            
        }

        @Override
        public Class<?> getColumnClass(final int column) {
            return Move.class;
        }

        @Override
        public String getColumnName(final int column) {
            return HEADER_STRINGS[column];
        }
    }

    private static class Row {
        private String whiteMove;
        private String blackMove;

        public Row() {

        }

        public String getWhiteMove() {
            return this.whiteMove;
        }

        public String getBlackMove() {
            return this.blackMove;
        }

        public void setWhiteMove(String move) {
            this.whiteMove = move;
        }

        public void setBlackMove(String move) {
            this.blackMove = move;
        }
    }
}
