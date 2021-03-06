package Client;

import Messages.Request;
import Messages.Response;

import java.io.*;
import javax.net.ssl.*;
import java.nio.channels.FileChannel;
import java.security.KeyStore;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {

    private SSLSocket socket;

    private void createConnection(String[] args) throws Exception {
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

        /* set up a key manager for client authentication */
        SSLSocketFactory factory;
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter your Name: ");
            String identity = scanner.next();
            System.out.print("Enter your password: ");
            char[] password = scanner.next().toCharArray();


            KeyStore ks = KeyStore.getInstance("JKS");
            KeyStore ts = KeyStore.getInstance("JKS");
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            SSLContext ctx = SSLContext.getInstance("TLS");
            String KSPath = "Client/"+identity+"/clientkeystore.jks";
            String TSPath = "Client/"+identity+"/clienttruststore.jks";
            ks.load(new FileInputStream(KSPath), password);  // keystore password (storepass)
            ts.load(new FileInputStream(TSPath), password); // truststore password (storepass);
            kmf.init(ks, password); // user password (keypass)
            tmf.init(ts); // keystore can be used as truststore here
            ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            factory = ctx.getSocketFactory();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        socket = (SSLSocket) factory.createSocket(host, port);
        socket.startHandshake();
    }

    private void sendRequest() throws IOException {
        Request request;
        ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Patient: ");
        String patient = read.readLine();
        System.out.println("Request type: ");
        String requestType = read.readLine();
        if(requestType.toLowerCase().equals("write")) {
            System.out.println("File path: ");
            String filePath = read.readLine();
            request = new Request(patient, requestType, filePath);
        } else if (requestType.toLowerCase().equals("create")){
            System.out.println("Nurse ");
            String nurseName = read.readLine();
            request = new Request(patient, requestType, nurseName, true);
        } else {
            request = new Request(patient, requestType);
        }

        os.writeObject(request);
    }

    private void recieveResponse() {
        try{
            InputStream is_tmp = socket.getInputStream();
            ObjectInputStream is = new ObjectInputStream(is_tmp);
            Response response = (Response)is.readObject();
            System.out.println(response.getStatus());

            if(response.getRecord() != null){
                File file = new File("Client/record.txt");
                if(file.createNewFile()){
                    System.out.println("File created in directory");
                } else {
                    System.out.println("File already exists");
                }

                FileChannel src = new FileInputStream(response.getRecord()).getChannel();
                FileChannel dest = new FileOutputStream(file).getChannel();
                dest.transferFrom(src, 0, src.size());
            }

        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{
        Client client = new Client();

        client.createConnection(args);

        client.sendRequest();
        client.recieveResponse();
        client.socket.close();
    }
}
