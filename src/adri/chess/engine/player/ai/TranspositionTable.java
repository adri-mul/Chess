package adri.chess.engine.player.ai;

import adri.chess.engine.board.Move;

public class TranspositionTable {
    private final TTEntry[] table;
    
    public TranspositionTable(int size) {
        table = new TTEntry[size];
        for (int i = 0; i < size; i++) {
            table[i] = new TTEntry();
        }
    }

    public TTEntry probe(long hash) {
        return table[(int)(hash & table.length - 1)];
    }

    public void store(long hash, int depth, int score, byte flag, Move move) {
        TTEntry e = table[(int)(hash & (table.length - 1))];
        if (e.depth <= depth) {
            e.setKey(hash);
            e.setDepth(depth);
            e.setScore(score);
            e.setFlag(flag);
            e.setBestMove(move);
        }
    }
    
    
    private class TTEntry {
        private long key;
        private int depth;
        private int score;
        private byte flag;
        private Move bestMove;
        
        public void setKey(long hash) {
            this.key = hash;
        }

        public void setBestMove(Move move) {
            this.bestMove = move;
        }

        public void setFlag(byte flag) {
            this.flag = flag;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public void setDepth(int depth) {
            this.depth = depth;
        }
    }
}
