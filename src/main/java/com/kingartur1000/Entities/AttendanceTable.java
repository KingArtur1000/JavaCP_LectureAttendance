package com.kingartur1000.Entities;

import javax.swing.table.AbstractTableModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceTable extends AbstractTableModel {
    private final List<Student> students;
    private final String[] columns = {"ФИО", "Присутствовал"};
    private final Map<Student, Boolean> marks;

    public AttendanceTable(List<Student> students) {
        this.students = students;
        this.marks = new HashMap<>();
        for (Student s : students) {
            marks.put(s, Boolean.FALSE);
        }
    }

    public void loadFromRecord(AttendanceRecord record) {
        if (record == null) {
            for (Student s : students) {
                marks.put(s, Boolean.FALSE);
            }
        } else {
            for (Student s : students) {
                marks.put(s, record.isPresent(s));
            }
        }
        fireTableDataChanged();
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
        return columnIndex == 1 ? Boolean.class : String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Student s = students.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> s.getFullName();
            case 1 -> marks.getOrDefault(s, false);
            default -> null;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 1 && aValue instanceof Boolean present) {
            Student s = students.get(rowIndex);
            marks.put(s, present);
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    public boolean isPresent(Student s) {
        return marks.getOrDefault(s, false);
    }


    // Очистка таблицы: все студенты отмечены как отсутствующие
    public void clear() {
        for (Student s : students) {
            marks.put(s, Boolean.FALSE);
        }
        fireTableDataChanged();
    }


    public Student getStudent(int index) {
        return students.get(index);
    }
}