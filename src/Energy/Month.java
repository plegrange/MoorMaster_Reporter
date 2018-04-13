package Energy;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Month {
    private SimpleStringProperty month;
    private SimpleStringProperty year;
    private SimpleStringProperty combokWh, TPTkWh, AMSkWh, comboCost, TPTCost, AMSCost, maxDemand, PoNCost, PoNkWh, berthMonthTotal;
    public List<Ship> ships;
    private double rate;

    public Month(String month, String year) {
        rate = -1.0;
        this.month = new SimpleStringProperty(month);
        this.year = new SimpleStringProperty(year);
        this.combokWh = new SimpleStringProperty("0.0");
        this.TPTkWh = new SimpleStringProperty("0.0");

        this.AMSkWh = new SimpleStringProperty("0.0");
        this.comboCost = new SimpleStringProperty("0.0");
        this.TPTCost = new SimpleStringProperty("0.0");
        this.AMSCost = new SimpleStringProperty("0.0");
        this.maxDemand = new SimpleStringProperty("0.0");
        this.PoNCost = new SimpleStringProperty("0.0");
        this.berthMonthTotal = new SimpleStringProperty("0.0");
        this.PoNkWh = new SimpleStringProperty("0.0");
        ships = new ArrayList<>();
    }

    private double getRate(double winterRate, double summerRate) {
        switch (month.get()) {
            case "May":
            case "June":
            case "July":
            case "August":
                return Double.valueOf(winterRate) / 100.0;
            default:
                return Double.valueOf(summerRate) / 100.0;
        }
    }

    public void update(double winterRate, double summerRate) {
        if (rate == -1.0) {
            rate = getRate(winterRate, summerRate);
        }
        comboCost.set(roundValue(String.valueOf(Double.valueOf(combokWh.get()) * rate)));
        TPTCost.set(roundValue(String.valueOf(Double.valueOf(TPTkWh.get()) * rate)));
        AMSCost.set(roundValue(String.valueOf(Double.valueOf(AMSkWh.get()) * rate)));
        PoNCost.set(roundValue(String.valueOf(
                Double.valueOf(comboCost.get())
                        - Double.valueOf(TPTCost.get())
                        - Double.valueOf(AMSCost.get())
                        - Double.valueOf(maxDemand.get()))));
        PoNkWh.set(roundValue(String.valueOf(Double.valueOf(PoNCost.get()) / rate)));
    }

    public List<String> getAttributes() {
        List<String> attributes = new ArrayList<>();
        Collections.addAll(attributes,
                month.get() + "_" + year.get(),
                combokWh.get(),
                comboCost.get(),
                TPTkWh.get(),
                TPTCost.get(),
                AMSkWh.get(),
                AMSCost.get(),
                maxDemand.get(),
                berthMonthTotal.get(),
                PoNkWh.get(),
                PoNCost.get());
        return attributes;
    }

    int hours, minutes, seconds;

    public void calculateTotalMonthHours() {
        hours = 0;
        minutes = 0;
        seconds = 0;

        for (Ship shipFile : ships)
            addShipToTotal(shipFile);

        minutes += seconds / 60;
        seconds = seconds % 60;
        hours += minutes / 60;
        minutes = minutes % 60;

        double secondsInMinutes = seconds * 1.0 / 60.0;
        double tempMinutes = minutes * 1.0 + secondsInMinutes;
        double minutesInHours = tempMinutes / 60.0;
        double tempHours = hours * 1.0 + minutesInHours;
        berthMonthTotal.set(String.valueOf(tempHours));
    }

    private void addShipToTotal(Ship shipFile) {
        hours += shipFile.getDurationHours();
        minutes += shipFile.getDurationMinutes();
        seconds += shipFile.getDurationSeconds();
    }

    public void addShip(Ship newShip) {
        ships.add(newShip);
    }

    public String getMonth() {
        return month.get();
    }

    public SimpleStringProperty monthProperty() {
        return month;
    }

    public String getYear() {
        return year.get();
    }

    public SimpleStringProperty yearProperty() {
        return year;
    }

    public String getCombokWh() {
        return combokWh.get();
    }

    public SimpleStringProperty combokWhProperty() {
        return combokWh;
    }

    public void setCombokWh(String combokWh) {
        this.combokWh.set(roundValue(combokWh));
    }

    public String getTPTkWh() {
        return TPTkWh.get();
    }

    public SimpleStringProperty TPTkWhProperty() {
        return TPTkWh;
    }

    public void setTPTkWh(String TPTkWh) {
        this.TPTkWh.set(roundValue(TPTkWh));
    }

    private String roundValue(String value) {
        double roundedValue = Math.round(Double.valueOf(value) * 100.0) / 100.0;
        return String.valueOf(roundedValue);
    }

    public String getAMSkWh() {
        return AMSkWh.get();
    }

    public SimpleStringProperty AMSkWhProperty() {
        return AMSkWh;
    }

    public void setAMSkWh(String AMSkWh) {
        this.AMSkWh.set(roundValue(AMSkWh));
    }

    public String getComboCost() {
        return comboCost.get();
    }

    public SimpleStringProperty comboCostProperty() {
        return comboCost;
    }

    public void setComboCost(String comboCost) {
        this.comboCost.set(comboCost);
    }

    public String getTPTCost() {
        return TPTCost.get();
    }

    public SimpleStringProperty TPTCostProperty() {
        return TPTCost;
    }

    public void setTPTCost(String TPTCost) {
        this.TPTCost.set(TPTCost);
    }

    public String getAMSCost() {
        return AMSCost.get();
    }

    public SimpleStringProperty AMSCostProperty() {
        return AMSCost;
    }

    public void setAMSCost(String AMSCost) {
        this.AMSCost.set(AMSCost);
    }

    public String getMaxDemand() {
        return maxDemand.get();
    }

    public SimpleStringProperty maxDemandProperty() {
        return maxDemand;
    }

    public void setMaxDemand(String maxDemand) {
        this.maxDemand.set(roundValue(maxDemand));
    }

    public String getPoNCost() {
        return PoNCost.get();
    }

    public SimpleStringProperty poNCostProperty() {
        return PoNCost;
    }

    public void setPoNCost(String poNCost) {
        this.PoNCost.set(poNCost);
    }

    public String getPoNkWh() {
        return PoNkWh.get();
    }

    public SimpleStringProperty poNkWhProperty() {
        return PoNkWh;
    }

    public void setPoNkWh(String poNkWh) {
        this.PoNkWh.set(poNkWh);
    }

    public String getBerthMonthTotal() {
        return berthMonthTotal.get();
    }

    public SimpleStringProperty berthMonthTotalProperty() {
        return berthMonthTotal;
    }

    public void setBerthMonthTotal(String berthMonthTotal) {
        this.berthMonthTotal.set(berthMonthTotal);
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
