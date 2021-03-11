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

    // TODO

    public class Port implements IEncryption {
        @Override
        public String encrypt(String plainMessage, File publicKeyfile) {
            // TODO: 11.03.2021
            return plainMessage;
        }

        @Override
        public String decrypt(String encryptedMessage, File privateKeyfile) {
            // TODO: 11.03.2021
            return encryptedMessage;
        }
    }
}
