package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

@SuppressWarnings("unused")
public class AttendanceHistoryPage extends Application {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/attendancesystem?serverTimezone=UTC";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Attendance History");

        TextArea attendanceHistoryTextArea = new TextArea();
        attendanceHistoryTextArea.setEditable(false);
        attendanceHistoryTextArea.setWrapText(true);
        Button loadButton = new Button("Load Attendance History");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(attendanceHistoryTextArea, loadButton);

        loadButton.setOnAction(event -> {
            String attendanceHistory = retrieveAttendanceHistory();
            if (!attendanceHistory.isEmpty()) {
                attendanceHistoryTextArea.setText(attendanceHistory);
            } else {
                showErrorMessage("No attendance history found.");
            }
        });

        primaryStage.setScene(new Scene(layout, 400, 300));
        primaryStage.show();
    }

    private String retrieveAttendanceHistory() {
        StringBuilder attendanceHistory = new StringBuilder();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement statement = connection.createStatement()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE"});

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                if (tableName.startsWith("attendance_")) {
                    attendanceHistory.append("Table: ").append(tableName).append("\n");

                    String query = "SELECT * FROM " + tableName;
                    ResultSet resultSet = statement.executeQuery(query);

                    while (resultSet.next()) {
                        String name = resultSet.getString("name");
                        String matrixNo = resultSet.getString("matrix_no");
                        String course = resultSet.getString("course");
                        String phone = resultSet.getString("phone");
                        String status = resultSet.getString("status");
                        String timestamp = resultSet.getString("timestamp");

                        attendanceHistory.append("Name: ").append(name).append("\n");
                        attendanceHistory.append("Matrix Number: ").append(matrixNo).append("\n");
                        attendanceHistory.append("Course: ").append(course).append("\n");
                        attendanceHistory.append("Phone: ").append(phone).append("\n");
                        attendanceHistory.append("Status: ").append(status).append("\n");
                        attendanceHistory.append("Timestamp: ").append(timestamp).append("\n");
                        attendanceHistory.append("\n");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attendanceHistory.toString();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
