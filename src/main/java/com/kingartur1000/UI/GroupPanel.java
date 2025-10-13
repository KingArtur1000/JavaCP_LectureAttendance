package com.kingartur1000.UI;

import Entities.Group;
import Entities.GroupTable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class GroupPanel extends GridPanel {
    private JTable table;
    private GroupTable groupTable;
    private StudentPanel studentPanel;
    private AttendancePanel attendancePanel;
    private ReportPanel reportPanel;

    public GroupPanel(StudentPanel studentPanel, AttendancePanel attendancePanel) {
        super(2, 1);
        this.studentPanel = studentPanel;
        this.attendancePanel = attendancePanel;

        List<Group> groups = new ArrayList<>();
        groups.add(new Group("10702423"));
        groups.add(new Group("10702424"));

        groupTable = new GroupTable(groups);
        table = new JTable(groupTable);

        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Добавить группу");
        JButton delBtn = new JButton("Удалить группу");

        buttonPanel.add(addBtn);
        buttonPanel.add(delBtn);

        addToGrid(buttonPanel, 0, 0);
        addToGrid(new JScrollPane(table), 1, 0);

        addBtn.addActionListener(this::onAddGroup);
        delBtn.addActionListener(this::onDeleteGroup);

        // Обработчик выбора группы
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    Group g = groupTable.getGroup(row);
                    studentPanel.setGroup(g);
                    attendancePanel.setGroup(g);
                }
            }
        });
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
        int row = table.getSelectedRow();
        if (row >= 0) {
            groupTable.removeGroup(row);
            studentPanel.setGroup(null);
            attendancePanel.setGroup(null);
            if (reportPanel != null) {
                reportPanel.updateGroups(groupTable.getAllGroups());
            }
        }
    }

    public List<Group> getAllGroups() {
        return groupTable.getAllGroups();
    }
}