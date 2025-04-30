package examseatingsystem;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class ExamSeatingController {
    private ExamSeatingModel model;
    private ExamSeatingView view;
    private Scanner scanner;
    private UpdateStudentList newStudent = new UpdateStudentList();

    public ExamSeatingController(ExamSeatingModel model, ExamSeatingView view) {
        this.model = model;
        this.view = view;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            view.displayMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    generateSeatingArrangement();
                    break;
                case "2":
                    viewSeatingArrangement();
                    break;
                case "3":
                    saveSeatingArrangement();
                    break;
                case "4":
                    newStudent.updateStudentList();
                    break;
                case "5":
                    running = false;
                    view.showMessage("Exiting system. Goodbye!");
                    break;
                default:
                    view.showError("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private void generateSeatingArrangement() {
        List<Exam> exams = model.getExams();
        if (exams.isEmpty()) {
            view.showError("No exams available.");
            return;
        }

        view.displayExams(exams);
        String input = scanner.nextLine();
        try {
            int examIndex = Integer.parseInt(input) - 1;
            if (examIndex >= 0 && examIndex < exams.size()) {
                Exam selectedExam = exams.get(examIndex);

                // Check if arrangement already exists
                boolean arrangementExists = checkExistingArrangement(selectedExam);

                if (arrangementExists) {
                    view.showMessage("Seating arrangement already exists for " + selectedExam.getCourseCode());
                    view.showMessage("Do you want to regenerate it? (Y?N)");
                    String regenerate = scanner.nextLine().trim().toUpperCase();

                    if (!regenerate.equals("Y")) {
                        view.showMessage("Generation cancelled.");
                        return;
                    }
                    // User wants to regenerate - continue with generation
                }

                // Reset seats for the room before assigning
                model.getSeats().stream()
                        .filter(s -> s.getRoom().equals(selectedExam.getRoom()))
                        .forEach(s -> s.assignStudent(null));
                model.assignSeating(selectedExam);
                view.showMessage("Seating arrangement generated for " + selectedExam.getCourseCode());
            } else {
                view.showError("Invalid exam selection.");
            }
        } catch (NumberFormatException e) {
            view.showError("Please enter a valid number.");
        } catch (SeatingException e) {
            view.showError(e.getMessage());
        }
    }

    private void viewSeatingArrangement() {
        List<Exam> exams = model.getExams();
        if (exams.isEmpty()) {
            view.showError("No exams available.");
            return;
        }

        view.displayExams(exams);
        String input = scanner.nextLine();
        try {
            int examIndex = Integer.parseInt(input) - 1;
            if (examIndex >= 0 && examIndex < exams.size()) {
                Exam selectedExam = exams.get(examIndex);
                List<Seat> seats = model.getSeats();
                view.displaySeatingArrangement(seats, selectedExam.getRoom());
                view.displaySeatingTable(selectedExam, seats);
            } else {
                view.showError("Invalid exam selection.");
            }
        } catch (NumberFormatException e) {
            view.showError("Please enter a valid number.");
        }
    }

    private void saveSeatingArrangement() {
        List<Exam> exams = model.getExams();
        if (exams.isEmpty()) {
            view.showError("No exams available.");
            return;
        }

        view.displayExams(exams);
        String input = scanner.nextLine();
        try {
            int examIndex = Integer.parseInt(input) - 1;
            if (examIndex >= 0 && examIndex < exams.size()) {
                Exam selectedExam = exams.get(examIndex);
                String room = selectedExam.getRoom();
                String fileName = "Seating_Arrangement_" + selectedExam.getCourseCode() + "_" + room + ".csv";

                // Define output directory under 'resources/seating_arrangements'
                File resourceDir = new File("C:\\Users\\Zarra\\IdeaProjects\\OOPFinalProject\\OOPFinalProject\\src\\examseatingsystem\\seating_arrangement");

                // Ensure directory exists
                if (!resourceDir.exists()) {
                    resourceDir.mkdirs();
                }

                File file = new File(resourceDir, fileName);

                // Check if file already exists
                if (file.exists()) {
                    view.showMessage("File " + fileName + " already exists.");
                    view.showMessage("Do you want to overwrite it? (Y/N)");
                    String overwrite = scanner.nextLine().trim().toUpperCase();

                    if (!overwrite.equals("Y")) {
                        view.showMessage("Save operation cancelled.");
                        return;
                    }
                    // User confirmed overwrite - continue with save
                }

                try (PrintWriter writer = new PrintWriter(file)) {
                    // Write CSV header
                    writer.println("Room,Row,Column,StudentId,StudentName");
                    for (Seat seat : model.getSeats()) {
                        if (seat.getRoom().equals(room)) {
                            String studentId = seat.isOccupied() ? seat.getStudent().getStudentId() : "";
                            String studentName = seat.isOccupied() ? seat.getStudent().getName() : "";
                            writer.printf("%s,%d,%d,%s,%s%n",
                                    seat.getRoom(), seat.getRow(), seat.getColumn(), studentId, studentName);
                        }
                    }
                    view.showMessage("Seating arrangement saved to " + fileName);
                } catch (IOException e) {
                    view.showError("Failed to save seating arrangement: " + e.getMessage());
                }
            } else {
                view.showError("Invalid exam selection.");
            }
        } catch (NumberFormatException e) {
            view.showError("Please enter a valid number.");
        }
    }

    private boolean checkExistingArrangement(Exam exam) {
        String room = exam.getRoom();

        // Check if at least one seat in this room has a student assigned
        return model.getSeats().stream()
                .filter(s -> s.getRoom().equals(room))
                .anyMatch(Seat::isOccupied);
    }
}
