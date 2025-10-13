package com.kingartur1000.UI;

import Entities.Group;
import Entities.Student;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ReportPanel extends GridPanel {
    private JTable table;
    private ReportTable reportTable;
    private JRadioButton byName;
    private JRadioButton byVisits;
    private List<Group> groups;

    public ReportPanel(List<Group> groups) {
        super(3, 1);
        this.groups = groups;

        JPanel sortPanel = new JPanel();
        byName = new JRadioButton("По фамилии");
        byVisits = new JRadioButton("По количеству посещений");
        ButtonGroup group = new ButtonGroup();
        group.add(byName);
        group.add(byVisits);
        sortPanel.add(byName);
        sortPanel.add(byVisits);

        reportTable = new ReportTable(collectStudents());
        table = new JTable(reportTable);

        JButton exportButton = new JButton("Экспорт");

        addToGrid(sortPanel, 0, 0);
        addToGrid(new JScrollPane(table), 1, 0);
        addToGrid(exportButton, 2, 0);

        byName.addActionListener(this::onSort);
        byVisits.addActionListener(this::onSort);
        exportButton.addActionListener(this::onExport);
    }

    public void updateGroups(List<Group> groups) {
        this.groups = groups;
        onSort(null);
    }

    private List<Student> collectStudents() {
        List<Student> all = new ArrayList<>();
        for (Group g : groups) {
            all.addAll(g.getStudents());
        }
        return all;
    }

    private void onSort(ActionEvent e) {
        List<Student> all = collectStudents();
        if (byName.isSelected()) {
            all.sort(Comparator.comparing(Student::getFullName));
        } else if (byVisits.isSelected()) {
            all.sort(Comparator.comparingInt(Student::getAttendanceCount).reversed());
        }
        reportTable.setStudents(all);
    }

    private void onExport(ActionEvent e) {
        StringBuilder sb = new StringBuilder("Отчёт по посещаемости:\n\n");
        sb.append(String.format("%-30s | %-15s | %s\n", "ФИО", "Группа", "Посещений"));
        sb.append("-".repeat(60)).append("\n");

        for (int i = 0; i < reportTable.getRowCount(); i++) {
            sb.append(String.format("%-30s | %-15s | %s\n",
                    reportTable.getValueAt(i, 0),
                    reportTable.getValueAt(i, 1),
                    reportTable.getValueAt(i, 2)));
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setRows(20);
        textArea.setColumns(60);

        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane, "Отчёт", JOptionPane.INFORMATION_MESSAGE);
    }

    // Внутренний класс для таблицы отчёта
    private static class ReportTable extends AbstractTableModel {
        private List<Student> students;
        private final String[] columns = {"ФИО", "Группа", "Посещений"};

        public ReportTable(List<Student> students) {
            this.students = students;
        }

        public void setStudents(List<Student> students) {
            this.students = students;
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
        public Object getValueAt(int rowIndex, int columnIndex) {
            Student s = students.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> s.getFullName();
                case 1 -> s.getGroupName();
                case 2 -> s.getAttendanceCount();
                default -> null;
            };
        }
    }
}