package controller.command;

import data.Database;
import network.Channel;
import network.EnterpriseNetwork;

import java.util.List;

public class DropCommand implements ICommand {
    private final String channelName;

    public DropCommand(String query) {
        System.out.println(query);
        if (!query.matches("drop channel \\S*")) {
            throw new UnsupportedOperationException("Invalid Syntax. Syntax for dropping is 'drop channel [channel_name]'");
        }

        channelName = query.replace("drop channel ", "");
    }

    @Override
    public String execute(EnterpriseNetwork network) {
        List<Channel> channels = network.getChannels();
        Channel toDelete = channels.stream().filter(c -> c.getName().equals(channelName)).findFirst().orElse(null);

        if (toDelete == null) {
            return "unknown channel " + channelName;
        }

        channels.remove(toDelete);
        Database.instance.deleteChannel(channelName);

        network.getChannels().stream().map(Channel::getName).forEach(System.out::println);

        return "channel " + channelName + " deleted";
    }
}
