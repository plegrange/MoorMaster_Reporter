package Weather;

import Energy.Month;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class ListManager {
    final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    public ObservableList<Ship> filterByDate(ObservableList<Ship> list, int startYear, int endYear, String startMonth, String endMonth) {
        List<Ship> filteredList = new ArrayList<>();
        for (Ship ship : list) {
            if (Integer.valueOf(ship.getStartYear()) == startYear) {
                if (ship.getStartMonth() >= getIndex(startMonth)) {
                    if (Integer.valueOf(ship.getStartYear()) < endYear) {
                        filteredList.add(ship);
                    } else if (Integer.valueOf(ship.getStartYear()) == endYear) {
                        if (ship.getStartMonth() <= getIndex(endMonth)) {
                            filteredList.add(ship);
                        }
                    }
                }
            } else if (Integer.valueOf(ship.getStartYear()) > startYear) {
                if (Integer.valueOf(ship.getStartYear()) < endYear) {
                    filteredList.add(ship);
                } else if (Integer.valueOf(ship.getStartYear()) == endYear) {
                    if (ship.getStartMonth() <= getIndex(endMonth)) {
                        filteredList.add(ship);
                    }
                }
            }
        }
        return FXCollections.observableArrayList(filteredList);
    }

    public ObservableList<Ship> filterByShip(ObservableList<Ship> list, String shipName) {
        List<Ship> filteredList = new ArrayList<>();
        for (Ship ship : list) {
            if (ship.getName().equals(shipName))
                filteredList.add(ship);
        }
        return FXCollections.observableArrayList(filteredList);
    }

    private int getIndex(String month) {
        for (int m = 0; m < 12; m++) {
            if (months[m] == month) {
                return m;
            }
        }
        return -1;
    }
}
