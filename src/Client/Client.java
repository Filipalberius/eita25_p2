package Client;

import Messages.Request;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Client {
    private Request test1;
    private Request test2;

    private void testRequest() {
        test1 = new Request("Smith", "Read");
        try {
            test2 = new Request("Smith", "Read", "resources//database//Smith.txt");
            BufferedReader br = new BufferedReader(new FileReader(test2.getJournal()));
            String st;
            while ((st = br.readLine()) != null)
                System.out.println(st);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        System.out.println("I am the client");
        Client client = new Client();
        client.testRequest();
    }
}
