package controller.command;

import data.Database;
import network.Channel;
import network.EnterpriseNetwork;
import network.client.EnterpriseBranch;
import network.client.Participant;

public class CreateCommand implements ICommand {

    String name;
    String participant01;
    EnterpriseBranch branch1;
    String participant02;
    EnterpriseBranch branch2;
    Channel newChannel;

    public CreateCommand(String query) {
        if (!query.matches("create channel .* from .* to .*")) {
            throw new UnsupportedOperationException("Invalid Syntax. Syntax for creating is 'create channel [name] from [participant01] to [participant02]'");
        }

        // query : create channel [name] from [participant01] to [participant02]
        name = query.substring(15).split(" ")[0];
        participant01 = query.substring(21 + name.length()).split(" ")[0];
        participant02 = query.substring(25 + name.length() + participant01.length());

    }

    @Override
    public String execute(EnterpriseNetwork network) {

        //Get Participants and check for type normal
        for (int i = 0 ; i < network.getParticipants().size() ; i++) {
            if (network.getParticipants().get(i).getName().equals(participant01) && network.getParticipants().get(i).getType().equals(Participant.ParticipantType.NORMAL)) {
                branch1 = (EnterpriseBranch) network.getParticipants().get(i);
            } else if (network.getParticipants().get(i).getName().equals(participant02) && network.getParticipants().get(i).getType().equals(Participant.ParticipantType.NORMAL))
                branch2 = (EnterpriseBranch) network.getParticipants().get(i);
        }
        if (branch1 == null || branch2 == null) {
            return "Could not find at least one of the Participants";
        }

        for (Channel channel : Database.instance.getChannels()) {
            if (channel.getName().equals(name))
                return "channel " + name + " already exists";
            if ((channel.getParticipant01().equals(branch1) && channel.getParticipant02().equals(branch2)) || (channel.getParticipant02().equals(branch1) && channel.getParticipant01().equals(branch2)))
                return "communication channel between " + participant01 + " and " + participant02 + "already exists";
            if (branch1.equals(branch2))
                return participant01 + " and " + participant02 + " are identical - cannot create channel on itself";
        }

        //channel table entry + message
        newChannel = new Channel(name, participant01, participant02, network);

        return "channel " + name + " from " + participant01 + " to " + participant02 + " successfully created";
    }
}
