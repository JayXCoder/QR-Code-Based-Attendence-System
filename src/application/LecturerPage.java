package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LecturerPage extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Lecturer Page");

        Button startSessionButton = new Button("Start a New Session");
        startSessionButton.setOnAction(event -> {
            StartSessionPage startSessionPage = new StartSessionPage();
            startSessionPage.start(primaryStage);
        });

        Button searchStudentButton = new Button("Search Student");
        searchStudentButton.setOnAction(event -> {
            SearchStudentPage searchStudentPage = new SearchStudentPage();
            searchStudentPage.start(primaryStage);
        });

        Button checkAttendanceHistoryButton = new Button("Check Attendance History");
        checkAttendanceHistoryButton.setOnAction(event -> {
            AttendanceHistoryPage attendanceHistoryPage = new AttendanceHistoryPage();
            attendanceHistoryPage.start(primaryStage);
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(startSessionButton, searchStudentButton, checkAttendanceHistoryButton);

        primaryStage.setScene(new Scene(layout, 300, 200));
        primaryStage.show();
    }
}
