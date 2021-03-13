package controller;

import controller.command.CrackCommand;
import controller.command.CreateCommand;
import controller.command.DecryptCommand;
import controller.command.DropCommand;
import controller.command.EncryptCommand;
import controller.command.ICommand;
import controller.command.IntrudeCommand;
import controller.command.RegisterCommand;
import controller.command.SendCommand;
import controller.command.ShowCommand;
import network.EnterpriseNetwork;

import java.util.Locale;

public class CQLInterpreter implements IInterpreter {
    private final EnterpriseNetwork enterpriseNetwork;
    private boolean debugMode;

    public CQLInterpreter(EnterpriseNetwork enterpriseNetwork) {
        this.enterpriseNetwork = enterpriseNetwork;
    }

    @Override
    public boolean isDebugMode() {
        return debugMode;
    }

    @Override
    public void setDebugMode(boolean isDebugMode) {
        debugMode = isDebugMode;
    }

    @Override
    public String execute(String query) {
        return parse(query).execute(enterpriseNetwork);
    }

    private ICommand parse(String query) {
        return switch (query.split(" ")[0].toLowerCase(Locale.ROOT)) {
            case "encrypt" -> new EncryptCommand(query);
            case "decrypt" -> new DecryptCommand(query);
            case "crack" -> new CrackCommand(query);
            case "register" -> new RegisterCommand(query);
            case "create" -> new CreateCommand(query);
            case "show" -> new ShowCommand(query);
            case "drop" -> new DropCommand(query);
            case "intrude" -> new IntrudeCommand(query);
            case "send" -> new SendCommand(query);
            default -> throw new UnsupportedOperationException("No valid CQL Syntax");
        };
    }
}
