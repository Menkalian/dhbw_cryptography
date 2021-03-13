package controller.command;

import network.EnterpriseNetwork;

public class SendCommand implements ICommand {
    public SendCommand(String query) {

    }

    @Override
    public String execute(EnterpriseNetwork network) {
        return "";
    }
}
