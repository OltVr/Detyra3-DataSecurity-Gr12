package Test;

import javax.crypto.*;
import java.security.*;

public class CryptoUtils {

    public static byte[] encryptAES(byte[] plaintextBytes, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(plaintextBytes);
    }

    public static byte[] decryptAES(byte[] ciphertextBytes, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(ciphertextBytes);
    }
}