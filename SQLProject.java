import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.text.FontWeight;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SQLProject extends Application {

    private Stage primaryStage;
    private Scene scene1, scene2, scene3, scene4;

    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        Label welcomeLabel = new Label("Welcome to SQL*Plus Graphical");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        Button startButton = new Button("Start");
        startButton.setOnAction(e -> primaryStage.setScene(scene2));
        VBox layout1 = new VBox(10);
        layout1.getChildren().addAll(welcomeLabel, startButton);
        layout1.setPadding(new Insets(20));
        scene1 = new Scene(layout1, 400, 400);
        Label selectLabel = new Label("Select command type:");
        Button sqlButton = new Button("SQL");
        Button plsqlButton = new Button("PLSQL");
        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> primaryStage.close());
        VBox layout2 = new VBox(10);
        layout2.getChildren().addAll(selectLabel, sqlButton, plsqlButton, exitButton);
        layout2.setPadding(new Insets(20));
        scene2 = new Scene(layout2, 400, 400);
        scene3 = createScene("Enter your SQL command:", "sqlplus", "-S", "c##AP/1234");
        scene4 = createScene("Enter your PLSQL command:", "plsql", "-S", "c##AP/1234");
        sqlButton.setOnAction(e -> primaryStage.setScene(scene3));
        plsqlButton.setOnAction(e -> primaryStage.setScene(scene4));
        primaryStage.setScene(scene1);
        primaryStage.setTitle("SQL*Plus Executor");
        primaryStage.show();
    }
    private Scene createScene(String labelStr, String program, String... args) {
        Label sqlLabel = new Label(labelStr);
        TextArea commandInput = new TextArea();
        Button executeButton = new Button("Execute");
        TextArea resultArea = new TextArea();
        executeButton.setOnAction(e -> {
            String command = commandInput.getText();
            executeCommandInBackground(command, program, args, resultArea);
        });
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(scene2));
        VBox layout = new VBox(10);
        layout.getChildren().addAll(sqlLabel, commandInput, executeButton, resultArea, backButton);
        layout.setPadding(new Insets(20));
        return new Scene(layout, 400, 400);
    }
    private void executeCommandInBackground(String command, String program, String[] args, TextArea resultArea) {
        new Thread(() -> {
            String result = executeCommand(command, program, args);
            resultArea.setText(result);
        }).start();
    }
    private String executeCommand(String command, String program, String... args) {
        StringBuilder result = new StringBuilder();
        try {
            String[] connectCommand = new String[args.length + 1];
            connectCommand[0] = program;
            System.arraycopy(args, 0, connectCommand, 1, args.length);
            ProcessBuilder processBuilder = new ProcessBuilder(connectCommand);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            process.getOutputStream().write((command + "\nexit\n").getBytes());
            process.getOutputStream().flush();
            BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = outputReader.readLine()) != null) {
                result.append(line).append("\n");
            }
            process.waitFor(); 
            process.destroy(); 
        } catch (IOException | InterruptedException e) {
            result.append("Error executing command: ").append(e.getMessage());
        }
        return result.toString();
    }
    public static void main(String[] args) {
        launch(args);
    }
}