package Messages;

import java.io.File;
import java.io.FileNotFoundException;

public class Response {
    private String status;
    private File journal;

    public Response(String status){
        this.status = status;
    }

    public Response(String status, String journalPath) throws FileNotFoundException{
        this.status = status;
        try{
            this.journal = new File(journalPath);
        } catch (Exception e){
            System.out.println("Can't find file " + journalPath);
            throw new FileNotFoundException();
        }
    }

    public File getJournal() {
        return journal;
    }

    public String getStatus() {
        return status;
    }
}
