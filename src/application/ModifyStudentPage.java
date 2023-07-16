package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModifyStudentPage extends Application {
	private static final String DB_URL = "jdbc:mysql://localhost:3306/attendancesystem?serverTimezone=UTC";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Modify Student");

        Label studentIdLabel = new Label("Enter Matrix No:");
        TextField studentIdField = new TextField();
        Button retrieveButton = new Button("Retrieve");
        Label detailsLabel = new Label();
        TextField newNameField = new TextField();
        TextField newPhoneField = new TextField();
        TextField newCourseField = new TextField();
        Button updateButton = new Button("Update");

        retrieveButton.setOnAction(event -> {
            String matrixNo = studentIdField.getText();

            // Execute SQL query to retrieve student details
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                String sql = "SELECT * FROM students WHERE matrix_no = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, matrixNo);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String phone = resultSet.getString("phone");
                    String course = resultSet.getString("course");

                    String details = "Name: " + name + "\nPhone: " + phone + "\nCourse: " + course;
                    detailsLabel.setText(details);
                } else {
                    detailsLabel.setText("Student not found.");
                }
            } catch (SQLException e) {
                System.out.println("Failed to retrieve student details.");
                e.printStackTrace();
            }
        });

        updateButton.setOnAction(event -> {
            String matrixNo = studentIdField.getText();
            String newName = newNameField.getText();
            String newPhone = newPhoneField.getText();
            String newCourse = newCourseField.getText();

            // Execute SQL query to update student details
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                String sql = "UPDATE students SET name = ?, phone = ?, course = ? WHERE matrix_no = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, newName);
                statement.setString(2, newPhone);
                statement.setString(3, newCourse);
                statement.setString(4, matrixNo);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Student details updated successfully!");
                } else {
                    System.out.println("Student not found.");
                }
            } catch (SQLException e) {
                System.out.println("Failed to update student details.");
                e.printStackTrace();
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> {
            AdminPage adminPage = new AdminPage();
            adminPage.start(primaryStage);
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(studentIdLabel, studentIdField, retrieveButton, detailsLabel,
                new Label("Enter New Name:"), newNameField,
                new Label("Enter New Phone:"), newPhoneField,
                new Label("Enter New Course:"), newCourseField,
                updateButton, backButton);

        primaryStage.setScene(new Scene(layout, 650, 600));
        primaryStage.show();
    }
}
