package com.kingartur1000.Entities;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class StudentTable extends AbstractTableModel {
    private final List<Student> students;
    private final String[] columns = {"ФИО", "Группа", "Посещений"};

    public StudentTable(List<Student> students) {
        this.students = students;
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
    public Object getValueAt(int rowIndex, int columnIndex) {
        Student s = students.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> s.getFullName();
            case 1 -> s.getGroupName();
            case 2 -> s.getAttendanceCount();
            default -> null;
        };
    }

    public void addStudent(Student s) {
        students.add(s);
        fireTableRowsInserted(students.size() - 1, students.size() - 1);
    }

    public void removeStudent(int index) {
        students.remove(index);
        fireTableRowsDeleted(index, index);
    }

    public Student getStudent(int index) {
        return students.get(index);
    }

    public void refresh() {
        fireTableDataChanged();
    }
}