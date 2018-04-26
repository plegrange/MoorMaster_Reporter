package Weather;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Ship {
    private String name, startDate, endDate, startTime, endTime;
    private double windSpeedStart, windSpeedEnd, windSpeedAverage;
    private double hsLongStart, tpLongStart, hsSwellStart, tpSwellStart,
            hsLongEnd, tpLongEnd, hsSwellEnd, tpSwellEnd,
            hsLongAverage, tpLongAverage, hsSwellAverage, tpSwellAverage;

    private List<Double> windSpeeds, windDirections, hsLong, tpLong, hsSwell, tpSwell;
    private List<Date> timeStamps;

    public Ship(List<String[]> entries, String name) {
        String data = name.split("_")[3];
        this.name = data.replaceAll(".combined.csv", "");
        windSpeeds = new ArrayList<>();
        windDirections = new ArrayList<>();
        hsLong = new ArrayList<>();
        tpLong = new ArrayList<>();
        hsSwell = new ArrayList<>();
        tpSwell = new ArrayList<>();
        timeStamps = new ArrayList<>();
        calculate(entries);
    }

    public boolean isBefore(Ship other) {
        if (this.timeStamps.get(0).after(other.timeStamps.get(0)))
            return false;
        return true;
    }

    public Object[][][] getWindData() {
        Object[][][] windData = new Object[1][windSpeeds.size()][3];
        for (int i = 0; i < timeStamps.size(); i++) {
            windData[0][i][0] = timeStamps.get(i);
            windData[0][i][1] = windDirections.get(i);
            windData[0][i][2] = windSpeeds.get(i);
        }
        return windData;
    }

    public List<Date> getTimeStamps() {
        return timeStamps;
    }

    public List<Double> getWindDirections() {
        return windDirections;
    }

    public List<Double> getWindSpeeds() {
        return windSpeeds;
    }

    public List<Double> getHsLong() {
        return hsLong;
    }

    public List<Double> getTpLong() {
        return tpLong;
    }

    public List<Double> getHsSwell() {
        return hsSwell;
    }

    public List<Double> getTpSwell() {
        return tpSwell;
    }

    private void calculate(List<String[]> entries) {
        String[] startEntry = entries.get(0);
        String[] endEntry = entries.get(entries.size() - 1);
        startDate = startEntry[0];
        startTime = startEntry[1];
        endDate = endEntry[0];
        endTime = endEntry[1];

        windSpeedStart = convertToBeaufort(Math.round(Float.valueOf(startEntry[2])));
        windSpeedEnd = convertToBeaufort(Math.round(Float.valueOf(endEntry[2])));

        //windSpeedAverage = getAverage(entries, 2);

        hsLongStart = (Double.valueOf(startEntry[6]) + Double.valueOf(startEntry[12])) / 2.0;
        hsLongEnd = (Double.valueOf(endEntry[6]) + Double.valueOf(endEntry[12])) / 2.0;
        //hsLongAverage = (getAverage(entries, 6) + getAverage(entries, 12)) / 2.0;

        tpLongStart = (Double.valueOf(startEntry[7]) + Double.valueOf(startEntry[13])) / 2.0;
        tpLongEnd = (Double.valueOf(endEntry[7]) + Double.valueOf(endEntry[13])) / 2.0;
        //tpLongAverage = (getAverage(entries, 7) + getAverage(entries, 13)) / 2.0;

        hsSwellStart = (Double.valueOf(startEntry[8]) + Double.valueOf(startEntry[14])) / 2.0;
        hsSwellEnd = (Double.valueOf(endEntry[8]) + Double.valueOf(endEntry[14])) / 2.0;
        //hsSwellAverage = (getAverage(entries, 8) + getAverage(entries, 14)) / 2.0;

        tpSwellStart = (Double.valueOf(startEntry[9]) + Double.valueOf(startEntry[15])) / 2.0;
        tpSwellEnd = (Double.valueOf(endEntry[9]) + Double.valueOf(endEntry[15])) / 2.0;
        //tpSwellAverage = (getAverage(entries, 9) + getAverage(entries, 15)) / 2.0;

        calculateAverages(entries);
    }

    private double convertToBeaufort(int knots) {
        double beaufort;
        if (knots == 1)
            beaufort = 0;
        else if (knots > 1 && knots <= 3) {
            beaufort = 1;
        } else if (knots >= 4 && knots <= 6)
            beaufort = 2;
        else if (knots >= 7 && knots <= 10)
            beaufort = 3;
        else if (knots >= 11 && knots <= 15)
            beaufort = 4;
        else if (knots >= 16 && knots <= 21)
            beaufort = 5;
        else if (knots >= 22 && knots <= 27)
            beaufort = 6;
        else if (knots >= 28 && knots <= 33)
            beaufort = 7;
        else if (knots >= 34 && knots <= 40)
            beaufort = 8;
        else if (knots >= 41 && knots <= 47)
            beaufort = 9;
        else if (knots >= 48 && knots <= 55)
            beaufort = 10;
        else if (knots >= 56 && knots <= 63)
            beaufort = 11;
        else if (knots >= 64)
            beaufort = 12;
        else beaufort = 0;
        return beaufort;
    }

    private void calculateAverages(List<String[]> entries) {
        double totalWind = 0.0, totalHSLong = 0.0, totalTPLong = 0.0, totalHSSwell = 0.0, totalTPSwell = 0.0;
        String[] splitTime;
        int hour = -1;
        for (String[] entry : entries) {

            totalWind = totalWind + convertToBeaufort(Math.round(Float.valueOf(entry[2])));
            totalHSLong = totalHSLong + (Double.valueOf(entry[6]) + Double.valueOf(entry[12])) / 2.0;
            totalTPLong = totalTPLong + (Double.valueOf(entry[7]) + Double.valueOf(entry[13])) / 2.0;
            totalHSSwell = totalHSSwell + (Double.valueOf(entry[8]) + Double.valueOf(entry[14])) / 2.0;
            totalTPSwell = totalTPSwell + (Double.valueOf(entry[9])+ Double.valueOf(entry[15])) / 2.0;

            splitTime = entry[1].split(":");
            if (Integer.valueOf(splitTime[0]) != hour) {
                hour = Integer.valueOf(splitTime[0]);
                windSpeeds.add(convertToBeaufort(Math.round(Float.valueOf(entry[2]))));
                windDirections.add(round((Double.valueOf(entry[3]) / 360.0) * 12.0));
                hsLong.add((Double.valueOf(entry[6]) + Double.valueOf(entry[12])) / 2.0);
                tpLong.add((Double.valueOf(entry[7]) + Double.valueOf(entry[13])) / 2.0);
                hsSwell.add((Double.valueOf(entry[8]) + Double.valueOf(entry[14])) / 2.0);
                tpSwell.add((Double.valueOf(entry[9]) + Double.valueOf(entry[15])) / 2.0);

                String[] date = entry[0].split("/");
                //String[] timeOfDay = entry[1].split(" ");
                String[] time = entry[1].split(":");

                int addPM = 0;
                if (time[2].endsWith("PM")) {
                    if (Integer.valueOf(time[0]) < 12)
                        addPM = 12;
                }if (time[2].endsWith("AM")) {
                    if (Integer.valueOf(time[0]) >= 12)
                        addPM = -12;
                }
                timeStamps.add(new Date(Integer.valueOf(date[2]),
                        Integer.valueOf(date[1]) - 1,
                        Integer.valueOf(date[0]),
                        Integer.valueOf(time[0]) + addPM,
                        Integer.valueOf(time[1]),
                        Integer.valueOf(time[2].replaceAll("PM", "").replaceAll("AM", ""))));

                //totalTPSwell = totalTPSwell + (Double.valueOf(entry[9]) + Double.valueOf(entry[15])) / 2.0;
            }
        }
        windSpeedAverage = totalWind / entries.size();
        hsLongAverage = totalHSLong / entries.size();
        tpLongAverage = totalTPLong / entries.size();
        hsSwellAverage = totalHSSwell / entries.size();
        tpSwellAverage = totalTPSwell / entries.size();
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }

    private double getAverage(List<String[]> entries, int index) {
        int counter = 0;
        double total = 0.0;
        for (String[] entry : entries) {
            counter++;
            try {

                total = Math.round((total + Double.valueOf(entry[index])) * 100.0) / 100.0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return total / (counter * 1.0);
    }

    public int getStartYear() {
        String date[] = startDate.split("/");
        return Integer.valueOf(date[2]);
    }

    public int getStartDay() {
        String date[] = startDate.split("/");
        return Integer.valueOf(date[0]) - 1;
    }

    public int getEndYear() {
        String date[] = endDate.split("/");
        return Integer.valueOf(date[2]);
    }

    public int getEndMonth() {
        String date[] = endDate.split("/");
        return Integer.valueOf(date[1]) - 1;
    }

    public int getStartMonth() {
        String date[] = startDate.split("/");
        return Integer.valueOf(date[1]) - 1;
    }

    public String getName() {
        return name;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public double getWindSpeedAverage() {
        return windSpeedAverage;
    }

    public double getWindSpeedEnd() {
        return windSpeedEnd;
    }

    public double getWindSpeedStart() {
        return windSpeedStart;
    }

    public double getHsLongStart() {
        return hsLongStart;
    }

    public double getTpLongStart() {
        return tpLongStart;
    }

    public double getHsSwellStart() {
        return hsSwellStart;
    }

    public double getTpSwellStart() {
        return tpSwellStart;
    }

    public double getTpSwellAverage() {
        return tpSwellAverage;
    }

    public double getHsSwellAverage() {
        return hsSwellAverage;
    }

    public double getTpSwellEnd() {
        return tpSwellEnd;
    }

    public double getHsSwellEnd() {
        return hsSwellEnd;
    }

    public double getTpLongEnd() {
        return tpLongEnd;
    }

    public double getTpLongAverage() {
        return tpLongAverage;
    }

    public double getHsLongAverage() {
        return hsLongAverage;
    }

    public double getHsLongEnd() {
        return hsLongEnd;
    }
}
