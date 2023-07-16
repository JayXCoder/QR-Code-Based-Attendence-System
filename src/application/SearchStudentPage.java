package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class SearchStudentPage extends Application {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/attendancesystem?serverTimezone=UTC";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Search Student");

        Label matrixNumberLabel = new Label("Matrix Number:");
        TextField matrixNumberTextField = new TextField();
        Button searchButton = new Button("Search");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(matrixNumberLabel, matrixNumberTextField, searchButton);

        searchButton.setOnAction(event -> {
            String matrixNumber = matrixNumberTextField.getText();
            Student student = retrieveStudentDetails(matrixNumber);
            if (student != null) {
                showStudentInformation(student);
            } else {
                showErrorMessage("Student not found.");
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> {
            LecturerPage lecturerPage = new LecturerPage();
            lecturerPage.start(primaryStage);
        });

        layout.getChildren().add(backButton);

        primaryStage.setScene(new Scene(layout, 300, 200));
        primaryStage.show();
    }

    private Student retrieveStudentDetails(String matrixNumber) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM students WHERE matrix_no = ?")) {
            statement.setString(1, matrixNumber);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String matrixNo = resultSet.getString("matrix_no");
                String course = resultSet.getString("course");
                String phone = resultSet.getString("phone");

                return new Student(name, matrixNo, course, phone);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void showStudentInformation(Student student) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Student Information");
        alert.setHeaderText(null);
        alert.setContentText("Name: " + student.getName() + "\nMatrix Number: " + student.getMatrixNo() +
                "\nCourse: " + student.getCourse() + "\nPhone: " + student.getPhone());
        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private class Student {
        private String name;
        private String matrixNo;
        private String course;
        private String phone;

        public Student(String name, String matrixNo, String course, String phone) {
            this.name = name;
            this.matrixNo = matrixNo;
            this.course = course;
            this.phone = phone;
        }

        public String getName() {
            return name;
        }

        public String getMatrixNo() {
            return matrixNo;
        }

        public String getCourse() {
            return course;
        }

        public String getPhone() {
            return phone;
        }
    }
}
