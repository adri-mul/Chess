package adri.chess.engine.player.ai;

import adri.chess.engine.board.Board;
import adri.chess.engine.board.Move;

public interface BoardEvaluator {
    
    int evaluate(Board board);
    void logBoardHistory(Board board, Move move);
}
