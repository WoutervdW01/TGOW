package nl.tgow.models;

public class Piece {
    private int player;
    private Coordinate coordinate;

    public Piece(int player, Coordinate coordinate){
        this.player = player;
        this.coordinate = coordinate;
    }

    public int getPlayer() {
        return player;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }
}
