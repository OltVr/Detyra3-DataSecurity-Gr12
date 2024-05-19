package Test;

import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
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
        // Gjenerohet key pair
        keyPairGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGen.initialize(2048);
        keyPair = keyPairGen.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();

        System.out.println("\033[33mClient DH public key: " + Base64.getEncoder().encodeToString(publicKey.getEncoded()) + "\033[0m");

        // Inicializohet KeyAgreement
        keyAgree = KeyAgreement.getInstance(ALGORITHM);
        keyAgree.init(privateKey);

        // Inicializohet Signature
        signature = Signature.getInstance("SHA256withRSA");

        Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
        try (ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            // Merret public key i serverit
            byte[] serverPubKeyEnc = (byte[]) input.readObject();
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(serverPubKeyEnc);
            PublicKey serverPubKey = keyFactory.generatePublic(x509KeySpec);

            System.out.println("\033[33mServer DH public key: " + Base64.getEncoder().encodeToString(serverPubKeyEnc) + "\033[0m");

            // Dergohet public key i klientit
            output.writeObject(publicKey.getEncoded());

            // Faza 1 of key agreement
            keyAgree.doPhase(serverPubKey, true);

            // Gjenerohet shared secret
            byte[] sharedSecret = keyAgree.generateSecret();
            sharedSecretKey = new SecretKeySpec(sharedSecret, 0, 16, "AES");

            System.out.println("\033[33mShared secret (AES key): " + Base64.getEncoder().encodeToString(sharedSecretKey.getEncoded()) + "\033[0m");

            // Merret dhe verifikohet welcome message i nenshkruar dhe public key i serverit
            byte[] signedMessage = (byte[]) input.readObject();
            byte[] serverSignaturePubKeyEnc = (byte[]) input.readObject();
            KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec rsaKeySpec = new X509EncodedKeySpec(serverSignaturePubKeyEnc);
            PublicKey serverSignaturePubKey = rsaKeyFactory.generatePublic(rsaKeySpec);

            System.out.println("\033[33mServer RSA public key: " + Base64.getEncoder().encodeToString(serverSignaturePubKeyEnc) + "\033[0m");

            String welcomeMessage = "Welcome to the secure server!";
            if (verifyMessage(welcomeMessage, signedMessage, serverSignaturePubKey)) {
                System.out.println("\033[32mSignature valid. Trusted communication established.\033[0m");
            } else {
                System.out.println("\033[31mSignature invalid. Connection may be compromised.\033[0m");
            }

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
