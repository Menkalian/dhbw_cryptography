package controller.command;

import network.EnterpriseNetwork;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Instant;

public class DecryptCommand implements ICommand {
    PrintStream originalOut;

    public DecryptCommand(String query, boolean isDebug) {
        originalOut = System.out;
        String algorithm = "rsa"; // TODO: 31.03.2021 Set algo correct


        if (isDebug) {
            try {
                File logFile = new File("decrypt_" + algorithm + "_" + Instant.now().getEpochSecond() + ".txt");
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
        // Do stuff

        System.setOut(originalOut);
        return "";
    }
}
