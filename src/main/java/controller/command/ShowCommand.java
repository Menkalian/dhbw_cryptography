package controller.command;

import network.Channel;
import network.EnterpriseNetwork;

import java.util.stream.Collectors;

public class ShowCommand implements ICommand {
    public ShowCommand(String query) {
        if (!query.equals("show channel")) {
            throw new UnsupportedOperationException("Invalid syntax. Suggested: 'show channel'");
        }
    }

    @Override
    public String execute(EnterpriseNetwork network) {
        return network.getChannels().stream().map(Channel::toString).collect(Collectors.joining("\n"));
    }
}
