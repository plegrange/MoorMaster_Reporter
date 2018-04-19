package Weather;

import Main.MainController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class WeatherController {
    private Button backButton, directoryButton, filterDateButton, filterShipButton, generateButton;
    private Scene mainScene;
    private MainController mainController;
    private ComboBox<String> endMonthPicker, endYearPicker, startMonthPicker, startYearPicker, shipPicker;
    private File selectedDirectory;
    private Label directoryLabel;
    private DirectoryChooser directoryChooser;
    private ListView<Ship> listView;
    private Weather.FileReader fileReader;
    private ListManager listManager;
    private ObservableList<Ship> filteredList, mainList;
    private OutputManager outputManager;
    private String reportName = "";
    private String option = "ALL", DATE = "DATE", SHIP = "SHIP";
    private Label feedbackLabel;
    private Scene waitScene, weatherScene;
    private Stage secondaryStage;

    public void connectToUI(Stage primaryStage, Scene mainScene, Scene waitScene) {
        this.mainScene = mainScene;
        this.waitScene = waitScene;
        this.weatherScene = primaryStage.getScene();
        feedbackLabel = (Label) primaryStage.getScene().lookup("#feedbackLabel");
        listManager = new ListManager();
        this.listView = (ListView) primaryStage.getScene().lookup("#listView");
        directoryLabel = (Label) primaryStage.getScene().lookup("#directoryLabel");
        directoryButton = (Button) primaryStage.getScene().lookup("#directoryButton");
        directoryChooser = new DirectoryChooser();
        startYearPicker = (ComboBox) primaryStage.getScene().lookup("#startYearPicker");

        startMonthPicker = (ComboBox) primaryStage.getScene().lookup("#startMonthPicker");

        endYearPicker = (ComboBox) primaryStage.getScene().lookup("#endYearPicker");

        endMonthPicker = (ComboBox) primaryStage.getScene().lookup("#endMonthPicker");
        shipPicker = (ComboBox) primaryStage.getScene().lookup("#shipPicker");
        generateButton = (Button) primaryStage.getScene().lookup("#generateButton");
        generateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                outputManager = new OutputManager();
                switch (option) {
                    default:
                        reportName = startMonthPicker.getValue() + " " + startYearPicker.getValue() + " - " + endMonthPicker.getValue() + " " + endYearPicker.getValue() + ".xls";
                        break;
                    case "SHIP":
                        reportName = shipPicker.getValue() + ".xls";
                        break;
                }
                feedbackLabel.setText("Generating reports. Please wait...");
                outputManager.generateReport(filteredList, "Weather Report " + reportName);
                feedbackLabel.setText("Reports Generated!");
            }
        });
        filterDateButton = (Button) primaryStage.getScene().lookup("#filterDateButton");
        filterShipButton = (Button) primaryStage.getScene().lookup("#filterShipButton");

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


        Label waitFeedbackLabel = (Label) waitScene.lookup("#feedbackLabel");
        directoryButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Service<FileReader> service = new Service<FileReader>() {
                    @Override
                    protected Task<FileReader> createTask() {

                        return new Task<FileReader>() {
                            @Override
                            protected FileReader call() throws Exception {
                                return new FileReader(selectedDirectory);
                            }
                        };
                    }

                };
                //waitFeedbackLabel.textProperty().bind(service.valueProperty());
                //waitFeedbackLabel.textProperty().bind(service.valueProperty().get().readFiles);
                primaryStage.hide();
                primaryStage.setScene(waitScene);
                primaryStage.show();
                selectedDirectory = directoryChooser.showDialog(primaryStage);

                if (selectedDirectory != null) {
                    directoryLabel.setText(selectedDirectory.getAbsolutePath());
                    //fileReader = new FileReader(selectedDirectory);
                    service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent event) {
                            fileReader = service.getValue();
                            mainList = fileReader.getShipsList();
                            filteredList = mainList;
                            Collections.sort(filteredList, new Comparator<Ship>() {
                                @Override
                                public int compare(Ship o1, Ship o2) {
                                    if (o1.isBefore(o2))
                                        return -1;
                                    else if (o2.isBefore(o1))
                                        return 1;
                                    else return 0;
                                }
                            });
                            listView.setItems(filteredList);
                            listView.setCellFactory(param -> new ListCell<Ship>() {
                                @Override
                                protected void updateItem(Ship item, boolean empty) {
                                    super.updateItem(item, empty);
                                    if (empty || item == null || item.getName() == null) {
                                        setText(null);
                                    } else {
                                        setText(item.getName() + " | " + item.getStartDate()
                                                + " - " + item.getEndDate());
                                    }
                                }
                            });
                            primaryStage.hide();
                            buildComboBoxes();
                            primaryStage.setScene(weatherScene);
                            primaryStage.show();
                        }
                    });
                    service.start();
                }else{
                    primaryStage.hide();
                    primaryStage.setScene(weatherScene);
                    primaryStage.show();
                }
            }
        });
        filterDateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                filteredList = listManager.filterByDate(mainList, Integer.valueOf(startYearPicker.getValue()), Integer.valueOf(endYearPicker.getValue())
                        , (startMonthPicker.getValue()), (endMonthPicker.getValue()));
                listView.setItems(filteredList);
                option = DATE;
                Collections.sort(filteredList, new Comparator<Ship>() {
                    @Override
                    public int compare(Ship o1, Ship o2) {
                        if (o1.isBefore(o2))
                            return -1;
                        else if (o2.isBefore(o1))
                            return 1;
                        else return 0;
                    }
                });
            }
        });
        filterShipButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                filteredList = listManager.filterByShip(mainList, shipPicker.getValue());
                listView.setItems(filteredList);

                option = SHIP;
                Collections.sort(filteredList, new Comparator<Ship>() {
                    @Override
                    public int compare(Ship o1, Ship o2) {
                        if (o1.isBefore(o2))
                            return -1;
                        else if (o2.isBefore(o1))
                            return 1;
                        else return 0;
                    }
                });
            }
        });
    }


    private void buildComboBoxes() {
        ObservableList<String> monthIndex =
                FXCollections.observableArrayList(
                        "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");

        ObservableList<String> year = FXCollections.observableArrayList();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        int thisMonth = Calendar.getInstance().get(Calendar.MONTH);
        for (int i = 2000; i <= thisYear; i++) {
            year.add(String.valueOf(i));
        }
        startMonthPicker.setItems(monthIndex);
        startMonthPicker.getSelectionModel().selectFirst();
        endMonthPicker.setItems(monthIndex);
        endMonthPicker.getSelectionModel().select(thisMonth);
        startYearPicker.setItems(year);
        startYearPicker.getSelectionModel().selectFirst();
        endYearPicker.setItems(year);
        endYearPicker.getSelectionModel().selectLast();

        ObservableList<String> ships = FXCollections.observableArrayList();
        for (int i = 0; i < mainList.size(); i++) {
            ships.add(mainList.get(i).getName());
        }
        shipPicker.setItems(ships);
    }
}
