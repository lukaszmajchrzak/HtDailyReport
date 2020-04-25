package ReportsManager;

public class Incident {
    private String cIName;
    private int incidentId;
    private String status;

    public Incident(int incidentId, String cIName,String status) {
        this.cIName = cIName;
        this.incidentId = incidentId;
        this.status = status;
    }

    public String getcIName() {
        return cIName;
    }

    public int getIncidentId() {
        return incidentId;
    }

    public String getStatus() {
        return status;
    }
}
