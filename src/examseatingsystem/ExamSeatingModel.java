package examseatingsystem;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class ExamSeatingModel {
    private List<Student> students;
    private List<Course> courses;
    private List<Exam> exams;
    private List<Seat> seats;
    private SeatingStrategy seatingStrategy;
    private Map<String, List<String>> courseEnrollments;
    //private List<String> enrolledStudents = new ArrayList<>();

    public ExamSeatingModel() {
        students = new ArrayList<>();
        courses = new ArrayList<>();
        exams = new ArrayList<>();
        seats = new ArrayList<>();
        seatingStrategy = new SequentialSeatingStrategy();
        courseEnrollments = new HashMap<>();
    }

    public void loadData(String directoryPath) throws IOException {
        loadStudents(directoryPath + "\\2BSCS-1_StudentsList.csv");
        loadStudents(directoryPath + "\\2BSCS-2_StudentsList.csv");
        loadCourses(directoryPath + "\\Courses.csv");
        loadEnrollments(directoryPath);
        loadExamSchedule(directoryPath + "\\Exam_Schedule.csv");
        generateSeats(); // Replace loadSeats with generateSeats
    }

    public void loadStudents(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 3) {
                    students.add(new Student(data[0].trim(), data[1].trim(), data[3].trim()));
                }
            }
        }
    }

    public void loadCourses(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2) {
                    String courseCode = data[0].trim().replace(" ", "").toUpperCase();
                    if (courses.stream().noneMatch(c -> c.getCourseCode().equals(courseCode))) {
                        courses.add(new Course(courseCode, data[1].trim()));
                        System.out.println("Loaded course: " + courseCode);
                    } else {
                        System.out.println("Skipped duplicate course: " + courseCode);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading Courses.csv: " + e.getMessage());
            throw e;
        }
    }

    public void loadEnrollments(String directoryPath) throws IOException {
        String[] enrollmentFiles = {
                "2BSCS-1_CCC211-18_Enrolled.csv", "2BSCS-1_CCL211-18_Enrolled.csv",
                "2BSCS-1_CCS212-18_Enrolled.csv", "2BSCS-1_CCS213-18_Enrolled.csv",
                "2BSCS-1_FILDIS-18_Enrolled.csv", "2BSCS-1_GECLWR-18_Enrolled.csv",
                "2BSCS-1_GECSTS-18_Enrolled.csv", "2BSCS-1_GECTCW-18_Enrolled.csv",
                "2BSCS-1_PE3ID-18_Enrolled.csv",
                "2BSCS-2_CCC211-18_Enrolled.csv", "2BSCS-2_CCL211-18_Enrolled.csv",
                "2BSCS-2_CCS212-18_Enrolled.csv", "2BSCS-2_CCS213-18_Enrolled.csv",
                "2BSCS-2_FILDIS-18_Enrolled.csv", "2BSCS-2_GECLWR-18_Enrolled.csv",
                "2BSCS-2_GECSTS-18_Enrolled.csv", "2BSCS-2_GECTCW-18_Enrolled.csv",
                "2BSCS-2_PE3ID-18_Enrolled.csv"
        };

        boolean anyFileLoaded = false;
        for (String file : enrollmentFiles) {
            String filePath = directoryPath + "\\" + file;
            String courseCode = file.split("_")[1].toUpperCase();
            List<String> enrolledStudents = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                reader.readLine();
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length >= 2) {
                        enrolledStudents.add(data[1].trim()); //Changed 0 to 1
                    }
                }
                courseEnrollments.put(courseCode, enrolledStudents);
                anyFileLoaded = true;
            } catch (IOException e) {
                System.err.println("Warning: Could not load enrollment file " + file + ": " + e.getMessage());
            }
        }

        if (!anyFileLoaded) {
            throw new IOException("No enrollment files could be loaded.");
        }
    }

    private void loadExamSchedule(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] data = line.split(",");
                if (data.length >= 5) {
                    try {
                        String courseCode = data[0].trim().replace(" ", "").toUpperCase();
                        Course course = courses.stream()
                                .filter(c -> c.getCourseCode().equals(courseCode))
                                .findFirst()
                                .orElse(null);
                        if (course == null) {
                            System.err.println("Warning: Course code " + courseCode +
                                    " not found at line " + lineNumber + ". Skipping row.");
                            continue;
                        }
                        LocalDate date = LocalDate.parse(data[2].trim(), dateFormatter);
                        LocalTime time = LocalTime.parse(data[3].trim().split(" - ")[0], timeFormatter);
                        LocalDateTime dateTime = LocalDateTime.of(date, time);
                        String room = data[4].trim();
                        String section = data[1].trim();
                        if (exams.stream().noneMatch(e ->
                                e.getCourseCode().equals(courseCode) &&
                                        e.getSection().equals(section) &&
                                        e.getExamDateTime().equals(dateTime) &&
                                        e.getRoom().equals(room))) {
                            exams.add(new Exam(course, dateTime, room, section));
                        } else {
                            System.err.println("Skipped duplicate exam at line " + lineNumber +
                                    ": " + courseCode + ", " + section + ", " + dateTime + ", " + room);
                        }
                    } catch (Exception e) {
                        System.err.println("Warning: Error processing line " + lineNumber +
                                ": " + line + ". Skipping row. Error: " + e.getMessage());
                    }
                } else {
                    System.err.println("Warning: Insufficient columns at line " + lineNumber +
                            ": " + line + ". Skipping row.");
                }
            }
        }
    }

    private void generateSeats() {
        seats.clear();
        // Define room configurations (room ID, rows, columns)
        Map<String, int[]> roomConfigs = new HashMap<>();
        roomConfigs.put("101", new int[]{5, 10});
        roomConfigs.put("104", new int[]{5, 10});
        roomConfigs.put("201", new int[]{5, 10});
        roomConfigs.put("217", new int[]{5, 10});
        roomConfigs.put("301", new int[]{5, 10});
        roomConfigs.put("302", new int[]{5, 10});
        roomConfigs.put("305", new int[]{5, 10});
        roomConfigs.put("306", new int[]{5, 10});
        roomConfigs.put("307", new int[]{5, 10});
        roomConfigs.put("309", new int[]{5, 10});
        roomConfigs.put("310", new int[]{5, 10});
        roomConfigs.put("405", new int[]{5, 10});

        // Generate seats for each room
        for (Map.Entry<String, int[]> entry : roomConfigs.entrySet()) {
            String room = entry.getKey();
            int rows = entry.getValue()[0];
            int cols = entry.getValue()[1];
            for (int row = 1; row <= rows; row++) {
                for (int col = 1; col <= cols; col++) {
                    seats.add(new Seat(room, row, col));
                }
            }
        }
    }

    public List<Student> getEnrolledStudents(String courseCode) {
        List<String> enrolledStudentIds = courseEnrollments.getOrDefault(courseCode.toUpperCase(), new ArrayList<>());
        return students.stream()
                .filter(s -> enrolledStudentIds.contains(s.getStudentId()))
                .collect(Collectors.toList());
    }

    public void assignSeating(Exam exam) throws SeatingException {
        List<Student> enrolledStudents = getEnrolledStudents(exam.getCourseCode());
        List<Seat> availableSeats = seats.stream()
                .filter(s -> s.getRoom().equals(exam.getRoom()) && !s.isOccupied())
                .collect(Collectors.toList());

        seatingStrategy.assignSeats(enrolledStudents, availableSeats, exam);
    }

 /*   public void clearList(){
        students.clear();
        exams.clear();
        seats.clear();
        enrolledStudents.clear();
        courseEnrollments.clear();
    }*/

    public List<Exam> getExams() {
        return exams;
    }
    public List<Seat> getSeats() {
        return seats;
    }
}
