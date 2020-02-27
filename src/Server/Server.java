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
            sendResponse(socket, request);

            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client died: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendResponse(SSLSocket socket, Request request) throws IOException {
        ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
        String requestType = request.getRequestType();
        Response response;

        //Run only if access is given!
        String fileName = "../resources/database/" + request.getPatient() + ".txt";

        switch (requestType) {
            case "Read":
                response = new Response("Success", fileName);
                break;
            case "Write": {
                File record = new File(fileName);
                try {
                    FileWriter fw = new FileWriter(record, false);
                    Scanner myReader = new Scanner(request.getRecord());
                    while (myReader.hasNextLine()) {
                        String data = myReader.nextLine();
                        fw.write(data);
                    }
                    myReader.close();
                    response = new Response("Success");
                } catch (IOException e) {
                    response = new Response("Failure");
                    e.printStackTrace();
                }
                break;
            }
            case "Delete": {
                File record = new File(fileName);
                if (record.exists()) {
                    record.delete();
                    response = new Response("Success");
                } else {
                    response = new Response("Failure");
                }
                break;
            }
            default:
                response = new Response("Failure");
                break;
        }

        os.writeObject(response);
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
