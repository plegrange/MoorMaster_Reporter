package Energy;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class TableManager {
    private String[] monthIndex = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    public ObservableList<Month> filter(ObservableList<Month> list, int startYear, int endYear, String startMonth, String endMonth) {
        List<Month> filteredList = new ArrayList<>();
        for (Month month : list) {
            if (Integer.valueOf(month.getYear()) > startYear) {
                if (Integer.valueOf(month.getYear()) < endYear) {
                    filteredList.add(month);
                } else if (Integer.valueOf(month.getYear()) == endYear) {
                    if (getIndex(monthIndex, month.getMonth()) <= getIndex(monthIndex, endMonth)) {
                        filteredList.add(month);
                    }
                }
            } else if (Integer.valueOf(month.getYear()) == startYear) {
                if (getIndex(monthIndex, month.getMonth()) >= getIndex(monthIndex, startMonth)) {
                    filteredList.add(month);
                }
            }
        }
        return FXCollections.observableArrayList(filteredList);
    }

    private int getIndex(String[] months, String month) {
        for (int m = 0; m < 12; m++) {
            if (months[m] == month) {
                return m;
            }
        }
        return -1;
    }
}
