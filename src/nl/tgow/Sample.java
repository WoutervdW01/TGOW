package nl.tgow;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.awt.*;
import java.io.IOException;

public class Sample {

    @FXML
    private Button startButton;
    @FXML
    private Button uitlegButton;

    public void startGame() throws IOException {
        Stage stage = (Stage) startButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(TGOW.class.getResource("/GameScreen.fxml"));
        stage.setScene(new Scene(loader.load(), 300, 300));
        stage.show();
    }
}
