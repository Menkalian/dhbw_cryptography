package controller.command;

import config.Configuration;
import data.Database;
import encryption.EncryptionUtil;
import event.MessageEvent;
import network.Channel;
import network.EnterpriseNetwork;
import network.client.Participant;

import java.io.File;

public class SendCommand implements ICommand {
    private final String plainMsg;
    private final String fromName;
    private final String toName;
    private final String algorithm;
    private final String keyfileName;

    public SendCommand(String query) {
        if (!query.matches("send message \".*\" from .+ to .+ using .+ and keyfile .+")) {
            throw new UnsupportedOperationException(
                    "Invalid Syntax. Syntax for sending is 'send message \"[msg]\" from [participant01] to [participant02] using [algorithm] and keyfile [filename]'"
            );
        }

        // Parse params (structure is already checked above, so we can assume a correct query here)
        String[] tokens = query.split(" ", 12);
        plainMsg = tokens[2].substring(1, tokens[2].length() - 1);
        fromName = tokens[4];
        toName = tokens[6];
        algorithm = tokens[8];
        keyfileName = tokens[11];
    }

    @Override
    public String execute(EnterpriseNetwork network) {
        Participant participantFrom = network.getParticipants().stream().filter(p -> p.getName().equals(fromName)).findFirst().orElse(null);
        Participant participantTo = network.getParticipants().stream().filter(p -> p.getName().equals(toName)).findFirst().orElse(null);
        Channel usedChannel = network.getChannels().stream()
                                     .filter(ch ->
                                                     (ch.getParticipant01().getName().equals(toName) && ch.getParticipant02().getName().equals(fromName)) ||
                                                     (ch.getParticipant02().getName().equals(toName) && ch.getParticipant01().getName().equals(fromName))
                                     ).findFirst().orElse(null);

        if (participantFrom == null) {
            return "no participant named " + fromName + " found";
        }
        if (participantTo == null) {
            return "no participant named " + toName + " found";
        }
        if (usedChannel == null) {
            return "no valid channel from " + fromName + " to " + toName;
        }

        String keyfile;
        File encryptionKey;
        if (algorithm.equals("rsa")) {
            if (keyfileName.contains("pub")) {
                keyfile = keyfileName + ";" + keyfileName.replace("pub", "priv");
            } else {
                keyfile = keyfileName.replace("priv", "pub") + ";" + keyfileName;
            }
            encryptionKey = new File(keyfile.split(";")[0]);
        } else {
            keyfile = keyfileName;
            encryptionKey = new File(keyfile);
        }

        String message;

        try {
            Object encryptorPort;
            if (algorithm.equals("rsa")) {
                encryptorPort = EncryptionUtil.loadVerifiedJar(Configuration.instance.pathToRsa);
            } else {
                encryptorPort = EncryptionUtil.loadVerifiedJar(Configuration.instance.pathToShift);
            }

            message = (String) encryptorPort.getClass().getDeclaredMethod("encrypt", String.class, File.class)
                                            .invoke(encryptorPort, plainMsg, encryptionKey);
        } catch (Exception ex) {
            System.err.println("Could not encrypt message");
            ex.printStackTrace();
            message = plainMsg;
        }

        MessageEvent messageEvent = new MessageEvent(
                participantFrom,
                usedChannel,
                message,
                algorithm,
                keyfile
        );

        Database.instance.createMessage(messageEvent, participantTo.getName(), plainMsg);
        network.publish(messageEvent);

        return toName + " received new message";
    }
}
