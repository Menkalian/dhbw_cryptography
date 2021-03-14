package event;

import network.Channel;
import network.client.Participant;

public class MessageEvent extends Event {
    private final Participant from;
    private final Channel channel;
    private final String message;
    private final String algorithm;
    private final String keyFile;

    public MessageEvent(Participant from, Channel channel, String message, String algorithm, String keyFile) {
        this.from = from;
        this.channel = channel;
        this.message = message;
        this.algorithm = algorithm;
        this.keyFile = keyFile;
    }

    public Participant getFrom() {
        return from;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getKeyFile() {
        return keyFile;
    }
}
