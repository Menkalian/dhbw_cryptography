import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

public class Encryption {
    // static instance
    private static Encryption instance = new Encryption();
    // port
    public Port port;

    private Encryption() {
        port = new Port();
    }

    public static Encryption getInstance() {
        return instance;
    }

    private BigInteger crypt(BigInteger message, BigInteger common, BigInteger keySpecific) {
        return message.modPow(keySpecific, common);
    }

    public class Port implements IEncryption {
        @Override
        public String encrypt(String plainMessage, File publicKeyfile) {
            System.out.println("Encrypting " + plainMessage + " with keyfile " + publicKeyfile)
            try {
                JsonParser.JsonObject key = new JsonParser().parse(publicKeyfile);
                BigInteger exponent = BigInteger.valueOf(key.getInt("e"));
                BigInteger common = BigInteger.valueOf(key.getInt("n"));
                System.out.println("e=" + exponent + "; n=" + common);

                byte[] messageBytes = plainMessage.getBytes(StandardCharsets.UTF_8);
                List<byte[]> encrypted = new LinkedList<>();
                System.out.println("Split message in bytes");

                System.out.println("Calculating m^e mod n");
                for (byte messageByte : messageBytes) {
                    encrypted.add(crypt(BigInteger.valueOf(messageByte), common, exponent).toByteArray());
                }

                int blocklength = encrypted.stream().map(b -> b.length).max(Integer::compareTo).orElse(1);
                byte[] toSave = new byte[blocklength * encrypted.size()];
                Arrays.fill(toSave, (byte) 0);
                System.out.println("Saving with blocklength " + blocklength);

                int i = 0;
                for (byte[] bytes : encrypted) {
                    int blankBlocks = blocklength - bytes.length;
                    System.arraycopy(bytes, 0, toSave, i + blankBlocks, bytes.length);
                    i += blocklength;
                }

                System.out.println("Encode as Base64");
                // Save as base64 and with blocksizes (to be able to store in DB
                return blocklength + ";" + Base64.getEncoder().encodeToString(toSave);
            } catch (IOException e) {
                e.printStackTrace();
                return plainMessage;
            }
        }

        @Override
        public String decrypt(String encryptedMessage, File privateKeyfile) {
            System.out.println("Decrypting " + encryptedMessage + " with keyfile " + privateKeyfile)
            try {
                JsonParser.JsonObject key = new JsonParser().parse(privateKeyfile);
                BigInteger exponent = BigInteger.valueOf(key.getInt("d"));
                BigInteger common = BigInteger.valueOf(key.getInt("n"));
                System.out.println("d=" + exponent + "; n=" + common);

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
                    decrypted[i] = crypt(cipher, common, exponent).byteValue();

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
