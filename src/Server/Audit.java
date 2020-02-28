package Server;

import Messages.Request;
import Messages.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;

public class Audit {

    public void addEntry(String subject, Request request, Response response){
        String requesterName = "";

        String[] split = subject.split(",");
        for (String x : split) {
            if (x.contains("CN=")) {
                requesterName = x.trim().substring(3);
                System.out.println(requesterName);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(new Timestamp(System.currentTimeMillis()).toString()).
                append("Request: ").
                append(request.toString()).
                append(". Requester: ").
                append(requesterName).
                append(". Response: ").
                append(response.toString());

        try {
            Files.write(Paths.get("../resources/database/AuditLog.txt"), sb.toString().getBytes(), StandardOpenOption.APPEND);
        }catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }

    public static void main(String[] args) {

    }
}
