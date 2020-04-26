package ReportsManager;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelFileWriter {
    private String filePath;
    private String outputFileName;

    public ExcelFileWriter(String filePath, String outputFileName) {
        this.filePath = filePath;
        this.outputFileName = outputFileName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public void WriteFile(Workbook workbook){
        try {
            File outputFile = new File(filePath + outputFileName);
            FileOutputStream fos = new FileOutputStream(outputFile);

            workbook.write(fos);
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
