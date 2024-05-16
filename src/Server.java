import Functions.Key;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.Arrays;


public class Server {
    public static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";
    private static final String disconnect= "!q";
    public static void ServerStart(){
     final BigInteger P = BigInteger.valueOf(11);
     final BigInteger G = BigInteger.valueOf(6);
        try {
            System.out.println(GREEN+"[SERVER] Serveri eshte duke degjuar"+ RESET);
            ServerSocket serverSocket = new ServerSocket(1543);
            Socket clientSocket = serverSocket.accept();



            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String initmessage = in.readLine();

            System.out.println(GREEN+"[SERVER] Mesazhi i pranuar : " + initmessage+ RESET);

            // Komunikime + kalkulime

            BigInteger A = Key.generateKey();

            BigInteger calculatedServerValue = G.modPow(A, P);
            System.out.println(GREEN+"[SERVER] Mesazhi qe dergohet te klienti eshte: " + calculatedServerValue+ RESET);
            out.println(calculatedServerValue);

            String strvalueFromClient = in.readLine();
            BigInteger valueFromClient= new BigInteger(strvalueFromClient);
            System.out.println(GREEN+"[SERVER] Vlera e pranuar nga klienti eshte: "+ valueFromClient+ RESET);


            // Calculate final key

            BigInteger exchagedKey = valueFromClient.modPow(A, P);
            System.out.println(GREEN+"[SERVER] Celesi i perbashket i shkembyer eshte: " + exchagedKey+ RESET);

            String message;
            while(true){
                message=in.readLine();
                if (message.equals(disconnect)){
                    System.out.println(GREEN+"The client "+ clientSocket.getLocalSocketAddress() +" has disconnected!"+ LocalTime.now()+ RESET);
                    break;
                }

                System.out.print(GREEN+"[SERVER] "+ RESET );
                System.out.println(message + RESET);
                out.println(GREEN+ "[SERVER] Recieved: "+message+ RESET );
            }

            serverSocket.close();
            clientSocket.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

