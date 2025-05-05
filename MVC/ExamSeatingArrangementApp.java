package com.example.examseatingarangementfinal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ExamSeatingArrangementApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/examseatingarangementfinal/MainView.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Exam Seating Arrangement System aaaaaaa");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }
    // shows window

    public static void main(String[] args) {
        launch(args);
    }
}
