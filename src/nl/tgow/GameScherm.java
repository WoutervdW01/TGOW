package nl.tgow;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import nl.tgow.datastructures.Stapel;
import nl.tgow.models.*;

import java.io.IOException;

public class GameScherm {

    @FXML
    AnchorPane pane;
    @FXML
    VBox Mid;
    @FXML
    Label PlayerLabel;
    @FXML
    Label ScorePlayer1;
    @FXML
    Label ScorePlayer2;
    @FXML
    VBox Left;

    private final int GROOTTE = 7;
    private final SpelType spelType;

    private int[][] bord = new int[GROOTTE][GROOTTE];
    private Vakje geselecteerdVakje = null;
    private int huidigeSpeler = 1;
    private boolean klaar = false;

    private Stapel score = new Stapel();

    private final Stapel<SpelStand> SpelHistorie = new Stapel<SpelStand>();
    private SpelStand huidigeSpelStand;

    public GameScherm(SpelType spelType) {
        this.spelType = spelType;
    }

    public void initialize(){
        HBox bordContainer = new HBox();
        bordContainer.setId("boardContainer");
        bordContainer.setPrefSize(700, 700);
        // fill whole pane
        AnchorPane.setTopAnchor(bordContainer, 0.0);
        AnchorPane.setBottomAnchor(bordContainer, 0.0);
        AnchorPane.setLeftAnchor(bordContainer, 0.0);
        AnchorPane.setRightAnchor(bordContainer, 0.0);

        // allign center
        bordContainer.setStyle("-fx-alignment: center;");


        GridPane root = new GridPane();
        root.setId("board");
        //final int size = 4;
        for (int i = 0; i < GROOTTE; i++) {
            for (int j = 0; j < GROOTTE; j++) {
                StackPane vierkant = new StackPane();
                vierkant.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #000000; -fx-border-width: 1px;");
                vierkant.setId(i + "," + j);
                vierkant.setOnMouseClicked(event -> {
                    String[] coordinates = vierkant.getId().split(",");
                    int x = Integer.parseInt(coordinates[0]);
                    int y = Integer.parseInt(coordinates[1]);
                    klikOpVakje(x, y);
                    tekenBord();
                });
                Label label = new Label("" + i + "," + j);
                vierkant.getChildren().add(label);
                vierkant.setPrefSize(100, 100);
                root.add(vierkant, i, j);
            }
        }
        bordContainer.getChildren().add(root);
        Mid.getChildren().add(bordContainer);

        Button gaTerug = new Button("Ga terug");
        gaTerug.setOnMouseClicked(event -> {
            gaTerug();
        });
        Mid.getChildren().add(gaTerug);

        Button hoofdMenuButton = new Button("Ga terug naar het hoofdmenu");
        hoofdMenuButton.setOnMouseClicked(event -> {
            try {
                terugNaarHoofdMenu();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Mid.getChildren().add(hoofdMenuButton);
        startPositie();
        tekenBord();
        SpelHistorie.duw(new SpelStand(copyBoard(bord), huidigeSpeler));
        printScores(getScores());
        laatSpelerZien();
    }

    private void gaTerug() {
        if(SpelHistorie.lengte() > 1){
            SpelStand vorig = SpelHistorie.pak();
            bord = vorig.getBord();
            huidigeSpeler = vorig.getHuidigeSpeler();
            //SpelHistorie.duw(new SpelStand(copyBoard(board), currentPlayer));
            tekenBord();
            printScores(getScores());
            laatSpelerZien();
        }
        if(klaar) klaar = false;
    }

    private void klikOpVakje(int x, int y){
        // if player clicked on his own piece
        int huidigeSpeler = this.huidigeSpeler;
        int[][] huidigBord = copyBoard(bord);

        if(bord[x][y] == huidigeSpeler) {
            if (geselecteerdVakje != null) {
                bord[geselecteerdVakje.getCoordinate().getX()][geselecteerdVakje.getCoordinate().getY()] = geselecteerdVakje.getPlayer();
                mogelijkeZettenOpschonen();
                huidigBord = copyBoard(bord);
            }
            huidigeSpelStand = new SpelStand(huidigBord, huidigeSpeler);
            geselecteerdVakje = new Vakje(bord[x][y], new Coordinaat(x, y));
            bord[x][y] = -1;
            Stapel<Coordinaat> mogelijkeAangrenzendeZetten = getMogelijkeAangrenzendeZetten(geselecteerdVakje.getCoordinate());
            Stapel<Coordinaat> mogelijkeSpringZetten = getMogelijkeSpringZetten(geselecteerdVakje.getCoordinate());
            while(mogelijkeAangrenzendeZetten.lengte() > 0){
                Coordinaat zet = mogelijkeAangrenzendeZetten.pak();
                bord[zet.getX()][zet.getY()] = -2;
            }
            while(mogelijkeSpringZetten.lengte() > 0){
                Coordinaat zet = mogelijkeSpringZetten.pak();
                bord[zet.getX()][zet.getY()] = -3;
            }
            tekenBord();
        }
        else if(bord[x][y] == -2 || bord[x][y] == -3){
            SpelHistorie.duw(huidigeSpelStand);
            if(bord[x][y] == -2){
                handelAangrenzendeZet(x, y);
            }
            else if(bord[x][y] == -3){
                handelSpringZet(x, y);
            }
            checkVijandelijkeStukken(x, y);
            veranderSpeler();
            printScores(getScores());
        }
        // if player clicked on an empty square
        else if(bord[x][y] == -1){
            mogelijkeZettenOpschonen();
            if(geselecteerdVakje != null){
                bord[geselecteerdVakje.getCoordinate().getX()][geselecteerdVakje.getCoordinate().getY()] = geselecteerdVakje.getPlayer();
                geselecteerdVakje = null;
                tekenBord();
            }
        }
    }

    // ------------------------------------ MOVE LOGIC -------------------------------------------- //
    private void handelAangrenzendeZet(int x, int y){
        bord[geselecteerdVakje.getCoordinate().getX()][geselecteerdVakje.getCoordinate().getY()] = geselecteerdVakje.getPlayer();
        bord[x][y] = geselecteerdVakje.getPlayer();
        geselecteerdVakje = null;
        mogelijkeZettenOpschonen();
        tekenBord();
    }

    private void handelSpringZet(int x, int y) {
        bord[geselecteerdVakje.getCoordinate().getX()][geselecteerdVakje.getCoordinate().getY()] = 0;
        bord[x][y] = geselecteerdVakje.getPlayer();
        geselecteerdVakje = null;
        mogelijkeZettenOpschonen();
        tekenBord();
    }

    private void checkVijandelijkeStukken(int x, int y){
        int vijand = huidigeSpeler == 1 ? 2 : 1;
        for(int a = x-1; a <= x+1; a++){
            for(int b = y-1; b <= y+1; b++){
                if(a >= 0 && a <= GROOTTE - 1 && b >= 0 && b <= GROOTTE - 1 && bord[a][b] == vijand){
                    System.out.println("vijandelijk stuk op " + a + "," + b);
                    System.out.println("Huidige speler: " + huidigeSpeler);
                    bord[a][b] = huidigeSpeler;
                }
            }
        }
        tekenBord();
    }

    // ------------------------------------ POSSIBLE MOVES -------------------------------------------- //

    private void mogelijkeZettenOpschonen(){
        for(int x = 0; x < GROOTTE; x++){
            for(int y = 0; y < GROOTTE; y++){
                if(bord[x][y] == -2 || bord[x][y] == -3){
                    bord[x][y] = 0;
                }
            }
        }
    }

    private Stapel<Coordinaat> getMogelijkeAangrenzendeZetten(Coordinaat vak) {
        mogelijkeZettenOpschonen();
        Stapel<Coordinaat> zetten = new Stapel<Coordinaat>();
        int x = vak.getX();
        int y = vak.getY();

        // all adjacent squares
        for(int a = x-1; a <= x+1; a++){
            for(int b = y-1; b <= y+1; b++){
                if(a >= 0 && a <= GROOTTE - 1 && b >= 0 && b <= GROOTTE - 1 && bord[a][b] == 0){
                    zetten.duw(new Coordinaat(a, b));
                }
            }
        }
        return zetten;
    }

    private Stapel<Coordinaat> getMogelijkeSpringZetten(Coordinaat vak) {
        mogelijkeZettenOpschonen();
        Stapel<Coordinaat> zetten = new Stapel<Coordinaat>();
        int x = vak.getX();
        int y = vak.getY();
        // all squares two squares away
        for(int a = x-2; a <= x+2; a++){
            for(int b = y-2; b <= y+2; b++){
                if(a >= 0 && a <= GROOTTE - 1 && b >= 0 && b <= GROOTTE - 1 && bord[a][b] == 0 &&
                        (Math.abs(a-x) == 2 || Math.abs(b-y) == 2)){
                    zetten.duw(new Coordinaat(a, b));
                }
            }
        }
        return zetten;
    }

    private Stapel<Coordinaat> alleMogelijkeZetten(Coordinaat vak){
        Stapel<Coordinaat> zetten = new Stapel<Coordinaat>();
        Stapel<Coordinaat> aanliggendeZetten = getMogelijkeAangrenzendeZetten(vak);
        Stapel<Coordinaat> sprongZetten = getMogelijkeSpringZetten(vak);
        while(aanliggendeZetten.lengte() > 0){
            zetten.duw(aanliggendeZetten.pak());
        }
        while(sprongZetten.lengte() > 0){
            zetten.duw(sprongZetten.pak());
        }
        return zetten;
    }

    // --------------------------------------------- BOARD LOGIC ---------------------------------------------- //

    private void startPositie(){
        for(int x = GROOTTE - 1; x >= GROOTTE - 2 ; x--){
            for(int y = 0; y < 2; y++){
                bord[x][y] = 1;
            }
        }

        for(int x = 0; x < 2; x++){
            for(int y = GROOTTE - 1; y >= GROOTTE - 2; y--){
                bord[x][y] = 2;
            }
        }
    }

    public void tekenBord(){
        // Get the board by id
        HBox boardContainer = (HBox) pane.lookup("#boardContainer");
        GridPane boardGrid = (GridPane) boardContainer.lookup("#board");

        for(int x = 0; x < GROOTTE; x++){
            for(int y = 0; y < GROOTTE; y++){
                stopStukkenInVakje(boardGrid, x, y, bord[x][y]);
            }
        }
    }

    public void stopStukkenInVakje(GridPane board, int x, int y, int player){
        StackPane square = (StackPane) board.lookup("#" + x + "," + y);
        if(player == 1)
            square.setStyle("-fx-background-color: #ffa2a2; -fx-border-color: #000000; -fx-border-width: 1px;");
        else if(player == 2)
            square.setStyle("-fx-background-color: #a5ffa2; -fx-border-color: #000000; -fx-border-width: 1px;");
        else if(player == -1)
            square.setStyle("-fx-background-color: #ffe7b3; -fx-border-color: #000000; -fx-border-width: 1px;");
        else if(player == -2)
            square.setStyle("-fx-background-color: #b3ffe7; -fx-border-color: #000000; -fx-border-width: 1px;");
        else if(player == -3)
            square.setStyle("-fx-background-color: #b3b3ff; -fx-border-color: #000000; -fx-border-width: 1px;");
        else{
            square.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #000000; -fx-border-width: 1px;");
        }
        //Label label = new Label("" + player);
        //square.getChildren().add(label);
    }

    // --------------------------------------------- SCORE AND PLAYER ---------------------------------------------- //

    private Stapel<Score> getScores(){
        Stapel<Score> scores = new Stapel<Score>();
        int player1 = 0;
        int player2 = 0;
        for(int x = 0; x < GROOTTE; x++){
            for(int y = 0; y < GROOTTE; y++){
                if(bord[x][y] == 1)
                    player1++;
                else if(bord[x][y] == 2)
                    player2++;
            }
        }
        scores.duw(new Score(1, player1));
        scores.duw(new Score(2, player2));
        return scores;
    }

    private void printScores(Stapel<Score> scores){
        Score score2 = scores.pak();
        Score score1 = scores.pak();
        System.out.println("Player 1: " + score1.getScore());
        System.out.println("Player 2: " + score2.getScore());
        ScorePlayer1.setText("" + score1.getScore());
        ScorePlayer2.setText("" + score2.getScore());
    }

    private void laatSpelerZien(){
        PlayerLabel.setText("Player " + huidigeSpeler);
        if(huidigeSpeler == 1)
            Left.setStyle("-fx-background-color: #ffa2a2;");
        else
            Left.setStyle("-fx-background-color: #a5ffa2;");
    }

    private void veranderSpeler(){
        //SpelStand huidigeStand = new SpelStand(copyBoard(board), currentPlayer);
        //SpelHistorie.duw(huidigeStand);
        checkVoorWinst();
        if(!klaar) {
            if(spelType == SpelType.Multiplayer) {
                huidigeSpeler = huidigeSpeler == 1 ? 2 : 1;
                laatSpelerZien();
            } else if(spelType == SpelType.Singleplayer) {
                huidigeSpeler = huidigeSpeler == 1 ? 2 : 1;
                if(huidigeSpeler == 2)
                    try {
                        System.out.println("AI is aan de beurt");
                        doeComputerZet();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                laatSpelerZien();
            }
        }
    }

    private void doeComputerZet() throws InterruptedException {
        //Thread.sleep(500);
        Stapel<Coordinaat> stukken = getStukkenVanSpeler(2);
        Stapel<Zet> zetten = new Stapel<Zet>();
        while(stukken.lengte() > 0){
            Coordinaat stuk = stukken.pak();
            Stapel<Coordinaat> stukZetten = alleMogelijkeZetten(stuk);
            while(stukZetten.lengte() > 0){
                zetten.duw(new Zet(stuk, stukZetten.pak()));
            }
        }
        if(zetten.lengte() > 0){
            int lengte = zetten.lengte();
            int random = (int) (Math.random() * lengte);
            Zet zet = null;
            for(int i = 0; i <= random; i++){
                zet = zetten.pak();
            }
            System.out.println("AI zet: van " + zet.getVan().getX() + "," + zet.getVan().getY() + " naar " + zet.getNaar().getX() + "," + zet.getNaar().getY());
            doeZet(zet);
        }
    }

    private void doeZet(Zet zet) {
        int xVan = zet.getVan().getX();
        int yVan = zet.getVan().getY();
        int xNaar = zet.getNaar().getX();
        int yNaar = zet.getNaar().getY();

        Vakje piece = new Vakje(bord[xVan][yVan], new Coordinaat(xVan, yVan));
        geselecteerdVakje = piece;

        if(Math.abs(xVan - xNaar) == 1){
            handelAangrenzendeZet(xNaar, yNaar);
        }
        else{
            handelSpringZet(xNaar, yNaar);
        }
        checkVijandelijkeStukken(xNaar, yNaar);
        veranderSpeler();
    }

    private void checkVoorWinst() {
        // check of er voor de tegenstander geen zetten meer mogelijk zijn
        Stapel<Coordinaat> tegenstanderStukken = getStukkenVanSpeler(huidigeSpeler == 1 ? 2 : 1);
        Stapel<Coordinaat> zetten = new Stapel<Coordinaat>();
        while(tegenstanderStukken.lengte() > 0){
            Coordinaat stuk = tegenstanderStukken.pak();
            Stapel<Coordinaat> stukZetten = alleMogelijkeZetten(stuk);
            while(stukZetten.lengte() > 0){
                zetten.duw(stukZetten.pak());
            }
        }
        if(zetten.lengte() == 0){
            Stapel <Score> scores = getScores();
            Score score2 = scores.pak();
            Score score1 = scores.pak();
            if(score1.getScore() > score2.getScore()) {
                System.out.println("Player 1 heeft gewonnen!");
                PlayerLabel.setText("Player 1 heeft gewonnen!");
            }
            else if(score1.getScore() < score2.getScore()) {
                System.out.println("Player 2 heeft gewonnen!");
                PlayerLabel.setText("Player 2 heeft gewonnen!");
            }

            else {
                System.out.println("Player " + huidigeSpeler + " heeft gewonnen!");
                PlayerLabel.setText("Player " + huidigeSpeler + " heeft gewonnen!");
                Left.setStyle("-fx-background-color: #FFFFFF;");
            }

            klaar = true;
        }


    }

    private void terugNaarHoofdMenu() throws IOException {
        Stage stage = (Stage) pane.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/BeginScherm.fxml"));
        stage.setScene(new Scene(loader.load(), 300, 300));
        stage.show();
    }

    private Stapel<Coordinaat> getStukkenVanSpeler(int speler) {
        Stapel<Coordinaat> stukken = new Stapel<Coordinaat>();
        for(int x = 0; x < GROOTTE; x++){
            for(int y = 0; y < GROOTTE; y++){
                if(bord[x][y] == speler)
                    stukken.duw(new Coordinaat(x, y));
            }
        }
        return stukken;
    }

    private int[][] copyBoard(int[][] board){
        int[][] newBoard = new int[GROOTTE][GROOTTE];
        for(int x = 0; x < GROOTTE; x++){
            for(int y = 0; y < GROOTTE; y++){
                newBoard[x][y] = board[x][y];
            }
        }
        return newBoard;
    }

}
