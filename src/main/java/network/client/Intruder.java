package network.client;

import com.google.common.eventbus.Subscribe;
import event.MessageEvent;

public class Intruder extends Participant {
    public Intruder(String name) {
        super(name, ParticipantType.INTRUDER);
    }

    @Override
    @Subscribe
    public void receiveMessage(MessageEvent message) {
        String keyfile;
        if (message.getAlgorithm().equals("rsa")) {
            keyfile = message.getKeyFile().split(";")[0];
        } else {
            keyfile = "";
        }

        System.out.println(message);
        System.out.println(keyfile);
    }
}
