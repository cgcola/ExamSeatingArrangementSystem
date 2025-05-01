package com.example.examseatingarangementfinal;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Student {
    private final StringProperty courseCode;
    private final StringProperty courseName;
    private final StringProperty examDateTime;
    private final StringProperty studentId;
    private final StringProperty studentName;
    private final StringProperty section;

    public Student(String courseCode, String courseName, String examDateTime,
                   String studentId, String studentName, String section) {
        this.courseCode = new SimpleStringProperty(courseCode);
        this.courseName = new SimpleStringProperty(courseName);
        this.examDateTime = new SimpleStringProperty(examDateTime);
        this.studentId = new SimpleStringProperty(studentId);
        this.studentName = new SimpleStringProperty(studentName);
        this.section = new SimpleStringProperty(section);
    }

    public String getCourseCode() { return courseCode.get(); }
    public StringProperty courseCodeProperty() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode.set(courseCode); }

    public String getCourseName() { return courseName.get(); }
    public StringProperty courseNameProperty() { return courseName; }
    public void setCourseName(String courseName) { this.courseName.set(courseName); }

    public String getExamDateTime() { return examDateTime.get(); }
    public StringProperty examDateTimeProperty() { return examDateTime; }
    public void setExamDateTime(String examDateTime) { this.examDateTime.set(examDateTime); }

    public String getStudentId() { return studentId.get(); }
    public StringProperty studentIdProperty() { return studentId; }
    public void setStudentId(String studentId) { this.studentId.set(studentId); }

    public String getStudentName() { return studentName.get(); }
    public StringProperty studentNameProperty() { return studentName; }
    public void setStudentName(String studentName) { this.studentName.set(studentName); }

    public String getSection() { return section.get(); }
    public StringProperty sectionProperty() { return section; }
    public void setSection(String section) { this.section.set(section); }

    @Override
    public String toString() {
        return studentId.get() + " - " + studentName.get();
    }
}
