package Functions;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

public class Key {

    public static BigInteger generateKey() {
        return BigInteger.valueOf((long) (Math.random() * 1000));
    }

    private static SecretKeySpec createSecretKey(BigInteger sharedKey){
        byte[] keyBytes =sharedKey.toByteArray();
        byte[] validKey= new byte[16];
        System.arraycopy(keyBytes,0,validKey,0,Math.min(keyBytes.length,validKey.length));
        return new SecretKeySpec(validKey,"AES");
    }

    public static byte[] encrypt(String plaintext,BigInteger sharedkey) throws Exception {
        SecretKeySpec secretKeySpec = createSecretKey(sharedkey);
        Cipher cipher =Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);
        return cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
    }

    public static String decrypt(byte [] ciphertext ,BigInteger sharedkey) throws Exception {
        SecretKeySpec secretKeySpec= createSecretKey(sharedkey);
        Cipher cipher= Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE,secretKeySpec);
        byte [] decryptedBytes= cipher.doFinal(ciphertext);
        return new String(decryptedBytes,StandardCharsets.UTF_8);

    }

}
