package com.example.examseatingarangementfinal;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.util.*;

public class ExamSeatingServiceModel {

    public List<StudentModel> loadStudentsFromCSV(File file) throws IOException {
        List<StudentModel> students = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // Skip header line
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 6) {
                    String courseCode = data[0].trim();
                    String courseName = data[1].trim();
                    String examDateTime = data[2].trim();
                    String studentId = data[3].trim();
                    String studentName = data[4].trim();
                    String section = data[5].trim();

                    students.add(new StudentModel(
                            courseCode, courseName, examDateTime,
                            studentId, studentName, section));
                }
            }
        }

        return students;
    }

    // Filter students based on search criteria
    public List<StudentModel> filterStudents(List<StudentModel> students, String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return new ArrayList<>(students);
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            List<StudentModel> filteredStudents = new ArrayList<>();

            for (StudentModel student : students) {
                if (student.getStudentId().toLowerCase().contains(lowerCaseFilter) ||
                        student.getStudentName().toLowerCase().contains(lowerCaseFilter) ||
                        student.getCourseCode().toLowerCase().contains(lowerCaseFilter) ||
                        student.getCourseName().toLowerCase().contains(lowerCaseFilter) ||
                        student.getSection().toLowerCase().contains(lowerCaseFilter)) {
                    filteredStudents.add(student);
                }
            }
            return filteredStudents;
        }
    }

    // Assign rooms based on sorting criteria and max students per room
    public List<SeatMapModel> assignRooms(List<StudentModel> students, String sortCriteria, int maxStudentsPerRoom) {
        List<SeatMapModel> roomAssignments = new ArrayList<>();

        // Group students by the selected criteria
        Map<String, List<StudentModel>> groupedStudents = new HashMap<>();
        for (StudentModel student : students) {
            String key;
            switch (sortCriteria) {
                case "Course Code":
                    key = student.getCourseCode() + "_" + student.getExamDateTime() + "_" + student.getSection();
                    break;
                case "Exam Date/Time":
                    key = student.getExamDateTime() + "_" + student.getCourseCode() + "_" + student.getSection();
                    break;
                case "Section":
                    key = student.getSection() + "_" + student.getCourseCode() + "_" + student.getExamDateTime();
                    break;
                default:
                    key = student.getCourseCode() + "_" + student.getExamDateTime() + "_" + student.getSection();
            }

            if (!groupedStudents.containsKey(key)) {
                groupedStudents.put(key, new ArrayList<>());
            }
            groupedStudents.get(key).add(student);
        }

        // Assign rooms
        int roomCounter = 200;

        for (Map.Entry<String, List<StudentModel>> entry : groupedStudents.entrySet()) {
            List<StudentModel> studentGroup = entry.getValue();
            String[] keyParts = entry.getKey().split("_");

            // Calculate number of rooms needed
            int numRooms = (int) Math.ceil((double) studentGroup.size() / maxStudentsPerRoom);

            for (int i = 0; i < numRooms; i++) {
                String roomNumber = "Room " + roomCounter++;

                int startIdx = i * maxStudentsPerRoom;
                int endIdx = Math.min(startIdx + maxStudentsPerRoom, studentGroup.size());
                List<StudentModel> roomStudents = studentGroup.subList(startIdx, endIdx);

                String courseCode = keyParts[0];
                String examDateTime = keyParts[1];
                String section = keyParts[2];

                if (sortCriteria.equals("Exam Date/Time")) {
                    courseCode = keyParts[1];
                    examDateTime = keyParts[0];
                    section = keyParts[2];
                } else if (sortCriteria.equals("Section")) {
                    section = keyParts[0];
                    courseCode = keyParts[1];
                    examDateTime = keyParts[2];
                }

                SeatMapModel roomAssignment = new SeatMapModel(
                        roomNumber, courseCode, examDateTime, section, roomStudents);
                roomAssignments.add(roomAssignment);
            }
        }

        return roomAssignments;
    }
    //export csv
    public static boolean exportRoomAssignments(Stage owner, List<SeatMapModel> roomAssignments) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Room Assignments");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("room_assignments.csv");

        File file = fileChooser.showSaveDialog(owner);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {

                writer.println("Room Number,Course Code,Exam Date/Time,Section,Student Count");

                for (SeatMapModel room : roomAssignments) {
                    writer.println(
                            room.getRoomNumber() + "," +
                                    room.getCourseCode() + "," +
                                    room.getExamDateTime() + "," +
                                    room.getSection() + "," +
                                    room.getStudentCount()
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
