package application;

import controller.CQLInterpreter;
import controller.IInterpreter;
import network.EnterpriseNetwork;

import java.util.List;

public class Application {
    public static void main(String[] args) {
        // Run Non-Interactive Simulation
        EnterpriseNetwork network = new EnterpriseNetwork();
        IInterpreter interpreter = new CQLInterpreter(network);

        List<String> queries = List.of(
                "show channel",
                "encrypt message \"vaccine for covid is stored in building abc\" using shift and keyfile keys/shiftkey1.json",
                "encrypt message \"vaccine for covid is stored in building abc\" using rsa and keyfile keys/rsa_1_priv.json",
                "decrypt message \"rwyyeja bkn ykrez eo opknaz ej xqehzejc wxy\" using shift and keyfile keys/shiftkey1.json",
                "decrypt message \"4;BzQX0xy8mlEQdOLlEHTi5R/W1fgeYVp0IHkc2w9A5UEVpuB2HVEBGxPXzn0PQOVBEHTi5R1RARsHNBfTH9bV+AdSVHYPQOVBH9bV+BeJGpgPQOVBF4kamADrqtsdUQEbE9fOfSB5HNsHUlR2D0DlQR/W1fgeYVp0D0DlQQP6V0gO4XjfH9bV+B6XvusHUlR2H9bV+B5hWnQIHjjOD0DlQRy8mlED+ldIEHTi5Q==\" using rsa and keyfile keys/rsa_1_priv.json",
                "crack encrypted message \"rwyyeja bkn ykrez eo opknaz ej xqehzejc wxy\" using shift",
                "crack encrypted message \"4;BzQX0xy8mlEQdOLlEHTi5R/W1fgeYVp0IHkc2w9A5UEVpuB2HVEBGxPXzn0PQOVBEHTi5R1RARsHNBfTH9bV+AdSVHYPQOVBH9bV+BeJGpgPQOVBF4kamADrqtsdUQEbE9fOfSB5HNsHUlR2D0DlQR/W1fgeYVp0D0DlQQP6V0gO4XjfH9bV+B6XvusHUlR2H9bV+B5hWnQIHjjOD0DlQRy8mlED+ldIEHTi5Q==\" using rsa and keyfile keys/rsa_1_pub.json",
                "register participant pfizer with type normal",
                "register participant astra_zen with type normal",
                "register participant jonjon with type intruder",
                "create channel vaccination from pfizer to astra_zen",
                "show channel",
                "drop channel vaccination",
                "create channel vaccination from pfizer to astra_zen",
                "intrude channel vaccination by jonjon",
                "send message \"vaccine for covid is stored in building abc\" from pfizer to astra_zen using shift and keyfile keys/shiftkey2.json",
                "send message \"vaccine for covid is stored in building abc\" from astra_zen to pfizer using rsa and keyfile keys/rsa_1_priv.json"
        );

        for (String query : queries) {
            System.out.println(interpreter.execute(query));
            System.out.println("--------");
        }
    }
}
