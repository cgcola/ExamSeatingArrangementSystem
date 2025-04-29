package examseatingsystem;

import java.util.Iterator;
import java.util.List;

public class SequentialSeatingStrategy implements SeatingStrategy {
    @Override
    public void assignSeats(List<Student> students, List<Seat> seats, Exam exam) throws SeatingException {
        if (students.size() > seats.size()) {
            throw new SeatingException("Not enough seats for all students");
        }

        Iterator<Student> studentIterator = students.iterator();
        for (Seat seat : seats) {
            if (studentIterator.hasNext() && !seat.isOccupied()) {
                seat.assignStudent(studentIterator.next());
            }
        }
    }
}