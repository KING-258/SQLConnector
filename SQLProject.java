import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class SQLProject extends Application {

    private Stage primaryStage;
    private Scene welcomeScene;
    private Scene commandScene;
    private Scene sqlScene;
    private Scene plsqlScene;
    private Scene tableScene;
    private TextField urlField, usernameField;
    private PasswordField passwordField;
    private TextArea queryTextArea;
    private TextArea resultTextArea;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setMaximized(true);

        VBox welcomeLayout = new VBox(20);
        welcomeLayout.setPadding(new Insets(20));
        Label welcomeLabel = new Label("Welcome to SQL Project by Amulya, Suchit and Tejas");
        Button startButton = new Button("Start");
        startButton.setOnAction(event -> primaryStage.setScene(commandScene));
        welcomeLayout.getChildren().addAll(welcomeLabel, startButton);
        welcomeScene = new Scene(welcomeLayout);

        VBox commandLayout = new VBox(20);
        commandLayout.setPadding(new Insets(20));
        Button sqlButton = new Button("SQL Command");
        Button plsqlButton = new Button("PLSQL Command");
        Button tableButton = new Button("Table Description");
        sqlButton.setOnAction(event -> primaryStage.setScene(sqlScene));
        plsqlButton.setOnAction(event -> primaryStage.setScene(plsqlScene));
        tableButton.setOnAction(event -> primaryStage.setScene(tableScene));
        commandLayout.getChildren().addAll(sqlButton, plsqlButton, tableButton);
        commandScene = new Scene(commandLayout);

        GridPane sqlLayout = new GridPane();
        sqlLayout.setPadding(new Insets(20));
        sqlLayout.setVgap(10);
        sqlLayout.setHgap(10);
        urlField = new TextField();
        usernameField = new TextField();
        passwordField = new PasswordField();
        queryTextArea = new TextArea();
        resultTextArea = new TextArea();
        Button executeSQLButton = new Button("Execute SQL");
        executeSQLButton.setOnAction(event -> executeSQLQuery());
        sqlLayout.add(new Label("Database URL:"), 0, 0);
        sqlLayout.add(urlField, 1, 0);
        sqlLayout.add(new Label("Username:"), 0, 1);
        sqlLayout.add(usernameField, 1, 1);
        sqlLayout.add(new Label("Password:"), 0, 2);
        sqlLayout.add(passwordField, 1, 2);
        sqlLayout.add(new Label("SQL Command:"), 0, 3);
        sqlLayout.add(queryTextArea, 1, 3);
        sqlLayout.add(executeSQLButton, 1, 4);
        sqlLayout.add(new Label("Result:"), 0, 5);
        sqlLayout.add(resultTextArea, 1, 5);
        sqlScene = new Scene(sqlLayout);

        GridPane plsqlLayout = new GridPane();
        plsqlLayout.setPadding(new Insets(20));
        plsqlLayout.setVgap(10);
        plsqlLayout.setHgap(10);
        urlField = new TextField();
        usernameField = new TextField();
        passwordField = new PasswordField();
        queryTextArea = new TextArea();
        resultTextArea = new TextArea();
        Button executePLSQLButton = new Button("Execute PLSQL");
        executePLSQLButton.setOnAction(event -> executePLSQLQuery());
        plsqlLayout.add(new Label("Database URL:"), 0, 0);
        plsqlLayout.add(urlField, 1, 0);
        plsqlLayout.add(new Label("Username:"), 0, 1);
        plsqlLayout.add(usernameField, 1, 1);
        plsqlLayout.add(new Label("Password:"), 0, 2);
        plsqlLayout.add(passwordField, 1, 2);
        plsqlLayout.add(new Label("PLSQL Command:"), 0, 3);
        plsqlLayout.add(queryTextArea, 1, 3);
        plsqlLayout.add(executePLSQLButton, 1, 4);
        plsqlLayout.add(new Label("Result:"), 0, 5);
        plsqlLayout.add(resultTextArea, 1, 5);
        plsqlScene = new Scene(plsqlLayout);

        GridPane tableLayout = new GridPane();
        tableLayout.setPadding(new Insets(20));
        tableLayout.setVgap(10);
        tableLayout.setHgap(10);
        TextField tableUrlField = new TextField();
        TextField tableNameField = new TextField();
        TextArea tableDescriptionTextArea = new TextArea();
        Button describeTableButton = new Button("Describe Table");
        describeTableButton.setOnAction(event -> describeTable(tableUrlField.getText(), tableNameField.getText(), tableDescriptionTextArea));
        tableLayout.add(new Label("Database URL:"), 0, 0);
        tableLayout.add(tableUrlField, 1, 0);
        tableLayout.add(new Label("Table Name:"), 0, 1);
        tableLayout.add(tableNameField, 1, 1);
        tableLayout.add(describeTableButton, 1, 2);
        tableLayout.add(new Label("Table Description:"), 0, 3);
        tableLayout.add(tableDescriptionTextArea, 1, 3);
        tableScene = new Scene(tableLayout);

        primaryStage.setTitle("SQL Project");
        primaryStage.setScene(welcomeScene);
        primaryStage.show();
    }

    private void executeSQLQuery() {
        String url = urlField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String query = queryTextArea.getText();
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            StringBuilder result = new StringBuilder();
            while (resultSet.next()) {
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    result.append(resultSet.getString(i)).append("\t");
                }
                result.append("\n");
            }
            resultTextArea.setText(result.toString());
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            resultTextArea.setText("Error: " + e.getMessage());
        }
    }

    private void executePLSQLQuery() {
        String url = urlField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String query = queryTextArea.getText();

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            CallableStatement statement = connection.prepareCall(query);
            boolean hasResultSet = statement.execute();

            if (hasResultSet) {
                ResultSet resultSet = statement.getResultSet();
                StringBuilder result = new StringBuilder();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (resultSet.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        result.append(resultSet.getString(i)).append("\t");
                    }
                    result.append("\n");
                }

                resultTextArea.setText(result.toString());
                resultSet.close();
            } else {
                int updateCount = statement.getUpdateCount();
                resultTextArea.setText("Update count: " + updateCount);
            }

            statement.close();
            connection.close();
        } catch (SQLException e) {
            resultTextArea.setText("Error: " + e.getMessage());
        }
    }

    private void describeTable(String url, String tableName, TextArea tableDescriptionTextArea) {
        try {
            Connection connection = DriverManager.getConnection(url, usernameField.getText(), passwordField.getText());
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rsColumns = metaData.getColumns(null, null, tableName, null);

            StringBuilder tableDescription = new StringBuilder();
            while (rsColumns.next()) {
                String columnName = rsColumns.getString("COLUMN_NAME");
                String columnType = rsColumns.getString("TYPE_NAME");
                int columnSize = rsColumns.getInt("COLUMN_SIZE");

                tableDescription.append(columnName).append("\t").append(columnType).append("\t").append(columnSize).append("\n");
            }

            tableDescriptionTextArea.setText(tableDescription.toString());

            rsColumns.close();
            connection.close();
        } catch (SQLException e) {
            tableDescriptionTextArea.setText("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
