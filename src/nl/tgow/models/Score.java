package nl.tgow.models;

public class Score {
    private int player;
    private int score;

    public Score(int player, int score){
        this.player = player;
        this.score = score;
    }

    public int getPlayer() {
        return player;
    }

    public int getScore() {
        return score;
    }
}
