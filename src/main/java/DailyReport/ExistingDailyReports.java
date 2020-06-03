package DailyReport;

import java.util.Date;
import java.util.HashMap;

public class ExistingDailyReports {
    private int cellPosition;
    private Date date;
    private HashMap<String,Integer> statusCounts = new HashMap<>();

    public ExistingDailyReports(int cellPosition, Date date, HashMap<String, Integer> statusCounts) {
        this.cellPosition = cellPosition;
        this.date = date;
        this.statusCounts = statusCounts;
    }

    public int getCellPosition() {
        return cellPosition;
    }

    public Date getDate() {
        return date;
    }

    public HashMap<String, Integer> getStatusCounts() {
        return statusCounts;
    }

    public void setCellPosition(int cellPosition) {
        this.cellPosition = cellPosition;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setStatusCounts(HashMap<String, Integer> statusCounts) {
        this.statusCounts = statusCounts;
    }
}
