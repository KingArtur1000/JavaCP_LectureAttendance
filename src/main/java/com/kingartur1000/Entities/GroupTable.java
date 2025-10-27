package com.kingartur1000.Entities;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class GroupTable extends AbstractTableModel {
    private final List<Group> groups;
    private final String[] columns = {"Номер группы", "Кол-во студ."};

    public GroupTable(List<Group> groups) {
        this.groups = groups;
    }

    @Override
    public int getRowCount() {
        return groups.size();
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
        Group g = groups.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> g.getName();
            case 1 -> g.getStudents().size();
            default -> null;
        };
    }

    public void addGroup(Group g) {
        groups.add(g);
        fireTableRowsInserted(groups.size() - 1, groups.size() - 1);
    }

    public void removeGroup(int index) {
        groups.remove(index);
        fireTableRowsDeleted(index, index);
    }

    public Group getGroup(int index) {
        return groups.get(index);
    }

    public List<Group> getAllGroups() {
        return groups;
    }
}