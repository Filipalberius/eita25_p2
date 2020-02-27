package Messages;

import java.io.File;
import java.io.FileNotFoundException;

public class Request {
    private String patient;
    private String requestType;
    private File journal;

    public Request(String patient, String requestType, String journalPath) throws FileNotFoundException {
        this.patient = patient;
        this.requestType = requestType;
        try{
            this.journal = new File(journalPath);
        } catch (Exception e){
            System.out.println("File " + journalPath + " not found");
            throw new FileNotFoundException();
        }
    }

    public Request(String patient, String requestType){
        this.patient = patient;
        this.requestType = requestType;
    }

    public String getPatient() {
        return patient;
    }

    public String getRequestType() {
        return requestType;
    }

    public File getJournal() {
        return journal;
    }
}
