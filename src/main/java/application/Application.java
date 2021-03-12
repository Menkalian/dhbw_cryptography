package application;

import config.Configuration;
import encryption.EncryptionUtil;

public class Application {
    public static void main(String[] args) {
        // Run Non-Interactive Simulation
        try {
            Class.forName("data.Database", true, Application.class.getClassLoader());
            System.out.println(Configuration.instance.msaDatabase);
            System.out.println(EncryptionUtil.loadVerifiedJar("components/rsa/build/libs/rsa.jar"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
