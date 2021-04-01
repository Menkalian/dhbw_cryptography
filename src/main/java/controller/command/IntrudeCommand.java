package controller.command;

import network.Channel;
import network.EnterpriseNetwork;
import network.client.Intruder;

public class IntrudeCommand implements ICommand {
    String name;
    String participant;
    Intruder intruder;

    public IntrudeCommand(String query) {
        //check if query matches
        if (!query.matches("intrude channel .* by .*")) {
            throw new UnsupportedOperationException("Invalid Syntax. Syntax for decrypting is 'intrude channel [name] by [participant]'");
        }

        // query : intrude channel [name] by [participant]
        name = query.substring(16).split(" ")[0];
        participant = query.substring(20 + name.length());

    }

    @Override
    public String execute(EnterpriseNetwork network) {

        //channel name exists
        for (Channel channel : network.getChannels()) {
            if (channel.getName().equals(name)) {
                intruder = (Intruder) network.getParticipants().stream().filter(p -> p.getName().equals(participant)).findFirst().orElse(null);
                channel.intrude(intruder);

                return "Intruder " + participant + " intruded Channel " + channel.getName();
            }
        }

        return "Channel " + name + " not found, " + participant + " could not intrude";
    }
}
