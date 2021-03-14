package network;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import data.Database;
import event.Event;
import network.client.Participant;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class EnterpriseNetwork {
    private final EventBus eventBus;
    private final List<Channel> channels;
    private final List<Participant> participants;

    public EnterpriseNetwork() {
        eventBus = new EventBus("EnterpriseNetwork");
        participants = new LinkedList<>();
        channels = new LinkedList<>();

        participants.addAll(Database.instance.getParticipants());
        channels.addAll(Database.instance.getChannels());
        channels.forEach(this::subscribe);
    }

    public void subscribe(Channel subscriber) {
        eventBus.register(subscriber);
    }

    public void publish(Event e) {
        eventBus.post(e);
    }

    @Subscribe
    public void onEvent(Object e) {
        System.out.println("Event received: " + e);
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public List<Participant> getParticipants() {
        return participants;
    }
}
