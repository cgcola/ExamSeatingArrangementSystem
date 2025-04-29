package examseatingsystem;

import java.io.*;
import java.util.*;

public class UpdateStudentList {
    Scanner sc = new Scanner(System.in);
    StringBuilder stb = new StringBuilder();

    ExamSeatingModel model = new ExamSeatingModel();
    ExamSeatingView view = new ExamSeatingView();

    public void updateStudentList() {
        System.out.println("Process:\n[A] Add\n[R] Remove");
        System.out.print("Enter: ");
        String input = sc.nextLine().trim().toUpperCase();

        System.out.print("Enter student ID: ");
        String id = sc.nextLine().trim();
        stb.append(id).append(", ");
        System.out.print("Name: ");
        String name = sc.nextLine().trim();
        stb.append(name).append(", ");
        System.out.print("Type (Regular/Irregular): ");
        String type = sc.nextLine().trim();
        stb.append(type).append(", ");
        System.out.print("Program Code: ");
        String programCode = sc.nextLine().trim();
        stb.append(programCode);
        System.out.print("Year and Section: ");
        String section = sc.nextLine().trim();

        switch (input) {
            case "A":
                addData(stb, id, section);
                enrolledCourses(id, section);
                reloadData();
                break;
            case "R":
                removeStudent(id, section);
                reloadData();
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    public void addData(StringBuilder stb, String studentID, String section) {
        //Copy and paste the resources path/folder
        String filename = "C:\\Users\\Zarra\\IdeaProjects\\OOPFinalProject\\OOPFinalProject\\src\\examseatingsystem\\resources\\" + section + "_StudentsList.csv";
        try (FileWriter writer = new FileWriter(filename, true)) {
            writer.write("\n" + stb.toString());
            System.out.println("Student" + studentID + "added successfully to " + filename);
        } catch (IOException e) {
            System.err.println("Error adding student: " + e.getMessage());
        }
    }

    public void enrolledCourses(String studentId, String section) {
        String[] courses = {"[1]CCC211-18", "[2]CCL211-18", "[3]CCS212-18", "[4]CCS213-18", "[5]FILDIS-18", "[6]GECLWR-18", "[7]GECSTS-18", "[8]GECTCW-18", "[9]PE3ID-18"};
        System.out.println("Available Courses:");
        for (String course : courses) {
            System.out.println(course);
        }

        System.out.println("Enter enrolled courses (Use comma-separated numbers, e.g., 1,2,5): ");
        String input = sc.nextLine().trim();
        String[] selectedIndexes = input.split(",");

        Map<String, String> courseCodeMap = new HashMap<>();
        courseCodeMap.put("1", "CCC211-18");
        courseCodeMap.put("2", "CCL211-18");
        courseCodeMap.put("3", "CCS212-18");
        courseCodeMap.put("4", "CCS213-18");
        courseCodeMap.put("5", "FILDIS-18");
        courseCodeMap.put("6", "GECLWR-18");
        courseCodeMap.put("7", "GECSTS-18");
        courseCodeMap.put("8", "GECTCW-18");
        courseCodeMap.put("9", "PE3ID-18");

        for (String idx : selectedIndexes) {
            idx = idx.trim();
            if (courseCodeMap.containsKey(idx)) {
                String courseCode = courseCodeMap.get(idx);
                //Copy and paste the resources path/folder
                String courseFile = "C:\\Users\\Zarra\\IdeaProjects\\OOPFinalProject\\OOPFinalProject\\src\\examseatingsystem\\resources\\" + section + "_" + courseCode + "_Enrolled.csv";
                try (FileWriter writer = new FileWriter(courseFile, true)) {
                    writer.write("\n" + courseCode + ", " + studentId);
                } catch (IOException e) {
                    System.err.println("Error enrolling student in " + courseCode + ": " + e.getMessage());
                }
            } else {
                System.out.println("Invalid course selection: " + idx);
            }
        }

        System.out.println("Student enrollment complete.");
    }

    // Remove student from the system
    private void removeStudent(String studentId, String section) {
        String resourcesPath = "C:\\Users\\Zarra\\IdeaProjects\\OOPFinalProject\\OOPFinalProject\\src\\examseatingsystem\\resources\\";

        // Remove student from the StudentList.csv
        String studentFile = resourcesPath + section + "_StudentsList.csv";
        deleteLineFromFile(studentFile, studentId);

        // Remove student from each course's enrollment list
        String[] courses = {
                "CCC211-18", "CCL211-18", "CCS212-18", "CCS213-18",
                "FILDIS-18", "GECLWR-18", "GECSTS-18", "GECTCW-18", "PE3ID-18"
        };

        for (String course : courses) {
            String courseFile = resourcesPath + section + "_" + course + "_Enrolled.csv";
            deleteLineFromFile(courseFile, studentId);
        }

        System.out.println("Student " + studentId + " removed from the system.");
    }

    // Delete a specific line (containing studentId) from a file
    private void deleteLineFromFile(String filePath, String studentId) {
        File inputFile = new File(filePath);

        // List to store file content without the studentId line
        List<String> fileContent = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.contains(studentId)) {
                    fileContent.add(line);
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
            return;
        }

        //Overwrite the file with the updated content
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(inputFile))) {
            for (String line : fileContent) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + filePath);
        }
    }

    public void reloadData(){
        try {
            String directoryPath = "C:\\Users\\Zarra\\IdeaProjects\\OOPFinalProject\\OOPFinalProject\\src\\examseatingsystem\\resources";
            model.loadData(directoryPath);
        } catch (IOException e) {
            view.showError("Error loading data: " + e.getMessage());
        }
    }
}
