package com.example.examseatingarangementfinal;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

import static com.example.examseatingarangementfinal.CsvExporter.*;

public class MainController implements Initializable {

    @FXML private TextField maxStudentsPerRoomField;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private TextField searchField;

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> courseCodeCol;
    @FXML private TableColumn<Student, String> courseNameCol;
    @FXML private TableColumn<Student, String> examDateTimeCol;
    @FXML private TableColumn<Student, String> studentIdCol;
    @FXML private TableColumn<Student, String> studentNameCol;
    @FXML private TableColumn<Student, String> sectionCol;

    @FXML private TableView<RoomAssignment> roomTable;
    @FXML private TableColumn<RoomAssignment, String> roomNumberCol;
    @FXML private TableColumn<RoomAssignment, String> roomCourseCodeCol;
    @FXML private TableColumn<RoomAssignment, String> roomExamDateTimeCol;
    @FXML private TableColumn<RoomAssignment, String> roomSectionCol;
    @FXML private TableColumn<RoomAssignment, String> studentsCol;
    @FXML private TableColumn<RoomAssignment, String> seatMapCol;

    private List<Student> students = new ArrayList<>();
    private List<Student> filteredStudents = new ArrayList<>();
    private List<RoomAssignment> roomAssignments = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        sortComboBox.setItems(FXCollections.observableArrayList(
                "Course Code", "Exam Date/Time", "Section"));
        sortComboBox.setValue("Course Code");

        courseCodeCol.setCellValueFactory(cellData -> cellData.getValue().courseCodeProperty());
        courseNameCol.setCellValueFactory(cellData -> cellData.getValue().courseNameProperty());
        examDateTimeCol.setCellValueFactory(cellData -> cellData.getValue().examDateTimeProperty());
        studentIdCol.setCellValueFactory(cellData -> cellData.getValue().studentIdProperty());
        studentNameCol.setCellValueFactory(cellData -> cellData.getValue().studentNameProperty());
        sectionCol.setCellValueFactory(cellData -> cellData.getValue().sectionProperty());

        roomNumberCol.setCellValueFactory(cellData -> cellData.getValue().roomNumberProperty());
        roomCourseCodeCol.setCellValueFactory(cellData -> cellData.getValue().courseCodeProperty());
        roomExamDateTimeCol.setCellValueFactory(cellData -> cellData.getValue().examDateTimeProperty());
        roomSectionCol.setCellValueFactory(cellData -> cellData.getValue().sectionProperty());
        studentsCol.setCellValueFactory(cellData -> cellData.getValue().studentCountProperty());

        seatMapCol.setCellFactory(param -> new TableCell<RoomAssignment, String>() {
            private final Button seatMapButton = new Button("View");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(seatMapButton);
                    seatMapButton.setOnAction(event -> {
                        RoomAssignment roomAssignment = getTableView().getItems().get(getIndex());
                        showSeatMap(roomAssignment);
                    });
                }
            }
        });

        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterStudents(newValue);
            });
        }
    }

    private void filterStudents(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            filteredStudents = new ArrayList<>(students);
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            filteredStudents = new ArrayList<>();

            for (Student student : students) {
                if (student.getStudentId().toLowerCase().contains(lowerCaseFilter) ||
                        student.getStudentName().toLowerCase().contains(lowerCaseFilter) ||
                        student.getCourseCode().toLowerCase().contains(lowerCaseFilter) ||
                        student.getCourseName().toLowerCase().contains(lowerCaseFilter) ||
                        student.getSection().toLowerCase().contains(lowerCaseFilter)) {
                    filteredStudents.add(student);
                }
            }
        }
        updateStudentTable();
    }

    @FXML
    private void loadCSVFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open CSV File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showOpenDialog(maxStudentsPerRoomField.getScene().getWindow());
        if (file != null) {
            students.clear();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                // Skip header line
                reader.readLine();

                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length >= 6) {
                        String courseCode = data[0].trim();
                        String courseName = data[1].trim();
                        String examDateTime = data[2].trim();
                        String studentId = data[3].trim();
                        String studentName = data[4].trim();
                        String section = data[5].trim();

                        students.add(new Student(
                                courseCode, courseName, examDateTime,
                                studentId, studentName, section));
                    }
                }

                filteredStudents = new ArrayList<>(students);
                updateStudentTable();
                showAlert(Alert.AlertType.INFORMATION,
                        "Success", "CSV File Loaded",
                        "Successfully loaded " + students.size() + " student records.");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR,
                        "Error", "Error Reading File",
                        "An error occurred while reading the file: " + e.getMessage());
            }
        }
    }

    @FXML
    private void exportStudentCSV() {
        if (students.isEmpty()) {
            showAlert(Alert.AlertType.WARNING,
                    "Warning", "No Data",
                    "No student data to export.");
            return;
        }

        Stage stage = (Stage) maxStudentsPerRoomField.getScene().getWindow();
        boolean success = exportStudentData(stage, students);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION,
                    "Success", "Export Successful",
                    "Student data has been exported successfully.");
        } else {
            showAlert(Alert.AlertType.ERROR,
                    "Error", "Export Failed",
                    "Failed to export student data.");
        }
    }

    @FXML
    private void exportRoomAssignmentsCSV() {
        if (roomAssignments.isEmpty()) {
            showAlert(Alert.AlertType.WARNING,
                    "Warning", "No Data",
                    "No room assignments to export.");
            return;
        }

        Stage stage = (Stage) maxStudentsPerRoomField.getScene().getWindow();
        boolean success = exportStudentData(stage, students);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION,
                    "Success", "Export Successful",
                    "Room assignments have been exported successfully.");
        } else {
            showAlert(Alert.AlertType.ERROR,
                    "Error", "Export Failed",
                    "Failed to export room assignments.");
        }
    }

    @FXML
    private void assignRooms() {
        if (students.isEmpty()) {
            showAlert(Alert.AlertType.WARNING,
                    "Warning", "No Data",
                    "Please load a CSV file first.");
            return;
        }

        int maxStudentsPerRoom;
        try {
            maxStudentsPerRoom = Integer.parseInt(maxStudentsPerRoomField.getText().trim());
            if (maxStudentsPerRoom <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR,
                    "Error", "Invalid Input",
                    "Please enter a valid positive number for max students per room.");
            return;
        }

        // Group students by the selected criteria
        Map<String, List<Student>> groupedStudents = new HashMap<>();
        for (Student student : students) {
            String key;
            switch (sortComboBox.getValue()) {
                case "Course Code":
                    key = student.getCourseCode() + "_" + student.getExamDateTime() + "_" + student.getSection();
                    break;
                case "Exam Date/Time":
                    key = student.getExamDateTime() + "_" + student.getCourseCode() + "_" + student.getSection();
                    break;
                case "Section":
                    key = student.getSection() + "_" + student.getCourseCode() + "_" + student.getExamDateTime();
                    break;
                default:
                    key = student.getCourseCode() + "_" + student.getExamDateTime() + "_" + student.getSection();
            }

            if (!groupedStudents.containsKey(key)) {
                groupedStudents.put(key, new ArrayList<>());
            }
            groupedStudents.get(key).add(student);
        }

        // Assign rooms
        roomAssignments.clear();
        int roomCounter = 1;

        for (Map.Entry<String, List<Student>> entry : groupedStudents.entrySet()) {
            List<Student> studentGroup = entry.getValue();
            String[] keyParts = entry.getKey().split("_");

            // Calculate number of rooms needed
            int numRooms = (int) Math.ceil((double) studentGroup.size() / maxStudentsPerRoom);

            for (int i = 0; i < numRooms; i++) {
                String roomNumber = "Room " + roomCounter++;

                int startIdx = i * maxStudentsPerRoom;
                int endIdx = Math.min(startIdx + maxStudentsPerRoom, studentGroup.size());
                List<Student> roomStudents = studentGroup.subList(startIdx, endIdx);

                String courseCode = keyParts[0];
                String examDateTime = keyParts[1];
                String section = keyParts[2];

                if (sortComboBox.getValue().equals("Exam Date/Time")) {
                    courseCode = keyParts[1];
                    examDateTime = keyParts[0];
                    section = keyParts[2];
                } else if (sortComboBox.getValue().equals("Section")) {
                    section = keyParts[0];
                    courseCode = keyParts[1];
                    examDateTime = keyParts[2];
                }

                RoomAssignment roomAssignment = new RoomAssignment(
                        roomNumber, courseCode, examDateTime, section, roomStudents);
                roomAssignments.add(roomAssignment);
            }
        }

        updateRoomTable();

        // Show success message
        showAlert(Alert.AlertType.INFORMATION,
                "Success", "Rooms Assigned",
                "Successfully assigned " + roomAssignments.size() + " rooms.");
    }

    @FXML
    private void showAddStudentDialog() {
        // Create the custom dialog
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Add New Student");
        dialog.setHeaderText("Enter student information");

        // Set the button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create the grid for the form fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField courseCodeField = new TextField();
        TextField courseNameField = new TextField();
        TextField examDateTimeField = new TextField();
        TextField studentIdField = new TextField();
        TextField studentNameField = new TextField();
        TextField sectionField = new TextField();

        grid.add(new Label("Course Code:"), 0, 0);
        grid.add(courseCodeField, 1, 0);
        grid.add(new Label("Course Name:"), 0, 1);
        grid.add(courseNameField, 1, 1);
        grid.add(new Label("Exam Date/Time:"), 0, 2);
        grid.add(examDateTimeField, 1, 2);
        grid.add(new Label("Student ID:"), 0, 3);
        grid.add(studentIdField, 1, 3);
        grid.add(new Label("Student Name:"), 0, 4);
        grid.add(studentNameField, 1, 4);
        grid.add(new Label("Section:"), 0, 5);
        grid.add(sectionField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> courseCodeField.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Student(
                        courseCodeField.getText().trim(),
                        courseNameField.getText().trim(),
                        examDateTimeField.getText().trim(),
                        studentIdField.getText().trim(),
                        studentNameField.getText().trim(),
                        sectionField.getText().trim()
                );
            }
            return null;
        });

        Optional<Student> result = dialog.showAndWait();

        result.ifPresent(student -> {
            students.add(student);
            filteredStudents = new ArrayList<>(students);
            updateStudentTable();
        });
    }
    @FXML
    private void editSelectedStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showAlert(Alert.AlertType.WARNING,
                    "No Selection", "No Student Selected",
                    "Please select a student to edit.");
            return;
        }

        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Edit Student");
        dialog.setHeaderText("Edit student information");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField courseCodeField = new TextField(selectedStudent.getCourseCode());
        TextField courseNameField = new TextField(selectedStudent.getCourseName());
        TextField examDateTimeField = new TextField(selectedStudent.getExamDateTime());
        TextField studentIdField = new TextField(selectedStudent.getStudentId());
        TextField studentNameField = new TextField(selectedStudent.getStudentName());
        TextField sectionField = new TextField(selectedStudent.getSection());

        grid.add(new Label("Course Code:"), 0, 0);
        grid.add(courseCodeField, 1, 0);
        grid.add(new Label("Course Name:"), 0, 1);
        grid.add(courseNameField, 1, 1);
        grid.add(new Label("Exam Date/Time:"), 0, 2);
        grid.add(examDateTimeField, 1, 2);
        grid.add(new Label("Student ID:"), 0, 3);
        grid.add(studentIdField, 1, 3);
        grid.add(new Label("Student Name:"), 0, 4);
        grid.add(studentNameField, 1, 4);
        grid.add(new Label("Section:"), 0, 5);
        grid.add(sectionField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> courseCodeField.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Student(
                        courseCodeField.getText().trim(),
                        courseNameField.getText().trim(),
                        examDateTimeField.getText().trim(),
                        studentIdField.getText().trim(),
                        studentNameField.getText().trim(),
                        sectionField.getText().trim()
                );
            }
            return null;
        });

        Optional<Student> result = dialog.showAndWait();

        result.ifPresent(updatedStudent -> {
            int index = students.indexOf(selectedStudent);
            students.set(index, updatedStudent);
            filteredStudents = new ArrayList<>(students);
            updateStudentTable();
        });
    }

    @FXML
    private void deleteSelectedStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showAlert(Alert.AlertType.WARNING,
                    "No Selection", "No Student Selected",
                    "Please select a student to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Student");
        confirmAlert.setContentText("Are you sure you want to delete this student?\n" +
                "ID: " + selectedStudent.getStudentId() + "\n" +
                "Name: " + selectedStudent.getStudentName());

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            students.remove(selectedStudent);
            filteredStudents.remove(selectedStudent);
            updateStudentTable();
        }
    }

    @FXML
    private void clearAllStudents() {
        if (students.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION,
                    "Information", "No Data",
                    "There are no students to clear.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Clear All");
        confirmAlert.setHeaderText("Clear All Students");
        confirmAlert.setContentText("Are you sure you want to clear all student data? This action cannot be undone.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            students.clear();
            filteredStudents.clear();
            updateStudentTable();
        }
    }

    @FXML
    private void printRoomAssignments() {
        if (roomAssignments.isEmpty()) {
            showAlert(Alert.AlertType.WARNING,
                    "No Data", "No Room Assignments",
                    "Please assign rooms before printing.");
            return;
        }

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            boolean proceed = job.showPrintDialog(maxStudentsPerRoomField.getScene().getWindow());

            if (proceed) {
                VBox printContent = new VBox(10);
                printContent.setPadding(new javafx.geometry.Insets(20));
                printContent.getChildren().add(new Label("Room Assignments Report"));
                printContent.getChildren().add(new Label("Generated on: " + java.time.LocalDate.now()));
                printContent.getChildren().add(new Separator());

                Map<String, List<RoomAssignment>> roomGroups = new HashMap<>();
                for (RoomAssignment assignment : roomAssignments) {
                    String key = assignment.getCourseCode() + " - " + assignment.getSection();
                    if (!roomGroups.containsKey(key)) {
                        roomGroups.put(key, new ArrayList<>());
                    }
                    roomGroups.get(key).add(assignment);
                }

                for (Map.Entry<String, List<RoomAssignment>> entry : roomGroups.entrySet()) {
                    VBox groupBox = new VBox(5);
                    groupBox.setPadding(new javafx.geometry.Insets(10, 0, 10, 0));

                    Label groupLabel = new Label(entry.getKey());
                    groupLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    groupBox.getChildren().add(groupLabel);

                    for (RoomAssignment ra : entry.getValue()) {
                        Label roomLabel = new Label("Room: " + ra.getRoomNumber() +
                                " - Exam: " + ra.getExamDateTime() +
                                " - Students: " + ra.getStudentCount());
                        groupBox.getChildren().add(roomLabel);

                        // Add student list (optional, might make the output too long)
                        if (ra.getStudents().size() <= 10) {  // Only show students if there aren't too many
                            VBox studentBox = new VBox(2);
                            studentBox.setPadding(new javafx.geometry.Insets(0, 0, 0, 20));  // Indent student list

                            for (Student s : ra.getStudents()) {
                                Label studentLabel = new Label(s.getStudentId() + " - " + s.getStudentName());
                                studentBox.getChildren().add(studentLabel);
                            }

                            groupBox.getChildren().add(studentBox);
                        }
                    }

                    groupBox.getChildren().add(new Separator());
                    printContent.getChildren().add(groupBox);
                }

                boolean printed = job.printPage(printContent);
                if (printed) {
                    job.endJob();
                    showAlert(Alert.AlertType.INFORMATION,
                            "Print Successful", "Room Assignments Printed",
                            "Room assignments have been sent to the printer.");
                } else {
                    showAlert(Alert.AlertType.ERROR,
                            "Print Failed", "Printing Error",
                            "Failed to print room assignments.");
                }
            }
        } else {
            showAlert(Alert.AlertType.ERROR,
                    "No Printer", "No Printer Available",
                    "No printer was found.");
        }
    }

    @FXML
    private void showHelpDialog() {
        Alert helpAlert = new Alert(Alert.AlertType.INFORMATION);
        helpAlert.setTitle("Help");
        helpAlert.setHeaderText("Exam Seating Arrangement System - Help");

        String helpContent =
                        "1. Load CSV File: (CSV format: Course Code| Course Name| Exam Date/Time| Student ID | Student Name| Section)  \n\n" +
                        "2. Max Students per Room: Set the maximum number of students allowed in each room.\n\n" +
                        "3. Sort by: Choose how to group students (by Course Code, Exam Date/Time, or Section).\n\n" +
                        "4. Assign Rooms: Generate room assignments based on your settings.\n\n" +
                        "5. Student Data Tab: View, add, edit, or delete student records.\n\n" +
                        "6. Room Assignments Tab: View assigned rooms and seating maps.\n\n" +
                        "7. Export: Save student data or room assignments to CSV files.\n\n" +
                        "8. Print: Print room assignments for physical distribution.";

        helpAlert.setContentText(helpContent);
        helpAlert.showAndWait();
    }

    @FXML
    private void exit() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Exit");
        confirmAlert.setHeaderText("Exit Application");
        confirmAlert.setContentText("Are you sure you want to exit? Any unsaved data will be lost.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Platform.exit();
        }
    }

    private void updateStudentTable() {
        ObservableList<Student> studentData = FXCollections.observableArrayList(filteredStudents);
        studentTable.setItems(studentData);
    }

    private void updateRoomTable() {
        ObservableList<RoomAssignment> roomData = FXCollections.observableArrayList(roomAssignments);
        roomTable.setItems(roomData);
    }

    private void showSeatMap(RoomAssignment roomAssignment) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/examseatingarangementfinal/seat_map.fxml"));
            Parent root = loader.load();

            SeatMapController controller = loader.getController();
            controller.setRoomAssignment(roomAssignment);

            Stage stage = new Stage();
            stage.setTitle("Seat Map - " + roomAssignment.getRoomNumber());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(maxStudentsPerRoomField.getScene().getWindow());
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot Load Seat Map",
                    "Failed to load seat map: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
