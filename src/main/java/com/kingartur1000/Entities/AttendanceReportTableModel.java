package com.kingartur1000.Entities;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceReportTableModel extends AbstractTableModel {
    private List<Student> students = new ArrayList<>();
    private List<LocalDate> dates = new ArrayList<>();
    private Map<LocalDate, AttendanceRecord> records = new HashMap<>();

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
        return 1 + dates.size(); // ФИО + даты
    }

    @Override
    public String getColumnName(int column) {
        return column == 0 ? "ФИО" : dates.get(column - 1).toString();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Student s = students.get(rowIndex);
        if (columnIndex == 0) return s.getFullName();

        LocalDate date = dates.get(columnIndex - 1);
        AttendanceRecord record = records.get(date);
        return (record != null && !record.isPresent(s)) ? "-" : "";
    }
}


