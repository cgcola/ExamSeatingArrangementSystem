package examseatingsystem;

import java.util.List;

public interface SeatingStrategy {
    void assignSeats(List<Student> students, List<Seat> seats, Exam exam) throws SeatingException;
}