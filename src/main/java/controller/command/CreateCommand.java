package controller.command;

import network.EnterpriseNetwork;

public class CreateCommand implements ICommand {
    public CreateCommand(String query) {
        
    }

    @Override
    public String execute(EnterpriseNetwork network) {
        return "";
    }
}
