package ReportsManager;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ExcelReaderFinal {
    private String fileName;
    private String outputFileName;
    private String filePath;
    private int lastColumId;
    private final int statusColumnId = 0;
    private ArrayList<Incident> dailyIncidents;
    private HashMap<String,Integer> statusCounts = new HashMap<>();
    private DbConnect dbConnect = new DbConnect();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public void readExcel(Date date) {
        prepareStatus();
        boolean dateExists = false;
        int existingDatePos =0;
        getDataFromDB(date);
        try {
            FileInputStream fis = new FileInputStream(new File(filePath + fileName));
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet reportSheet = workbook.getSheet("Report");
            Row datesRow = reportSheet.getRow(1);
            int i=1;

            while(reportSheet.getRow(1).getCell(i) != null) {
                if (reportSheet.getRow(1).getCell(i).getCellType() == 0) {
                    System.out.println(sdf.format(reportSheet.getRow(1).getCell(i).getDateCellValue()) + "||" + sdf.format(date));
                    if (sdf.format(reportSheet.getRow(1).getCell(i).getDateCellValue()).equals(sdf.format(date))) {
                        dateExists = true;
                        existingDatePos = i;
                    }
                    i++;
                    lastColumId = i;
                } else break;
            }


        insertValues(prepare(dateExists,existingDatePos,lastColumId),reportSheet,date);
            if(dateExists) {
                countTotal(lastColumId - 1, reportSheet);
            } else
                countTotal(lastColumId,reportSheet);
            FileOutputStream fos = new FileOutputStream(new File(filePath + outputFileName));
            workbook.write(fos);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private void countTotal(int lastColumId, Sheet reportSheet) {
        lastColumId++;
        reportSheet.getRow(1).createCell(lastColumId);
        reportSheet.getRow(1).getCell(lastColumId).setCellValue("TOTAL");
        reportSheet.getRow(1).getCell(lastColumId).setCellStyle(reportSheet.getRow(1).getCell(lastColumId-1).getCellStyle());
        int i = 2;
        int k = 1;
        int count = 0;
        while (!reportSheet.getRow(i).getCell(0).getStringCellValue().equals("TOTAL")) {
            k = 1;
            System.out.println(i);
            while (reportSheet.getRow(1).getCell(k) != null) {
                if (reportSheet.getRow(i).getCell(k) != null)
                    count += reportSheet.getRow(i).getCell(k).getNumericCellValue();
                k++;
            }
            reportSheet.getRow(i).createCell(lastColumId);
            reportSheet.getRow(i).getCell(lastColumId).setCellValue(count);
            count = 0;
            i++;
        }
        countDayTotal(lastColumId,reportSheet);
        setFormatting(lastColumId,reportSheet);
    }
    private void countDayTotal(int lastColumId, Sheet reportSheet){
        reportSheet.getRow(8).createCell(lastColumId);
        int count = 0;
        for(int i=2;i<8;i++){
            count+=reportSheet.getRow(i).getCell(lastColumId).getNumericCellValue();
        }
        reportSheet.getRow(8).getCell(lastColumId).setCellValue(count);
    }

    private void setFormatting(int lastColumId, Sheet reportSheet){
        for(int i=1;i<9;i++){
            reportSheet.getRow(i).getCell(lastColumId).setCellStyle(reportSheet.getRow(i).getCell(lastColumId-1).getCellStyle());
        }

    }
    private int prepare(boolean isExisting, int insertCellPos, int lastColumId){
        if(isExisting)
            return insertCellPos;
        return lastColumId;
    }
    private void insertValues(int insertCellPos, Sheet reportSheet, Date date){
        int i=2;
        int count = 0;
        System.out.printf("ipos: "+ insertCellPos);
        reportSheet.getRow(1).createCell(insertCellPos);
        reportSheet.getRow(1).getCell(insertCellPos).setCellValue(sdf.format(date));
                while(reportSheet.getRow(i).getCell(0).getStringCellValue() != null && statusCounts.get(reportSheet.getRow(i).getCell(this.statusColumnId).getStringCellValue()) != null){
                    System.out.println(statusCounts.get(reportSheet.getRow(i).getCell(this.statusColumnId).getStringCellValue()));
                    count +=statusCounts.get(reportSheet.getRow(i).getCell(this.statusColumnId).getStringCellValue());
                    reportSheet.getRow(i).createCell(insertCellPos);
                    reportSheet.getRow(i).getCell(insertCellPos).setCellValue(statusCounts.get(reportSheet.getRow(i).getCell(this.statusColumnId).getStringCellValue()));
                    i++;
                    }
                    reportSheet.getRow(8).createCell(insertCellPos);
        reportSheet.getRow(8).getCell(insertCellPos).setCellValue(count);
        setFormatting(insertCellPos,reportSheet);
    }

    private void getDataFromDB(Date date){
        dailyIncidents = dbConnect.getDailyReport(sdf.format(date));
        int tempCount;
        for(int i=0;i<dailyIncidents.size();i++){
            tempCount = this.statusCounts.get(dailyIncidents.get(i).getStatus());
            this.statusCounts.replace(dailyIncidents.get(i).getStatus(),++tempCount);
        }
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
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
