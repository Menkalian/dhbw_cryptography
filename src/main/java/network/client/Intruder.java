package network.client;

import data.Database;
import event.MessageEvent;

public class Intruder extends Participant {
    public Intruder(String name) {
        super(name, ParticipantType.INTRUDER);
    }

    @Override
    public void receiveMessage(MessageEvent message) {
        System.out.println(message);
    }
}
