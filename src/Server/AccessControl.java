package Server;

import Messages.Request;

import java.io.File;
import java.io.FileNotFoundException;


public class AccessControl {

    public static Boolean checkCredentials(String requester, Request request) {
        String recordPath = "/h/d6/h/el0860de-s/Documents/EITA25/eita25_p2/resources/database/" +
                request.getPatient() + ".txt";
        Record record = null;
        try {
            record = new Record(new File(recordPath));
            System.out.println(record.getPatient());
        } catch (FileNotFoundException e) {
            System.out.println("Record not found.");
        }

        String requesterName = requester.split(", ")[0];
        String requesterDivision = requester.split(", ")[1];

        boolean access = false;

        if (request.getRequestType().matches("read")) {
            access = (requesterName.matches(record.getDoctor()) || requesterName.matches(record.getNurse()) ||
                    requesterDivision.matches(record.getDivision()) || requesterName.matches(record.getPatient()));
        }

        return access;
    }
}
