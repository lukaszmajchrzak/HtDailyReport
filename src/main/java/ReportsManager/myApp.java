package ReportsManager;


import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class myApp {
    public static void main(String[] args) {

        MyLogger logger = new MyLogger();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationConfig.xml");
        CreateDailyReport createDailyReport = context.getBean("CreateDailyReport", CreateDailyReport.class);




        try {
            if(args.length>0){
                for(int i=0;i<args.length;i++){
                    System.out.println(args[i]);
                    Date date = sdf.parse(args[i]);
                    createDailyReport.createReport(date);
                }
            } else{
                createDailyReport.createReport(new Date());
                logger.sendLog("{DailyReport} : " + new Date() + " processed!");
            }
        } catch(ParseException e){
            System.out.println("did nut wurk");
            e.printStackTrace();
            logger.sendLog("{Daily Report} : "+ e.getMessage());
        }
    }
}
