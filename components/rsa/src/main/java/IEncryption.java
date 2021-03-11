import java.io.File;

public interface IEncryption {
    String encrypt(String plainMessage, File publicKeyfile);

    String decrypt(String encryptedMessage, File privateKeyfile);
}
