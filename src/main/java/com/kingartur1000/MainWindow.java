package com.kingartur1000;

import com.kingartur1000.UI.*;

import javax.swing.*;

public class MainWindow {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Учет посещаемости лекционных занятий");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 600);

            JTabbedPane tabbedPane = new JTabbedPane();

            StudentPanel studentPanel = new StudentPanel();
            AttendancePanel attendancePanel = new AttendancePanel();
            GroupPanel groupPanel = new GroupPanel(studentPanel, attendancePanel);
            ReportPanel reportPanel = new ReportPanel(groupPanel.getAllGroups());

            // Связываем GroupPanel с ReportPanel для обновлений
            groupPanel.setReportPanel(reportPanel);

            tabbedPane.addTab("Группы", groupPanel);
            tabbedPane.addTab("Студенты", studentPanel);
            tabbedPane.addTab("Посещаемость", attendancePanel);
            tabbedPane.addTab("Отчёты", reportPanel);

            frame.add(tabbedPane);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}