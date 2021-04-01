package controller.command;

import data.Database;
import network.EnterpriseNetwork;
import network.client.EnterpriseBranch;
import network.client.Intruder;
import network.client.Participant;

public class RegisterCommand implements ICommand {
    String name;
    String type;
    Participant newParticipant;


    public RegisterCommand(String query) {
        //check if query matches
        if (!query.matches("register participant .* with type (normal|intruder)")) {
            throw new UnsupportedOperationException("Invalid Syntax. Syntax for decrypting is 'register participant [name] with type [normal | intruder]'");
        }

        // query : register participant [name] and type [normal | intruder]
        name = query.substring(21).split(" ")[0];
        type = query.substring(32 + name.length());
    }

    @Override
    public String execute(EnterpriseNetwork network) {

        for (int i = 0 ; i < network.getParticipants().size() ; i++) {
            if (network.getParticipants().get(i).getName().equals(name)) return "participant " + name + " already exists, using existing postbox_" + name;
        }

        if (type.equalsIgnoreCase("NORMAL")) {
            newParticipant = new EnterpriseBranch(name);
        } else if (type.equalsIgnoreCase("INTRUDER")) {
            newParticipant = new Intruder(name);
        } else {
            throw new UnsupportedOperationException("Unknown participant type: " + type);
        }
        Database.instance.createParticipant(newParticipant);
        network.getParticipants().add(newParticipant);

        return "participant " + name + " with type " + type + " registered and postbox_" + name + " created";
    }
}
