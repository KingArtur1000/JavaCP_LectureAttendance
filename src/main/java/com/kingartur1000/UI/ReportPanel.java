package com.kingartur1000.UI;

import com.kingartur1000.Entities.Group;
import com.kingartur1000.Entities.Student;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.kingartur1000.MainWindow.globalFont;

public class ReportPanel extends GridPanel {
    private JTable table;
    private ReportTable reportTable;
    private JRadioButton byName;
    private JRadioButton byVisits;
    private List<Group> groups;

    public ReportPanel(List<Group> groups) {
        super(3, 3);
        this.groups = groups;

        JPanel sortPanel = new JPanel();
        byName = new JRadioButton("По фамилии");
        byName.setFont(globalFont);
        byVisits = new JRadioButton("По количеству посещений");
        byVisits.setFont(globalFont);
        ButtonGroup group = new ButtonGroup();
        group.add(byName);
        group.add(byVisits);
        sortPanel.add(byName);
        sortPanel.add(byVisits);

        reportTable = new ReportTable(collectStudents());
        table = new JTable(reportTable);
        table.setRowHeight(40);
        table.setFont(globalFont);
        table.getTableHeader().setFont(new Font(globalFont.getFontName(), Font.BOLD, globalFont.getSize()));
        table.getColumnModel().setColumnMargin(15);
        table.getColumnModel().getColumn(0).setPreferredWidth(250);
        table.getColumnModel().getColumn(1).setPreferredWidth(50);
        table.getColumnModel().getColumn(2).setPreferredWidth(25);

        // Выравнивание по правому краю в колонке -Кол-во студентов-
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);

        JButton exportButton = new JButton("Экспорт");
        exportButton.setFont(globalFont);

        addToGrid(sortPanel, 0, 0,1,3,1,1);
        addToGrid(new JPanel(), 1, 0);
        addToGrid(new JScrollPane(table), 1, 1,1,1,10,10);
        addToGrid(new JPanel(), 1, 2);

        GridPanel bottomButtons = new GridPanel(1, 3);
        bottomButtons.addToGrid(new JPanel(), 1, 0);
        bottomButtons.addToGrid(exportButton, 1, 1, 1, 1, 0.25, 1);
        bottomButtons.addToGrid(new JPanel(), 1, 2);
        addToGrid(bottomButtons, 2, 0, 1, 3);

        byName.addActionListener(this::onSort);
        byVisits.addActionListener(this::onSort);
        exportButton.addActionListener(this::onExport);

        byName.doClick();
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