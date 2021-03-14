package network.client;

import event.MessageEvent;

public class EnterpriseBranch extends Participant {
    public EnterpriseBranch(String name) {
        super(name, ParticipantType.NORMAL);
    }

    @Override
    public void receiveMessage(MessageEvent message) {
        System.out.println(message);
    }
}
