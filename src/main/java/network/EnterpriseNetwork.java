package network;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import event.Event;
import network.client.EnterpriseBranch;
import network.client.Intruder;
import network.client.Participant;

import java.util.Collections;
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

        participants.add(new EnterpriseBranch("branch_hkg"));
        participants.add(new EnterpriseBranch("branch_cpt"));
        participants.add(new EnterpriseBranch("branch_sfo"));
        participants.add(new EnterpriseBranch("branch_syd"));
        participants.add(new EnterpriseBranch("branch_wuh"));
        participants.add(new Intruder("msa"));

        channels.add(new Channel("hkg_wuh", "branch_hkg", "branch_wuh", this));
        channels.add(new Channel("hkg_cpt", "branch_hkg", "branch_cpt", this));
        channels.add(new Channel("cpt_syd", "branch_cpt", "branch_syd", this));
        channels.add(new Channel("syd_sfo", "branch_syd", "branch_sfo", this));
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
        return Collections.unmodifiableList(channels);
    }

    public List<Participant> getParticipants() {
        return Collections.unmodifiableList(participants);
    }
}
