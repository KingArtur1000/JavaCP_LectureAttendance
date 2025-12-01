package com.kingartur1000.Entities;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import com.kingartur1000.Entities.AttendanceTable.AttendanceMark;

public class AttendanceRecord {
    /**
     * Класс AttendanceRecord хранит информацию о посещаемости студентов в конкретную дату.
     * <p>Содержит карту Student -> AttendanceMark и методы для работы с отметками.</p>
     */
    private LocalDate date; /** Дата посещаемости */
    private Map<Student, AttendanceMark> marks; /** Карта: студент -> статус посещаемости */
    /**
     * Конструктор записи посещаемости.
     *
     * @param date дата посещаемости
     */
    public AttendanceRecord(LocalDate date) {
        this.date = date;
        this.marks = new HashMap<>();
    }

    public LocalDate getDate() {
        return date;
    } /** @return дата посещаемости */

    /**
     * Отметить студента с конкретным статусом (ABSENT, PRESENT, LATE).
     * <p>По умолчанию обновляет счётчик посещений студента.</p>
     *
     * @param s студент
     * @param status статус посещаемости
     */
    public void mark(Student s, AttendanceTable.AttendanceMark status) {
        mark(s, status, true);
    }

    /*
     * Метод для отметки студента с возможностью управлять обновлением счётчика.
     * Алгоритм:
     * 1. Получить предыдущую отметку студента.
     * 2. Записать новую отметку в карту.
     * 3. Если updateCounter = true:
     *    - Если новая отметка PRESENT или LATE:
     *      • Увеличить счётчик посещений, если раньше студент был ABSENT или без отметки.
     *    - Если новая отметка ABSENT:
     *      • Уменьшить счётчик посещений, если раньше студент был PRESENT или LATE.
     */
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
     *
     * @param s студент
     * @return true если студент присутствовал или опоздал, иначе false
     */
    public boolean isPresent(Student s) {
        AttendanceMark mark = marks.getOrDefault(s, AttendanceMark.ABSENT);
        return mark == AttendanceMark.PRESENT || mark == AttendanceMark.LATE;
    }
    /**
     * Получить отметку посещаемости студента.
     *
     * @param s студент
     * @return статус посещаемости (по умолчанию ABSENT)
     */
    public AttendanceMark getMark(Student s) {
        return marks.getOrDefault(s, AttendanceMark.ABSENT);
    }

    /**
     * Получить все отметки посещаемости.
     *
     * @return карта Student -> AttendanceMark
     */
    public Map<Student, AttendanceMark> getMarks() {
        return marks;
    }
}
