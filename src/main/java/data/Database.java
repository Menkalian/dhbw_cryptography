package data;

import config.Configuration;
import network.Channel;
import network.client.Participant;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings({"FieldCanBeLocal", "SameParameterValue"})
public enum Database {
    instance;
    private final String driverPrefix = "jdbc:hsqldb:";
    private final String databaseUser = "sa";
    private final String databasePassword = "";
    private Connection dbConnection;

    Database() {
        try {
            Class.forName(org.hsqldb.jdbcDriver.class.getCanonicalName());
            String databaseUrl = driverPrefix + Configuration.instance.databaseFile;
            dbConnection = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);

            // (Re)initialize DB
            String dropScript = loadSql("dropTables.sql");
            String initScript = loadSql("initDb.sql");
            String initDataScript = loadSql("initData.sql");

            Statement statement = dbConnection.createStatement();
            statement.execute(dropScript);
            statement.execute(initScript);
            statement.execute(initDataScript);
            statement.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Database could not be initialized! Shuting down!");
            System.exit(1);
        }
    }

    public void createParticipant(Participant participant) {
        HashMap<String, String> params = new HashMap<>();
        params.put("{{NAME}}", participant.getName());
        params.put("{{TYPE}}", String.valueOf(participant.getType().getDbValue()));

        try {
            executeStatements(
                    loadSqlTemplate("createParticipant.sql.template", params),
                    loadSqlTemplate("createPostbox.sql.template", params)
            );
        } catch (IOException | SQLException e) {
            System.err.println("Could not update database");
            e.printStackTrace();
        }
    }

    public void createChannel(Channel channel) {
        HashMap<String, String> params = new HashMap<>();
        params.put("{{CHANNEL_NAME}}", channel.getName());
        params.put("{{NAME_01}}", channel.getParticipant01().getName());
        params.put("{{NAME_02}}", channel.getParticipant02().getName());

        try {
            executeStatements(
                    loadSqlTemplate("createChannel.sql.template", params)
            );
        } catch (IOException | SQLException e) {
            System.err.println("Could not update database");
            e.printStackTrace();
        }
    }

    private void executeStatements(String... sqlStatements) throws SQLException {
        Statement statement = dbConnection.createStatement();
        for (String sql : sqlStatements) {
            statement.executeUpdate(sql);
        }
        statement.closeOnCompletion();
        statement.close();
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
            raw = raw.replace(replacement.getKey(), replacement.getValue());
        }

        return raw;
    }
}
