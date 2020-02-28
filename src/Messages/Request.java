package Messages;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;

public class Request implements Serializable {
    private String patient;
    private String requestType;
    private File record;
    private String nurse;

    public Request(String patient, String requestType, String recordPath) throws FileNotFoundException {
        this.patient = patient;
        this.requestType = requestType;
        try{
            this.record = new File(recordPath);
        } catch (Exception e){
            System.out.println("File " + recordPath + " not found");
            throw new FileNotFoundException();
        }
    }

    public Request(String patient, String requestType){
        this.patient = patient;
        this.requestType = requestType;
    }

    public Request(String patient, String requestType, String nurse, Boolean create){
        this.patient = patient;
        this.requestType = requestType;
        this.nurse = nurse;
    }

    public String getPatient() {
        return patient;
    }

    public String getRequestType() {
        return requestType;
    }

    public File getRecord() {
        return this.record;
    }

    public String getNurse() {
        return nurse;
    }

    public String toString(){
        return patient + ", " + requestType;
    }
}
