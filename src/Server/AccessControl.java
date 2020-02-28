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
            System.out.println(record.getPatient());
        } catch (FileNotFoundException e) {
            System.out.println("Record not found.");
        }

        String requesterName = "";
        String requesterDivision = "";

        String[] split = subject.split(",");
        for (String x : split) {
            if (x.contains("CN=")) {
                requesterName = x.trim();
                System.out.println(x);
            }
           if (x.contains("OU=")){
               requesterDivision =  x.trim();
           }

        }


        boolean access = false;

        if (request.getRequestType().matches("read")) {
            access = (requesterName.matches(record.getDoctor()) || requesterName.matches(record.getNurse()) ||
                    requesterDivision.matches(record.getDivision()) || requesterName.matches(record.getPatient()));
        }

        return access;
    }
}
