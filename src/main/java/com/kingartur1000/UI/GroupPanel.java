package com.kingartur1000.UI;

import com.kingartur1000.Entities.Group;
import com.kingartur1000.Entities.GroupTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

import static com.kingartur1000.MainWindow.globalFont;

public class GroupPanel extends GridPanel {
    private JTable table;
    private GroupTable groupTable;
    private StudentPanel studentPanel;
    private AttendancePanel attendancePanel;
    private ReportPanel reportPanel;

    public GroupPanel(StudentPanel studentPanel, AttendancePanel attendancePanel, List<Group> groups) {
        super(3, 3);
        this.studentPanel = studentPanel;
        this.attendancePanel = attendancePanel;

        groupTable = new GroupTable(groups);
        table = new JTable(groupTable);
        table.setFont(globalFont);
        table.setAutoCreateRowSorter(true); // включаем сортировку
        table.getTableHeader().setFont(new Font(globalFont.getFontName(), Font.BOLD, globalFont.getSize()));
        table.getColumnModel().getColumn(0).setPreferredWidth(250);
        table.getColumnModel().getColumn(1).setPreferredWidth(25);
        table.getColumnModel().setColumnMargin(10);
        table.getTableHeader().getColumnModel().setColumnMargin(10);
        table.setRowMargin(5);
        table.getColumnModel().setColumnMargin(20);
        table.setRowHeight(70);

        // Выравнивание по правому краю в колонке -Кол-во студентов-
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);

        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Добавить");
        addBtn.setFont(globalFont);
        JButton delBtn = new JButton("Удалить");
        delBtn.setFont(globalFont);

        buttonPanel.add(addBtn);
        buttonPanel.add(delBtn);

        addToGrid(buttonPanel, 0, 0, 1, 5);
        addToGrid(new JScrollPane(table), 1, 1, 1, 1, 5, 5);
        addToGrid(new GridPanel(1, 1), 1, 0);
        addToGrid(new GridPanel(1, 1), 1, 4);
        addToGrid(new GridPanel(1, 1), 2, 0);

        addBtn.addActionListener(this::onAddGroup);
        delBtn.addActionListener(this::onDeleteGroup);

        // Обработчик выбора группы
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int viewRow = table.getSelectedRow();
                if (viewRow >= 0) {
                    int modelRow = table.convertRowIndexToModel(viewRow);
                    Group g = groupTable.getGroup(modelRow);
                    studentPanel.setGroup(g);
                    attendancePanel.setGroup(g);
                    if (reportPanel != null) {
                        reportPanel.setGroup(g);
                    }
                }
            }
        });

        // Создаём сортировщик
        TableRowSorter<GroupTable> sorter = new TableRowSorter<>(groupTable);
        table.setRowSorter(sorter);

        // Включаем сортировку по первому столбцу (Номер группы) по возрастанию
        sorter.toggleSortOrder(0);
    }

    public void setReportPanel(ReportPanel reportPanel) {
        this.reportPanel = reportPanel;
    }

    private void onAddGroup(ActionEvent e) {
        String name = JOptionPane.showInputDialog(this, "Введите название группы:");
        if (name != null && !name.isBlank()) {
            groupTable.addGroup(new Group(name));
            if (reportPanel != null) {
                reportPanel.updateGroups(groupTable.getAllGroups());
            }
        }
    }

    private void onDeleteGroup(ActionEvent e) {
        int viewRow = table.getSelectedRow();
        if (viewRow >= 0) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            int result = JOptionPane.showConfirmDialog(this,
                    "Удалить группу и всех её студентов?",
                    "Подтверждение",
                    JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                groupTable.removeGroup(modelRow);
                studentPanel.setGroup(null);
                attendancePanel.setGroup(null);
                if (reportPanel != null) {
                    reportPanel.updateGroups(groupTable.getAllGroups());
                }
            }
        }
    }

    public List<Group> getAllGroups() {
        return groupTable.getAllGroups();
    }

    public void reloadGroups(List<Group> groups) {
        groupTable = new GroupTable(groups);
        table.setModel(groupTable);

        // Переподключаем обработчик выбора
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int viewRow = table.getSelectedRow();
                if (viewRow >= 0) {
                    int modelRow = table.convertRowIndexToModel(viewRow);
                    Group g = groupTable.getGroup(modelRow);
                    studentPanel.setGroup(g);
                    attendancePanel.setGroup(g);
                }
            }
        });
    }
}
