package com.kingartur1000.Entities;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import com.kingartur1000.Entities.AttendanceTable.AttendanceMark;

public class AttendanceRecord {
    private LocalDate date;
    private Map<Student, AttendanceMark> marks;

    public AttendanceRecord(LocalDate date) {
        this.date = date;
        this.marks = new HashMap<>();
    }

    public LocalDate getDate() {
        return date;
    }

    /**
     * Отметить студента с конкретным статусом (ABSENT, PRESENT, LATE).
     */
    public void mark(Student s, AttendanceTable.AttendanceMark status) {
        mark(s, status, true);
    }

    public void mark(Student s, AttendanceMark status, boolean updateCounter) {
        AttendanceMark previousMark = marks.get(s);
        marks.put(s, status);

        if (!updateCounter) return;

        // Инкрементируем счётчик только если раньше не было отметки
        // или раньше был отсутствующим, а теперь присутствующий/опоздавший
        if (status == AttendanceMark.PRESENT || status == AttendanceMark.LATE) {
            if (previousMark == null || previousMark == AttendanceMark.ABSENT) {
                s.incrementAttendance();
            }
        } else {
            // Если был присутствующим/опоздавшим, а теперь отсутствует — декрементируем
            if (previousMark == AttendanceMark.PRESENT || previousMark == AttendanceMark.LATE) {
                s.setAttendanceCount(Math.max(0, s.getAttendanceCount() - 1));
            }
        }
    }

    /**
     * Проверка: студент присутствовал (включая опоздание).
     */
    public boolean isPresent(Student s) {
        AttendanceMark mark = marks.getOrDefault(s, AttendanceMark.ABSENT);
        return mark == AttendanceMark.PRESENT || mark == AttendanceMark.LATE;
    }

    public AttendanceMark getMark(Student s) {
        return marks.getOrDefault(s, AttendanceMark.ABSENT);
    }

    public Map<Student, AttendanceMark> getMarks() {
        return marks;
    }
}
