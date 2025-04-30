package examseatingsystem;

import java.time.LocalDateTime;

public class Exam {
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

    public Course getCourse() {
        return course;
    }
    public LocalDateTime getExamDateTime()
    { return examDateTime;
    }
    public String getRoom() {
        return room;
    }
    public String getSection() {
        return section;
    }
}