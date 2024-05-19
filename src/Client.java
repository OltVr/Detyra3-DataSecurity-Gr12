import Functions.Key;

    import java.io.*;
    import java.math.BigInteger;
import java.net.Socket;
import java.util.Arrays;


public class Client {
        private static final String disconnect= "!q";
        private static final String YELLOW = "\u001B[33m";
        private static final String CYAN = "\u001B[36m";
        private static final String RED= "\u001B[31m";
        private static final String BOLD = "\u001B[1m";
        private static final String RESET = "\u001B[0m";
        private static void clientString(String word){
            System.out.println(YELLOW+BOLD+"[CLIENT] "+RESET+ word);
        }
        private static void CyanOP(String word){
            System.out.println(CYAN+BOLD+"[+] "+RESET+word);
        }
        private static void RedOP(String word){
            System.out.println(RED+BOLD+"[!] "+RESET+word);
        }
        private static void clientMSG(){
            System.out.print(YELLOW+BOLD+"[CLIENT] "+RESET);
        }
        private static void clientExit(){
            System.out.println(YELLOW+BOLD+"[*] "+RESET+"Logging off!"+RESET);
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

                byte[] verify = Key.encrypt(String.valueOf(exchagedKey), exchagedKey);
                sender.writeObject(verify);
                if ((boolean)receiver.readObject()){
                    CyanOP("Connection has been established!");
                }
                else {
                    RedOP("We found an illegal intervention! We are terminating the connection");
                    socket.close();
                    System.exit(1);
                }

                BufferedReader messageStream = new BufferedReader(new InputStreamReader(System.in));

                while (true) {
                    clientMSG();
                    String message = messageStream.readLine();
                    byte[] encrypted = Key.encrypt(message,exchagedKey);
                    if(message.equals(disconnect)){
                        clientExit();
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


