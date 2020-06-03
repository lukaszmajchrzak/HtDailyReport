package DailyReport;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CreateDailyReport {
    private final int dateRowIndex = 1;
    private final int dateFirstColumnIndex = 1;
    private final int statusColumnIndex = 0;
    private final int statusFirstRowIndex = 2;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    private int lastColumnIndex;

    private DbConnect dbConnect;
    private ArrayList<Incident> dailyIncidents = new ArrayList<>();

    private String outputFileName;
    private String inputFileName;
    private String filePath;
    private HashMap<String,Integer> statusCounts = new HashMap<>();
    private ArrayList<ExistingDailyReports> existingDailyReportsArrayList = new ArrayList<>();


    public CreateDailyReport() {
        ClassPathXmlApplicationContext dbContext = new ClassPathXmlApplicationContext("DBConnector.xml");
        ConnectionReader connectionReader = dbContext.getBean("ConnectionReader", ConnectionReader.class);
        connectionReader.readConnectionSetup();
        dbConnect = new DbConnect(connectionReader);

    }

    public void setLastColumnIndex(int lastColumnIndex) {
        this.lastColumnIndex = lastColumnIndex;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void createReport(Date date){
        Workbook excelWorkbook = new ExcelFileReader(filePath,inputFileName).readExcel();
        Sheet reportSheet = excelWorkbook.getSheet("Report");
        Sheet formatSheet = excelWorkbook.getSheet("Format");
        readExistingDates(reportSheet);
        if(isDailyReportUpdate(date)){
            updateDailyReport(reportSheet,formatSheet,date);
        } else{
            newDailyReport(reportSheet,formatSheet,date);
        }
        countTotalTotal(reportSheet,formatSheet);
//        new SortDailyReports(excelWorkbook,this.dateRowIndex,this.dateFirstColumnIndex,this.statusColumnIndex,this.statusFirstRowIndex).sortDailyReports();
        new ExcelFileWriter(this.filePath,this.outputFileName).WriteFile(excelWorkbook);

    }

    public ArrayList<ExistingDailyReports> readExistingDates(Sheet reportSheet){

        Row dateRow = reportSheet.getRow(this.dateRowIndex);
        int i = dateFirstColumnIndex;
        clearStatuses();

        System.out.println(dateRow.getCell(i).getDateCellValue());
        while(dateRow.getCell(i) != null && dateRow.getCell(i).getCellType() == 0){
            for(int v = dateRowIndex +1; v<7; v++){
                if(reportSheet.getRow(v).getCell(i) != null){
                    statusCounts.replace(reportSheet.getRow(v).getCell(statusColumnIndex).getStringCellValue(),(int)(reportSheet.getRow(v).getCell(i).getNumericCellValue()));
                }
            }
//            System.out.println("i: ["+i+"]; date: [" + sdf.format(dateRow.getCell(i).getDateCellValue())+"]; statuscount: ");
//            System.out.println(statusCounts);
            existingDailyReportsArrayList.add(new ExistingDailyReports(i,dateRow.getCell(i).getDateCellValue(),statusCounts));
            clearStatuses();
            i++;
        }
        setLastColumnIndex(i);
        clearStatuses();
        return new ArrayList<>(existingDailyReportsArrayList);
    }
    private void newDailyReport(Sheet reportSheet, Sheet formatSheet, Date date){
        getDataFromDB(date);
        if(reportSheet.getRow(this.dateRowIndex).getCell(this.lastColumnIndex) == null)
            reportSheet.getRow(this.dateRowIndex).createCell(this.lastColumnIndex);
        reportSheet.getRow(this.dateRowIndex).getCell(this.lastColumnIndex).setCellValue(date);
        reportSheet.getRow(this.dateRowIndex).getCell(this.lastColumnIndex).setCellType(0);
        insertData(reportSheet,formatSheet, this.lastColumnIndex,this.statusCounts);

    }

    public void insertData(Sheet reportSheet, Sheet formatSheet, int insertPos, HashMap<String,Integer> statusCounts){
        for(int i = this.dateRowIndex+1;i<8;i++){
            if(reportSheet.getRow(i).getCell(insertPos) == null)
                reportSheet.getRow(i).createCell(insertPos);
            reportSheet.getRow(i).getCell(insertPos).setCellValue(statusCounts.get(reportSheet.getRow(i).getCell(this.statusColumnIndex).getStringCellValue()));
        }
        countDailyTotal(reportSheet,insertPos);
        setFormatting(reportSheet,formatSheet,insertPos,false);
    }

    private void updateDailyReport(Sheet reportSheet, Sheet formatSheet, Date date){
        getDataFromDB(date);
        for(int i=0;i<this.existingDailyReportsArrayList.size();i++){
            if(this.existingDailyReportsArrayList.get(i).getDate() == date){
                this.statusCounts = this.existingDailyReportsArrayList.get(i).getStatusCounts();
                insertData(reportSheet,formatSheet,this.existingDailyReportsArrayList.get(i).getCellPosition(),this.statusCounts);
            }
        }
    }

    private void countTotalTotal(Sheet reportSheet, Sheet formatSheet){
        int totalColumnIndex = findTotalPosition(reportSheet);
        int r = 2;
        int totalCount;
        while(!reportSheet.getRow(r).getCell(0).getStringCellValue().equals("TOTAL")) {
            totalCount = 0;
            for (int i = this.statusColumnIndex + 1; i < totalColumnIndex; i++) {
                if(reportSheet.getRow(r).getCell(i) != null){
                    if(reportSheet.getRow(r).getCell(i).getCellType() == 0){
                        totalCount+=reportSheet.getRow(r).getCell(i).getNumericCellValue();
                    }
                }
            }
            if(reportSheet.getRow(r).getCell(totalColumnIndex) == null)
                reportSheet.getRow(r).createCell(totalColumnIndex);
            reportSheet.getRow(r).getCell(totalColumnIndex).setCellValue(totalCount);
            r++;
        }
            countDailyTotal(reportSheet,totalColumnIndex);
        setFormatting(reportSheet,formatSheet, totalColumnIndex,true);
    }

    private void setFormatting(Sheet reportSheet, Sheet formatSheet, int columnIndex, boolean isTotal){
        for(int i=this.dateRowIndex;i<10;i++){
            if(reportSheet.getRow(i).getCell(columnIndex) == null)
                reportSheet.getRow(i).createCell(columnIndex);
            reportSheet.getRow(i).getCell(columnIndex).setCellStyle(formatSheet.getRow(i-1).getCell(0).getCellStyle());
        }
        if(isTotal)
            reportSheet.getRow(this.dateRowIndex).getCell(columnIndex).setCellType(1);
    }

    private int findTotalPosition(Sheet reportSheet){
        int i=1;
        boolean isFound = false;
        int totalColumnIndex = 0;
        do {
            if(reportSheet.getRow(this.dateRowIndex).getCell(i).getCellType() != 0 && reportSheet.getRow(this.dateRowIndex).getCell(i).getStringCellValue().equals("TOTAL")) {
                totalColumnIndex = i;
//                System.out.println("TOTAL found: [" + i + "]");
                isFound = true;
            }
            i++;
        }
        while(reportSheet.getRow(this.dateRowIndex).getCell(i) != null);
        if(!isFound){
         totalColumnIndex = i;
            reportSheet.getRow(this.dateRowIndex).createCell(totalColumnIndex);
            reportSheet.getRow(this.dateRowIndex).getCell(totalColumnIndex).setCellValue("TOTAL");
            reportSheet.getRow(this.dateRowIndex).getCell(totalColumnIndex).setCellType(1);
        } else {
            if (totalColumnIndex > i + 1){

            }
        }
        return totalColumnIndex;
    }

    private void countDailyTotal(Sheet reportSheet, int dailyReportColumnIndex){
        int totalCount = 0;
        for(int i=this.dateRowIndex+1;i<8;i++){
            if(reportSheet.getRow(i).getCell(dailyReportColumnIndex) != null){
                totalCount += reportSheet.getRow(i).getCell(dailyReportColumnIndex).getNumericCellValue();
            }
        }
        if(reportSheet.getRow(8).getCell(dailyReportColumnIndex) == null)
            reportSheet.getRow(8).createCell(dailyReportColumnIndex);
        reportSheet.getRow(8).getCell(dailyReportColumnIndex).setCellValue(totalCount);
    }

    private void getDataFromDB(Date date){
        clearStatuses();
        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
        dailyIncidents = dbConnect.getDailyReport(dbFormat.format(date));
        int tempCount;
        for(int i=0;i<dailyIncidents.size();i++){
            System.out.println(this.statusCounts.get(dailyIncidents.get(i).getStatus()));
            tempCount = this.statusCounts.get(dailyIncidents.get(i).getStatus());
            tempCount++;
            this.statusCounts.replace(dailyIncidents.get(i).getStatus(),tempCount);
            System.out.println(statusCounts.get(dailyIncidents.get(i).getStatus()));
        }


    }

    private boolean isDailyReportUpdate(Date date){
//        System.out.println("ex size: " + existingDailyReportsArrayList.size());
        for(int i=0;i<existingDailyReportsArrayList.size();i++) {
            if (existingDailyReportsArrayList.get(i).getDate().getDay() == date.getDay() &&
                    existingDailyReportsArrayList.get(i).getDate().getMonth() == date.getMonth() &&
                    existingDailyReportsArrayList.get(i).getDate().getYear() == date.getYear()) {
                System.out.println("DMY : TRUE");
                return true;
            }
        }
        return false;
    }

    private void clearStatuses(){
        statusCounts.clear();
        prepareStatus();
    }

    private void prepareStatus(){
        statusCounts.put("Closed",0);
        statusCounts.put("Resolved",0);
        statusCounts.put("Waiting for Customer",0);
        statusCounts.put("Waiting for 3rd Party",0);
        statusCounts.put("Active",0);
        statusCounts.put("Logged",0);
    }
}
