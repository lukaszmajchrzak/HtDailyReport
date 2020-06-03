package DailyReport;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ExcelFileReader {
    private String fileName;
    private String filePath;
    private MyLogger logger = new MyLogger();

    public ExcelFileReader(String filePath, String fileName) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Workbook readExcel(){
        try{
            File excelFile = new File(this.filePath + this.fileName);
            FileInputStream fis = new FileInputStream(excelFile);
            Workbook excelWorkbook = new XSSFWorkbook(fis);
            return excelWorkbook;

        } catch(IOException e){
            logger.sendLog("{DailyHeatReport} FAIL : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
