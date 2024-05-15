package Functions;

import java.math.BigInteger;

public class Key {

    public static BigInteger generateKey() {
        return BigInteger.valueOf((long) (Math.random() * 1000));
    }
}
