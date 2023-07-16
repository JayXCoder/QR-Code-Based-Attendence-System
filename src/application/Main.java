package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    private static final String ADMIN_ID = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String LECTURER_ID = "lecturer";
    private static final String LECTURER_PASSWORD = "lecturer123";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        Button loginButton = new Button("Login");

        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.equals(ADMIN_ID) && password.equals(ADMIN_PASSWORD)) {
                // Redirect to AdminPage
                AdminPage adminPage = new AdminPage();
                adminPage.start(primaryStage);
            } else if (username.equals(LECTURER_ID) && password.equals(LECTURER_PASSWORD)) {
                // Redirect to LecturerPage
                LecturerPage lecturerPage = new LecturerPage();
                lecturerPage.start(primaryStage);
            } else {
                showAlert("Error", "Invalid credentials");
            }
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(usernameField, passwordField, loginButton);

        primaryStage.setScene(new Scene(layout, 500, 500));
        primaryStage.show();
    }

    private void showAlert(String title, String message) {
        // Implement your alert logic here
    }
}