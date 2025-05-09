package View;

import Model.SeatMapModel;
import Model.StudentModel;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;

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

    public static Dialog<StudentModel> createStudentDialog(String title, StudentModel student) {
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
