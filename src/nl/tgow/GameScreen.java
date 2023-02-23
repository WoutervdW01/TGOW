package nl.tgow;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import nl.tgow.datastructures.Stapel;
import nl.tgow.models.Coordinate;
import nl.tgow.models.Piece;
import nl.tgow.models.Score;
import nl.tgow.models.SpelStand;

import java.util.Arrays;

public class GameScreen {

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

    private int[][] board = new int[7][7];
    private Piece selectedPiece = null;
    private int currentPlayer = 1;

    private Stapel score = new Stapel();

    private final Stapel<SpelStand> SpelHistorie = new Stapel<SpelStand>();
    private SpelStand huidigeSpelStand;

    public void initialize(){
        HBox boardContainer = new HBox();
        boardContainer.setId("boardContainer");
        boardContainer.setPrefSize(700, 700);
        // fill whole pane
        AnchorPane.setTopAnchor(boardContainer, 0.0);
        AnchorPane.setBottomAnchor(boardContainer, 0.0);
        AnchorPane.setLeftAnchor(boardContainer, 0.0);
        AnchorPane.setRightAnchor(boardContainer, 0.0);

        // allign center
        boardContainer.setStyle("-fx-alignment: center;");


        GridPane root = new GridPane();
        root.setId("board");
        final int size = 7;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                StackPane square = new StackPane();
                square.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #000000; -fx-border-width: 1px;");
                square.setId(i + "," + j);
                square.setOnMouseClicked(event -> {
                    String[] coordinates = square.getId().split(",");
                    int x = Integer.parseInt(coordinates[0]);
                    int y = Integer.parseInt(coordinates[1]);
                    clickSquare(x, y);
                    setPieces();
                });
                Label label = new Label("" + i + "," + j);
                square.getChildren().add(label);
                square.setPrefSize(100, 100);
                root.add(square, i, j);
            }
        }
        boardContainer.getChildren().add(root);
        Mid.getChildren().add(boardContainer);

        Button undoButton = new Button("Ga terug");
        undoButton.setOnMouseClicked(event -> {
            undo();
        });
        Mid.getChildren().add(undoButton);
        startPosition();
        setPieces();
        SpelHistorie.duw(new SpelStand(copyBoard(board), currentPlayer));
        printScores(getScores());
        showPlayer();
    }

    private void undo() {
        if(SpelHistorie.lengte() > 1){
            SpelStand vorig = SpelHistorie.pak();
            board = vorig.getBord();
            currentPlayer = vorig.getHuidigeSpeler();
            //SpelHistorie.duw(new SpelStand(copyBoard(board), currentPlayer));
            setPieces();
            printScores(getScores());
            showPlayer();
        }
    }

    private void clickSquare(int x, int y){
        // if player clicked on his own piece
        int currentPlayer = this.currentPlayer;
        int[][] currentBoard = copyBoard(board);

        if(board[x][y] == currentPlayer) {
            huidigeSpelStand = new SpelStand(currentBoard, currentPlayer);
            if (selectedPiece != null) {
                board[selectedPiece.getCoordinate().getX()][selectedPiece.getCoordinate().getY()] = selectedPiece.getPlayer();
            }
            selectedPiece = new Piece(board[x][y], new Coordinate(x, y));
            board[x][y] = -1;
            Stapel<Coordinate> possibleAdjacentMoves = getPossibleAdjacentMoves();
            Stapel<Coordinate> possibleMoveMoves = getPossibleMoveMoves();
            while(possibleAdjacentMoves.lengte() > 0){
                Coordinate move = possibleAdjacentMoves.pak();
                board[move.getX()][move.getY()] = -2;
            }
            while(possibleMoveMoves.lengte() > 0){
                Coordinate move = possibleMoveMoves.pak();
                board[move.getX()][move.getY()] = -3;
            }
            setPieces();
        }
        else if(board[x][y] == -2 || board[x][y] == -3){
            SpelHistorie.duw(huidigeSpelStand);
            if(board[x][y] == -2){
                handleDuplicateMove(x, y);
            }
            else if(board[x][y] == -3){
                handleMoveMove(x, y);
            }
            checkEnemyPieces(x, y);
            changePlayer();
            printScores(getScores());
        }
        // if player clicked on an empty square
        else if(board[x][y] == -1){
            cleanupPossibleMoves();
            if(selectedPiece != null){
                board[selectedPiece.getCoordinate().getX()][selectedPiece.getCoordinate().getY()] = selectedPiece.getPlayer();
                selectedPiece = null;
                setPieces();
            }
        }
    }

    // ------------------------------------ MOVE LOGIC -------------------------------------------- //
    private void handleDuplicateMove(int x, int y){
        board[selectedPiece.getCoordinate().getX()][selectedPiece.getCoordinate().getY()] = selectedPiece.getPlayer();
        board[x][y] = selectedPiece.getPlayer();
        selectedPiece = null;
        cleanupPossibleMoves();
        setPieces();
    }

    private void handleMoveMove(int x, int y) {
        board[selectedPiece.getCoordinate().getX()][selectedPiece.getCoordinate().getY()] = 0;
        board[x][y] = selectedPiece.getPlayer();
        selectedPiece = null;
        cleanupPossibleMoves();
        setPieces();
    }

    private void checkEnemyPieces(int x, int y){
        int enemy = currentPlayer == 1 ? 2 : 1;
        for(int a = x-1; a <= x+1; a++){
            for(int b = y-1; b <= y+1; b++){
                if(a >= 0 && a <= 6 && b >= 0 && b <= 6 && board[a][b] == enemy){
                    System.out.println("enemy piece at " + a + "," + b);
                    System.out.println("Current player: " + currentPlayer);
                    board[a][b] = currentPlayer;
                }
            }
        }
        setPieces();
    }

    // ------------------------------------ POSSIBLE MOVES -------------------------------------------- //

    private void cleanupPossibleMoves(){
        for(int x = 0; x < 7; x++){
            for(int y = 0; y < 7; y++){
                if(board[x][y] == -2 || board[x][y] == -3){
                    board[x][y] = 0;
                }
            }
        }
    }

    private Stapel<Coordinate> getPossibleAdjacentMoves() {
        cleanupPossibleMoves();
        Stapel<Coordinate> moves = new Stapel<Coordinate>();
        int x = selectedPiece.getCoordinate().getX();
        int y = selectedPiece.getCoordinate().getY();

        // all adjacent squares
        for(int a = x-1; a <= x+1; a++){
            for(int b = y-1; b <= y+1; b++){
                if(a >= 0 && a <= 6 && b >= 0 && b <= 6 && board[a][b] == 0){
                    moves.duw(new Coordinate(a, b));
                }
            }
        }
        return moves;
    }

    private Stapel<Coordinate> getPossibleMoveMoves() {
        cleanupPossibleMoves();
        Stapel<Coordinate> moves = new Stapel<Coordinate>();
        int x = selectedPiece.getCoordinate().getX();
        int y = selectedPiece.getCoordinate().getY();
        // all squares two squares away
        for(int a = x-2; a <= x+2; a++){
            for(int b = y-2; b <= y+2; b++){
                if(a >= 0 && a <= 6 && b >= 0 && b <= 6 && board[a][b] == 0 && (Math.abs(a-x) == 2 || Math.abs(b-y) == 2)){
                    moves.duw(new Coordinate(a, b));
                }
            }
        }
        return moves;
    }

    // --------------------------------------------- BOARD LOGIC ---------------------------------------------- //

    private void startPosition(){
        for(int x = 6; x >= 5 ; x--){
            for(int y = 0; y < 2; y++){
                board[x][y] = 1;
            }
        }

        for(int x = 0; x < 2; x++){
            for(int y = 6; y >=5; y--){
                board[x][y] = 2;
            }
        }
    }

    public void setPieces(){
        // Get the board by id
        HBox boardContainer = (HBox) pane.lookup("#boardContainer");
        GridPane boardGrid = (GridPane) boardContainer.lookup("#board");

        for(int x = 0; x < 7; x++){
            for(int y = 0; y < 7; y++){
                setPieceInSquare(boardGrid, x, y, board[x][y]);
            }
        }
    }

    public void setPieceInSquare(GridPane board, int x, int y, int player){
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
        for(int x = 0; x < 7; x++){
            for(int y = 0; y < 7; y++){
                if(board[x][y] == 1)
                    player1++;
                else if(board[x][y] == 2)
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

    private void showPlayer(){
        PlayerLabel.setText("Player " + currentPlayer);
        if(currentPlayer == 1)
            Left.setStyle("-fx-background-color: #ffa2a2;");
        else
            Left.setStyle("-fx-background-color: #a5ffa2;");
    }

    private void changePlayer(){
        //SpelStand huidigeStand = new SpelStand(copyBoard(board), currentPlayer);
        //SpelHistorie.duw(huidigeStand);
        currentPlayer = currentPlayer == 1 ? 2 : 1;
        showPlayer();
    }

    private int[][] copyBoard(int[][] board){
        int[][] newBoard = new int[7][7];
        for(int x = 0; x < 7; x++){
            for(int y = 0; y < 7; y++){
                newBoard[x][y] = board[x][y];
            }
        }
        return newBoard;
    }

}
