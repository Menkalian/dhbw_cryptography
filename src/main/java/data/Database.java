package data;

import config.Configuration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings({"FieldCanBeLocal", "SameParameterValue"})
public class Database {
    private final String driverPrefix = "jdbc:hsqldb:";
    private final String databaseUser = "sa";
    private final String databasePassword = "";

    private Connection dbConnection;

    private Database() {
        try {
            Class.forName(org.hsqldb.jdbcDriver.class.getCanonicalName());
            String databaseUrl = driverPrefix + Configuration.instance.databaseFile;
            dbConnection = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);

            // (Re)initialize DB
            String initScript = loadSql("initDb.sql");

            Statement statement = dbConnection.createStatement();
            statement.execute(initScript);
            statement.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Database could not be initialized! Shuting down!");
            System.exit(1);
        }

        Configuration.instance.msaDatabase = this;
    }

    private String loadSql(String filename) throws IOException {
        return loadSqlTemplate(filename, new HashMap<>());
    }

    private String loadSqlTemplate(String filename, HashMap<String, String> replacementData) throws IOException {
        String raw = new String(
                Objects.requireNonNull(
                        getClass().getClassLoader().getResourceAsStream("data/" + filename)
                ).readAllBytes()
        );

        for (Map.Entry<String, String> replacement : replacementData.entrySet()) {
            raw = raw.replaceAll(replacement.getKey(), replacement.getValue());
        }

        return raw;
    }
}
