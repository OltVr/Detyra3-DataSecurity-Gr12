package Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class DHServer {

    private static final int PORT = 12345;
    private static final String ALGORITHM = "DH";
    private static KeyPair keyPair;
    private static PrivateKey privateKey;
    private static PublicKey publicKey;
    private static SecretKey sharedSecretKey;
    private static KeyPairGenerator keyPairGen;
    private static KeyAgreement keyAgree;
    private static Signature signature;
    private static String disconnect="!q";

    public static void main(String[] args) throws Exception {
        // Gjenerohet Key pair
        keyPairGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGen.initialize(2048);
        keyPair = keyPairGen.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();

        Operation.yellowOp("Server DH public key: " + Base64.getEncoder().encodeToString(publicKey.getEncoded()));

        // Inicializohet KeyAgreement
        keyAgree = KeyAgreement.getInstance(ALGORITHM);
        keyAgree.init(privateKey);

        // InicializohetSignature
        signature = Signature.getInstance("SHA256withRSA");
        KeyPair signatureKeyPair = generateRSAKeyPair();
        PrivateKey signaturePrivateKey = signatureKeyPair.getPrivate();
        PublicKey signaturePublicKey = signatureKeyPair.getPublic();

        Operation.yellowOp("Server RSA public key: " + Base64.getEncoder().encodeToString(signaturePublicKey.getEncoded()));

        ServerSocket serverSocket = new ServerSocket(PORT);
        Operation.cyanOp("Listening for connections...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            Operation.greenOp("Client connected.");
            new Thread(new ClientHandler(clientSocket, signaturePrivateKey, signaturePublicKey)).start();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final PrivateKey signaturePrivateKey;
        private final PublicKey signaturePublicKey;
        private final Scanner scanner;

        ClientHandler(Socket clientSocket, PrivateKey signaturePrivateKey, PublicKey signaturePublicKey) {
            this.clientSocket = clientSocket;
            this.signaturePrivateKey = signaturePrivateKey;
            this.signaturePublicKey = signaturePublicKey;
            this.scanner = new Scanner(System.in);
        }

        public void run() {
            try (ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
                 ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream())) {

                // Dergon public key te serverit
                output.writeObject(publicKey.getEncoded());

                // Merr public key te klientit
                byte[] clientPubKeyEnc = (byte[]) input.readObject();
                KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
                X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(clientPubKeyEnc);
                PublicKey clientPubKey = keyFactory.generatePublic(x509KeySpec);

                Operation.yellowOp("Client DH public key: " + Base64.getEncoder().encodeToString(clientPubKeyEnc));

                // Faza 1 of key agreement
                keyAgree.doPhase(clientPubKey, true);

                // Gjenerohet shared secret
                byte[] sharedSecret = keyAgree.generateSecret();
                sharedSecretKey = new SecretKeySpec(sharedSecret, 0, 16, "AES");

                Operation.yellowOp("Shared secret (AES key): " + Base64.getEncoder().encodeToString(sharedSecretKey.getEncoded()));

                // Dergohet welcome message i nenshkruar dhe public key i serverit
                String welcomeMessage = "Welcome to the secure server!";
                byte[] signedMessage = signMessage(welcomeMessage, signaturePrivateKey);
                output.writeObject(signedMessage);
                output.writeObject(signaturePublicKey.getEncoded());

                Operation.greenOp("Shared secret established. Sending signed welcome message...");


                Thread readerThread = new Thread(() -> {
                    try {
                        while (true) {
                            String EncryptedMessageString = (String) input.readObject();
                            byte[] recievedMessage = Base64.getDecoder().decode(EncryptedMessageString);
                            byte[] decryptedMessageBytes = CryptoUtils.decryptAES(recievedMessage, sharedSecretKey);
                            String decryptedMessage = new String(decryptedMessageBytes);
                            if (decryptedMessage.equals(disconnect)){
                                System.out.println("");
                                Operation.cyanOp("Client has disconnected");
                                Operation.redOp("Terminating Console");
                                System.exit(1);
                                break;
                            }
                            else {
                                Operation.client(decryptedMessage);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                readerThread.start();

                while (true) {
                   Operation.servermsg();
                    String message = scanner.nextLine();
                    byte[] encryptedMessage = CryptoUtils.encryptAES(message.getBytes(), sharedSecretKey);
                    String encryptedMessageString = Base64.getEncoder().encodeToString(encryptedMessage);
                    output.writeObject(encryptedMessageString);
                    if (message.equals(disconnect)){
                        Operation.cyanOp("We are terminating the connection");
                        readerThread.interrupt();
                        clientSocket.close();
                        System.exit(1);
                        break;
                    }
                }

            } catch (Exception e) {
                System.out.println("{ERROR} "+e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    private static byte[] signMessage(String message, PrivateKey privateKey) throws Exception {
        signature.initSign(privateKey);
        signature.update(message.getBytes());
        return signature.sign();
    }
}