package controller.command;

import network.EnterpriseNetwork;

public interface ICommand {
    String  execute(EnterpriseNetwork network);
}
