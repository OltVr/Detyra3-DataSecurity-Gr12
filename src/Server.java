import Functions.Key;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.Scanner;

public class Server {
    public static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";
    private static final String disconnect= "!q";
    public static void ServerStart(){
     final BigInteger P = BigInteger.valueOf(11);
     final BigInteger G = BigInteger.valueOf(6);
        try {
            Scanner messager= new Scanner(System.in);
            System.out.println(GREEN+"[SERVER] Serveri eshte duke degjuar"+ RESET);
            ServerSocket serverSocket = new ServerSocket(1543);
            Socket clientSocket = serverSocket.accept();



            ObjectInputStream serverIn = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream serverOut = new ObjectOutputStream(clientSocket.getOutputStream());

            String initmessage = (String) serverIn.readObject();

            System.out.println(GREEN+"[SERVER] Mesazhi i pranuar : " + initmessage+ RESET);

            // Komunikime + kalkulime

            BigInteger A = Key.generateKey();

            BigInteger calculatedServerValue = G.modPow(A, P);
            System.out.println(GREEN+"[SERVER] Mesazhi qe dergohet te klienti eshte: " + calculatedServerValue+ RESET);
            serverOut.writeObject(calculatedServerValue);

            BigInteger valueFromClient = (BigInteger) serverIn.readObject();
            System.out.println(GREEN+"[SERVER] Vlera e pranuar nga klienti eshte: "+ valueFromClient+ RESET);


            // Calculate final key

            BigInteger exchagedKey = valueFromClient.modPow(A, P);
            System.out.println(GREEN+"[SERVER] Celesi i perbashket i shkembyer eshte: " + exchagedKey+ RESET);

            String message;
            while(true) {
                System.out.print(GREEN+"[SERVER]");
                message= messager.nextLine();
                if (message.equals(disconnect))
                {
                    serverOut.writeObject(GREEN+"This client has disconnected!"+ LocalTime.now());
                    serverSocket.close();
                    clientSocket.close();
                    break;
                }
                serverOut.writeObject(message);

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

