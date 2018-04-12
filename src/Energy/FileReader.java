package Energy;

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

            //filter files
            readFile(file);
        }
        temp = cloneList(shipFiles);
        groupShipsByMonth();
        shipFiles = cloneList(temp);
        filtered = months;
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
}
