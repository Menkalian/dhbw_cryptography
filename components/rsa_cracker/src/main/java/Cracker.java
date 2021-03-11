import java.io.File;

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

    // TODO

    public class Port implements ICracker {
        @Override
        public String decrypt(String encryptedMessage, File publicKeyFile) {
            // TODO: 11.03.2021  
            return encryptedMessage;
        }
    }
}
