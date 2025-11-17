package com.kingartur1000.Entities;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private String name;
    private List<Student> students;
    private List<AttendanceRecord> attendanceRecords;

    public Group(String name) {
        this.name = name;
        this.students = new ArrayList<>();
        this.attendanceRecords = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void addStudent(Student s) {
        if (!students.contains(s)) {
            students.add(s);
            s.setGroup(this);
        }
    }

    public void removeStudent(Student s) {
        students.remove(s);
    }

    public List<AttendanceRecord> getAttendanceRecords() {
        return attendanceRecords;
    }

    public void addAttendanceRecord(AttendanceRecord record) {
        attendanceRecords.add(record);
    }

    public AttendanceRecord getAttendanceRecordByDate(java.time.LocalDate date) {
        for (AttendanceRecord record : attendanceRecords) {
            if (record.getDate().equals(date)) {
                return record;
            }
        }
        return null;
    }

    // метод для удаления записи посещаемости
    public void removeAttendanceRecord(AttendanceRecord record) {
        attendanceRecords.remove(record);
    }
}
