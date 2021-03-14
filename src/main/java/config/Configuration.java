package config;

import java.io.File;

public enum Configuration {
    instance;
    public String jarsigner = System.getProperty("java.home", "") + File.separator + "bin" + File.separator + "jarsigner";
    public String tempKeystoreLocation = "signing.jks";
    public String keystorePass = "msa123";

    public String pathToRsa = buildJarPath("rsa");
    public String pathToShift = buildJarPath("shift");
    public String pathToRsaCracker = buildJarPath("rsa_cracker");
    public String pathToShiftCracker = buildJarPath("shift_cracker");

    public String databaseFile = "data" + File.separator + "msa.db";

    private String buildJarPath(String component) {
        return "components" + File.separator + component + File.separator + "build" + File.separator + "libs" + File.separator + component + ".jar";
    }
}
