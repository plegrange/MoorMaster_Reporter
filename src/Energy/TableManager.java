package Energy;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class TableManager {
    final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    public ObservableList<Month> filter(ObservableList<Month> list, int startYear, int endYear, String startMonth, String endMonth) {
        List<Month> filteredList = new ArrayList<>();
        for (Month month : list) {
            if (Integer.valueOf(month.getYear()) == startYear) {
                if(getIndex(month.getMonth())>= getIndex(startMonth)){
                    if()
                }
            }
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
