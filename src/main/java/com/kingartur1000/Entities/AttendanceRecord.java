package com.kingartur1000.Entities;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class AttendanceRecord {
    private LocalDate date;
    private Map<Student, Boolean> marks;

    public AttendanceRecord(LocalDate date) {
        this.date = date;
        this.marks = new HashMap<>();
    }

    public LocalDate getDate() {
        return date;
    }

    public void mark(Student s, boolean present) {
        Boolean previousMark = marks.get(s);
        marks.put(s, present);

        // Инкрементируем счётчик только если раньше не было отметки
        // или раньше был отсутствующим, а теперь присутствующий
        if (present) {
            if (previousMark == null || !previousMark) {
                s.incrementAttendance();
            }
        } else {
            // Если был присутствующим, а теперь отсутствует - декрементируем
            if (previousMark != null && previousMark) {
                s.setAttendanceCount(Math.max(0, s.getAttendanceCount() - 1));
            }
        }
    }

    public Boolean isPresent(Student s) {
        return marks.getOrDefault(s, false);
    }

    public Map<Student, Boolean> getMarks() {
        return marks;
    }
}