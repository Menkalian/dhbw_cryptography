package network.client;

import com.google.common.eventbus.Subscribe;
import components.JarUtil;
import config.Configuration;
import data.Database;
import event.MessageEvent;

import java.io.File;

public class EnterpriseBranch extends Participant {
    public EnterpriseBranch(String name) {
        super(name, ParticipantType.NORMAL);
    }

    @Override
    @Subscribe
    public void receiveMessage(MessageEvent message) {
        if (message.getFrom().getName().equals(getName()))
            return;

        try {
            Object decryptorPort;
            if (message.getAlgorithm().equals("rsa")) {
                decryptorPort = JarUtil.loadVerifiedJar(Configuration.instance.pathToRsa);
            } else {
                decryptorPort = JarUtil.loadVerifiedJar(Configuration.instance.pathToShift);
            }

            String keyfileName;
            if (message.getAlgorithm().equals("rsa")) {
                keyfileName = message.getKeyFile().split(";")[1];
            } else {
                keyfileName = message.getKeyFile();
            }
            File keyfile = new File(keyfileName);

            String plain = (String) decryptorPort.getClass().getDeclaredMethod("decrypt", String.class, File.class)
                                                 .invoke(decryptorPort, message.getMessage(), keyfile);

            Database.instance.insertMessageInPostbox(message.getFrom().getName(), getName(), plain);

        } catch (Exception ex) {
            System.err.println("Something went wrong when receiving the Message.");
            ex.printStackTrace();
        }
    }
}
