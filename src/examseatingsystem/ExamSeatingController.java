package examseatingsystem;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class ExamSeatingController {
    private ExamSeatingModel model;
    private ExamSeatingView view;
    private Scanner scanner;

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
                // Reset seats for the room before assigning
                model.getSeats().stream()
                        .filter(s -> s.getRoom().equals(selectedExam.getRoom()))
                        .forEach(s -> s.assignStudent(null));
                model.assignSeating(selectedExam);
                view.showMessage("Seating arrangement generated for " + selectedExam.getCourse().getCourseCode());
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
                String fileName = "Seating_Arrangement_" + selectedExam.getCourse().getCourseCode() + "_" + room + ".csv";
                try (PrintWriter writer = new PrintWriter(new File(fileName))) {
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
}