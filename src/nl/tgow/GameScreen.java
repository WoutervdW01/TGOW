package nl.tgow;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class GameScreen {

    @FXML
    AnchorPane pane;

    @FXML
    VBox Mid;

    private int[][] board = new int[7][7];

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
        if(board[x][y] == 1){
            board[x][y] = 2;
        } else if(board[x][y] == 2){
            board[x][y] = 1;
        }

    }

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
                if(board[x][y] == 1){
                    setPieceInSquare(boardGrid, x, y, 1);
                } else if(board[x][y] == 2){
                    setPieceInSquare(boardGrid, x, y, 2);
                }
            }
        }
    }

    public void setPieceInSquare(GridPane board, int x, int y, int player){
        StackPane square = (StackPane) board.lookup("#" + x + "," + y);
        if(player == 1)
            square.setStyle("-fx-background-color: #ffa2a2; -fx-border-color: #000000; -fx-border-width: 1px;");
        else if(player == 2)
            square.setStyle("-fx-background-color: #a5ffa2; -fx-border-color: #000000; -fx-border-width: 1px;");
        //Label label = new Label("" + player);
        //square.getChildren().add(label);
    }

}
