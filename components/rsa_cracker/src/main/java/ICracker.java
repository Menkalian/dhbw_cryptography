import java.io.File;

public interface ICracker {
    String decrypt(String encryptedMessage, File publicKeyFile);
}
