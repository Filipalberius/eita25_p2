package Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Record {
    private String patient, doctor, nurse, division;
    private File record; //name.txt

    public Record(File record) throws FileNotFoundException {
        String data = "";
        Scanner myReader = new Scanner(record);
        while (myReader.hasNextLine()) {
            data += myReader.nextLine();
        }
        String[] splitData = data.split("¤"); //First line of Record file needs to end with ", "

        patient=splitData[0];
        doctor=splitData[1];
        nurse=splitData[2];
        division=splitData[3];
    }

    public String getPatient(){
        return this.patient;
    }

    public String getNurse(){
        return this.nurse;
    }

    public String getDoctor(){
        return this.doctor;
    }

    public String getDivision(){
        return this.division;
    }

    public static void main(String[] args) {
        try {
            Record record = new Record(new File("/h/d6/h/el0860de-s/Documents/EITA25/eita25_p2/RecordTestFile"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
