package View;

import Model.DateAndTime;
import Model.SeatMapModel;
import Model.StudentModel;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static Model.DateAndTime.TimePicker;

public class HelpPopUpView {

    public static void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    public static void showHelpDialog() {
        String helpContent =
                "1. Load CSV File: (CSV format: Course Code| Course Name| Exam Date/Time| Student ID | Student Name| Section)\n\n" +
                        "2. Max Students per Room: Set the maximum number of students allowed in each room.\n\n" +
                        "3. Sort by: Choose how to group students (by Course Code, Exam Date/Time, or Section).\n\n" +
                        "4. Assign Rooms: Generate room assignments based on your settings.\n\n" +
                        "5. Student Data Tab: View, add, edit, or delete student records.\n\n" +
                        "6. Room Assignments Tab: View assigned rooms and seating maps.\n\n" +
                        "7. Export: Save student data or room assignments to CSV files.\n\n" +
                        "8. Print: Print room assignments for physical distribution.";

        showAlert(Alert.AlertType.INFORMATION, "HELP", "Exam Seating Arrangement System", helpContent);
    }

    public static void exit() {
        if (showConfirmation("Confirm Exit", "Exit Application",
                "Are you sure you want to exit? Any unsaved data will be lost.")) {
            Platform.exit();
        }
    }


    // Method to update the course code to name mappings
    private static Map<String, String> courseCodeToNameMap = new HashMap<>();
    private static Map<String, String> courseNameToCodeMap = new HashMap<>();

    // Method to update the course code to name mappings
    public static void updateCourseCodeMap(List<StudentModel> students) {

        courseCodeToNameMap.clear();
        courseNameToCodeMap.clear(); // Clear reverse map

        for (StudentModel student : students) {
            if (student.getCourseCode() != null && !student.getCourseCode().isEmpty() &&
                    student.getCourseName() != null && !student.getCourseName().isEmpty()) {
                courseCodeToNameMap.put(student.getCourseCode().toUpperCase(), student.getCourseName());
                courseNameToCodeMap.put(student.getCourseName().toLowerCase(), student.getCourseCode());
            }
        }


    }


    public static Dialog<StudentModel> createStudentDialog(String title, StudentModel student, List<StudentModel> allStudents) {
        Dialog<StudentModel> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(title.replace("Student", "student information"));

        ButtonType actionBtn = new ButtonType(student == null ? "Add" : "Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(actionBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Define labels
        String[] labels = {"Course Code:", "Course Name:", "Exam Date/Time:",
                "Student ID:", "Student Name:", "Section:"};

        // Create text fields for most inputs
        TextField courseNameField = new TextField("");
        TextField studentIdField = new TextField("");
        TextField studentNameField = new TextField("");
        TextField sectionField = new TextField("");

        // Create ComboBox for course code with auto-complete
        ComboBox<String> courseCodeComboBox = new ComboBox<>();
        courseCodeComboBox.setEditable(true);


        // Make sure we have course codes in our map
        if (courseCodeToNameMap.isEmpty() && allStudents != null && !allStudents.isEmpty()) {
            updateCourseCodeMap(allStudents);
        }

        // Get unique course codes and sort them
        Set<String> uniqueCourseCodes = new HashSet<>(courseCodeToNameMap.keySet());
        if (allStudents != null) {
            for (StudentModel s : allStudents) {
                if (s.getCourseCode() != null && !s.getCourseCode().isEmpty()) {
                    uniqueCourseCodes.add(s.getCourseCode());
                }
            }
        }
        List<String> sortedCourseCodes = new ArrayList<>(uniqueCourseCodes);
        Collections.sort(sortedCourseCodes);

        // Populate ComboBox with course codes
        courseCodeComboBox.setItems(FXCollections.observableArrayList(sortedCourseCodes));

        // Update course name field when course code changes
        ChangeListener<String> courseCodeListener = (obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty()) {
                String trimmedCode = newVal.trim();
                if (courseCodeToNameMap.containsKey(trimmedCode)) {
                    courseNameField.setText(courseCodeToNameMap.get(trimmedCode));
                    courseNameField.setEditable(false);
                } else {
                    courseNameField.clear();
                    courseNameField.setEditable(true);
                }
            } else {
                courseNameField.clear();
                courseNameField.setEditable(true);
            }
        };

        courseCodeComboBox.getSelectionModel().selectedItemProperty().addListener(courseCodeListener);
        courseCodeComboBox.getEditor().textProperty().addListener(courseCodeListener);
        courseNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty()) {
                String lowerName = newVal.trim().toLowerCase();
                if (courseNameToCodeMap.containsKey(lowerName)) {
                    String code = courseNameToCodeMap.get(lowerName);
                    courseCodeComboBox.setValue(code);
                    courseCodeComboBox.setDisable(false); // Disable code entry if known
                } else {
                    courseCodeComboBox.setDisable(false); // Allow manual input
                }
            } else {
                courseCodeComboBox.setDisable(false);
            }
        });






        // DatePicker and TimePicker for exam date/time
        DatePicker datePicker = new DatePicker();
        ComboBox<LocalTime> timePicker = TimePicker(); // Using the imported TimePicker method
        datePicker.setValue(LocalDate.now());

        // Create a horizontal box to hold date and time pickers side by side
        HBox dateTimeBox = new HBox(10);
        dateTimeBox.getChildren().addAll(datePicker, timePicker);

        // Set values if editing an existing student
        if (student != null) {
            courseCodeComboBox.setValue(student.getCourseCode());
            courseNameField.setText(student.getCourseName());
            studentIdField.setText(student.getStudentId());
            studentNameField.setText(student.getStudentName());
            sectionField.setText(student.getSection());

            // Parse date and time from the existing value if possible
            try {
                String dateTimeStr = student.getExamDateTime();
                if (dateTimeStr != null && !dateTimeStr.isEmpty()) {
                    // Assuming format like "2025-05-11 14:30"
                    LocalDateTime dateTime = LocalDateTime.parse(
                            dateTimeStr,
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    );
                    datePicker.setValue(dateTime.toLocalDate());
                    timePicker.setValue(dateTime.toLocalTime());
                }
            } catch (Exception e) {
                // If parsing fails, leave the date/time pickers at default values
                System.err.println("Could not parse date/time: " + e.getMessage());
            }
        }

        // Remove characters only accepts Integer
        studentIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[\\d-]*")) {
                studentIdField.setText(oldValue);
            }
        });

        // Add labels and fields to the grid
        grid.add(new Label(labels[0]), 0, 0);
        grid.add(courseCodeComboBox, 1, 0);

        grid.add(new Label(labels[1]), 0, 1);
        grid.add(courseNameField, 1, 1);

        grid.add(new Label(labels[2]), 0, 2);
        grid.add(dateTimeBox, 1, 2);

        grid.add(new Label(labels[3]), 0, 3);
        grid.add(studentIdField, 1, 3);

        grid.add(new Label(labels[4]), 0, 4);
        grid.add(studentNameField, 1, 4);

        grid.add(new Label(labels[5]), 0, 5);
        grid.add(sectionField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Set initial focus
        Platform.runLater(() -> courseCodeComboBox.requestFocus());

        dialog.setResultConverter(buttonType -> {
            if (buttonType == actionBtn) {
                // Format the date and time as a single string
                String dateTimeStr = "";
                if (datePicker.getValue() != null && timePicker.getValue() != null) {
                    dateTimeStr = datePicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE) +
                            " " + timePicker.getValue().format(DateTimeFormatter.ofPattern("HH:mm"));
                }

                return new StudentModel(
                        courseCodeComboBox.getValue(),  // Course Code
                        courseNameField.getText().trim(), // Course Name
                        dateTimeStr,                    // Exam Date/Time
                        studentIdField.getText().trim(), // Student ID
                        studentNameField.getText().trim(), // Student Name
                        sectionField.getText().trim()   // Section
                );
            }
            return null;
        });

        return dialog;
    }



    public static VBox createPrintContent(List<SeatMapModel> roomAssignments) {
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
