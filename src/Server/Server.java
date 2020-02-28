package Server;

import Messages.Request;
import Messages.Response;

import java.io.*;
import java.net.*;
import java.security.KeyStore;
import java.util.Scanner;
import javax.net.*;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;


public class Server implements Runnable {
    private ServerSocket serverSocket;

    public Server(ServerSocket ss) {
        serverSocket = ss;
        newListener();
    }

    public void run() {
        try {
            SSLSocket socket = (SSLSocket) serverSocket.accept();
            newListener();
            SSLSession session = socket.getSession();
            X509Certificate cert = (X509Certificate) session.getPeerCertificateChain()[0];
            String subject = cert.getSubjectDN().getName();

            System.out.println("client connected");
            System.out.println("client name (cert subject DN field): " + subject);

            //Receive Request
            ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
            Request request =(Request)is.readObject();

            //Send Response
            sendResponse(socket, request, subject);

            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client died: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendResponse(SSLSocket socket, Request request, String subject) throws IOException {
        ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
        String requestType = request.getRequestType();
        Response response;

        if(AccessControl.checkCredentials(subject, request)){
            String fileName = "../resources/database/" + request.getPatient() + ".txt";
            File record = new File(fileName);

            switch (requestType.toLowerCase()) {
                case "read":
                    response = new Response("Success", record);
                    break;
                case "write": {
                    response = writeFile(request, record);
                    break;
                }
                case "delete": {
                    response = deleteFile(record);
                    break;
                }
                case "create": {
                    response = createFile(record);
                    break;
                }
                default:
                    response = new Response("Failure");
                    break;
            }
        } else {
            response = new Response("Access Denied");
        }
        os.writeObject(response);
    }

    //TODO
    private Response createFile(File record) {
        return new Response("Success");
    }

    //TODO: fix this
    private Response deleteFile(File record) {
        if (record.exists()) {
            record.delete();
            return new Response("Success");
        } else {
            return new Response("Failure");
        }
    }

    //TODO: fix this
    private Response writeFile(Request request, File record) {
        try {
            if(record.exists()){
                FileWriter fw = new FileWriter(record, false);
                Scanner myReader = new Scanner(request.getRecord());
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    fw.write(data);
                }
                myReader.close();
                return new Response("Success");
            } else {
                return new Response("Record does not exist.");
            }
        } catch (IOException e) {
            return new Response("Failure");
        }
    }

    private void newListener() {
        (new Thread(this)).start();
    } // calls run()

    public static void main(String[] args) {
        System.out.println("\nServer Started\n");
        int port = -1;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        String type = "TLS";
        try {
            ServerSocketFactory ssf = getServerSocketFactory(type);
            ServerSocket ss = ssf.createServerSocket(port);
            ((SSLServerSocket)ss).setNeedClientAuth(true); // enables client authentication
            new Server(ss);
        } catch (IOException e) {
            System.out.println("Unable to start Server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static ServerSocketFactory getServerSocketFactory(String type) {
        if (type.equals("TLS")) {
            SSLServerSocketFactory ssf;
            try { // set up key manager to perform server authentication
                SSLContext ctx = SSLContext.getInstance("TLS");
                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                KeyStore ks = KeyStore.getInstance("JKS");
                KeyStore ts = KeyStore.getInstance("JKS");
                char[] password = "password".toCharArray();

                ks.load(new FileInputStream("Server/serverkeystore.jks"), password);  // keystore password (storepass)
                ts.load(new FileInputStream("Server/servertruststore.jks"), password); // truststore password (storepass)
                kmf.init(ks, password); // certificate password (keypass)
                tmf.init(ts);  // possible to use keystore as truststore here
                ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
                ssf = ctx.getServerSocketFactory();
                return ssf;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return ServerSocketFactory.getDefault();
        }
        return null;
    }
}
