package DailyReport;

import java.sql.*;
import java.util.ArrayList;

public class DbConnect {
    protected Connection con;
    private MyLogger logger = new MyLogger();
    private ConnectionReader connectionReader;

    public DbConnect(ConnectionReader connectionReader) {
        this.connectionReader = connectionReader;
    }

    /**
     * <p> Method connects to database using connection string typed in connectionString.xml file
     * <p>
     * to read the file method runs</p>
     */

    public void connect() {
        try {

            String connectionString = connectionReader.getConnectionURL();
            String username = connectionReader.getUsername();
            String password = connectionReader.getPassword();
            this.con = DriverManager.getConnection(connectionString,username,password);
        } catch (SQLException e) {
            e.printStackTrace();
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
                    System.out.println(rs.getInt(1) + rs.getString(3) +rs.getString(2));
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
