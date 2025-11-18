package com.kingartur1000.Entities;

import javax.swing.table.AbstractTableModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceTable extends AbstractTableModel {
    public enum AttendanceMark {
        ABSENT, PRESENT, LATE
    }

    private final List<Student> students;
    private final String[] columns = {"ФИО", "Статус"};
    private final Map<Student, AttendanceMark> marks;

    public AttendanceTable(List<Student> students) {
        this.students = students;
        this.marks = new HashMap<>();
        for (Student s : students) {
            marks.put(s, AttendanceMark.ABSENT);
        }
    }

    public void loadFromRecord(AttendanceRecord record) {
        for (Student s : students) {
            boolean present = record != null && record.isPresent(s);
            marks.put(s, present ? AttendanceMark.PRESENT : AttendanceMark.ABSENT);
        }
        fireTableDataChanged();
    }

    public void clear() {
        for (Student s : students) {
            marks.put(s, AttendanceMark.ABSENT);
        }
        fireTableDataChanged();
    }

    public AttendanceMark getMark(Student s) {
        return marks.getOrDefault(s, AttendanceMark.ABSENT);
    }

    public void setMark(Student s, AttendanceMark mark) {
        marks.put(s, mark);
        fireTableDataChanged();
    }

    public boolean isPresent(Student s) {
        AttendanceMark mark = getMark(s);
        return mark == AttendanceMark.PRESENT || mark == AttendanceMark.LATE;
    }

    public Student getStudent(int index) {
        return students.get(index);
    }

    @Override
    public int getRowCount() {
        return students.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 1 ? AttendanceMark.class : String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Student s = students.get(rowIndex);
        return columnIndex == 0 ? s.getFullName() : getMark(s);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
