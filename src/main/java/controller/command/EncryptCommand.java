package controller.command;

import components.JarUtil;
import config.Configuration;
import network.EnterpriseNetwork;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Instant;

public class EncryptCommand implements ICommand {
    PrintStream originalOut;
    String message;
    String algorithm;
    String filePath;

    public EncryptCommand(String query, boolean isDebug) {
        originalOut = System.out;

        //check if query matches
        if (!query.matches("encrypt message \".*\" using .* and keyfile .*")) {
            throw new UnsupportedOperationException("Invalid Syntax. Syntax for encrypting is 'encrypt message \"[message]\" using [algorithm] and keyfile [file]'");
        }

        //query : decrypt message "[message]" using [algorithm] and keyfile [file]
        String[] splitQuery = query.split("\"");
        message = splitQuery[1];
        algorithm = splitQuery[2].substring(7).split(" ")[0];
        filePath = splitQuery[2].substring(20 + algorithm.length());

        if (isDebug) {
            try {
                File logFile = new File("log/encrypt_" + algorithm + "_" + Instant.now().getEpochSecond() + ".txt");
                logFile.getParentFile().mkdirs();
                logFile.createNewFile();
                PrintStream fileOut = new PrintStream(logFile);

                System.setOut(fileOut);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Could not activate debug mode");
            }
        }
    }

    @Override
    public String execute(EnterpriseNetwork network) {
        String keyfile;
        File encryptionKey;

        if (algorithm.equals("rsa")) {
            if (filePath.contains("pub")) {
                keyfile = filePath + ";" + filePath.replace("pub", "priv");
            } else {
                keyfile = filePath.replace("priv", "pub") + ";" + filePath;
            }
            encryptionKey = new File(keyfile.split(";")[0]);
        } else {
            keyfile = filePath;
            encryptionKey = new File(keyfile);
        }

        try {
            Object encryptorPort;
            if (algorithm.equals("rsa")) {
                encryptorPort = JarUtil.loadVerifiedJar(Configuration.instance.pathToRsa);
            } else {
                encryptorPort = JarUtil.loadVerifiedJar(Configuration.instance.pathToShift);
            }

            message = (String) encryptorPort.getClass().getDeclaredMethod("encrypt", String.class, File.class)
                                            .invoke(encryptorPort, message, encryptionKey);
        } catch (Exception ex) {
            System.err.println("Could not encrypt message");
            ex.printStackTrace();
        }

        System.out.flush();
        System.setOut(originalOut);
        return "Encrypted: " + message;
    }
}