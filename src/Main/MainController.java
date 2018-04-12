package Main;

import Energy.EnergyController;
import Weather.WeatherController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    private Button energyButton, weatherButton;
    private EnergyController energyController;
    private WeatherController weatherController;
    private Scene mainScene;

    public void connectToUI(Stage primaryStage) {
        mainScene = primaryStage.getScene();
        energyButton = (Button) primaryStage.getScene().lookup("#energyBtn");
        weatherButton = (Button) primaryStage.getScene().lookup("#weatherBtn");
        energyButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("energyScreen.fxml"));
                    primaryStage.setTitle("Energy Reporter");
                    Scene scene = new Scene(root, 900, 600);
                    primaryStage.hide();
                    primaryStage.setScene(scene);
                    primaryStage.centerOnScreen();
                    primaryStage.show();
                    energyController = new EnergyController();
                    energyController.connectToUI(primaryStage,mainScene);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        weatherButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("weatherScreen.fxml"));
                    primaryStage.setTitle("Weather Reporter");
                    Scene scene = new Scene(root, 900, 600);
                    primaryStage.hide();
                    primaryStage.setScene(scene);
                    primaryStage.centerOnScreen();
                    primaryStage.show();
                    weatherController = new WeatherController();
                    weatherController.connectToUI(primaryStage,mainScene);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
