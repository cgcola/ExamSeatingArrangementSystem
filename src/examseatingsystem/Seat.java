package examseatingsystem;

public class Seat {
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

    public boolean isOccupied() { return student != null; }
    public void assignStudent(Student student) { this.student = student; }
    public Student getStudent() { return student; }
    public String getRoom() { return room; }
    public int getRow() { return row; }
    public int getColumn() { return column; }
}