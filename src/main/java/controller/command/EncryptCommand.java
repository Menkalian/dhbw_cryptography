package controller.command;

import network.EnterpriseNetwork;

public class EncryptCommand implements ICommand {
    public EncryptCommand(String query, boolean isDebug) {

    }

    @Override
    public String execute(EnterpriseNetwork network) {
        return "";
    }
}
