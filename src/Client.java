
    import Functions.Key;

    import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;

    public class Client {
        private static final String YELLOW = "\u001B[33m";
        private static final String RESET = "\u001B[0m";
        public static void ClientStart(){
          final BigInteger P = BigInteger.valueOf(11);
          final BigInteger G = BigInteger.valueOf(6);

            try  {
                Socket socket = new Socket("localhost", 1543);
                ObjectOutputStream clientOut = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream clientIn = new ObjectInputStream(socket.getInputStream());

                System.out.println(YELLOW+"[CLIENT] Klienti po e dergon nje mesazh tek serveri"+ RESET);
                clientOut.writeObject(YELLOW+"Mesazhi per serverin"+ RESET);

                // Komunikime dhe kalkulime

                BigInteger B = Key.generateKey();

                BigInteger calculatedClientValue = G.modPow(B, P);

                BigInteger valueFromServer = (BigInteger) clientIn.readObject();
                System.out.println(YELLOW+"[CLIENT] Mesazhi i pranuar nga serveri eshte: "+ valueFromServer+ RESET);

                System.out.println(YELLOW+"[CLIENT] Vlera e derguar tek serveri eshte: " + calculatedClientValue+ RESET);
                clientOut.writeObject(calculatedClientValue);

                BigInteger exchagedKey = valueFromServer.modPow(B, P);
                System.out.println(YELLOW+"[CLIENT] Celesi i perbashket i shkembyer eshte: " + exchagedKey+ RESET);

                socket.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }


