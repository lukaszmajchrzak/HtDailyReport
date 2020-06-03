package DailyReport;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;

public class SortDailyReports {
    private Workbook excelWorkbook;
    private final int dateRowIndex;
    private final int dateFirstColumnIndex;
    private final int statusColumnIndex ;
    private final int statusFirstRowIndex;
    private ArrayList<ExistingDailyReports> existingDailyReportsArrayList;

    public SortDailyReports(Workbook excelWorkbook, int dateRowIndex, int dateFirstColumnIndex, int statusColumnIndex, int statusFirstRowIndex) {
        this.excelWorkbook = excelWorkbook;
        this.dateRowIndex = dateRowIndex;
        this.dateFirstColumnIndex = dateFirstColumnIndex;
        this.statusColumnIndex = statusColumnIndex;
        this.statusFirstRowIndex = statusFirstRowIndex;
    }

    public void sortDailyReports(){
        Sheet reportSheet = excelWorkbook.getSheet("Report");
        Sheet formatSheet = excelWorkbook.getSheet("Format");
        existingDailyReportsArrayList = new CreateDailyReport().readExistingDates(reportSheet);
        if(sortArray())
            insertSorted(reportSheet,formatSheet);
    }
    private boolean sortArray(){
        int tempPos;
        boolean isChanged = false;
        for(int i=0;i<existingDailyReportsArrayList.size();i++) {
            for (int j = 0; j < existingDailyReportsArrayList.size() - i - 1; j++) {
//                System.out.println("comparing: ["+ existingDailyReportsArrayList.get(j).getDate()+ "] : [" + existingDailyReportsArrayList.get(j+1).getDate());
//                System.out.println("comparing: ["+ existingDailyReportsArrayList.get(j).getCellPosition()+ "] : [" + existingDailyReportsArrayList.get(j+1).getCellPosition());
//                System.out.println(existingDailyReportsArrayList.get(j).getDate().after(existingDailyReportsArrayList.get(j + 1).getDate()) && existingDailyReportsArrayList.get(j).getCellPosition() < existingDailyReportsArrayList.get(j + 1).getCellPosition());
                if (existingDailyReportsArrayList.get(j).getDate().after(existingDailyReportsArrayList.get(j + 1).getDate())
                        && existingDailyReportsArrayList.get(j).getCellPosition() < existingDailyReportsArrayList.get(j + 1).getCellPosition()) {

                    tempPos = existingDailyReportsArrayList.get(j).getCellPosition();
                    existingDailyReportsArrayList.get(j).setCellPosition(existingDailyReportsArrayList.get(j + 1).getCellPosition());
                    existingDailyReportsArrayList.get(j + 1).setCellPosition(tempPos);
                    isChanged = true;
                }
            }
        }
            return isChanged;
    }
    private void insertSorted(Sheet reportSheet, Sheet formatSheet){
        CreateDailyReport crd = new CreateDailyReport();
        for(int i=0;i<existingDailyReportsArrayList.size();i++){
            reportSheet.getRow(this.dateRowIndex).getCell(existingDailyReportsArrayList.get(i).getCellPosition()).setCellValue(existingDailyReportsArrayList.get(i).getDate());
            crd.insertData(reportSheet,formatSheet,existingDailyReportsArrayList.get(i).getCellPosition(),existingDailyReportsArrayList.get(i).getStatusCounts());
        }
    }



}

