package Controller;

import Model.ExamSeatingServiceModel;
import Model.SeatMapModel;
import Model.StudentModel;
import View.HelpPopUpView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.io.*;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {

    @FXML private TextField maxStudentsPerRoomField;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private TextField searchField;
    @FXML private TableView<StudentModel> studentTable;
    @FXML private TableColumn<StudentModel, String> courseCodeCol, courseNameCol, examDateTimeCol,
            studentIdCol, studentNameCol, sectionCol;
    @FXML private TableView<SeatMapModel> roomTable;
    @FXML private TableColumn<SeatMapModel, String> roomNumberCol, roomCourseCodeCol, roomExamDateTimeCol,
            roomSectionCol, studentsCol, seatMapCol;

    private List<StudentModel> students = new ArrayList<>();
    private List<StudentModel> filteredStudents = new ArrayList<>();
    private List<SeatMapModel> roomAssignments = new ArrayList<>();
    private ExamSeatingServiceModel examService = new ExamSeatingServiceModel();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize UI components
        sortComboBox.setItems(FXCollections.observableArrayList("Course Code", "Exam Date/Time", "Section"));
        sortComboBox.setValue("Course Code");

        // Initialize table columns
        initializeTableColumns();

        // Add view button to seat map column
        seatMapCol.setCellFactory(param -> new TableCell<SeatMapModel, String>() {
            private final Button viewBtn = new Button("View");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewBtn);
                    viewBtn.setOnAction(e -> showSeatMap(getTableView().getItems().get(getIndex())));
                }
            }
        });

        // Setup search field listener
        searchField.textProperty().addListener((observable, oldValue, newValue) ->
                updateFilteredStudents(newValue));

        maxStudentsPerRoomField.setText("30");
    }

    private void initializeTableColumns() {
        // Student table
        courseCodeCol.setCellValueFactory(cell -> cell.getValue().courseCodeProperty());
        courseNameCol.setCellValueFactory(cell -> cell.getValue().courseNameProperty());
        examDateTimeCol.setCellValueFactory(cell -> cell.getValue().examDateTimeProperty());
        studentIdCol.setCellValueFactory(cell -> cell.getValue().studentIdProperty());
        studentNameCol.setCellValueFactory(cell -> cell.getValue().studentNameProperty());
        sectionCol.setCellValueFactory(cell -> cell.getValue().sectionProperty());

        // Room table
        roomNumberCol.setCellValueFactory(cell -> cell.getValue().roomNumberProperty());
        roomCourseCodeCol.setCellValueFactory(cell -> cell.getValue().courseCodeProperty());
        roomExamDateTimeCol.setCellValueFactory(cell -> cell.getValue().examDateTimeProperty());
        roomSectionCol.setCellValueFactory(cell -> cell.getValue().sectionProperty());
        studentsCol.setCellValueFactory(cell -> cell.getValue().studentCountProperty());
    }

    private void updateFilteredStudents(String searchText) {
        filteredStudents = examService.filterStudents(students, searchText);
        studentTable.setItems(FXCollections.observableArrayList(filteredStudents));
    }

    @FXML
    private void loadCSVFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open CSV File");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = chooser.showOpenDialog(getStage());
        if (file != null) {
            try {
                students = examService.loadStudentsFromCSV(file);
                filteredStudents = new ArrayList<>(students);
                updateFilteredStudents("");
                HelpPopUpView.showAlert(Alert.AlertType.INFORMATION, "Success", "CSV File Loaded",
                        "Successfully loaded " + students.size() + " student records.");
            } catch (IOException e) {
                HelpPopUpView.showAlert(Alert.AlertType.ERROR, "Error", "Error Reading File",
                        "An error occurred: " + e.getMessage());
            }
        }
    }

    @FXML
    private void exportStudentCSV() {
        if (checkEmptyData(students, "export", "students")) return;

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export Student Data");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        chooser.setInitialFileName("students.csv");

        File file = chooser.showSaveDialog(getStage());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("Course Code,Course Name,Exam Date/Time,Student ID,Student Name,Section");

                for (StudentModel student : students) {
                    writer.println(String.format("%s,%s,%s,%s,%s,%s",
                            student.getCourseCode(), student.getCourseName(), student.getExamDateTime(),
                            student.getStudentId(), student.getStudentName(), student.getSection()));
                }

                HelpPopUpView.showAlert(Alert.AlertType.INFORMATION, "Success", "Export Successful",
                        "Student data has been exported successfully.");
            } catch (IOException e) {
                HelpPopUpView.showAlert(Alert.AlertType.ERROR, "Error", "Export Failed",
                        "Failed to export: " + e.getMessage());
            }
        }
    }

    @FXML
    private void exportRoomAssignmentsCSV() {
        if (checkEmptyData(roomAssignments, "export", "room assignments")) return;

        if (ExamSeatingServiceModel.exportRoomAssignments(getStage(), roomAssignments)) {
            HelpPopUpView.showAlert(Alert.AlertType.INFORMATION, "Success", "Export Successful",
                    "Room assignments have been exported successfully.");
        }
    }

    @FXML
    private void assignRooms() {
        if (checkEmptyData(students, "assign rooms to", "students")) return;

        try {
            int maxStudentsPerRoom = parseMaxStudents();
            if (maxStudentsPerRoom <= 0) return;

            String sortCriteria = sortComboBox.getValue();
            roomAssignments = examService.assignRooms(students, sortCriteria, maxStudentsPerRoom);
            roomTable.setItems(FXCollections.observableArrayList(roomAssignments));

            HelpPopUpView.showAlert(Alert.AlertType.INFORMATION, "Success", "Rooms Assigned",
                    "Successfully assigned " + roomAssignments.size() + " rooms.");
        } catch (Exception e) {
            HelpPopUpView.showAlert(Alert.AlertType.ERROR, "Error", "Assignment Failed",
                    "Error assigning rooms: " + e.getMessage());
        }
    }

    @FXML
    private void showAddStudentDialog() {
        createStudentDialog("Add New Student", null).showAndWait().ifPresent(student -> {
            students.add(student);
            updateFilteredStudents(searchField.getText());
        });
    }

    @FXML
    private void editSelectedStudent() {
        StudentModel selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            HelpPopUpView.showAlert(Alert.AlertType.WARNING, "No Selection", "No Student Selected",
                    "Please select a student to edit.");
            return;
        }

        createStudentDialog("Edit Student", selected).showAndWait().ifPresent(updated -> {
            students.set(students.indexOf(selected), updated);
            updateFilteredStudents(searchField.getText());
        });
    }

    @FXML
    private void deleteSelectedStudent() {
        StudentModel selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            HelpPopUpView.showAlert(Alert.AlertType.WARNING, "No Selection", "No Student Selected",
                    "Please select a student to delete.");
            return;
        }

        if (HelpPopUpView.showConfirmation("Confirm Deletion", "Delete Student",
                "Are you sure you want to delete this student?\nID: " + selected.getStudentId() +
                        "\nName: " + selected.getStudentName())) {
            students.remove(selected);
            updateFilteredStudents(searchField.getText());
        }
    }

    @FXML
    private void clearAllStudents() {
        if (students.isEmpty()) {
            HelpPopUpView.showAlert(Alert.AlertType.INFORMATION, "Information", "No Data",
                    "There are no students to clear.");
            return;
        }

        if (HelpPopUpView.showConfirmation("Confirm Clear All", "Clear All Students",
                "Are you sure you want to clear all student data? This action cannot be undone.")) {
            students.clear();
            filteredStudents.clear();
            studentTable.setItems(FXCollections.observableArrayList(filteredStudents));
        }
    }

    @FXML
    private void printRoomAssignments() {
        if (checkEmptyData(roomAssignments, "print", "room assignments")) return;

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            HelpPopUpView.showAlert(Alert.AlertType.ERROR, "No Printer", "No Printer Available",
                    "No printer was found.");
            return;
        }

        if (job.showPrintDialog(getStage()) && job.printPage(createPrintContent())) {
            job.endJob();
            HelpPopUpView.showAlert(Alert.AlertType.INFORMATION, "Print Successful",
                    "Room Assignments Printed", "Room assignments have been sent to the printer.");
        }
    }

    @FXML private void showHelpDialog() { HelpPopUpView.showHelpDialog(); }

    @FXML private void exit() { HelpPopUpView.exit(); }

    // Helper methods
    private <T> boolean checkEmptyData(List<T> data, String action, String dataType) {
        if (data.isEmpty()) {
            HelpPopUpView.showAlert(Alert.AlertType.WARNING, "Warning", "No Data",
                    "Please load data first to " + action + " " + dataType + ".");
            return true;
        }
        return false;
    }

    private Stage getStage() {
        return (Stage) maxStudentsPerRoomField.getScene().getWindow();
    }

    private void showSeatMap(SeatMapModel room) {
        if (room == null) {
            HelpPopUpView.showAlert(Alert.AlertType.ERROR, "Error", "No Data",
                    "Room assignment is null.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/examseatingarangementfinal/SeatMapView.fxml"));
            Parent root = loader.load();

            SeatMapController controller = loader.getController();
            controller.setRoomAssignment(room);

            Stage stage = new Stage();
            stage.setTitle("Seat Map - " + room.getRoomNumber());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(getStage());
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            HelpPopUpView.showAlert(Alert.AlertType.ERROR, "Loading Error", "Cannot Load Seat Map",
                    "Failed to load seat map: " + e.getMessage());
        }
    }

    private Dialog<StudentModel> createStudentDialog(String title, StudentModel student) {
        Dialog<StudentModel> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(title.replace("Student", "student information"));

        ButtonType actionBtn = new ButtonType(student == null ? "Add" : "Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(actionBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        String[] labels = {"Course Code:", "Course Name:", "Exam Date/Time:",
                "Student ID:", "Student Name:", "Section:"};
        String[] values = student != null ?
                new String[]{student.getCourseCode(), student.getCourseName(), student.getExamDateTime(),
                        student.getStudentId(), student.getStudentName(), student.getSection()} :
                new String[]{"", "", "", "", "", ""};

        TextField[] fields = new TextField[6];
        for (int i = 0; i < labels.length; i++) {
            grid.add(new Label(labels[i]), 0, i);
            fields[i] = new TextField(values[i]);
            grid.add(fields[i], 1, i);
        }

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> fields[0].requestFocus());

        dialog.setResultConverter(buttonType -> {
            if (buttonType == actionBtn) {
                return new StudentModel(
                        fields[0].getText().trim(), // Course Code
                        fields[1].getText().trim(), // Course Name
                        fields[2].getText().trim(), // Exam Date/Time
                        fields[3].getText().trim(), // Student ID
                        fields[4].getText().trim(), // Student Name
                        fields[5].getText().trim()  // Section
                );
            }
            return null;
        });

        return dialog;
    }

    private int parseMaxStudents() {
        try {
            int value = Integer.parseInt(maxStudentsPerRoomField.getText().trim());
            if (value <= 0) {
                HelpPopUpView.showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input",
                        "Please enter a valid positive number for max students per room.");
            }
            return value;
        } catch (NumberFormatException e) {
            HelpPopUpView.showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input",
                    "Please enter a valid positive number for max students per room.");
            return -1;
        }
    }

    private VBox createPrintContent() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        Label title = new Label("Room Assignments Report");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        content.getChildren().addAll(
                title,
                new Label("Generated on: " + java.time.LocalDate.now()),
                new Separator()
        );

        Map<String, List<SeatMapModel>> roomGroups = new HashMap<>();
        for (SeatMapModel room : roomAssignments) {
            String key = room.getCourseCode() + " - " + room.getSection();
            roomGroups.computeIfAbsent(key, k -> new ArrayList<>()).add(room);
        }

        for (Map.Entry<String, List<SeatMapModel>> entry : roomGroups.entrySet()) {
            VBox groupBox = new VBox(7);
            groupBox.setPadding(new Insets(10, 0, 10, 0));

            Label groupLabel = new Label(entry.getKey());
            groupLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            groupBox.getChildren().add(groupLabel);

            for (SeatMapModel room : entry.getValue()) {
                groupBox.getChildren().add(new Label(String.format(
                        "Room: %s - Exam: %s - Students: %s",
                        room.getRoomNumber(), room.getExamDateTime(), room.getStudentCount())));

                // Add student list (only for smaller groups)
                if (room.getStudents().size() <= 10) {
                    VBox studentBox = new VBox(2);
                    studentBox.setPadding(new Insets(0, 0, 0, 20));

                    for (StudentModel student : room.getStudents()) {
                        studentBox.getChildren().add(new Label(
                                student.getStudentId() + " - " + student.getStudentName()));
                    }
                    groupBox.getChildren().add(studentBox);
                }
            }

            groupBox.getChildren().add(new Separator());
            content.getChildren().add(groupBox);
        }

        return content;
    }
}
