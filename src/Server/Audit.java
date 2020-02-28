package Server;

import Messages.Request;
import Messages.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;

public class Audit {

    void addEntry(String requesterName, Request request, Response response){

        StringBuilder sb = new StringBuilder();
        sb.append(new Timestamp(System.currentTimeMillis()).toString()).
                append(". Request: ").
                append(request.toString()).
                append(". Requester: ").
                append(requesterName).
                append(". Response: ").
                append(response.toString()).
                append("\n");

        try {
            Files.write(Paths.get("../resources/database/AuditLog.txt"), sb.toString().getBytes(), StandardOpenOption.APPEND);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
