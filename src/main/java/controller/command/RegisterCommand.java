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

        // query : register participant [name] and type [normal | intruder]
        name = query.substring(21).split(" ")[0];
        type = query.substring(31+name.length());

    }

    @Override
    public String execute(EnterpriseNetwork network) {

        for (int i = 0; i < network.getParticipants().size(); i++) {
            if(network.getParticipants().get(i).getName().matches(name)) return "participant " + name + " already exists, using existing postbox_" + name;
        }

        if(type.matches("NORMAL")) {
            newParticipant = new EnterpriseBranch(name);
        }
        if(type.matches("INTRUDER"))
            newParticipant = new Intruder(name);
        Database.instance.createParticipant(newParticipant);

        //ToDo : Was returnen / Branch zu network hinzufügen und bei Intruder?
        return "participant " + name + " with type " + type + "registered and postbox_" + name+ " created";
    }
}
