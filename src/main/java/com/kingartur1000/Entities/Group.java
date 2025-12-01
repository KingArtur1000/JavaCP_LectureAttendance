package com.kingartur1000.Entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс Group представляет учебную группу.
 * <p>Содержит список студентов и записи посещаемости.</p>
 */
public class Group {
    /** Название группы */
    private String name;
    /** Список студентов группы */
    private List<Student> students;
    /** Список записей посещаемости группы */
    private List<AttendanceRecord> attendanceRecords;

    /**
     * Конструктор группы.
     *
     * @param name название группы
     */
    public Group(String name) {
        this.name = name;
        this.students = new ArrayList<>();
        this.attendanceRecords = new ArrayList<>();
    }

    /** @return название группы */
    public String getName() {
        return name;
    }

    /** Установить новое название группы */
    public void setName(String name) {
        this.name = name;
    }

    /** @return список студентов группы */
    public List<Student> getStudents() {
        return students;
    }

    /*
     * Метод для добавления студента в группу.
     * Алгоритм:
     * 1. Проверить, что студент ещё не в списке.
     * 2. Добавить студента в список.
     * 3. Установить ссылку на группу в объекте Student.
     */
    public void addStudent(Student s) {
        if (!students.contains(s)) {
            students.add(s);
            s.setGroup(this); // связываем студента с группой
        }
    }

    /**
     * Удалить студента из группы.
     *
     * @param s студент для удаления
     */
    public void removeStudent(Student s) {
        students.remove(s);
    }

    /** @return список записей посещаемости группы */
    public List<AttendanceRecord> getAttendanceRecords() {
        return attendanceRecords;
    }

    /**
     * Добавить запись посещаемости.
     *
     * @param record запись посещаемости
     */
    public void addAttendanceRecord(AttendanceRecord record) {
        attendanceRecords.add(record);
    }

    /*
     * Метод для поиска записи посещаемости по дате.
     * Алгоритм:
     * 1. Пройтись по всем записям посещаемости.
     * 2. Сравнить дату записи с переданной датой.
     * 3. Вернуть найденную запись или null, если такой нет.
     */
    public AttendanceRecord getAttendanceRecordByDate(java.time.LocalDate date) {
        for (AttendanceRecord record : attendanceRecords) {
            if (record.getDate().equals(date)) {
                return record;
            }
        }
        return null; // если запись не найдена
    }

    // метод для удаления записи посещаемости
    public void removeAttendanceRecord(AttendanceRecord record) {
        attendanceRecords.remove(record);
    }
}
