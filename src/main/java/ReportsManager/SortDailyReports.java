package ReportsManager;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;

public class SortDailyReports {
    private Workbook excelWorkbook;
    private final int dateRowIndex;
    private final int dateFirstColumnIndex;
    private final int statusColumnIndex ;
    private final int statusFirstRowIndex;
    private ArrayList<ExistingDailyReports> existingDailyReportsArrayList = new ArrayList<>();

    public SortDailyReports(Workbook excelWorkbook, int dateRowIndex, int dateFirstColumnIndex, int statusColumnIndex, int statusFirstRowIndex) {
        this.excelWorkbook = excelWorkbook;
        this.dateRowIndex = dateRowIndex;
        this.dateFirstColumnIndex = dateFirstColumnIndex;
        this.statusColumnIndex = statusColumnIndex;
        this.statusFirstRowIndex = statusFirstRowIndex;
    }

    public void sortDailyReports(){
        Sheet reportSheet = excelWorkbook.getSheet("Report");
        existingDailyReportsArrayList = new CreateDailyReport().readExistingDates(reportSheet);

    }




}

