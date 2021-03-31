package application;

import controller.CQLInterpreter;
import controller.IInterpreter;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import network.EnterpriseNetwork;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;

public class GUI extends Application {
    private static TextArea outputArea;
    private final IInterpreter interpreter = new CQLInterpreter(new EnterpriseNetwork());
    private PrintStream originalPrintStream = null;

    public void start(Stage primaryStage) {
        primaryStage.setTitle("MSA | Mergentheim/Mosbach Security Agency");

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(15, 12, 15, 12));
        hBox.setSpacing(10);
        hBox.setStyle("-fx-background-color: #336699;");

        Button executeButton = new Button("Execute");
        executeButton.setPrefSize(100, 20);

        Button closeButton = new Button("Close");
        closeButton.setPrefSize(100, 20);

        TextArea commandLineArea = new TextArea();
        commandLineArea.setWrapText(true);

        TextArea outputArea = new TextArea();
        outputArea.setWrapText(true);
        outputArea.setEditable(false);
        GUI.outputArea = outputArea;

        executeButton.setOnAction(event -> {
            System.out.println("[execute] pressed");
            execute(commandLineArea, outputArea);
        });

        closeButton.setOnAction(actionEvent -> {
            System.out.println("[close] pressed");
            System.exit(0);
        });

        hBox.getChildren().addAll(executeButton, closeButton);

        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(25, 25, 25, 25));
        vbox.getChildren().addAll(hBox, commandLineArea, outputArea);

        Scene scene = new Scene(vbox, 950, 500);
        primaryStage.setScene(scene);

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case F3:
                    System.out.println("F3 -> Debug Mode");
                    interpreter.setDebugMode(!interpreter.isDebugMode());
                    break;
                case F5:
                    System.out.println("F5 -> Executing Query");
                    execute(commandLineArea, outputArea);
                    break;
                case F8:
                    System.out.println("F8 -> Opening Logfile");

                    File logDirectory = new File("log");
                    File[] logFiles = logDirectory.listFiles();

                    if (!logDirectory.exists() || !logDirectory.isDirectory() || logFiles == null || logFiles.length == 0) {
                        outputArea.setText("Could not load any logfiles.");
                        break;
                    }

                    Comparator<File> compareByTimestamps = (f1, f2) -> {
                        int ts1 = Integer.parseInt(f1.getName()
                                .split("\\.")[0]
                                .split("_")[2]
                        );
                        int ts2 = Integer.parseInt(f2.getName()
                                .split("\\.")[0]
                                .split("_")[2]
                        );
                        return Integer.compare(ts1, ts2);
                    };

                    File latestLog = Arrays.stream(logFiles).max(compareByTimestamps).get();
                    try {
                        FileInputStream fis = new FileInputStream(latestLog);
                        outputArea.setText(new String(
                                fis.readAllBytes()
                        ));
                        fis.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        outputArea.setText("Could not load latest logfile.");
                    }
                    break;
            }
        });
        primaryStage.show();
    }

    public static void outputMessage(String msg) {
        outputArea.appendText("\n" + msg);
    }


    private void execute(TextArea commandLineArea, TextArea outputArea) {
        String output;
        try {
            output = interpreter.execute(commandLineArea.getText());
        } catch (Exception ex) {
            output = "Could not execute query:\n" + ex.getMessage();
        }
        outputArea.setText(output);
    }
}