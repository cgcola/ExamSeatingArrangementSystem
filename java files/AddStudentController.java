package com.example.examseatingarangementfinal;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AddStudentController implements Initializable {

    @FXML private TextField courseCodeField;
    @FXML private TextField courseNameField;
    @FXML private TextField examDateTimeField;
    @FXML private TextField studentIdField;
    @FXML private TextField studentNameField;
    @FXML private TextField sectionField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Student result;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        courseCodeField.requestFocus();
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (validateInputs()) {
            result = new Student(
                    courseCodeField.getText().trim(),
                    courseNameField.getText().trim(),
                    examDateTimeField.getText().trim(),
                    studentIdField.getText().trim(),
                    studentNameField.getText().trim(),
                    sectionField.getText().trim()
            );
            closeWindow(event);
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        result = null;
        closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private boolean validateInputs() {
        StringBuilder errorMessage = new StringBuilder();

        if (courseCodeField.getText().trim().isEmpty()) {
            errorMessage.append("Course Code is required.\n");
        }
        if (courseNameField.getText().trim().isEmpty()) {
            errorMessage.append("Course Name is required.\n");
        }
        if (examDateTimeField.getText().trim().isEmpty()) {
            errorMessage.append("Exam Date/Time is required.\n");
        }
        if (studentIdField.getText().trim().isEmpty()) {
            errorMessage.append("Student ID is required.\n");
        }
        if (studentNameField.getText().trim().isEmpty()) {
            errorMessage.append("Student Name is required.\n");
        }
        if (sectionField.getText().trim().isEmpty()) {
            errorMessage.append("Section is required.\n");
        }

        if (errorMessage.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "Please correct the following errors:", errorMessage.toString());
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public Student getResult() {
        return result;
    }
}
