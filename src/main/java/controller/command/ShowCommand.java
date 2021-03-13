package controller.command;

import network.EnterpriseNetwork;

public class ShowCommand implements ICommand {
    public ShowCommand(String query) {
        
    }

    @Override
    public String execute(EnterpriseNetwork network) {
        return "";
    }
}
