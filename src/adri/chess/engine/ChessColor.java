package adri.chess.engine;

import adri.chess.engine.player.BlackPlayer;
import adri.chess.engine.player.Player;
import adri.chess.engine.player.WhitePlayer;

public enum ChessColor {
    WHITE {
        @Override
        public int getDirection() {
            return -1;
        }

        @Override
        public boolean isWhite() {
            return true;
        }

        @Override
        public boolean isBlack() {
            return false;
        }

        @Override
        public Player choosePlayer(WhitePlayer wp, BlackPlayer bp) {
            return wp;
        }

        @Override
        public String toString() {
            return "W";
        }

        @Override
        public String fileString() {
            return "White/";
        }

        @Override
        public int getOppositeDirection() {
            return 1;
        }

        @Override
        public boolean isPromotionSquare(int[] pos) {
            return pos[0] == 0;
        }
    },
    BLACK {
        @Override
        public int getDirection() {
            return 1;
        }

        @Override
        public boolean isWhite() {
            return false;
        }

        @Override
        public boolean isBlack() {
            return true;
        }

        @Override
        public Player choosePlayer(WhitePlayer wp, BlackPlayer bp) {
            return bp;
        }

        @Override
        public String toString() {
            return "B";
        }

        @Override
        public String fileString() {
            return "Black/";
        }

        @Override
        public int getOppositeDirection() {
            return -1;
        }

        @Override
        public boolean isPromotionSquare(int[] pos) {
            return pos[0] == 7;
        }
    };

    public abstract int getDirection();
    public abstract int getOppositeDirection();
    public abstract boolean isWhite();
    public abstract boolean isBlack();
    public abstract Player choosePlayer(WhitePlayer wp, BlackPlayer bp);
    public abstract String toString();
    public abstract String fileString();
    public abstract boolean isPromotionSquare(int[] pos);
}