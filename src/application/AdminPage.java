package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminPage extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Admin Page");

        Button generateQRButton = new Button("Generate QR Codes");
        generateQRButton.setOnAction(event -> {
            try {
                GenerateQRCodePage generateQRCodePage = new GenerateQRCodePage();
                generateQRCodePage.start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Button addStudentButton = new Button("Add New Student");
        addStudentButton.setOnAction(event -> {
            try {
                AddStudentPage addStudentPage = new AddStudentPage();
                addStudentPage.start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Button modifyStudentButton = new Button("Modify Existing Student");
        modifyStudentButton.setOnAction(event -> {
            try {
                ModifyStudentPage modifyStudentPage = new ModifyStudentPage();
                modifyStudentPage.start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(event -> {
            try {
                Main main = new Main();
                main.start(new Stage());
                primaryStage.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(generateQRButton, addStudentButton, modifyStudentButton, logoutButton);

        primaryStage.setScene(new Scene(layout, 300, 200));
        primaryStage.show();
    }
}
