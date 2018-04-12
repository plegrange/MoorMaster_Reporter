package Energy;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.List;

public class Month {
    private SimpleStringProperty month;
    private SimpleIntegerProperty year;
    private SimpleDoubleProperty combokWh, TPTkWh, AMSkWh, comboCost, TPTCost, AMSCost, maxDemand, PoNCost, PoNkWh, berthMonthTotal;
    public List<Ship> ships;

    public Month(String month, int year) {
        this.month = new SimpleStringProperty(month);
        this.year = new SimpleIntegerProperty(year);
        this.combokWh = new SimpleDoubleProperty(0.0);
        this.TPTkWh = new SimpleDoubleProperty(0.0);

        this.AMSkWh = new SimpleDoubleProperty(0.0);
        this.comboCost = new SimpleDoubleProperty(0.0);
        this.TPTCost = new SimpleDoubleProperty(0.0);
        this.AMSCost = new SimpleDoubleProperty(0.0);
        this.maxDemand = new SimpleDoubleProperty(0.0);
        this.PoNCost = new SimpleDoubleProperty(0.0);
        this.berthMonthTotal = new SimpleDoubleProperty(0.0);
        this.PoNkWh = new SimpleDoubleProperty(0.0);
        ships = new ArrayList<>();
    }
}
