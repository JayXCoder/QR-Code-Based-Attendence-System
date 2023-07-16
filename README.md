# QR-Code-Based-Attendence-System

The Attendance Management System is a JavaFX-based application that provides functionalities for user authentication, student management, attendance tracking, and historical data analysis. It offers an intuitive user interface and utilizes JDBC for database connectivity, Java libraries like OpenCV and Tesseract OCR for image processing tasks, and QRCode library for generating QR codes.

## Features

- **Login:** Users can log in using their credentials. The system verifies the entered username and password against predefined values for admin and lecturer accounts.

- **Admin Functionalities:** Administrators have access to various actions, including generating QR codes for students, adding new student information, and modifying existing student details.

- **Student Management:** The system allows administrators to add new student information to the database and modify existing student details.

- **Attendance Tracking:** Lecturers can start new attendance sessions using a webcam. The system captures images, decodes QR codes, fetches student details from the database, and generates attendance records based on the captured data.

- **Search Student:** Lecturers can search for student information by entering a matrix number. The system retrieves the student's details from the database and displays them in an alert dialog.

- **Attendance History:** The system displays the attendance history stored in the database. It retrieves attendance data from multiple tables and presents it in a readable format using JavaFX's TextArea.

## Prerequisites

- Java Development Kit (JDK-12)
- JavaFX SDK
- Sarxos Webcam Library
- OpenCV Library
- Tesseract OCR Library
- QRCode Library
- MySQL Database

## Getting Started

1. Clone the repository: `git clone https://github.com/JayXCoder/QR-Code-Based-Attendence-System.git`
2. Import the project into your preferred IDE.
3. Install the required libraries and dependencies mentioned in the Prerequisites section.
4. Set up a MySQL database and update the database connection details in the code.
5. Compile and run the `Main` class to start the application.

## Usage

- Upon running the application, a login screen will appear. Enter the admin or lecturer credentials to access the respective functionalities.
- Navigate through the different pages using the provided buttons to perform desired actions, such as generating QR codes, adding/modifying student information, starting attendance sessions, searching for student details, and viewing attendance history.

## Contributing

Contributions to the Attendance Management System are welcome! If you have any suggestions, bug reports, or feature requests, please open an issue or submit a pull request.

## License

The Attendance Management System is open-source and distributed under the [MIT License](LICENSE). Feel free to modify and use the code as per your requirements.

## Acknowledgements

The Attendance Management System utilizes various open-source libraries and frameworks. We would like to thank the developers of these projects for their contributions.

- Sarxos Webcam Library: [GitHub Repository](https://github.com/sarxos/webcam-capture)
- OpenCV: [OpenCV Website](https://opencv.org/)
- Tesseract OCR: [Tesseract OCR Website](https://github.com/tesseract-ocr/tesseract)
- QRCode: [QRCode GitHub Repository](https://github.com/kenglxn/QRGen)

## Contact

For any questions or inquiries, please contact [Jay](mailto:jawaharganesh99jg@gmail.com).

