package nl.tgow;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import nl.tgow.datastructures.Stapel;
import nl.tgow.models.Coordinate;
import nl.tgow.models.Piece;

public class GameScreen {

    @FXML
    AnchorPane pane;

    @FXML
    VBox Mid;

    private int[][] board = new int[7][7];
    private Piece selectedPiece = null;
    private int currentPlayer = 1;

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
        startPosition();
        setPieces();
    }

    private void clickSquare(int x, int y){
        // if player clicked on his own piece
        if(board[x][y] == currentPlayer) {
            if (selectedPiece != null) {
                board[selectedPiece.getCoordinate().getX()][selectedPiece.getCoordinate().getY()] = selectedPiece.getPlayer();
            }
            selectedPiece = new Piece(board[x][y], new Coordinate(x, y));
            board[x][y] = -1;
            Stapel possibleAdjacentMoves = getPossibleAdjacentMoves();
            Stapel possibleMoveMoves = getPossibleMoveMoves();
            while(possibleAdjacentMoves.lengte() > 0){
                Coordinate move = (Coordinate) possibleAdjacentMoves.pak();
                board[move.getX()][move.getY()] = -2;
            }
            while(possibleMoveMoves.lengte() > 0){
                Coordinate move = (Coordinate) possibleMoveMoves.pak();
                board[move.getX()][move.getY()] = -3;
            }
            setPieces();
        }
        // if player clicked on a duplicate move
        else if(board[x][y] == -2){
            handleDuplicateMove(x, y);
        }
        // if player clicked on a move move
        else if(board[x][y] == -3){
            handleMoveMove(x, y);
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
        currentPlayer = currentPlayer == 1 ? 2 : 1;
    }

    private void handleMoveMove(int x, int y) {
        board[selectedPiece.getCoordinate().getX()][selectedPiece.getCoordinate().getY()] = 0;
        //selectedPiece.getCoordinate().setX(x);
        //selectedPiece.getCoordinate().setY(y);
        board[x][y] = selectedPiece.getPlayer();
        selectedPiece = null;
        cleanupPossibleMoves();
        setPieces();
        currentPlayer = currentPlayer == 1 ? 2 : 1;
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

    private Stapel getPossibleAdjacentMoves() {
        cleanupPossibleMoves();
        Stapel moves = new Stapel();
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

    private Stapel getPossibleMoveMoves() {
        cleanupPossibleMoves();
        Stapel moves = new Stapel();
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

}
