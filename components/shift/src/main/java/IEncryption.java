import java.io.File;

public interface IEncryption {
    String encrypt(String plainMessage, File keyfile);

    String decrypt(String encryptedMessage, File keyfile);
}
