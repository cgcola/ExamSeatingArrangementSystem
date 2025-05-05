package com.example.examseatingarangementfinal;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.List;

public class SeatMapModel {
    private final StringProperty roomNumber;
    private final StringProperty courseCode;
    private final StringProperty examDateTime;
    private final StringProperty section;
    private final List<StudentModel> students;
    private final StringProperty studentCount;

    public SeatMapModel(String roomNumber, String courseCode, String examDateTime,
                        String section, List<StudentModel> students) {
        this.roomNumber = new SimpleStringProperty(roomNumber);
        this.courseCode = new SimpleStringProperty(courseCode);
        this.examDateTime = new SimpleStringProperty(examDateTime);
        this.section = new SimpleStringProperty(section);
        this.students = students;
        this.studentCount = new SimpleStringProperty(String.valueOf(students.size()));
    }

    public String getRoomNumber() { return roomNumber.get(); }
    public StringProperty roomNumberProperty() { return roomNumber; }

    public String getCourseCode() { return courseCode.get(); }
    public StringProperty courseCodeProperty() { return courseCode; }


    public String getExamDateTime() { return examDateTime.get(); }
    public StringProperty examDateTimeProperty() { return examDateTime; }

    public String getSection() { return section.get(); }
    public StringProperty sectionProperty() { return section; }


    public List<StudentModel> getStudents() { return students; }

    public String getStudentCount() { return studentCount.get(); }
    public StringProperty studentCountProperty() { return studentCount; }

    @Override
    public String toString() {
        return "Room: " + roomNumber.get() + ", Course: " + courseCode.get() +
                ", DateTime: " + examDateTime.get() + ", Section: " + section.get() +
                ", Students: " + students.size();
    }
}
