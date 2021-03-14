package application;

import controller.CQLInterpreter;
import controller.IInterpreter;
import network.EnterpriseNetwork;

public class Application {
    public static void main(String[] args) {
        // Run Non-Interactive Simulation
        EnterpriseNetwork network = new EnterpriseNetwork();
        IInterpreter interpreter = new CQLInterpreter(network);

        System.out.println(interpreter.execute("crack encrypted message \"caesar\" using rsa and keyfile keys/rsa_1_pub.json"));
    }
}
