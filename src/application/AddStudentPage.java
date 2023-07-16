package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddStudentPage extends Application {
	private static final String DB_URL = "jdbc:mysql://localhost:3306/attendancesystem?serverTimezone=UTC";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Add New Student");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField matrixNoField = new TextField();
        matrixNoField.setPromptText("Matrix No");
        TextField courseField = new TextField();
        courseField.setPromptText("Course");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");

        Button addButton = new Button("Add");
        addButton.setOnAction(event -> {
            String name = nameField.getText();
            String matrixNo = matrixNoField.getText();
            String course = courseField.getText();
            String phone = phoneField.getText();

            // Execute SQL query to add student details
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                String sql = "INSERT INTO students (name, matrix_no, course, phone) VALUES (?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, name);
                statement.setString(2, matrixNo);
                statement.setString(3, course);
                statement.setString(4, phone);
                statement.executeUpdate();
                System.out.println("Student details added successfully!");
            } catch (SQLException e) {
                System.out.println("Failed to add student details.");
                e.printStackTrace();
            }

            // Redirect to AdminPage
            AdminPage adminPage = new AdminPage();
            adminPage.start(primaryStage);
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> {
            AdminPage adminPage = new AdminPage();
            adminPage.start(primaryStage);
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(nameField, matrixNoField, courseField, phoneField, addButton, backButton);

        primaryStage.setScene(new Scene(layout, 600, 550));
        primaryStage.show();
    }
}
