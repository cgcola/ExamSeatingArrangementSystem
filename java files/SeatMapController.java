package com.example.examseatingarangementfinal;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SeatMapController {

    @FXML private Label roomInfoLabel;
    @FXML private GridPane seatGrid;

    private RoomAssignment roomAssignment;

    public void setRoomAssignment(RoomAssignment roomAssignment) {
        this.roomAssignment = roomAssignment;

        // Set room info
        roomInfoLabel.setText(
                "Room: " + roomAssignment.getRoomNumber() + "\n" +
                        "Course: " + roomAssignment.getCourseCode() + "\n" +
                        "Exam Date/Time: " + roomAssignment.getExamDateTime() + "\n" +
                        "Section: " + roomAssignment.getSection() + "\n" +
                        "Total Students: " + roomAssignment.getStudents().size()
        );

        seatGrid.getChildren().clear();

        int totalStudents = roomAssignment.getStudents().size();
        int columns = (int) Math.ceil(Math.sqrt(totalStudents));
        int rows = (int) Math.ceil((double) totalStudents / columns);

        for (int i = 0; i < totalStudents; i++) {
            Student student = roomAssignment.getStudents().get(i);

            VBox seatBox = new VBox(5);
            Label seatNumLabel = new Label("Seat " + (i + 1));
            Label idLabel = new Label("ID: " + student.getStudentId());
            Label nameLabel = new Label(student.getStudentName());

            seatBox.getChildren().addAll(seatNumLabel, idLabel, nameLabel);
            seatBox.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-border-radius: 5;");

            int row = i / columns;
            int col = i % columns;
            seatGrid.add(seatBox, col, row);
        }
    }

    @FXML
    private void printSeatMap() {
        if (roomAssignment == null) {
            showAlert(Alert.AlertType.WARNING, "No Data", "No Seat Map",
                    "No seat map data to print.");
            return;
        }

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            boolean proceed = job.showPrintDialog(seatGrid.getScene().getWindow());

            if (proceed) {
                VBox printContent = new VBox(10);
                printContent.getChildren().add(new Label("Room: " + roomAssignment.getRoomNumber()));
                printContent.getChildren().add(new Label("Course: " + roomAssignment.getCourseCode()));
                printContent.getChildren().add(new Label("Exam Date/Time: " + roomAssignment.getExamDateTime()));
                printContent.getChildren().add(new Label("Section: " + roomAssignment.getSection()));
                printContent.getChildren().add(new Label("Total Students: " + roomAssignment.getStudents().size()));
                printContent.getChildren().add(new Label("Seat Map:"));

                // Create a copy of the grid for printing
                GridPane printGrid = new GridPane();
                printGrid.setHgap(10);
                printGrid.setVgap(10);
                printGrid.setPadding(new javafx.geometry.Insets(20));

                for (Node node : seatGrid.getChildren()) {
                    Integer row = GridPane.getRowIndex(node);
                    Integer col = GridPane.getColumnIndex(node);
                    if (row != null && col != null) {
                        printGrid.add(node, col, row);
                    }
                }

                printContent.getChildren().add(printGrid);

                boolean printed = job.printPage(printContent);
                if (printed) {
                    job.endJob();
                    showAlert(Alert.AlertType.INFORMATION, "Print Successful",
                            "Seat Map Printed", "Seat map has been sent to the printer.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Print Failed",
                            "Printing Error", "Failed to print seat map.");
                }
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "No Printer",
                    "No Printer Available", "No printer was found.");
        }
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
