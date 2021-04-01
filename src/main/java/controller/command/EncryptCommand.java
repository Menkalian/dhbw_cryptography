package controller.command;

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

        //query : decrypt message "[message]" using [algorithm] and keyfile [file]
        String[] splitQuery = query.split("\"");
        message = splitQuery[1];
        algorithm = splitQuery[2].substring(7).split(" ")[0];
        filePath = splitQuery[2].substring(20+algorithm.length());

        if (isDebug) {
            try {
                File logFile = new File("encrypt_" + algorithm + "_" + Instant.now().getEpochSecond() + ".txt");
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
        // ToDo : Get instance of matching algorithm and execute decrypt(message, file)

        System.setOut(originalOut);
        return "";
    }
}