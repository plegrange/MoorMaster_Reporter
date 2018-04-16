package Energy;

import Main.MainController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.List;

public class EnergyController {
    private Button backButton;
    private Scene mainScene;
    private MainController mainController;
    private ComboBox<String> endMonthPicker, endYearPicker, startMonthPicker, startYearPicker;
    private Button generateButton, directoryButton, settingsButton, filterButton;
    private File selectedDirectory;
    private Label directoryLabel, feedback;
    private TableView tableView;
    private FileReader fileReader;
    private DirectoryChooser directoryChooser;
    private TableManager tableManager;
    private OutputManager outputManager;
    Parent password = null, rate = null;
    Scene passwordScene, rateScene;
    private TextField winterTxt, summerTxt;
    private String winterRate = "", summerRate = "";
    TableColumn yearCol, monthCol, comboCol, TPTCol, AMSCol, comboCostCol, TPTCostCol, AMSCostCol, maxDemandCol, PoNCostCol;

    public void connectToUI(Stage primaryStage, Scene mainScene) {
        tableManager = new TableManager();
        this.mainScene = mainScene;
        feedback = (Label) primaryStage.getScene().lookup("#feedback");
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

        feedback = (Label) primaryStage.getScene().lookup("#feedback");
        tableView = (TableView) primaryStage.getScene().lookup("#table");
        tableView.setEditable(true);


        directoryLabel = (Label) primaryStage.getScene().lookup("#directoryLbl");
        directoryButton = (Button) primaryStage.getScene().lookup("#directoryBtn");
        startYearPicker = (ComboBox) primaryStage.getScene().lookup("#startYearPicker");

        startMonthPicker = (ComboBox) primaryStage.getScene().lookup("#startMonthPicker");

        endYearPicker = (ComboBox) primaryStage.getScene().lookup("#endYearPicker");

        endMonthPicker = (ComboBox) primaryStage.getScene().lookup("#endMonthPicker");

        generateButton = (Button) primaryStage.getScene().lookup("#generateBtn");
        settingsButton = (Button) primaryStage.getScene().lookup("#settingsBtn");
        filterButton = (Button) primaryStage.getScene().lookup("#filterBtn");
        generateButton = (Button) primaryStage.getScene().lookup("#generateBtn");
        directoryChooser = new DirectoryChooser();
        try {
            password = FXMLLoader.load(getClass().getResource("passwordScreen.fxml"));
            rate = FXMLLoader.load(getClass().getResource("ratesScreen.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        passwordScene = new Scene(password, 300, 100);
        rateScene = new Scene(rate, 300, 100);
        final PasswordField passwordTxt = (PasswordField) passwordScene.lookup("#passwordTxt");
        final Button cancelBtn = (Button) passwordScene.lookup("#cancelBtn");
        final Button acceptBtn = (Button) passwordScene.lookup("#acceptBtn");
        final Button cancelEditBtn = (Button) rateScene.lookup("#cancelBtn");
        final Button saveBtn = (Button) rateScene.lookup("#saveBtn");
        winterTxt = (TextField) rateScene.lookup("#winterTxt");
        summerTxt = (TextField) rateScene.lookup("#summerTxt");
        buildComboBoxes();
        try {
            readRates("rates.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }

        directoryButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectedDirectory = directoryChooser.showDialog(primaryStage);
                if (selectedDirectory != null) {
                    directoryLabel.setText(selectedDirectory.getAbsolutePath());
                    fileReader = new FileReader(selectedDirectory);
                    tableView.setItems(fileReader.getObservableList());
                }
            }
        });
        filterButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (selectedDirectory != null && startYearPicker.getValue() != null && endYearPicker.getValue() != null && startMonthPicker.getValue() != null && endMonthPicker.getValue() != null)
                    filter();
            }
        });
        settingsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                final Stage secondaryStage = new Stage();
                secondaryStage.setScene(passwordScene);
                secondaryStage.setTitle("Administrator Password");
                secondaryStage.show();
                cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        secondaryStage.close();
                    }
                });

                passwordTxt.setText("");
                acceptBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (passwordTxt.getText().equals("cavotecP262")) {
                            secondaryStage.setTitle("Rate Editor");
                            winterTxt.setText(winterRate);
                            summerTxt.setText(summerRate);
                            secondaryStage.setScene(rateScene);
                            secondaryStage.show();
                        }
                    }
                });
                cancelEditBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        secondaryStage.close();
                    }
                });
                saveBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        winterRate = winterTxt.getText();
                        summerRate = summerTxt.getText();
                        try {
                            saveFile("rates.txt", encrypt(Double.valueOf(winterRate), Double.valueOf(summerRate)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        secondaryStage.close();
                    }
                });

            }
        });
        generateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                outputManager = new OutputManager();
                List<Month> months = (List<Month>) tableView.getItems();
                String startDate = startMonthPicker.getValue() + "_" + startYearPicker.getValue();
                String endDate = endMonthPicker.getValue() + "_" + endYearPicker.getValue();
                outputManager.generateReport(months, primaryStage, startDate, endDate);
                feedback.setText("Report Generated.");
            }
        });
        //buildComboBoxes();
        yearCol = (TableColumn) tableView.getColumns().get(0);
        monthCol = (TableColumn) tableView.getColumns().get(1);
        comboCol = (TableColumn) ((TableColumn) (tableView.getColumns().get(2))).getColumns().get(0);
        TPTCol = (TableColumn) ((TableColumn) (tableView.getColumns().get(2))).getColumns().get(1);
        AMSCol = (TableColumn) ((TableColumn) (tableView.getColumns().get(2))).getColumns().get(2);
        comboCostCol = (TableColumn) ((TableColumn) (tableView.getColumns().get(3))).getColumns().get(0);
        TPTCostCol = (TableColumn) ((TableColumn) (tableView.getColumns().get(3))).getColumns().get(1);
        AMSCostCol = (TableColumn) ((TableColumn) (tableView.getColumns().get(3))).getColumns().get(2);
        maxDemandCol = (TableColumn) tableView.getColumns().get(4);
        PoNCostCol = (TableColumn) tableView.getColumns().get(5);

        yearCol.setCellValueFactory(new PropertyValueFactory<Month, String>("year"));
        monthCol.setCellValueFactory(new PropertyValueFactory<Month, String>("month"));
        comboCol.setCellValueFactory(new PropertyValueFactory<Month, String>("combokWh"));
        TPTCol.setCellValueFactory(new PropertyValueFactory<Month, String>("TPTkWh"));
        AMSCol.setCellValueFactory(new PropertyValueFactory<Month, String>("AMSkWh"));
        comboCostCol.setCellValueFactory(new PropertyValueFactory<Month, String>("comboCost"));
        TPTCostCol.setCellValueFactory(new PropertyValueFactory<Month, String>("TPTCost"));
        AMSCostCol.setCellValueFactory(new PropertyValueFactory<Month, String>("AMSCost"));
        maxDemandCol.setCellValueFactory(new PropertyValueFactory<Month, String>("maxDemand"));
        PoNCostCol.setCellValueFactory(new PropertyValueFactory<Month, String>("PoNCost"));

        linkCells();
    }

    private void accumulateValues() {
        List<Month> months = (List<Month>) tableView.getItems();

    }

    private void linkCells() {

        comboCol.setCellFactory(TextFieldTableCell.forTableColumn());
        comboCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Month, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Month, String> t) {
                ((Month) t.getTableView().getItems().get(t.getTablePosition().getRow())).setCombokWh(t.getNewValue());
                ((Month) t.getTableView().getItems().get(t.getTablePosition().getRow())).update(Double.valueOf(winterRate), Double.valueOf(summerRate));
                refresh();
            }
        });
        TPTCol.setCellFactory(TextFieldTableCell.forTableColumn());
        TPTCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Month, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Month, String> t) {
                ((Month) t.getTableView().getItems().get(t.getTablePosition().getRow())).setTPTkWh(t.getNewValue());
                ((Month) t.getTableView().getItems().get(t.getTablePosition().getRow())).update(Double.valueOf(winterRate), Double.valueOf(summerRate));
                refresh();
            }
        });
        AMSCol.setCellFactory(TextFieldTableCell.forTableColumn());
        AMSCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Month, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Month, String> t) {
                ((Month) t.getTableView().getItems().get(t.getTablePosition().getRow())).setAMSkWh(t.getNewValue());
                ((Month) t.getTableView().getItems().get(t.getTablePosition().getRow())).update(Double.valueOf(winterRate), Double.valueOf(summerRate));
                refresh();
            }
        });
        maxDemandCol.setCellFactory(TextFieldTableCell.forTableColumn());
        maxDemandCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Month, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Month, String> t) {
                ((Month) t.getTableView().getItems().get(t.getTablePosition().getRow())).setMaxDemand(t.getNewValue());
                ((Month) t.getTableView().getItems().get(t.getTablePosition().getRow())).update(Double.valueOf(winterRate), Double.valueOf(summerRate));
                refresh();
            }
        });
    }

    private void refresh() {
        ((TableColumn) tableView.getColumns().get(0)).setVisible(false);
        ((TableColumn) tableView.getColumns().get(0)).setVisible(true);
    }

    private void readRates(String fileName) throws Exception {
        java.io.FileReader fileReader = new java.io.FileReader(fileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line == "") {
                winterRate = "139.32";
                summerRate = "74.2";
            } else {
                decrypt(line);
            }
            winterTxt.setText(winterRate);
            summerTxt.setText(summerRate);
        }
    }

    private String encrypt(double winter, double summer) throws Exception {
        return (Math.pow(winter, 2) + 5.5) + ":" + (Math.pow(summer, 2) + 17.5);
    }

    private void decrypt(String str) throws Exception {
        String[] elements = str.split(":");
        String winterStr = elements[0];
        String summerStr = elements[1];
        winterRate = String.valueOf(Math.sqrt(Double.valueOf(winterStr) - 5.5));
        summerRate = String.valueOf(Math.sqrt(Double.valueOf(summerStr) - 17.5));
    }

    private void saveFile(String fileName, String data) throws IOException {
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {

            //String content = "winter=" + winterRate + "\n" + "summer=" + summerRate;

            fw = new FileWriter(fileName);
            bw = new BufferedWriter(fw);
            bw.write(data);

            //System.out.println("Done");

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
    }

    private double getRate(String month) {
        switch (month) {
            case "May":
            case "June":
            case "July":
            case "August":
                return Double.valueOf(winterRate) / 100.0;
            default:
                return Double.valueOf(summerRate) / 100.0;
        }
    }

    private void buildComboBoxes() {
        ObservableList<String> monthIndex =
                FXCollections.observableArrayList(
                        "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
        startMonthPicker.setItems(monthIndex);
        endMonthPicker.setItems(monthIndex);
        ObservableList<String> year = FXCollections.observableArrayList();
        for (int i = 2000; i < 2050; i++) {
            year.add(String.valueOf(i));
        }
        startYearPicker.setItems(year);
        endYearPicker.setItems(year);
    }

    private void filter() {
        if (startMonthPicker.getValue() == null || endMonthPicker.getValue() == null)
            return;
        if (startYearPicker.getValue() == null || endYearPicker.getValue() == null)
            return;
        int startYear = Integer.valueOf(startYearPicker.getValue());
        int endYear = Integer.valueOf(endYearPicker.getValue());
        String startMonth = startMonthPicker.getValue();
        String endMonth = endMonthPicker.getValue();
        tableView.setItems(tableManager.filter(fileReader.getObservableList(), startYear, endYear, startMonth, endMonth));
    }
}
