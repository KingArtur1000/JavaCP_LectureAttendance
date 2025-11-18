package com.kingartur1000.Entities;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AttendanceDatesModel extends AbstractTableModel {
    private List<Student> students = new ArrayList<>();
    private List<LocalDate> dates = new ArrayList<>();
    private Map<LocalDate, AttendanceRecord> records = new HashMap<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");

    public void setGroup(Group group) {
        this.students = group.getStudents();
        this.records.clear();
        this.dates.clear();

        for (AttendanceRecord record : group.getAttendanceRecords()) {
            LocalDate date = record.getDate();
            dates.add(date);
            records.put(date, record);
        }

        dates.sort(LocalDate::compareTo);
        fireTableStructureChanged();
    }

    @Override
    public int getRowCount() {
        return students.size();
    }

    @Override
    public int getColumnCount() {
        return dates.size();
    }

    @Override
    public String getColumnName(int column) {
        return formatter.format(dates.get(column));
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Student s = students.get(rowIndex);
        LocalDate date = dates.get(columnIndex);
        AttendanceRecord record = records.get(date);

        if (record == null) return "";

        AttendanceTable.AttendanceMark mark = record.getMark(s);
        return mark; // возвращаем сам статус, а не текст
    }

    // вспомогательные методы для рендерера
    public AttendanceRecord getRecordAt(int columnIndex) {
        LocalDate date = dates.get(columnIndex);
        return records.get(date);
    }

    public Student getStudentAt(int rowIndex) {
        return students.get(rowIndex);
    }
}
