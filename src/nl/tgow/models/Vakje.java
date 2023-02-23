package nl.tgow.models;

public class Vakje {
    private int player;
    private Coordinaat coordinate;

    public Vakje(int player, Coordinaat coordinate){
        this.player = player;
        this.coordinate = coordinate;
    }

    public int getPlayer() {
        return player;
    }

    public Coordinaat getCoordinate() {
        return coordinate;
    }
}
