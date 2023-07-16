package application;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.glxn.qrgen.QRCode;

public class GenerateQRCodePage extends Application {
    private TextField matrixNumberField;
    private Button generateButton;
    private Button saveButton;
    private ImageView qrCodeImageView;

	private static final String DB_URL = "jdbc:mysql://localhost:3306/AttendanceSystem?serverTimezone=UTC";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";

    private String matrixNumber;
    private String studentName;

    public void generateQRCode(ActionEvent event) {
        matrixNumber = matrixNumberField.getText();

        if (matrixNumber.isEmpty()) {
            showAlert("Error", "Matrix Number cannot be empty.");
            return;
        }

        // Fetch student name from the database based on matrix number
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String query = "SELECT name FROM students WHERE matrix_no = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, matrixNumber);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                studentName = resultSet.getString("name");
            } else {
                showAlert("Error", "Student not found for the given matrix number.");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to fetch student details from the database.");
            return;
        }

        String qrCodeText = matrixNumber;

        ByteArrayOutputStream qrCodeStream = QRCode.from(qrCodeText).withSize(500, 500).stream(); // Specify the desired size here
        try {
            BufferedImage qrCodeImage = ImageIO.read(new ByteArrayInputStream(qrCodeStream.toByteArray()));
            Image image = SwingFXUtils.toFXImage(qrCodeImage, null);
            qrCodeImageView.setImage(image);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to generate QR Code.");
        }
    }

    public void saveQRCode(ActionEvent event) {
        if (qrCodeImageView.getImage() == null) {
            showAlert("Error", "Please generate a QR Code first.");
            return;
        }

        String fileName = matrixNumber + "-" + studentName + "QRCode.png";
        String savePath = "D:/Java/OOP_Try_2/QRCodes/" + fileName;  // Replace with your desired save path

        try {
            BufferedImage qrCodeImage = SwingFXUtils.fromFXImage(qrCodeImageView.getImage(), null);
            File outputFile = new File(savePath);
            ImageIO.write(qrCodeImage, "png", outputFile);
            showAlert("Success", "QR Code saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save QR Code.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void goBack(ActionEvent event) {
        AdminPage adminPage = new AdminPage();
        Stage primaryStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        adminPage.start(primaryStage);
    }

    @Override
    public void start(Stage primaryStage) {
        matrixNumberField = new TextField();
        generateButton = new Button("Generate QR Code");
        saveButton = new Button("Save QR Code");
        qrCodeImageView = new ImageView();

        generateButton.setOnAction(this::generateQRCode);
        saveButton.setOnAction(this::saveQRCode);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(matrixNumberField, generateButton, qrCodeImageView, saveButton);

        Scene scene = new Scene(layout, 700, 650);
        primaryStage.setTitle("Generate QR Code");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
