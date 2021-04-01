package controller.command;

import components.JarUtil;
import config.Configuration;
import network.EnterpriseNetwork;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Instant;

public class DecryptCommand implements ICommand {
    PrintStream originalOut;
    String message;
    String algorithm;
    String filePath;

    public DecryptCommand(String query, boolean isDebug) {
        originalOut = System.out;

        //check if query matches
        if (!query.matches("decrypt message \".*\" using .* and keyfile .*")) {
            throw new UnsupportedOperationException("Invalid Syntax. Syntax for decrypting is 'decrypt message \"[message]\" using [algorithm] and keyfile [file]'");
        }

        //query : encrypt message "[message]" using [algo] and keyfile [file]
        String[] splitQuery = query.split("\"");
        message = splitQuery[1];
        algorithm = splitQuery[2].substring(7).split(" ")[0];
        filePath = splitQuery[2].substring(20 + algorithm.length());

        if (isDebug) {
            try {
                File logFile = new File("log/decrypt_" + algorithm + "_" + Instant.now().getEpochSecond() + ".txt");
                logFile.getParentFile().mkdirs();
                logFile.createNewFile();
                PrintStream fileOut = new PrintStream(logFile);

                System.setOut(fileOut);
            } catch (IOException ex) {
                ex.printStackTrace();
                System.err.println("Could not activate debug mode");
            }
        }
    }

    @Override
    public String execute(EnterpriseNetwork network) {
        String keyfile;
        File decryptionKey;

        if (algorithm.equals("rsa")) {
            if (filePath.contains("pub")) {
                keyfile = filePath + ";" + filePath.replace("pub", "priv");
            } else {
                keyfile = filePath.replace("priv", "pub") + ";" + filePath;
            }
            decryptionKey = new File(keyfile.split(";")[1]);
        } else {
            keyfile = filePath;
            decryptionKey = new File(keyfile);
        }

        try {
            Object encryptorPort;
            if (algorithm.equals("rsa")) {
                encryptorPort = JarUtil.loadVerifiedJar(Configuration.instance.pathToRsa);
            } else {
                encryptorPort = JarUtil.loadVerifiedJar(Configuration.instance.pathToShift);
            }

            message = (String) encryptorPort.getClass().getDeclaredMethod("decrypt", String.class, File.class)
                                            .invoke(encryptorPort, message, decryptionKey);
        } catch (Exception ex) {
            System.err.println("Could not decrypt message");
            ex.printStackTrace();
        }

        System.out.flush();
        System.setOut(originalOut);
        return "Decrypted: " + message;
    }
}
