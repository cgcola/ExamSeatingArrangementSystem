package examseatingsystem;

public class Student {
    private String studentId;
    private String name;
    private String program;

    public Student(String studentId, String name, String program) {
        this.studentId = studentId;
        this.name = name;
        this.program = program;
    }

    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getProgram() { return program; }
}