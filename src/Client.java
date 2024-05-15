
    import Functions.Key;

    import java.io.*;
    import java.math.BigInteger;
import java.net.Socket;


    public class Client {
        private static final String disconnect= "!q";
        private static final String YELLOW = "\u001B[33m";
        private static final String RESET = "\u001B[0m";
        public static void ClientStart(){
          final BigInteger P = BigInteger.valueOf(11);
          final BigInteger G = BigInteger.valueOf(6);

            try  {
                Socket socket = new Socket("localhost", 1543);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                System.out.println(YELLOW+"[CLIENT] Klienti po e dergon nje mesazh tek serveri"+ RESET);
                out.println(YELLOW+"Mesazhi per serverin"+ RESET);

                // Komunikime dhe kalkulime

                BigInteger B = Key.generateKey();

                BigInteger calculatedClientValue = G.modPow(B, P);

                String strvalueFromServer = in.readLine();
                BigInteger valueFromServer =new BigInteger(strvalueFromServer);
                System.out.println(YELLOW+"[CLIENT] Mesazhi i pranuar nga serveri eshte: "+ valueFromServer+ RESET);

                System.out.println(YELLOW+"[CLIENT] Vlera e derguar tek serveri eshte: " + calculatedClientValue+ RESET);
                out.println(calculatedClientValue);

                BigInteger exchagedKey = valueFromServer.modPow(B, P);
                System.out.println(YELLOW+"[CLIENT] Celesi i perbashket i shkembyer eshte: " + exchagedKey+ RESET);


                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                String message;
                while (true) {
                    System.out.print(YELLOW+"[CLIENT] ");
                    message = userInput.readLine();
                    if(message.equals(disconnect)){
                        System.out.println(YELLOW+"[CLIENT] Logging Out"+ RESET);
                        out.println("!q");
                        break;
                    }
                    out.println(YELLOW+ message);
                    System.out.println(YELLOW+ "Server: " + in.readLine());
                }

                socket.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }


