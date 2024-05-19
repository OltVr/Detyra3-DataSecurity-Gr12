import Functions.Key;

    import java.io.*;
    import java.math.BigInteger;
import java.net.Socket;
import java.util.Arrays;


public class Client {
        private static final String disconnect= "!q";
        private static final String YELLOW = "\u001B[33m";
        private static final String BOLD = "\u001B[1m";
        private static final String RESET = "\u001B[0m";
        private static void clientString(String word){
            System.out.println(YELLOW+BOLD+"[CLIENT] "+RESET+ word);
        }
        private static void clientMSG(){
            System.out.print(YELLOW+BOLD+"[CLIENT] "+RESET);
        }
        public static void ClientStart(){
          final BigInteger P = BigInteger.valueOf(11);
          final BigInteger G = BigInteger.valueOf(6);

            try  {
                Socket socket = new Socket("localhost", 1441);
                ObjectOutputStream sender = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream receiver = new ObjectInputStream(socket.getInputStream());

                clientString("Klienti po e dergon nje mesazh tek serveri");
                sender.writeObject("Mesazhi per serverin");

                // Komunikime dhe kalkulime

                BigInteger B = Key.generateKey();

                BigInteger calculatedClientValue = G.modPow(B, P);

                BigInteger valueFromServer = (BigInteger) receiver.readObject();
               clientString("Mesazhi i pranuar nga serveri eshte: "+ valueFromServer);

                clientString("Vlera e derguar tek serveri eshte: " + calculatedClientValue);
                sender.writeObject(calculatedClientValue);

                BigInteger exchagedKey = valueFromServer.modPow(B, P);
                clientString("Celesi i perbashket i shkembyer eshte: " + exchagedKey);

                BufferedReader messageStream = new BufferedReader(new InputStreamReader(System.in));

                while (true) {
                    clientMSG();
                    String message = messageStream.readLine();
                    byte[] encrypted = Key.encrypt(message,exchagedKey);
                    if(message.equals(disconnect)){
                        clientString("Logging Out!");
                        sender.writeObject(disconnect);
                        break;
                    }
                    System.out.println(Arrays.toString(encrypted));
                    sender.writeObject(encrypted);
                    clientString("Server: " + receiver.readObject());
                }

                socket.close();
            } catch (Exception e) {
                System.out.println("[CLIENT] "+ e.getMessage());
                throw new RuntimeException(e);
            }

        }
    }


