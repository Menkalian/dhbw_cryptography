package controller.command;

import network.Channel;
import network.EnterpriseNetwork;
import network.client.Intruder;

public class IntrudeCommand implements ICommand {
    String name;
    String participant;
    Intruder intruder;

    public IntrudeCommand(String query) {

        // query : intrude channel [name] by [participant]
        name = query.substring(16).split(" ")[0];
        participant = query.substring(20 + name.length());

    }

    @Override
    public String execute(EnterpriseNetwork network) {

        // ToDo : returns hella stupid
        //channel name exists
        for (Channel channel: network.getChannels()) {
            if(channel.getName().matches(name))
                intruder = (Intruder) network.getParticipants().stream().filter(p -> p.getName().equals(participant)).findFirst().orElse(null);
                channel.intrude(intruder);

                return "Registered " + participant + " to Channel " + channel.getName();
        }

        return "Channel " + name + " not found, " + participant + " could not intrude";
    }
}
