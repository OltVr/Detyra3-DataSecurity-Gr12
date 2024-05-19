package Test;

import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Scanner;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class DHClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;
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

        // Initialize the KeyAgreement
        keyAgree = KeyAgreement.getInstance(ALGORITHM);
        keyAgree.init(privateKey);

        // Initialize Signature
        signature = Signature.getInstance("SHA256withRSA");

        Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
        try (ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            // Receive server's public key
            byte[] serverPubKeyEnc = (byte[]) input.readObject();
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(serverPubKeyEnc);
            PublicKey serverPubKey = keyFactory.generatePublic(x509KeySpec);

            // Send client's public key
            output.writeObject(publicKey.getEncoded());

            // Perform phase 1 of key agreement
            keyAgree.doPhase(serverPubKey, true);

            // Generate shared secret
            byte[] sharedSecret = keyAgree.generateSecret();
            sharedSecretKey = new SecretKeySpec(sharedSecret, 0, 16, "AES");

            // Receive and verify signed welcome message and server's public key
            byte[] signedMessage = (byte[]) input.readObject();
            byte[] serverSignaturePubKeyEnc = (byte[]) input.readObject();
            KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec rsaKeySpec = new X509EncodedKeySpec(serverSignaturePubKeyEnc);
            PublicKey serverSignaturePubKey = rsaKeyFactory.generatePublic(rsaKeySpec);

            String welcomeMessage = "Welcome to the secure server!";
            if (verifyMessage(welcomeMessage, signedMessage, serverSignaturePubKey)) {
                System.out.println("\033[32mSignature valid. Trusted communication established.\033[0m");
            } else {
                System.out.println("\033[31mSignature invalid. Connection may be compromised.\033[0m");
            }

            // Further communication logic here...
            Scanner scanner = new Scanner(System.in);
            Thread readerThread = new Thread(() -> {
                try {
                    while (true) {
                        String receivedMessage = (String) input.readObject();
                        System.out.println("\033[32mServer: " + receivedMessage + "\033[0m");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            readerThread.start();

            while (true) {
                System.out.print("Client: ");
                String message = scanner.nextLine();
                output.writeObject(message);
                System.out.println("\033[33mClient: " + message + "\033[0m");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean verifyMessage(String message, byte[] signatureBytes, PublicKey publicKey) throws Exception {
        signature.initVerify(publicKey);
        signature.update(message.getBytes());
        return signature.verify(signatureBytes);
    }
}