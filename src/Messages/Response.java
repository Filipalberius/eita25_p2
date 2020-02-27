package Messages;

import java.io.File;
import java.io.FileNotFoundException;

public class Response {
    private String status;
    private File record;

    public Response(String status){
        this.status = status;
    }

    public Response(String status, String recordPath) throws FileNotFoundException{
        this.status = status;
        try{
            this.record = new File(recordPath);
        } catch (Exception e){
            System.out.println("Can't find file " + recordPath);
            throw new FileNotFoundException();
        }
    }

    public File getRecord() {
        return record;
    }

    public String getStatus() {
        return status;
    }
}
