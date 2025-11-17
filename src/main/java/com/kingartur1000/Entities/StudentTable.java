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
            case 1 -> s.getGroup() != null ? s.getGroup().getName() : "";
            case 2 -> {
                int totalLectures = s.getGroup() != null ? s.getGroup().getAttendanceRecords().size() : 0;
                int attended = s.getAttendanceCount();
                if (totalLectures == 0) {
                    yield "0 (0%)";
                } else {
                    int percent = (int) Math.round((attended * 100.0) / totalLectures);
                    yield attended + " (" + percent + "%)";
                }
            }
            default -> null;
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    public Student getStudent(int index) {
        return students.get(index);
    }

    public void refresh() {
        fireTableDataChanged();
    }
}
