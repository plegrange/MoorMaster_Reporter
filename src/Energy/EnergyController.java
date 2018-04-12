package Energy;

import Main.MainController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class EnergyController {
    private Button backButton;
    private Scene mainScene;
    private MainController mainController;

    public void connectToUI(Stage primaryStage, Scene mainScene) {
        this.mainScene = mainScene;
        backButton = (Button) primaryStage.getScene().lookup("#backBtn");
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.hide();
                primaryStage.setTitle("MoorMaster");
                primaryStage.setScene(mainScene);
                primaryStage.centerOnScreen();
                mainController = new MainController();
                mainController.connectToUI(primaryStage);
                primaryStage.show();
            }
        });
    }
}
