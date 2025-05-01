package com.example.examseatingarangementfinal;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CsvExporter {
    public static boolean exportStudentData(Stage owner, List<Student> students) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Student Data");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("student_data.csv");

        File file = fileChooser.showSaveDialog(owner);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {

                writer.println("Course Code,Course Name,Exam Date/Time,Student ID,Student Name,Section");

                for (Student student : students) {
                    writer.println(
                            student.getCourseCode() + "," +
                                    student.getCourseName() + "," +
                                    student.getExamDateTime() + "," +
                                    student.getStudentId() + "," +
                                    student.getStudentName() + "," +
                                    student.getSection()
                    );
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

}
