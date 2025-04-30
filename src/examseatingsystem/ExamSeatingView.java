package examseatingsystem;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExamSeatingView {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void displayMenu() {
        System.out.println("\n=== Exam Seating Arrangement System ===");
        System.out.println("1. Generate Seating Arrangement");
        System.out.println("2. View Seating Arrangement");
        System.out.println("3. Save Seating Arrangement");
        System.out.println("4. Update Student List");
        System.out.println("5. Exit");
        System.out.print("Enter your choice: ");
    }

    public void displayExams(List<Exam> exams) {
        System.out.println("\nAvailable Exams:");
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s %-15s %-15s %-25s %-15s%n", "No.", "Course Code", "Section", "Date and Time", "Room");
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        for (int i = 0; i < exams.size(); i++) {
            Exam exam = exams.get(i);
            String formattedDateTime = exam.getExamDateTime().format(DATE_TIME_FORMATTER);
            System.out.printf("%-5d %-15s %-15s %-25s %-15s%n",
                    (i + 1),
                    exam.getCourseCode(),
                    exam.getSection(),
                    formattedDateTime,
                    exam.getRoom());
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        System.out.print("Select an exam (1-" + exams.size() + "): ");
    }

    public void displaySeatingArrangement(List<Seat> seats, String room) {
        System.out.println("\nSeating Arrangement Visualization for Room: " + room);
        System.out.println("---------------------------------------------");

        // Find max row and column
        int maxRow = 0;
        int maxCol = 0;
        for (Seat seat : seats) {
            if (seat.getRoom().equals(room)) {
                maxRow = Math.max(maxRow, seat.getRow());
                maxCol = Math.max(maxCol, seat.getColumn());
            }
        }

        if (maxRow == 0 || maxCol == 0) {
            System.out.println("No seats found for room: " + room);
            return;
        }

        // Create grid
        String[][] grid = new String[maxRow + 1][maxCol + 1];
        for (Seat seat : seats) {
            if (seat.getRoom().equals(room)) {
                String occupant = seat.isOccupied() ?
                        seat.getStudent().getName().substring(0, Math.min(10, seat.getStudent().getName().length())) :
                        "Empty";
                grid[seat.getRow()][seat.getColumn()] = String.format("%-10s", occupant);
            }
        }

        // Print grid
        for (int row = 1; row <= maxRow; row++) {
            System.out.print("Row " + row + ": ");
            for (int col = 1; col <= maxCol; col++) {
                System.out.print(grid[row][col] != null ? grid[row][col] : "          ");
                System.out.print(" | ");
            }
            System.out.println();
            System.out.println("---------------------------------------------");
        }
    }

    public void displaySeatingTable(Exam exam, List<Seat> seats) {
        System.out.println("\nSeating Arrangement Table");
        System.out.println("==================================================================================================");
        System.out.printf("Subject Code: %-15s%n", exam.getCourseCode());
        System.out.printf("Subject Name: %-40s%n", exam.getCourseName());
        System.out.printf("Date of Exam: %-15s%n", exam.getExamDateTime().toLocalDate());
        System.out.printf("Time of Exam: %-15s%n", exam.getExamDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        System.out.printf("Section: %-15s%n", exam.getSection());
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.printf("%-15s %-40s%n", "Seat Number", "Name of Student");
        System.out.println("--------------------------------------------------------------------------------------------------");

        seats.stream()
                .filter(s -> s.getRoom().equals(exam.getRoom()))
                .sorted((s1, s2) -> {
                    if (s1.getRow() != s2.getRow()) {
                        return Integer.compare(s1.getRow(), s2.getRow());
                    }
                    return Integer.compare(s1.getColumn(), s2.getColumn());
                })
                .forEach(s -> {
                    String seatNumber = s.getRow() + "-" + s.getColumn();
                    String studentName = s.isOccupied() ?
                            s.getStudent().getName() + " (" + s.getStudent().getStudentId() + ")" :
                            "Empty";
                    System.out.printf("%-15s %-40s%n", seatNumber, studentName);
                });
        System.out.println("==================================================================================================");
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void showError(String error) {
        System.err.println("Error: " + error);
    }
}
