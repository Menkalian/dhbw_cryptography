package application;

import encryption.EncryptionUtil;

import java.io.IOException;

public class Application {
    public static void main(String[] args) {
        // Run Non-Interactive Simulation
        try {
            System.out.println(EncryptionUtil.loadVerifiedJar("components/rsa/build/libs/rsa.jar"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
