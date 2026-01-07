package adri.chess.engine.player;

import adri.chess.engine.board.Board;
import adri.chess.engine.board.Move;

public class MoveUpdate {

    private final Board boardUpdate;
    private final Move move;
    private final MoveStatus moveStatus;

    public MoveUpdate(Board board, Move move, MoveStatus moveStatus) {
        this.boardUpdate = board;
        this.move = move;
        this.moveStatus = moveStatus;
    }

    public MoveStatus getMoveStatus() {
        return this.moveStatus;
    }

    public Board getBoard() {
        return this.boardUpdate;
    }

    public Move getMove() {
        return this.move;
    }
}
