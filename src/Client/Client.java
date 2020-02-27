package Client;

import Messages.Request;
import java.net.*;
import java.io.*;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;
import java.security.KeyStore;
import java.security.cert.*;

public class Client {

    private Request makeRequest(String patient, String requestType) {
        return new Request(patient, requestType);
    }


    public static void main(String[] args) {
        System.out.println("I am the client");
        Client client = new Client();
        Request test1 = client.makeRequest("Smith", "read");
        Request test2 = client.makeRequest("Boris", "read");
    }
}
