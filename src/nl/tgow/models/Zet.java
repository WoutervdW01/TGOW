package nl.tgow.models;

public class Zet {
    private Coordinate van;
    private Coordinate naar;

    public Zet(Coordinate van, Coordinate naar){
        this.van = van;
        this.naar = naar;
    }

    public Coordinate getVan() {
        return van;
    }

    public Coordinate getNaar() {
        return naar;
    }
}
