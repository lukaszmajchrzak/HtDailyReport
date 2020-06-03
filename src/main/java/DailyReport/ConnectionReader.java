package DailyReport;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

;

public class ConnectionReader {
    private Path filepath;
    private String username;
    private String password;
    private String connectionURL;

    public ConnectionReader(){
    }

    public void setFilePath(Path filepath) {
        this.filepath = filepath;
    }

    public void readConnectionSetup(){
        try{
            FileInputStream fis = new FileInputStream(filepath.toFile());
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            Document document = documentBuilder.parse(fis);

            this.username = document.getElementsByTagName("Username").item(0).getTextContent();
            this.password = document.getElementsByTagName("Password").item(0).getTextContent();
            this.connectionURL = document.getElementsByTagName("ConnectionString").item(0).getTextContent();

        }catch(IOException | SAXException | ParserConfigurationException e){
            e.printStackTrace();
        }

    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getConnectionURL() {
        return connectionURL;
    }
}
