package examseatingsystem;

import java.time.LocalDateTime;

class Course {
    private String courseCode;
    private String courseName;

    public Course(String courseCode, String courseName) {
        this.courseCode = courseCode;
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }
}

class Exam {
    private Course course;
    private LocalDateTime examDateTime;
    private String room;
    private String section;

    public Exam(Course course, LocalDateTime examDateTime, String room, String section) {
        this.course = course;
        this.examDateTime = examDateTime;
        this.room = room;
        this.section = section;
    }

    public String getCourseCode() {
        return course.getCourseCode();
    }

    public String getCourseName() {
        return course.getCourseName();
    }

    public LocalDateTime getExamDateTime() {
        return examDateTime;
    }

    public String getRoom() {
        return room;
    }

    public String getSection() {
        return section;
    }
}

class Seat {
    private String room;
    private int row;
    private int column;
    private Student student;

    public Seat(String room, int row, int column) {
        this.room = room;
        this.row = row;
        this.column = column;
        this.student = null;
    }

    public boolean isOccupied() {
        return student != null;
    }

    public void assignStudent(Student student) {
        this.student = student;
    }

    public String getRoom() {
        return room;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Student getStudent() {
        return student;
    }
}

class Student {
    private String studentId;
    private String name;
    private String program;

    public Student(String studentId, String name, String program) {
        this.studentId = studentId;
        this.name = name;
        this.program = program;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public String getProgram() {
        return program;
    }
}
