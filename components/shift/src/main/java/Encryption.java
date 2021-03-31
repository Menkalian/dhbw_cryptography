import java.io.File;

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

    // TODO a-zA-Z

    public class Port implements IEncryption {
        @Override
        public String encrypt(String plainMessage, File keyfile) {
            return innerEncrypt(plainMessage,keyfile);
        }

        @Override
        public String decrypt(String encryptedMessage, File keyfile) {
            StringBuilder stringBuilder = new StringBuilder();
            JsonParser jsonParser = new JsonParser();
            int key = 0;
            try {
                key = jsonParser.parse(keyfile).getInt("n");
            } catch (Exception e){
                System.out.println("help");
            }

            for (int i = 0; i < encryptedMessage.length(); i++) {
                char character = (char) (encryptedMessage.codePointAt(i) - key);
                stringBuilder.append(character);
            }

            return stringBuilder.toString();
        }
    }
}
