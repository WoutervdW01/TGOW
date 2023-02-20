package nl.tgow;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class GameScreen {

    @FXML
    AnchorPane pane;

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
                square.setPrefSize(100, 100);
                root.add(square, i, j);
            }
        }
        boardContainer.getChildren().add(root);
        pane.getChildren().add(boardContainer);
        setPieces();
    }

    public void setPieces(){
        // Get the board by id
        HBox boardContainer = (HBox) pane.lookup("#boardContainer");
        GridPane board = (GridPane) boardContainer.lookup("#board");

        // Set the pieces
        for(int x = 6; x >= 5 ; x--){
            for(int y = 0; y < 2; y++){
                setPieceInSquare(board, x, y, 1);
            }
        }

        for(int x = 0; x < 2; x++){
            for(int y = 6; y >=5; y--){
                setPieceInSquare(board, x, y, 2);
            }
        }
    }

    public void setPieceInSquare(GridPane board, int x, int y, int player){
        StackPane square = (StackPane) board.lookup("#" + x + "," + y);
        Label label = new Label("" + player);
        square.getChildren().add(label);
    }

}
