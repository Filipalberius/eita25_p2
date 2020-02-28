package Server;

import Messages.Request;

import java.io.File;
import java.io.FileNotFoundException;


public class AccessControl {

    public static Boolean checkCredentials(String subject, Request request) {
        String recordPath = "../resources/database/" + request.getPatient() + ".txt";
        Record record = null;
        try {
            record = new Record(new File(recordPath));
        } catch (FileNotFoundException e) {
            System.out.println("Record not found.");
        }

        String requesterName = "";
        String requesterDivision = "";

        String[] split = subject.split(",");
        for (String x : split) {
            if (x.contains("CN=")) {
                requesterName = x.trim().substring(3);
            }

            if (x.contains("OU=")){
                requesterDivision =  x.trim().substring(3);
            }
        }


        boolean access = false;

        if (request.getRequestType().toLowerCase().matches("read")) {
            access = (requesterName.matches(record.getDoctor()) || requesterName.matches(record.getNurse()) ||
                    requesterDivision.matches(record.getDivision()) || requesterName.matches(record.getPatient()) || requesterDivision.matches("Government"));
        }

        if (request.getRequestType().toLowerCase().matches("write")) {
            access = (requesterName.matches(record.getDoctor()) || requesterName.matches(record.getNurse()));
        }

        if (request.getRequestType().toLowerCase().matches("delete")) {
            access = (requesterName.matches("Government"));
        }

        if (request.getRequestType().toLowerCase().matches("create")) {
            access = (requesterName.substring(0,2).matches("Dr"));
        }

        return access;
    }
}
