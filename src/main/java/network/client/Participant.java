package network.client;

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
        // TODO: 14.03.2021 @Löh: Diese Zeile in den register participant Befehl übernehmen.
        // Database.instance.createParticipant(this);
    }

    public String getName() {
        return name;
    }

    public ParticipantType getType() {
        return type;
    }

    public abstract void receiveMessage(MessageEvent message);
}
