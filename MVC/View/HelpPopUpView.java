package com.example.examseatingarangementfinal;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public class HelpPopUpView {


    public static void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public static boolean showConfirmation(String title, String header, String content) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle(title);
        confirmAlert.setHeaderText(header);
        confirmAlert.setContentText(content);

        Optional<ButtonType> result = confirmAlert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    public static void showHelpDialog() {
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
    public static void exit()
    {
        boolean confirmed = HelpPopUpView.showConfirmation(
                "Confirm Exit", "Exit Application",
                "Are you sure you want to exit? Any unsaved data will be lost.");

        if (confirmed) {
            Platform.exit();
        }
    }
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
