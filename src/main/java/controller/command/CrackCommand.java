package controller.command;

import config.Configuration;
import components.JarUtil;
import network.EnterpriseNetwork;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CrackCommand implements ICommand {
    private final boolean isRsa;
    private final String message;
    private Object crackerPort;
    private File pubKeyFile;

    public CrackCommand(String query) {
        if (!query.matches("crack encrypted message \".*\" using ((shift)|(rsa and keyfile .*))")) {
            throw new UnsupportedOperationException("Invalid Syntax. Syntax for cracking is 'crack encrypted message \"[msg]\" using ((shift)|(rsa and keyfile [public_key_file]))'");
        }

        // Parse params
        int msgBegin = query.indexOf('"');
        int msgEnd = query.lastIndexOf('"');
        message = query.substring(msgBegin + 1, msgEnd);
        String modeInstructions = query.substring(msgEnd + 1);
        isRsa = modeInstructions.contains("using rsa and keyfile");
        try {
            if (isRsa) {
                crackerPort = JarUtil.loadVerifiedJar(Configuration.instance.pathToRsaCracker);
                pubKeyFile = new File(modeInstructions.substring(" using rsa and keyfile ".length()));
            } else {
                crackerPort = JarUtil.loadVerifiedJar(Configuration.instance.pathToShiftCracker);
            }
        } catch (IOException | InterruptedException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String execute(EnterpriseNetwork network) {
        if (crackerPort == null) {
            return "cracking encrypted message \"" + message + "\" failed";
        }

        Callable<String> crackMessage = () -> {
            try {
                if (isRsa) {
                    Method decryptMethod = crackerPort.getClass().getDeclaredMethod("decrypt", String.class, File.class);
                    return (String) decryptMethod.invoke(crackerPort, message, pubKeyFile);
                } else {
                    Method decryptMethod = crackerPort.getClass().getDeclaredMethod("decrypt", String.class);
                    return (String) decryptMethod.invoke(crackerPort, message);
                }
            } catch (Exception ex) {
                return ex.getMessage();
            }
        };
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        try {
            String toReturn = executorService.submit(crackMessage).get(30L, TimeUnit.SECONDS);
            executorService.shutdown();
            return toReturn;
        } catch (TimeoutException | InterruptedException | ExecutionException ex) {
            executorService.shutdown();
            return "cracking encrypted message \"" + message + "\" failed";
        }
    }
}
