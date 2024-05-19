package Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;
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

    public static void main(String[] args) throws Exception {
        // Initialize key pair generator and generate key pair
        keyPairGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGen.initialize(2048);
        keyPair = keyPairGen.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();

        System.out.println("\033[32mServer DH public key: " + Base64.getEncoder().encodeToString(publicKey.getEncoded()) + "\033[0m");

        // Initialize the KeyAgreement
        keyAgree = KeyAgreement.getInstance(ALGORITHM);
        keyAgree.init(privateKey);

        // Initialize Signature
        signature = Signature.getInstance("SHA256withRSA");
        KeyPair signatureKeyPair = generateRSAKeyPair();
        PrivateKey signaturePrivateKey = signatureKeyPair.getPrivate();
        PublicKey signaturePublicKey = signatureKeyPair.getPublic();

        System.out.println("\033[32mServer RSA public key: " + Base64.getEncoder().encodeToString(signaturePublicKey.getEncoded()) + "\033[0m");

        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Listening for connections...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected.");
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

                // Send server's public key
                output.writeObject(publicKey.getEncoded());

                // Receive client's public key
                byte[] clientPubKeyEnc = (byte[]) input.readObject();
                KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
                X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(clientPubKeyEnc);
                PublicKey clientPubKey = keyFactory.generatePublic(x509KeySpec);

                System.out.println("\033[32mClient DH public key: " + Base64.getEncoder().encodeToString(clientPubKeyEnc) + "\033[0m");

                // Perform phase 1 of key agreement
                keyAgree.doPhase(clientPubKey, true);

                // Generate shared secret
                byte[] sharedSecret = keyAgree.generateSecret();
                sharedSecretKey = new SecretKeySpec(sharedSecret, 0, 16, "AES");

                System.out.println("\033[32mShared secret (AES key): " + Base64.getEncoder().encodeToString(sharedSecretKey.getEncoded()) + "\033[0m");

                // Send signed welcome message and public key
                String welcomeMessage = "Welcome to the secure server!";
                byte[] signedMessage = signMessage(welcomeMessage, signaturePrivateKey);
                output.writeObject(signedMessage);
                output.writeObject(signaturePublicKey.getEncoded());

                System.out.println("\033[32mShared secret established. Sending signed welcome message...\033[0m");

                // Further communication logic here...
                Thread readerThread = new Thread(() -> {
                    try {
                        while (true) {
                            String receivedMessage = (String) input.readObject();
                            System.out.println("\033[33mClient: " + receivedMessage + "\033[0m");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                readerThread.start();

                while (true) {
                    System.out.print("Server: ");
                    String message = scanner.nextLine();
                    output.writeObject(message);
                    System.out.println("\033[32mServer: " + message + "\033[0m");
                }

            } catch (Exception e) {
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