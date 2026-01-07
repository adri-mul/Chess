package adri.chess.engine.player.ai;

import adri.chess.engine.board.Board;

public interface BoardEvaluator {
    
    int evaluate(Board board);
    int evaluate(Board board, BotMove4 bot);
}
