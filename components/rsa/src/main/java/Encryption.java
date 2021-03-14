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
            try {
                JsonParser.JsonObject key = new JsonParser().parse(publicKeyfile);
                BigInteger exponent = BigInteger.valueOf(key.getInt("e"));
                BigInteger common = BigInteger.valueOf(key.getInt("n"));

                byte[] messageBytes = plainMessage.getBytes(StandardCharsets.UTF_8);
                List<byte[]> encrypted = new LinkedList<>();

                for (byte messageByte : messageBytes) {
                    encrypted.add(crypt(BigInteger.valueOf(messageByte), common, exponent).toByteArray());
                }

                int blocklength = encrypted.stream().map(b -> b.length).max(Integer::compareTo).orElse(1);
                byte[] toSave = new byte[blocklength * encrypted.size()];
                Arrays.fill(toSave, (byte) 0);

                int i = 0;
                for (byte[] bytes : encrypted) {
                    int blankBlocks = blocklength - bytes.length;
                    System.arraycopy(bytes, 0, toSave, i + blankBlocks, bytes.length);
                    i += blocklength;
                }

                // Save as base64 and with blocksizes (to be able to store in DB
                return blocklength + ";" + Base64.getEncoder().encodeToString(toSave);
            } catch (IOException e) {
                e.printStackTrace();
                return plainMessage;
            }
        }

        @Override
        public String decrypt(String encryptedMessage, File privateKeyfile) {
            try {
                JsonParser.JsonObject key = new JsonParser().parse(privateKeyfile);
                BigInteger exponent = BigInteger.valueOf(key.getInt("d"));
                BigInteger common = BigInteger.valueOf(key.getInt("n"));

                String[] split = encryptedMessage.split(";", 2);
                int blocklength = Integer.parseInt(split[0]);
                byte[] messageData = Base64.getDecoder().decode(split[1]);

                int currentBlock = 0;
                byte[] decrypted = new byte[messageData.length / blocklength];

                for (int i = 0 ; i < decrypted.length ; i++) {
                    byte[] currentValue = Arrays.copyOfRange(messageData, currentBlock, currentBlock + blocklength);

                    BigInteger cipher = new BigInteger(currentValue);
                    decrypted[i] = crypt(cipher, common, exponent).byteValue();

                    currentBlock += blocklength;
                }

                return new String(decrypted);
            } catch (IOException e) {
                e.printStackTrace();
                return encryptedMessage;
            }
        }
    }
}
