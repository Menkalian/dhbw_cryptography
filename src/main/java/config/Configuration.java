package config;

import data.Database;

import java.io.File;

public enum Configuration {
    instance;
    public String jarsigner = System.getProperty("java.home", "") + File.separator + "bin" + File.separator + "jarsigner";
    public String tempKeystoreLocation = "signing.jks";
    public String keystorePass = "msa123";


    public String databaseFile = "data" + File.separator + "msa.db";

    public Database msaDatabase;
}
