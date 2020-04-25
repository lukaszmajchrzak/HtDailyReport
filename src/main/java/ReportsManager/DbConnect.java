package ReportsManager;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class DbConnect {
    protected Connection con;
    private MyLogger logger = new MyLogger();

    /**
     * <p> Method connects to database using connection string typed in connectionString.xml file
     * <p>
     * to read the file method runs</p>
     */

    public void connect() {
        try {
//            this.con = DriverManager.getConnection(conReader.getAddress(), conReader.getUsername(), conReader.getPassword());
              this.con =DriverManager.getConnection("jdbc:mysql://10.13.135.10:3306/db?serverTimezone=UTC", "LukMaj", "LukMaj123$%^");
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
    }
    public ArrayList<Incident> getDailyReport(String date){
        ArrayList<Incident> dailyIncidentList = new ArrayList<>();

        try {
            this.connect();
            Statement stmt = con.createStatement();
            System.out.println("SELECT `Incident ID`, `Status`,`CI Name` FROM HEAT.HEATDATA Where `Created On` = '"+ date + "' AND `Team` = 'IT Applications Operations Support'");
            ResultSet rs = stmt.executeQuery("SELECT `Incident ID`, `Status`,`CI Name` FROM HEAT.HEATDATA Where `Created On` = '"+ date + "' AND `Team` = 'IT Applications Operations Support'");
            if(rs.next() != false){
                while(rs.next()){
                    dailyIncidentList.add(new Incident(rs.getInt(1),rs.getString(3),rs.getString(2)));
                }
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return dailyIncidentList;
    }
    public void close(){
        try{
            con.close();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }
}
