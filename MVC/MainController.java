package com.example.examseatingarangementfinal;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {

    @FXML private TextField maxStudentsPerRoomField;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private TextField searchField;
    @FXML private TableView<StudentModel> studentTable;
    @FXML private TableColumn<StudentModel, String> courseCodeCol;
    @FXML private TableColumn<StudentModel, String> courseNameCol;
    @FXML private TableColumn<StudentModel, String> examDateTimeCol;
    @FXML private TableColumn<StudentModel, String> studentIdCol;
    @FXML private TableColumn<StudentModel, String> studentNameCol;
    @FXML private TableColumn<StudentModel, String> sectionCol;
    @FXML private TableView<SeatMapModel> roomTable;
    @FXML private TableColumn<SeatMapModel, String> roomNumberCol;
    @FXML private TableColumn<SeatMapModel, String> roomCourseCodeCol;
    @FXML private TableColumn<SeatMapModel, String> roomExamDateTimeCol;
    @FXML private TableColumn<SeatMapModel, String> roomSectionCol;
    @FXML private TableColumn<SeatMapModel, String> studentsCol;
    @FXML private TableColumn<SeatMapModel, String> seatMapCol;

    private List<StudentModel> students = new ArrayList<>();
    private List<StudentModel> filteredStudents = new ArrayList<>();
    private List<SeatMapModel> roomAssignments = new ArrayList<>();
    private ExamSeatingServiceModel examService = new ExamSeatingServiceModel();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeUIComponents();
        searchField.textProperty().addListener((observable, oldValue, newValue) ->
                updateFilteredStudents(newValue));
        maxStudentsPerRoomField.setText("30");
    }

    private void initializeUIComponents() {
        // Initialize sortComboBox
        sortComboBox.setItems(FXCollections.observableArrayList(
                "Course Code", "Exam Date/Time", "Section"));
        sortComboBox.setValue("Course Code");

        // Initialize table columns
        initializeTableColumns();

        // Setup seat map view button
        seatMapCol.setCellFactory(param -> new TableCell<SeatMapModel, String>() {
            private final Button seatMapButton = new Button("View");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(seatMapButton);
                    seatMapButton.setOnAction(event -> {
                        SeatMapModel roomAssignment = getTableView().getItems().get(getIndex());
                        showSeatMap(roomAssignment);
                    });
                }
            }
        });
    }

    private void initializeTableColumns() {
        // Student table
        courseCodeCol.setCellValueFactory(cellData -> cellData.getValue().courseCodeProperty());
        courseNameCol.setCellValueFactory(cellData -> cellData.getValue().courseNameProperty());
        examDateTimeCol.setCellValueFactory(cellData -> cellData.getValue().examDateTimeProperty());
        studentIdCol.setCellValueFactory(cellData -> cellData.getValue().studentIdProperty());
        studentNameCol.setCellValueFactory(cellData -> cellData.getValue().studentNameProperty());
        sectionCol.setCellValueFactory(cellData -> cellData.getValue().sectionProperty());

        // Room table
        roomNumberCol.setCellValueFactory(cellData -> cellData.getValue().roomNumberProperty());
        roomCourseCodeCol.setCellValueFactory(cellData -> cellData.getValue().courseCodeProperty());
        roomExamDateTimeCol.setCellValueFactory(cellData -> cellData.getValue().examDateTimeProperty());
        roomSectionCol.setCellValueFactory(cellData -> cellData.getValue().sectionProperty());
        studentsCol.setCellValueFactory(cellData -> cellData.getValue().studentCountProperty());
    }

    private void updateFilteredStudents(String searchText) {
        filteredStudents = examService.filterStudents(students, searchText);
        updateStudentTable();
    }

    @FXML
    private void loadCSVFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open CSV File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showOpenDialog(getStage());
        if (file == null) return;

        try {
            students = examService.loadStudentsFromCSV(file);
            filteredStudents = new ArrayList<>(students);
            updateStudentTable();
            HelpPopUpView.showAlert(Alert.AlertType.INFORMATION,
                    "Success", "CSV File Loaded",
                    "Successfully loaded " + students.size() + " student records.");
        } catch (IOException e) {
            HelpPopUpView.showAlert(Alert.AlertType.ERROR,
                    "Error", "Error Reading File",
                    "An error occurred while reading the file: " + e.getMessage());
        }
    }

    @FXML
    private void exportStudentCSV() {
        if (checkIfStudentsEmpty("export")) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Student Data");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("students.csv");

        File file = fileChooser.showSaveDialog(getStage());
        if (file == null) return;

        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(file))) {
            // Write header
            writer.println("Course Code,Course Name,Exam Date/Time,Student ID,Student Name,Section");

            // Write data
            for (StudentModel student : students) {
                writer.println(
                        student.getCourseCode() + "," +
                                student.getCourseName() + "," +
                                student.getExamDateTime() + "," +
                                student.getStudentId() + "," +
                                student.getStudentName() + "," +
                                student.getSection()
                );
            }

            HelpPopUpView.showAlert(Alert.AlertType.INFORMATION,
                    "Success", "Export Successful",
                    "Student data has been exported successfully.");
        } catch (IOException e) {
            HelpPopUpView.showAlert(Alert.AlertType.ERROR,
                    "Error", "Export Failed",
                    "Failed to export student data: " + e.getMessage());
        }
    }

    @FXML
    private void exportRoomAssignmentsCSV() {
        if (checkIfRoomsEmpty("export")) return;

        boolean success = ExamSeatingServiceModel.exportRoomAssignments(getStage(), roomAssignments);

        if (success) {
            HelpPopUpView.showAlert(Alert.AlertType.INFORMATION,
                    "Success", "Export Successful",
                    "Room assignments have been exported successfully.");
        }
    }

    @FXML
    private void assignRooms() {
        if (checkIfStudentsEmpty("assign rooms to")) return;

        try {
            int maxStudentsPerRoom = parseMaxStudents();
            if (maxStudentsPerRoom <= 0) return;

            String sortCriteria = sortComboBox.getValue();
            if (sortCriteria == null || sortCriteria.isEmpty()) {
                sortCriteria = "Course Code"; // Default value
            }

            roomAssignments = examService.assignRooms(students, sortCriteria, maxStudentsPerRoom);
            updateRoomTable();

            HelpPopUpView.showAlert(Alert.AlertType.INFORMATION,
                    "Success", "Rooms Assigned",
                    "Successfully assigned " + roomAssignments.size() + " rooms.");
        } catch (Exception e) {
            HelpPopUpView.showAlert(Alert.AlertType.ERROR,
                    "Error", "Assignment Failed",
                    "An error occurred while assigning rooms: " + e.getMessage());
        }
    }

    private int parseMaxStudents() {
        try {
            int value = Integer.parseInt(maxStudentsPerRoomField.getText().trim());
            if (value <= 0) {
                HelpPopUpView.showAlert(Alert.AlertType.ERROR,
                        "Error", "Invalid Input",
                        "Please enter a valid positive number for max students per room.");
            }
            return value;
        } catch (NumberFormatException e) {
            HelpPopUpView.showAlert(Alert.AlertType.ERROR,
                    "Error", "Invalid Input",
                    "Please enter a valid positive number for max students per room.");
            return -1;
        }
    }

    @FXML
    private void showAddStudentDialog() {
        Dialog<StudentModel> dialog = createStudentDialog("Add New Student", null);

        dialog.showAndWait().ifPresent(student -> {
            students.add(student);
            updateFilteredStudents(searchField.getText());
        });
    }

    @FXML
    private void editSelectedStudent() {
        StudentModel selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            HelpPopUpView.showAlert(Alert.AlertType.WARNING,
                    "No Selection", "No Student Selected",
                    "Please select a student to edit.");
            return;
        }

        Dialog<StudentModel> dialog = createStudentDialog("Edit Student", selectedStudent);

        dialog.showAndWait().ifPresent(updatedStudent -> {
            int index = students.indexOf(selectedStudent);
            students.set(index, updatedStudent);
            updateFilteredStudents(searchField.getText());
        });
    }

    private Dialog<StudentModel> createStudentDialog(String title, StudentModel student) {
        Dialog<StudentModel> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(title.replace("Student", "student information"));

        ButtonType actionButtonType = new ButtonType(
                student == null ? "Add" : "Save",
                ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(actionButtonType, ButtonType.CANCEL);

        GridPane grid = createStudentFormGrid(student);
        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> grid.getChildren().get(1).requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == actionButtonType) {
                return extractStudentFromGrid(grid);
            }
            return null;
        });

        return dialog;
    }

    @FXML
    private void deleteSelectedStudent() {
        StudentModel selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            HelpPopUpView.showAlert(Alert.AlertType.WARNING,
                    "No Selection", "No Student Selected",
                    "Please select a student to delete.");
            return;
        }

        boolean confirmed = HelpPopUpView.showConfirmation(
                "Confirm Deletion", "Delete Student",
                "Are you sure you want to delete this student?\n" +
                        "ID: " + selectedStudent.getStudentId() + "\n" +
                        "Name: " + selectedStudent.getStudentName());

        if (confirmed) {
            students.remove(selectedStudent);
            updateFilteredStudents(searchField.getText());
        }
    }

    @FXML
    private void clearAllStudents() {
        if (students.isEmpty()) {
            HelpPopUpView.showAlert(Alert.AlertType.INFORMATION,
                    "Information", "No Data",
                    "There are no students to clear.");
            return;
        }

        boolean confirmed = HelpPopUpView.showConfirmation(
                "Confirm Clear All", "Clear All Students",
                "Are you sure you want to clear all student data? This action cannot be undone.");

        if (confirmed) {
            students.clear();
            filteredStudents.clear();
            updateStudentTable();
        }
    }

    @FXML
    private void printRoomAssignments() {
        if (checkIfRoomsEmpty("print")) return;

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            HelpPopUpView.showAlert(Alert.AlertType.ERROR,
                    "No Printer", "No Printer Available",
                    "No printer was found.");
            return;
        }

        if (job.showPrintDialog(getStage())) {
            VBox printContent = createPrintContent();
            if (job.printPage(printContent)) {
                job.endJob();
                HelpPopUpView.showAlert(Alert.AlertType.INFORMATION,
                        "Print Successful", "Room Assignments Printed",
                        "Room assignments have been sent to the printer.");
            }
        }
    }

    @FXML
    private void showHelpDialog() {
        HelpPopUpView.showHelpDialog();
    }

    @FXML
    private void exit() {
        HelpPopUpView.exit();
    }

    // Helper methods
    private boolean checkIfStudentsEmpty(String action) {
        if (students.isEmpty()) {
            HelpPopUpView.showAlert(Alert.AlertType.WARNING,
                    "Warning", "No Data",
                    "Please load a CSV file first to " + action + " students.");
            return true;
        }
        return false;
    }

    private boolean checkIfRoomsEmpty(String action) {
        if (roomAssignments.isEmpty()) {
            HelpPopUpView.showAlert(Alert.AlertType.WARNING,
                    "Warning", "No Data",
                    "Please assign rooms first to " + action + " room assignments.");
            return true;
        }
        return false;
    }

    private Stage getStage() {
        return (Stage) maxStudentsPerRoomField.getScene().getWindow();
    }

    private void updateStudentTable() {
        studentTable.setItems(FXCollections.observableArrayList(filteredStudents));
        studentTable.refresh();
    }

    private void updateRoomTable() {
        roomTable.setItems(FXCollections.observableArrayList(roomAssignments));
        roomTable.refresh();
    }

    private void showSeatMap(SeatMapModel roomAssignment) {
        if (roomAssignment == null) {
            HelpPopUpView.showAlert(Alert.AlertType.ERROR, "Error", "No Data",
                    "Room assignment is null.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader();
            URL fxmlUrl = getClass().getResource("/com/example/examseatingarangementfinal/SeatMapView.fxml");

            if (fxmlUrl == null) {
                HelpPopUpView.showAlert(Alert.AlertType.ERROR, "Error", "Resource Not Found",
                        "Could not find SeatMapView.fxml resource.");
                return;
            }

            loader.setLocation(fxmlUrl);
            Parent root = loader.load();

            SeatMapController controller = loader.getController();
            controller.setRoomAssignment(roomAssignment);

            Stage stage = new Stage();
            stage.setTitle("Seat Map - " + roomAssignment.getRoomNumber());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(getStage());
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            HelpPopUpView.showAlert(Alert.AlertType.ERROR, "Loading Error", "Cannot Load Seat Map",
                    "Failed to load seat map: " + e.getMessage());
        }
    }

    private GridPane createStudentFormGrid(StudentModel student) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        String[] labels = {"Course Code:", "Course Name:", "Exam Date/Time:",
                "Student ID:", "Student Name:", "Section:"};
        String[] values = student != null ?
                new String[]{
                        student.getCourseCode(),
                        student.getCourseName(),
                        student.getExamDateTime(),
                        student.getStudentId(),
                        student.getStudentName(),
                        student.getSection()
                } :
                new String[]{"", "", "", "", "", ""};

        for (int i = 0; i < labels.length; i++) {
            grid.add(new Label(labels[i]), 0, i);
            grid.add(new TextField(values[i]), 1, i);
        }

        return grid;
    }

    private StudentModel extractStudentFromGrid(GridPane grid) {
        String[] fields = new String[6];
        for (int i = 0; i < 6; i++) {
            TextField field = (TextField) getNodeByRowColumnIndex(1, i, grid);
            fields[i] = field.getText().trim();
        }

        return new StudentModel(
                fields[0], // Course Code
                fields[1], // Course Name
                fields[2], // Exam Date/Time
                fields[3], // Student ID
                fields[4], // Student Name
                fields[5]  // Section
        );
    }

    private Node getNodeByRowColumnIndex(final int column, final int row, GridPane gridPane) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == column && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    private VBox createPrintContent() {
        VBox printContent = new VBox(10);
        printContent.setPadding(new javafx.geometry.Insets(20));

        Label title = new Label("Room Assignments Report");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        printContent.getChildren().add(title);
        printContent.getChildren().add(new Label("Generated on: " + java.time.LocalDate.now()));
        printContent.getChildren().add(new Separator());

        Map<String, List<SeatMapModel>> roomGroups = new HashMap<>();
        for (SeatMapModel assignment : roomAssignments) {
            String key = assignment.getCourseCode() + " - " + assignment.getSection();
            roomGroups.computeIfAbsent(key, k -> new ArrayList<>()).add(assignment);
        }

        for (Map.Entry<String, List<SeatMapModel>> entry : roomGroups.entrySet()) {
            VBox groupBox = new VBox(7);
            groupBox.setPadding(new javafx.geometry.Insets(10, 0, 10, 0));

            Label groupLabel = new Label(entry.getKey());
            groupLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            groupBox.getChildren().add(groupLabel);

            for (SeatMapModel ra : entry.getValue()) {
                Label roomLabel = new Label("Room: " + ra.getRoomNumber() +
                        " - Exam: " + ra.getExamDateTime() +
                        " - Students: " + ra.getStudentCount());
                groupBox.getChildren().add(roomLabel);

                // Add student list (only for smaller groups)
                if (ra.getStudents().size() <= 10) {
                    VBox studentBox = new VBox(2);
                    studentBox.setPadding(new javafx.geometry.Insets(0, 0, 0, 20));

                    for (StudentModel s : ra.getStudents()) {
                        studentBox.getChildren().add(
                                new Label(s.getStudentId() + " - " + s.getStudentName())
                        );
                    }
                    groupBox.getChildren().add(studentBox);
                }
            }

            groupBox.getChildren().add(new Separator());
            printContent.getChildren().add(groupBox);
        }

        return printContent;
    }
}
