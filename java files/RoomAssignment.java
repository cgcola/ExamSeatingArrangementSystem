package com.example.examseatingarangementfinal;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.List;

public class RoomAssignment {
    private final StringProperty roomNumber;
    private final StringProperty courseCode;
    private final StringProperty examDateTime;
    private final StringProperty section;
    private final List<Student> students;
    private final StringProperty studentCount;

    public RoomAssignment(String roomNumber, String courseCode, String examDateTime,
                          String section, List<Student> students) {
        this.roomNumber = new SimpleStringProperty(roomNumber);
        this.courseCode = new SimpleStringProperty(courseCode);
        this.examDateTime = new SimpleStringProperty(examDateTime);
        this.section = new SimpleStringProperty(section);
        this.students = students;
        this.studentCount = new SimpleStringProperty(String.valueOf(students.size()));
    }

    public String getRoomNumber() { return roomNumber.get(); }
    public StringProperty roomNumberProperty() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber.set(roomNumber); }

    public String getCourseCode() { return courseCode.get(); }
    public StringProperty courseCodeProperty() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode.set(courseCode); }

    public String getExamDateTime() { return examDateTime.get(); }
    public StringProperty examDateTimeProperty() { return examDateTime; }
    public void setExamDateTime(String examDateTime) { this.examDateTime.set(examDateTime); }

    public String getSection() { return section.get(); }
    public StringProperty sectionProperty() { return section; }
    public void setSection(String section) { this.section.set(section); }

    public List<Student> getStudents() { return students; }

    public String getStudentCount() { return studentCount.get(); }
    public StringProperty studentCountProperty() { return studentCount; }
    public void updateStudentCount() { this.studentCount.set(String.valueOf(students.size())); }

    @Override
    public String toString() {
        return "Room: " + roomNumber.get() + ", Course: " + courseCode.get() +
                ", DateTime: " + examDateTime.get() + ", Section: " + section.get() +
                ", Students: " + students.size();
    }
}
