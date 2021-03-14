package network;

import com.google.common.eventbus.Subscribe;
import data.Database;
import event.MessageEvent;
import network.client.Intruder;
import network.client.Participant;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class Channel {
    private final List<Intruder> intruders = new LinkedList<>();
    private final String name;
    private final Participant participant01;
    private final Participant participant02;

    public Channel(String channelName, String participant01, String participant02, EnterpriseNetwork network) {
        this.name = channelName;
        this.participant01 = network.getParticipants().stream().filter(p -> p.getName().equals(participant01)).findFirst().orElse(null);
        this.participant02 = network.getParticipants().stream().filter(p -> p.getName().equals(participant02)).findFirst().orElse(null);
        if (this.participant01 == null || this.participant02 == null || this.participant01.equals(this.participant02)) {
            throw new RuntimeException("Could not create Channel");
        }

        // TODO: 14.03.2021 @Johannes: Diesen Konstruktor für create channel nutzen.

        network.subscribe(this);
        Database.instance.createChannel(this);
    }

    public Channel(String name, Participant participant01, Participant participant02) {
        this.name = name;
        this.participant01 = participant01;
        this.participant02 = participant02;
    }

    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        // Only handle if it is for this channel
        if (!Objects.equals(event.getChannel(), this))
            return;

        Participant to;
        if (event.getFrom().getName().equals(participant01.getName())) {
            to = participant02;
        } else {
            to = participant01;
        }

        to.receiveMessage(event);

        if (!intruders.isEmpty()) {
            // Remove (private) keyfile from Message

            String keyfile;
            if (event.getAlgorithm().equals("rsa")) {
                keyfile = event.getKeyFile().split(";")[0];
            } else {
                keyfile = "";
            }

            MessageEvent intruderMessage = new MessageEvent(event.getFrom(), event.getChannel(), event.getMessage(), event.getAlgorithm(), keyfile);
            intruders.forEach(i -> i.receiveMessage(intruderMessage));
        }
    }

    public String getName() {
        return name;
    }

    public Participant getParticipant01() {
        return participant01;
    }

    public Participant getParticipant02() {
        return participant02;
    }

    @Override
    public String toString() {
        return getName() + " | " + getParticipant01().getName() + " and " + getParticipant02().getName();
    }

    private void intrude(Intruder intruder) {
        intruders.add(intruder);
    }
}
