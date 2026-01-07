package adri.chess.engine.player.ai;

import adri.chess.engine.board.Board;
import adri.chess.engine.board.Move;

public interface Bot {
    public Move execute(Board board, int depth);
    public int search(Board board, int depth);
}