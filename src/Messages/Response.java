package Messages;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;

public class Response implements Serializable {
    private String status;
    private File record;

    public Response(String status){
        this.status = status;
    }

    public Response(String status, File record) throws FileNotFoundException{
        this.status = status;
        this.record = record;
    }

    public File getRecord() {
        return record;
    }

    public String getStatus() {
        return status;
    }
}
