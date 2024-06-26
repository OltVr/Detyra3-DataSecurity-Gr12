package Test;

import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
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
    private static String disconnect="!q";

    public static void main(String[] args) throws Exception {
        // Gjenerohet key pair
        keyPairGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGen.initialize(2048);
        keyPair = keyPairGen.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();

        Operation.yellowOp("Client DH public key: " + Base64.getEncoder().encodeToString(publicKey.getEncoded()));

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

            Operation.yellowOp("Server DH public key: " + Base64.getEncoder().encodeToString(serverPubKeyEnc));

            // Dergohet public key i klientit
            output.writeObject(publicKey.getEncoded());

            // Faza 1 of key agreement
            keyAgree.doPhase(serverPubKey, true);

            // Gjenerohet shared secret
            byte[] sharedSecret = keyAgree.generateSecret();
            sharedSecretKey = new SecretKeySpec(sharedSecret, 0, 16, "AES");

           Operation.yellowOp("Shared secret (AES key): " + Base64.getEncoder().encodeToString(sharedSecretKey.getEncoded()));

            // Merret dhe verifikohet welcome message i nenshkruar dhe public key i serverit
            byte[] signedMessage = (byte[]) input.readObject();
            byte[] serverSignaturePubKeyEnc = (byte[]) input.readObject();
            KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec rsaKeySpec = new X509EncodedKeySpec(serverSignaturePubKeyEnc);
            PublicKey serverSignaturePubKey = rsaKeyFactory.generatePublic(rsaKeySpec);


            Operation.yellowOp("Server RSA public key: " + Base64.getEncoder().encodeToString(serverSignaturePubKeyEnc));

            String welcomeMessage = "Welcome to the secure server!";
            if (verifyMessage(welcomeMessage, signedMessage, serverSignaturePubKey)) {
                Operation.greenOp("Signature valid. Trusted communication established.");
            } else {
                Operation.redOp("Signature invalid. Connection may be compromised.");
            }

            Scanner scanner = new Scanner(System.in);
            Thread readerThread = new Thread(() -> {
                try {
                    while (true) {
                        // Receive the signed and encrypted message string from the client
                        String signedEncryptedMessageString = (String) input.readObject();
                        byte[] encryptedMessage = Base64.getDecoder().decode(signedEncryptedMessageString);
                        byte[] decryptedMessageBytes = CryptoUtils.decryptAES(encryptedMessage, sharedSecretKey); // Assuming RSA signature length is 256 bytes

                        String decryptedMessage = new String(decryptedMessageBytes);
                        if (decryptedMessage.equals(disconnect)){
                            System.out.println("");
                            Operation.cyanOp("Server has disconnected");
                            Operation.redOp("Terminating Console");
                            System.exit(1);
                        }
                        else {
                            Operation.server(decryptedMessage);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            readerThread.start();

            while (true) {
                Operation.clientmsg();
                String message = scanner.nextLine();
                byte[] encryptedMessage = CryptoUtils.encryptAES(message.getBytes(), sharedSecretKey);
                String EncryptedMessageString = Base64.getEncoder().encodeToString(encryptedMessage);
                output.writeObject(EncryptedMessageString);
                if (message.equals(disconnect)){
                    Operation.cyanOp("We are terminating the connection");
                    readerThread.interrupt();
                    socket.close();
                    System.exit(1);
                }

// Send the signed and encrypted message string to the server

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
