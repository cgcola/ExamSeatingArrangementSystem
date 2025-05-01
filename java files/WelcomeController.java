package com.example.examseatingarrangementfinal;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeController {

    @FXML private Button startButton;
    @FXML private ProgressIndicator loadingIndicator;

    @FXML
    private void startApplication() {
        // Show loading indicator
        loadingIndicator.setVisible(true);
        startButton.setDisable(true);

        // Simulate loading process with a delay
        new Thread(() -> {
            try {
                Thread.sleep(1000); 
                Platform.runLater(() -> {
                    try {
                        // Load the main view
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/examseatingarrangementfinal/main_view.fxml"));
                        Parent root = loader.load();
                        Stage stage = (Stage) startButton.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Exam Seating Arrangement System");
                        stage.show();
                    } catch (IOException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Failed to Load Application");
                        alert.setContentText("An error occurred while loading the main application: " + e.getMessage());
                        alert.showAndWait();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
