package com.kingartur1000.Entities;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class FixedColumnModel extends AbstractTableModel {
    private List<Student> students = new ArrayList<>();

    public void setGroup(Group group) {
        this.students = group.getStudents();
        fireTableStructureChanged();
    }

    @Override
    public int getRowCount() {
        return students.size();
    }

    @Override
    public int getColumnCount() {
        return 1; // Только ФИО
    }

    @Override
    public String getColumnName(int column) {
        return "ФИО";
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return students.get(rowIndex).getFullName();
    }
}
