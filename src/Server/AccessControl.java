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
                requesterName = x.trim().substring(3);
                System.out.println(requesterName);
                System.out.println(record.getDoctor());
            }
           if (x.contains("OU=")){
               requesterDivision =  x.trim().substring(3);
               System.out.println(requesterDivision);
           }

        }


        boolean access = false;

        if (request.getRequestType().matches("Read")) {
            access = (requesterName.matches(record.getDoctor()) || requesterName.matches(record.getNurse()) ||
                    requesterDivision.matches(record.getDivision()) || requesterName.matches(record.getPatient()));
        }

        return access;
    }
}
