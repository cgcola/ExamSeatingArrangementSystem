package examseatingsystem;

import java.util.Iterator;
import java.util.List;

public interface SeatingStrategy {
    void assignSeats(List<Student> students, List<Seat> availableSeats, Exam exam) throws SeatingException;
}

class SequentialSeatingStrategy implements SeatingStrategy {
    @Override
    public void assignSeats(List<Student> students, List<Seat> availableSeats, Exam exam) throws SeatingException {
        if (students.size() > availableSeats.size()) {
            throw new SeatingException("Not enough seats for all students");
        }

        Iterator<Student> studentIterator = students.iterator();

        for (Seat seat : availableSeats) {
            if (studentIterator.hasNext() && !seat.isOccupied()) {
                seat.assignStudent(studentIterator.next());
            }
        }
    }
}
