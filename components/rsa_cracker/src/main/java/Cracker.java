import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Base64;

public class Cracker {
    // static instance
    private static Cracker instance = new Cracker();
    // port
    public Port port;

    private Cracker() {
        port = new Port();
    }

    public static Cracker getInstance() {
        return instance;
    }

    // Using Pollard Rho algorithm
    private BigInteger findFactor(BigInteger n) {
        BigInteger x = BigInteger.valueOf(2);
        BigInteger y = x;
        BigInteger g = BigInteger.ONE;

        while (g.equals(BigInteger.ONE)) {
            x = f(x, n);
            y = f(f(y, n), n);
            g = gcd(x.subtract(y).abs(), n);
        }

        return g;
    }

    // (x^2 + 1) mod n
    private BigInteger f(BigInteger x, BigInteger n) {
        return x.multiply(x).add(BigInteger.ONE).mod(n);
    }

    // Euclidian Algorithm
    private BigInteger gcd(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) {
            return a;
        }
        return gcd(b, a.mod(b));
    }

    private BigInteger crypt(BigInteger message, BigInteger common, BigInteger keySpecific) {
        return message.modPow(keySpecific, common);
    }

    public class Port implements ICracker {
        @Override
        public String decrypt(String encryptedMessage, File publicKeyFile) {

            System.out.println("Cracking " + encryptedMessage + " using RSA with Public keyfile " + publicKeyFile);
            try {
                JsonParser.JsonObject key = new JsonParser().parse(publicKeyFile);
                BigInteger n = BigInteger.valueOf(key.getInt("n"));
                BigInteger e = BigInteger.valueOf(key.getInt("e"));
                System.out.println("e=" + e + "; n=" + n);

                BigInteger p = findFactor(n);
                BigInteger q = n.divide(p);
                BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
                BigInteger d = e.modInverse(phi);

                String[] split = encryptedMessage.split(";", 2);
                int blocklength = Integer.parseInt(split[0]);
                byte[] messageData = Base64.getDecoder().decode(split[1]);
                System.out.println("Decode from Base64");
                System.out.println("blocklength: " + blocklength);

                int currentBlock = 0;
                byte[] decrypted = new byte[messageData.length / blocklength];

                System.out.println("Calculating m^d mod n");
                for (int i = 0 ; i < decrypted.length ; i++) {
                    byte[] currentValue = Arrays.copyOfRange(messageData, currentBlock, currentBlock + blocklength);

                    BigInteger cipher = new BigInteger(currentValue);
                    decrypted[i] = crypt(cipher, n, d).byteValue();

                    currentBlock += blocklength;
                }

                System.out.println("Joining message from bytes");
                return new String(decrypted);
            } catch (IOException e) {
                e.printStackTrace();
                return encryptedMessage;
            }
        }
    }
}
