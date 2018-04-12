package Energy;

public class Ship {

    public String shipName;
    private String dateMoored;
    public int durationHours, durationMinutes, durationSeconds;
    public String month, year;

    public Ship(String shipName, String dateString, String durationString) {
        StringBuilder sb = new StringBuilder(shipName);
        sb.deleteCharAt(0);
        this.shipName = sb.toString();

        sb = new StringBuilder(dateString);
        sb.deleteCharAt(0);
        dateString = sb.toString();
        String[] splitLine = dateString.split(" ");
        dateString = splitLine[0];
        splitLine = dateString.split("-");
        this.dateMoored = splitLine[1] + "/" + splitLine[2] + "/" + splitLine[0];
        doDates(splitLine[1], splitLine[0]);
        sb = new StringBuilder(durationString);
        sb.deleteCharAt(0);
        durationString = sb.toString();
        splitLine = durationString.split("h");
        durationHours = Integer.valueOf(splitLine[0]);
        durationString = splitLine[1];
        sb = new StringBuilder(durationString);
        sb.deleteCharAt(0);
        durationString = sb.toString();
        splitLine = durationString.split("m");
        durationMinutes = Integer.valueOf(splitLine[0]);
        durationString = splitLine[1];
        sb = new StringBuilder(durationString);
        sb.deleteCharAt(0);
        sb.deleteCharAt(sb.length() - 1);
        durationString = sb.toString();
        durationSeconds = Integer.valueOf(durationString);
    }

    private void doDates(String month, String year) {
        String[] monthIndex = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        this.year = year;
        this.month = monthIndex[Integer.valueOf(month) - 1];
    }


    public boolean equals(Ship other) {
        if (this.year.equals(other.year) && this.month.equals(other.month))
            return true;
        else return false;
    }

    public String getDateMoored() {
        return dateMoored;
    }
}
