import Functions.Key;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

import java.time.LocalTime;


public class Server {
    private static final String GREEN = "\u001B[32m";
    private static final String CYAN = "\u001B[36m";
    private static final String RED= "\u001B[31m";
    private static final String YELLOW= "\u001B[33m";
    private static final String BOLD= "\u001B[1m";
    private static final String RESET = "\u001B[0m";
    private static final String disconnect= "!q";

    private static void CyanOP(String word){
        System.out.println(CYAN+BOLD+"[+] "+RESET+word);
    }
    private static void RedOP(String word){
        System.out.println(RED+BOLD+"[!] "+RESET+word);
    }
    private static void GreenOP(String word){
        System.out.println(GREEN+BOLD+"[SERVER] "+RESET+word);
    }
    private static void clientExit(Socket clientSocket){
        System.out.println(YELLOW+BOLD+"[*] "+RESET+"The client " + clientSocket.getLocalSocketAddress() + " has disconnected! " + LocalTime.now());
    }
    public static void ServerStart(){
     final BigInteger P = BigInteger.valueOf(11);
     final BigInteger G = BigInteger.valueOf(6);
        try {
            CyanOP("Server is listening...");
            ServerSocket serverSocket = new ServerSocket(1441);
            Socket clientSocket = serverSocket.accept();
            CyanOP("Connection established with "+ clientSocket.getLocalSocketAddress());


            ObjectOutputStream sender = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream receiver = new ObjectInputStream(clientSocket.getInputStream());

            CyanOP("Performing Diffie-Hellman key exchange...");

            String initmessage = (String) receiver.readObject();

            GreenOP("Mesazhi i pranuar : " + initmessage);

            // Komunikime + kalkulime

            BigInteger A = Key.generateKey();

            BigInteger calculatedServerValue = G.modPow(A, P);
            GreenOP("Mesazhi qe dergohet te klienti eshte: " + calculatedServerValue);
            sender.writeObject(calculatedServerValue);

            BigInteger valueFromClient = (BigInteger) receiver.readObject();
            GreenOP("Vlera e pranuar nga klienti eshte: "+ valueFromClient);


            // Calculate final key

            BigInteger exchagedKey = valueFromClient.modPow(A, P);
            GreenOP("Celesi i perbashket i shkembyer eshte: " + exchagedKey);

            String verification= Key.decrypt( (byte[]) receiver.readObject(),exchagedKey);
            if (verification.equals(String.valueOf(exchagedKey))){
                sender.writeObject(true);
                CyanOP("Shared secret established.");
            }
            else {
                sender.writeObject(false);
                RedOP("Shared secret Failed. Terminating connection.");
                clientSocket.close();
                serverSocket.close();
                System.exit(1);
            }




            while(true){
                Object received = receiver.readObject();
                if (received instanceof String && received.equals(disconnect)) {
                    clientExit(clientSocket);
                    break;
                }
                if (received instanceof byte[] encryptedMessage) {
                    String decryptedMessage = Key.decrypt(encryptedMessage, exchagedKey);
                    GreenOP(decryptedMessage);
                    sender.writeObject("[SERVER] Received: " + decryptedMessage + RESET);
                }

            }

            serverSocket.close();
            clientSocket.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

