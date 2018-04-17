package Weather;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileReader {
    private List<Ship> ships;

    public FileReader(File directory) {
        ships = new ArrayList<>();
        readDirectory(directory);
    }

    private void readDirectory(File directory) {
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                readDirectory(file);
            } else {
                readFile(file);
            }
        }
    }

    private void readFile(File file) {
        try {
            if (file.getName().endsWith("combined.csv")) {
                Scanner inputStream = new Scanner(file);
                String[] entry = new String[16];
                inputStream.useDelimiter(",");
                List<String[]> entries = new ArrayList<>();
                inputStream.nextLine();
                int counter = 0;

                while (inputStream.hasNext()) {
                    entry = new String[16];
                    //System.out.println(file.getName() + " " + counter);
                    counter++;
                    for (int i = 0; i < 16; i++) {
                        if (!inputStream.hasNextLine()) break;
                        try {
                            entry[i] = removeSpaces(inputStream.next());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //System.out.println(entry[i]);
                    }
                    boolean valid = true;
                    if (!entry[0].equals("")) {
                        for (int i = 0; i < 16; i++) {
                            if (entry[i].contains("n/a"))
                                valid = false;
                        }
                        if (valid) entries.add(entry);
                    }

                    //System.out.println();
                    if (inputStream.hasNextLine()) inputStream.nextLine();
                }
                inputStream.close();

                ships.add(new Ship(entries, file.getName()));
                System.out.println(file.getName() + " read");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String removeSpaces(String input) {
        return input.replaceAll("\\s+", "");
    }

    public ObservableList<Ship> getShipsList() {
        return FXCollections.observableArrayList(this.ships);
    }
}
