package network.client;

import application.GUI;
import com.google.common.eventbus.Subscribe;
import components.JarUtil;
import config.Configuration;
import data.Database;
import event.MessageEvent;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Intruder extends Participant {
    public Intruder(String name) {
        super(name, ParticipantType.INTRUDER);
    }

    @Override
    @Subscribe
    public void receiveMessage(MessageEvent message) {
        Callable<String> crackMessage = () -> {
            try {
                Object encryptorPort;
                String msg;

                if (message.getAlgorithm().equals("rsa")) {
                    File encryptionKey = new File(message.getKeyFile().split(";")[0]);
                    encryptorPort = JarUtil.loadVerifiedJar(Configuration.instance.pathToRsaCracker);
                    msg = (String) encryptorPort.getClass().getDeclaredMethod("decrypt", String.class, File.class)
                                                .invoke(encryptorPort, message.getMessage(), encryptionKey);
                } else {
                    encryptorPort = JarUtil.loadVerifiedJar(Configuration.instance.pathToShiftCracker);
                    msg = (String) encryptorPort.getClass().getDeclaredMethod("decrypt", String.class)
                                                .invoke(encryptorPort, message.getMessage());
                }
                return msg;
            } catch (Exception ex) {
                ex.printStackTrace();
                return "";
            }
        };

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            String decrypted = executorService.submit(crackMessage).get(30L, TimeUnit.SECONDS);
            executorService.shutdown();

            Database.instance.insertMessageInPostbox(message.getFrom().getName(), getName(), decrypted);
            GUI.outputMessage("intruder " + getName() + " cracked message from participant " + message.getFrom().getName() + " | " + decrypted);
        } catch (TimeoutException | InterruptedException | ExecutionException ex) {
            executorService.shutdown();
            Database.instance.insertMessageInPostbox(message.getFrom().getName(), getName(), "");
            GUI.outputMessage("intruder " + getName() + " crack message from participant " + message.getFrom().getName() + " failed");
        }
    }
}
