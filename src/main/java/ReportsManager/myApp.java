package ReportsManager;


import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class myApp {
    public static void main(String[] args) {

        MyLogger logger = new MyLogger();
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationConfig.xml");
        ExcelReaderFinal excelReader = context.getBean("ExcelReaderFinal", ExcelReaderFinal.class);




        try {
            if(args.length>0){
                for(int i=0;i<args.length;i++){
                    excelReader.readExcel(df.parse(args[i]));
                }
            } else{
                excelReader.readExcel(new Date());
                logger.sendLog("{DailyReport} : " + new Date() + " processed!");
            }
        } catch(ParseException e){
            logger.sendLog("{Daily Report} : "+ e.getMessage());
        }
    }
}
