import java.io.File;
import java.nio.charset.StandardCharsets;

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

    private byte[] shiftString(byte[] data, int shiftKey) {
        int[] dataCopy = new int[data.length];
        for (int i = 0 ; i < data.length ; i++) {
            dataCopy[i] = data[i];
        }

        for (int x = 0 ; x <= data.length - 1 ; x++) {
            dataCopy[x] = data[x];

            if (data[x] >= 'A' && data[x] <= 'Z') {
                dataCopy[x] += shiftKey;
                if (dataCopy[x] > 'Z') {
                    dataCopy[x] -= 26;
                }
                if (dataCopy[x] < 'A') {
                    dataCopy[x] += 26;
                }
            } else if (data[x] >= 'a' && data[x] <= 'z') {
                dataCopy[x] += shiftKey;
                if (dataCopy[x] > 'z') {
                    dataCopy[x] -= 26;
                }
                if (dataCopy[x] < 'a') {
                    dataCopy[x] += 26;
                }
            }
        }

        byte[] toReturn = new byte[dataCopy.length];
        for (int i = 0 ; i < toReturn.length ; i++) {
            toReturn[i] = (byte) dataCopy[i];
        }

        return toReturn;
    }

    public class Port implements IEncryption {
        @Override
        public String encrypt(String plainMessage, File keyfile) {
            System.out.println("Encrypting " + plainMessage + " with keyfile " + keyfile);
            JsonParser jsonParser = new JsonParser();
            int key = 0;
            try {
                JsonParser.JsonObject obj = jsonParser.parse(keyfile);
                key = obj.getInt("n");
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Shifting Characters from plainMessage " + key + " times");
            byte[] cypher = shiftString(plainMessage.getBytes(StandardCharsets.UTF_8), -key);

            return new String(cypher);
        }

        @Override
        public String decrypt(String encryptedMessage, File keyfile) {
            System.out.println("Decrypting " + encryptedMessage + " with keyfile " + keyfile);
            JsonParser jsonParser = new JsonParser();
            int key = 0;
            try {
                key = jsonParser.parse(keyfile).getInt("n");
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Shifting Characters from encryptedMessage " + key + " times");
            byte[] cypher = shiftString(encryptedMessage.getBytes(StandardCharsets.UTF_8), key);

            return new String(cypher);
        }
    }
}
