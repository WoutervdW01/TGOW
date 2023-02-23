package nl.tgow.models;

public class SpelStand {
    private int[][] bord;
    private int huidigeSpeler;

    public SpelStand(int[][] bord, int huidigeSpeler){
        this.bord = bord;
        this.huidigeSpeler = huidigeSpeler;
    }

    public int[][] getBord() {
        return bord;
    }

    public int getHuidigeSpeler() {
        return huidigeSpeler;
    }
}
