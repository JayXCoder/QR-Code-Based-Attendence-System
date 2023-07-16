package application;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.core.MatOfByte;
import java.awt.image.DataBufferByte;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

@SuppressWarnings("unused")
public class StartSessionPage extends Application {
	
	static {
		System.loadLibrary("opencv_java480");
	}

    private boolean capturing;
    private String sessionDate;
    private Connection connection;
    private Document document;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/attendancesystem?serverTimezone=UTC";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";

    private static final String SAVE_PATH = "D:/Java/OOP_Try_2/Attendence/Attendance.pdf";
    private static final String TESSERACT_DATA_PATH = "C:/tessdata/";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Start Session");

        Button startSessionButton = new Button("Start Session");
        Button endSessionButton = new Button("End Session");

        startSessionButton.setOnAction(event -> startSession());
        endSessionButton.setOnAction(event -> endSession());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(startSessionButton, endSessionButton);

        primaryStage.setScene(new Scene(layout, 300, 200));
        primaryStage.show();
    }

    private void startSession() {
        capturing = true;
        sessionDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());

        ImageView imageView = new ImageView();

        VBox layout = new VBox(10);
        layout.getChildren().add(imageView);

        Stage sessionStage = new Stage();
        sessionStage.setTitle("Session");
        sessionStage.setScene(new Scene(layout, 640, 480));
        sessionStage.show();

        Thread captureThread = new Thread(() -> {
            if (webcam == null) {
                showAlert("Error", "Failed to open webcam.");
                return;
            }

            webcam.open();

            while (capturing) {
                BufferedImage bufferedImage = webcam.getImage();
                if (bufferedImage != null) {
                    try {
                        processImage(bufferedImage, imageView);
                    } catch (TesseractException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            webcam.close();
        });
        captureThread.setDaemon(true);
        captureThread.start();
    }

    private void processImage(BufferedImage image, ImageView imageView) throws TesseractException, IOException {
        // Convert BufferedImage to Mat
        Mat mat = bufferedImageToMat(image);

        // Perform QR code detection and decoding
        Result result = decodeQRCode(mat);
        if (result != null) {
            String matrixNumber = result.getText();
            Student student = retrieveStudentDetails(matrixNumber);
            if (student != null) {
                addToAttendanceTable(student);
            }
        }

        // Display the image in JavaFX ImageView
        Image fxImage = SwingFXUtils.toFXImage(image, null);
        Platform.runLater(() -> imageView.setImage(fxImage));
    }

    private Result decodeQRCode(Mat frame) throws TesseractException, IOException {
        BufferedImage bufferedImage = matToBufferedImage(frame);
        LuminanceSource luminanceSource = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(luminanceSource));

        // Decode the QR code
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(TESSERACT_DATA_PATH);
        String result = tesseract.doOCR(bufferedImage);

        return new Result(result, null, null, null);
    }

    private Student retrieveStudentDetails(String matrixNumber) {
        try {
            String query = "SELECT * FROM students WHERE matrix_no = ?";
            PreparedStatement statement = connection.prepareStatement(query);
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

    private void addToAttendanceTable(Student student) {
        try {
            String tableName = "attendance_" + sessionDate;
            String query = "INSERT INTO " + tableName + " (name, matrix_no, course, phone) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, student.getName());
            statement.setString(2, student.getMatrixNo());
            statement.setString(3, student.getCourse());
            statement.setString(4, student.getPhone());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void endSession() {
        capturing = false;
        disconnectFromDatabase();
        generateAttendancePDF();
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void disconnectFromDatabase() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createAttendanceTable() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement statement = connection.createStatement()) {
            String tableName = "attendance_" + java.time.LocalDate.now().toString().replace("-", "_");

            String createTableQuery = "CREATE TABLE " + tableName + " ("
                    + "name VARCHAR(100), "
                    + "matrix_no VARCHAR(10), "
                    + "course VARCHAR(100), "
                    + "phone VARCHAR(15), "
                    + "status VARCHAR(10), "
                    + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

            statement.executeUpdate(createTableQuery);

            System.out.println("Attendance table created successfully: " + tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void generateAttendancePDF() {
        try {
            document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, new FileOutputStream(SAVE_PATH));
            document.open();
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            Font contentFont = FontFactory.getFont(FontFactory.HELVETICA);

            PdfPCell cell1 = new PdfPCell(new Phrase("Name", headerFont));
            PdfPCell cell2 = new PdfPCell(new Phrase("Matrix No", headerFont));
            PdfPCell cell3 = new PdfPCell(new Phrase("Course", headerFont));
            PdfPCell cell4 = new PdfPCell(new Phrase("Phone", headerFont));

            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);

            String tableName = "attendance_" + sessionDate;
            String query = "SELECT * FROM " + tableName;
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String matrixNo = resultSet.getString("matrix_no");
                String course = resultSet.getString("course");
                String phone = resultSet.getString("phone");

                PdfPCell contentCell1 = new PdfPCell(new Phrase(name, contentFont));
                PdfPCell contentCell2 = new PdfPCell(new Phrase(matrixNo, contentFont));
                PdfPCell contentCell3 = new PdfPCell(new Phrase(course, contentFont));
                PdfPCell contentCell4 = new PdfPCell(new Phrase(phone, contentFont));

                table.addCell(contentCell1);
                table.addCell(contentCell2);
                table.addCell(contentCell3);
                table.addCell(contentCell4);
            }

            document.add(table);
            document.close();

            showAlert("Success", "Attendance PDF generated successfully.");
        } catch (DocumentException | FileNotFoundException | SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to generate Attendance PDF.");
        }
    }

    private BufferedImage matToBufferedImage(Mat mat) {
        // Convert OpenCV Mat to Java BufferedImage
        MatOfByte byteMat = new MatOfByte();
        Imgcodecs.imencode(".bmp", mat, byteMat);
        byte[] byteArray = byteMat.toArray();
        BufferedImage bufferedImage = null;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufferedImage = ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferedImage;
    }

    private Mat bufferedImageToMat(BufferedImage image) {
        // Convert Java BufferedImage to OpenCV Mat
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, data);
        return mat;
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
