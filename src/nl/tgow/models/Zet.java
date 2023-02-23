package nl.tgow.models;

public class Zet {
    private Coordinaat van;
    private Coordinaat naar;

    public Zet(Coordinaat van, Coordinaat naar){
        this.van = van;
        this.naar = naar;
    }

    public Coordinaat getVan() {
        return van;
    }

    public Coordinaat getNaar() {
        return naar;
    }
}
