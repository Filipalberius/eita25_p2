package Client;

import Messages.Request;
import Messages.Response;

import java.io.*;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;
import java.security.KeyStore;
import java.util.Scanner;

public class Client {

    SSLSocket socket;

    private Request createRequest(String patient, String requestType) {
        return new Request(patient, requestType);
    }

    private Request createRequest(String patient, String requestType, String recordPath) throws FileNotFoundException{
        return new Request(patient, requestType, recordPath);
    }

    /*
    Sets up a connection to the client
     */
    private SSLSession createTLS(String[] args) throws Exception {
        String host = null;
        int port = -1;
        for (int i = 0; i < args.length; i++) {
            System.out.println("args[" + i + "] = " + args[i]);
        }
        if (args.length < 2) {
            System.out.println("USAGE: java client host port");
            System.exit(-1);
        }
        try { /* get input parameters */
            host = args[0];
            port = Integer.parseInt(args[1]);
        } catch (IllegalArgumentException e) {
            System.out.println("USAGE: java client host port");
            System.exit(-1);
        }

        try { /* set up a key manager for client authentication */
            SSLSocketFactory factory;
            try {
                Scanner scanner = new Scanner(System.in);
                System.out.print("Enter your password: ");
                char[] password = scanner.next().toCharArray();

                KeyStore ks = KeyStore.getInstance("JKS");
                KeyStore ts = KeyStore.getInstance("JKS");
                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                SSLContext ctx = SSLContext.getInstance("TLS");
                ks.load(new FileInputStream("Client/clientkeystore.jks"), password);  // keystore password (storepass)
                ts.load(new FileInputStream("Client/clienttruststore.jks"), password); // truststore password (storepass);
                kmf.init(ks, password); // user password (keypass)
                tmf.init(ts); // keystore can be used as truststore here
                ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
                factory = ctx.getSocketFactory();
            } catch (Exception e) {
                throw new IOException(e.getMessage());
            }
            socket = (SSLSocket) factory.createSocket(host, port);
            System.out.println("\nsocket before handshake:\n" + socket + "\n");

            socket.startHandshake();

            SSLSession session = socket.getSession();
            X509Certificate cert = (X509Certificate) session.getPeerCertificateChain()[0];
            String subject = cert.getSubjectDN().getName();
            System.out.println("certificate name (subject DN field) on certificate received from server:\n" + subject + "\n");
            System.out.println("socket after handshake:\n" + socket + "\n");
            System.out.println("secure connection established\n\n");

            return session;

        } catch (Exception e){
            e.printStackTrace();
            throw new Exception();
        }
    }

    public void sendRequest() throws IOException {
        Request request;
        ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Patient: ");
        String patient = read.readLine();
        System.out.println("Request type: ");
        String requestType = read.readLine();
        if(requestType.equals("Write")) {
            System.out.println("File path: ");
            String filePath = read.readLine();
            request = new Request(patient, requestType, filePath);
        } else {
            request = new Request(patient, requestType);
        }

        os.writeObject(request);

    }

    public void recieveResponse() {
        try{
            ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
            Response response = (Response)is.readObject();
            System.out.println(response.getStatus());
            if(response.getRecord() != null){
                Scanner myReader = new Scanner(response.getRecord());
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    System.out.println(data);
                }
                myReader.close();
            }
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            SSLSession sesh = client.createTLS(args);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            client.sendRequest();
        } catch (Exception e){
            e.printStackTrace();
        }

        client.recieveResponse();

        try {
            client.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
