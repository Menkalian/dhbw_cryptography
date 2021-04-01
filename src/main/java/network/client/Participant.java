package network.client;

import com.google.common.eventbus.Subscribe;
import data.Database;
import event.MessageEvent;

public abstract class Participant {
    private final String name;
    private final ParticipantType type;

    public enum ParticipantType {
        NORMAL(1),
        INTRUDER(2);

        private final int dbValue;

        ParticipantType(int dbValue) {
            this.dbValue = dbValue;
        }

        public int getDbValue() {
            return dbValue;
        }
    }

    public Participant(String name, ParticipantType type) {
        this.name = name;
        this.type = type;
        Database.instance.createParticipantPostbox(this);
    }

    public String getName() {
        return name;
    }

    public ParticipantType getType() {
        return type;
    }

    @Subscribe
    public abstract void receiveMessage(MessageEvent message);
}
