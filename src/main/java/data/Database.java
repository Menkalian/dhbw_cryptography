package data;

import config.Configuration;
import event.MessageEvent;
import network.Channel;
import network.client.EnterpriseBranch;
import network.client.Intruder;
import network.client.Participant;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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

            if (countTables() < 5) { // 5 Tables is minimum for valid db-scheme
                executeStatements(dropScript, initScript, initDataScript);
            } else {
                executeStatements(initScript);
            }
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
                    loadSqlTemplate("createParticipant.sql.template", params)
            );
        } catch (IOException | SQLException e) {
            System.err.println("Could not update database");
            e.printStackTrace();
        }
    }

    public void createParticipantPostbox(Participant participant) {
        HashMap<String, String> params = new HashMap<>();
        params.put("{{NAME}}", participant.getName());

        try {
            executeStatements(
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

    public void deleteChannel(String name) {
        HashMap<String, String> params = new HashMap<>();
        params.put("{{NAME}}", name);

        try {
            executeStatements(
                    loadSqlTemplate("deleteChannel.sql.template", params)
            );
        } catch (IOException | SQLException e) {
            System.err.println("Could not update database");
            e.printStackTrace();
        }
    }

    public void createMessage(MessageEvent messageEvent, String participantTo, String plainMessage) {
        HashMap<String, String> params = new HashMap<>();
        params.put("{{FROM_NAME}}", messageEvent.getFrom().getName());
        params.put("{{TO_NAME}}", participantTo);
        params.put("{{PLAIN}}", plainMessage);
        params.put("{{ALGO}}", messageEvent.getAlgorithm());
        params.put("{{CIPHER}}", messageEvent.getMessage());
        params.put("{{KEY}}", messageEvent.getKeyFile());

        try {
            executeStatements(
                    loadSqlTemplate("createMessage.sql.template", params)
            );
        } catch (IOException | SQLException e) {
            System.err.println("Could not update database");
            e.printStackTrace();
        }
    }

    public void insertMessageInPostbox(String participantFrom, String participantTo, String plainMessage) {
        HashMap<String, String> params = new HashMap<>();
        params.put("{{FROM_NAME}}", participantFrom);
        params.put("{{TO_NAME}}", participantTo);
        params.put("{{PLAIN}}", plainMessage);

        try {
            String sql = loadSqlTemplate("createPostboxEntry.sql.template", params);
            System.out.println(sql);
            executeStatements(
                    sql
            );
        } catch (IOException | SQLException e) {
            System.err.println("Could not update database");
            e.printStackTrace();
        }
    }

    public List<Participant> getParticipants() {
        try {
            Statement stmt = dbConnection.createStatement();
            ResultSet result = stmt.executeQuery(loadSql("selectParticipants.sql"));
            List<Participant> toReturn = new LinkedList<>();

            while (result.next()) {
                if (result.getInt("type_id") == 1) {
                    toReturn.add(new EnterpriseBranch(result.getString("name")));
                } else {
                    toReturn.add(new Intruder(result.getString("name")));
                }
            }

            return toReturn;
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Channel> getChannels() {
        try {
            Statement stmt = dbConnection.createStatement();
            ResultSet result = stmt.executeQuery(loadSql("selectChannels.sql"));
            List<Participant> participants = getParticipants();
            List<Channel> toReturn = new LinkedList<>();

            while (result.next()) {
                Participant p01 = participants.stream().filter(p -> {
                    try {
                        return p.getName().equals(result.getString("participant01"));
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    return false;
                }).findFirst().orElse(null);
                Participant p02 = participants.stream().filter(p -> {
                    try {
                        return p.getName().equals(result.getString("participant02"));
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    return false;
                }).findFirst().orElse(null);

                toReturn.add(new Channel(
                        result.getString("name"),
                        p01,
                        p02
                ));
            }

            return toReturn;
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    private int countTables() {
        try {
            Statement stmt = dbConnection.createStatement();
            ResultSet result = stmt.executeQuery(loadSql("countTables.sql"));
            result.next();
            return result.getInt("cnt");
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    private void executeStatements(String... sqlStatements) throws SQLException {
        Statement statement = dbConnection.createStatement();
        for (String sql : sqlStatements) {
            statement.executeUpdate(sql);
        }
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
