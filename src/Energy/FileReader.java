package Energy;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileReader {

    private File selectedDirectory;
    public List<Ship> shipFiles, temp;
    private List<File> allFiles;
    public List<Month> months, filtered;

    public FileReader(File directory) {
        filtered = new ArrayList<>();
        selectedDirectory = directory;
        shipFiles = new ArrayList<>();
        for (File file : selectedDirectory.listFiles()) {
            readFile(file);
        }
        temp = cloneList(shipFiles);
        groupShipsByMonth();
        shipFiles = cloneList(temp);
        //filtered = months;
        temp = new ArrayList<>();
    }
    private void readFile(File file) {
        String shipName = "", dateMoored = "", duration = "";
        try {
            java.io.FileReader fileReader = new java.io.FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            String[] lineElements;
            while ((line = bufferedReader.readLine()) != null) {
                lineElements = line.split(":");
                if (lineElements[0].startsWith("Name")) {
                    shipName = lineElements[1];
                    break;
                }
            }
            while ((line = bufferedReader.readLine()) != null) {
                lineElements = line.split(":");
                if (lineElements[0].startsWith("Moored")) {
                    dateMoored = lineElements[1];
                    break;
                }
            }
            while ((line = bufferedReader.readLine()) != null) {
                lineElements = line.split(":");
                if (lineElements[0].startsWith("Mooring Time")) {
                    duration = lineElements[1];
                    break;
                }
            }
            fileReader.close();
            System.out.println(shipName + "|" + dateMoored + "|" + duration);
            shipFiles.add(new Ship(shipName, dateMoored, duration));
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
    private List<Ship> cloneList(List<Ship> oldList) {
        List<Ship> newList = new ArrayList<>();
        for (Ship shipFile : oldList) {
            newList.add(shipFile);
        }
        return newList;
    }
    private void groupShipsByMonth() {
        months = new ArrayList<>();
        boolean[] indexes;
        Month newMonth;
        while (shipFiles.size() > 0) {
            Ship ship = shipFiles.get(0);
            newMonth = new Month(ship.getMonth(), ship.getYear());
            newMonth.addShip(ship);
            indexes = new boolean[shipFiles.size()];
            for (int i = 1; i < shipFiles.size(); i++) {

                if (ship.equals(shipFiles.get(i))) {
                    indexes[i] = true;
                } else indexes[i] = false;
            }
            for (int i = shipFiles.size() - 1; i > 0; i--) {
                if (indexes[i]) {
                    newMonth.addShip(shipFiles.remove(i));
                }
            }
            shipFiles.remove(0);
            months.add(newMonth);
        }
    }

    public ObservableList<Month> getObservableList(){
        return FXCollections.observableArrayList(months);
    }
}
