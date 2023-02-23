package nl.tgow;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Scene;
import nl.tgow.models.SpelType;

import java.io.IOException;

public class Sample {

    @FXML
    private Button MultiplayerButton;
    @FXML
    private Button SingleplayerButton;
    @FXML
    private Button uitlegButton;

    private SpelType spelType;

    public void startGame() throws IOException {
        Stage stage = (Stage) MultiplayerButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(TGOW.class.getResource("/GameScreen.fxml"));
        loader.setController(new GameScreen(spelType));
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        stage.setScene(new Scene(loader.load(), 0.5 * primaryScreenBounds.getWidth(), 0.5 * primaryScreenBounds.getHeight()));
        stage.setX(primaryScreenBounds.getMinX());
        stage.setY(primaryScreenBounds.getMinY());
        stage.setFullScreen(true);
        stage.setMinHeight(800);
        stage.setMinWidth(1000);
        stage.show();
    }

    public void startSingleplayer() throws IOException {
        spelType = SpelType.Singleplayer;
        startGame();
    }

    public void startMultiplayer() throws IOException {
        spelType = SpelType.Multiplayer;
        startGame();
    }
}
