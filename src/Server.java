import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static final BigInteger P = BigInteger.valueOf(11);
    public static final BigInteger G = BigInteger.valueOf(6);
    public static void main(String[] arg) {
        try {

            System.out.println("Serveri eshte duke degjuar");
            ServerSocket serverSocket = new ServerSocket(1543);
            Socket clientSocket = serverSocket.accept();



            ObjectInputStream serverIn = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream serverOut = new ObjectOutputStream(clientSocket.getOutputStream());

            String message = (String) serverIn.readObject();

            System.out.println("Mesazhi i pranuar : " + message);

            // Komunikime + kalkulime

            BigInteger A = generateKey();

            BigInteger calculatedServerValue = G.modPow(A, P);
            System.out.println("Mesazhi qe dergohet te klienti eshte: " + calculatedServerValue);
            serverOut.writeObject(calculatedServerValue);

            BigInteger valueFromClient = (BigInteger) serverIn.readObject();
            System.out.println("Vlera e pranuar nga klienti eshte: "+ valueFromClient);


            // Calculate final key

            BigInteger exchagedKey = valueFromClient.modPow(A, P);
            System.out.println("Celesi i perbashket i shkembyer eshte: " + exchagedKey);

            serverSocket.close();
            clientSocket.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static BigInteger generateKey() {
        return BigInteger.valueOf((long) (Math.random() * 1000));
    }
}

