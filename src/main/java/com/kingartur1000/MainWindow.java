package com.kingartur1000;

import com.kingartur1000.Data.DataManager;
import com.kingartur1000.Entities.Group;
import com.kingartur1000.UI.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class MainWindow {
    private static List<Group> groups;
    private static GroupPanel groupPanel;
    public static Font globalFont = new Font("Arial", Font.PLAIN, 20);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Курсовой проект Дмитриева А.А. - Учет посещаемости лекционных занятий");
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setSize(960, 600);

            // Создаём меню
            JMenuBar menuBar = new JMenuBar();
            menuBar.setFont(globalFont);
            JMenu fileMenu = new JMenu("Файл");
            JMenu whatMenu = new JMenu("?");
            fileMenu.setFont(new Font("Arial", Font.BOLD, 20));
            whatMenu.setFont(new Font("Arial", Font.BOLD, 20));

            JMenuItem saveItem = new JMenuItem("Сохранить");
            saveItem.setFont(globalFont);
            JMenuItem loadItem = new JMenuItem("Загрузить");
            loadItem.setFont(globalFont);
            JMenuItem exitItem = new JMenuItem("Выход");
            exitItem.setFont(globalFont);

            fileMenu.add(saveItem);
            fileMenu.add(loadItem);
            fileMenu.addSeparator();
            fileMenu.add(exitItem);

            JMenuItem aboutAuthorItem = new JMenuItem("Об авторе");
            JMenuItem aboutProgramItem = new JMenuItem("О программе");

            aboutAuthorItem.setFont(globalFont);
            aboutProgramItem.setFont(globalFont);
            whatMenu.add(aboutAuthorItem);
            whatMenu.add(aboutProgramItem);

            menuBar.add(fileMenu);
            menuBar.add(whatMenu);
            frame.setJMenuBar(menuBar);

            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.setFont(globalFont);


            StudentPanel studentPanel = new StudentPanel();
            AttendancePanel attendancePanel = new AttendancePanel();

            // Пытаемся загрузить данные при старте
            if (DataManager.dataFileExists()) {
                try {
                    groups = DataManager.loadData();
                    JOptionPane.showMessageDialog(frame,
                            "Данные успешно загружены из файла!",
                            "Загрузка",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    groups = createDefaultGroups();
                    JOptionPane.showMessageDialog(frame,
                            "Ошибка загрузки данных: " + e.getMessage() + "\nСозданы группы по умолчанию.",
                            "Ошибка",
                            JOptionPane.WARNING_MESSAGE);
                }
            } else {
                groups = createDefaultGroups();
            }

            groupPanel = new GroupPanel(studentPanel, attendancePanel, groups);
            ReportPanel reportPanel = new ReportPanel(groupPanel.getAllGroups());

            // Связываем GroupPanel с ReportPanel для обновлений
            groupPanel.setReportPanel(reportPanel);

            tabbedPane.addTab("Группы", groupPanel);
            tabbedPane.addTab("Студенты", studentPanel);
            tabbedPane.addTab("Посещаемость", attendancePanel);
            tabbedPane.addTab("Отчёты", reportPanel);

            GridPanel bottomGrid = new GridPanel(1, 6);
            bottomGrid.setFont(globalFont);

            Button exitButton = new Button("Выход");
            exitButton.setFont(globalFont);
            exitButton.setBackground(Color.RED);
            exitButton.setForeground(Color.WHITE);
            exitButton.addActionListener(e -> exitApplication(frame));
            bottomGrid.addToGrid(new JPanel(), 0, 0);
            bottomGrid.addToGrid(new JPanel(), 0, 1);
            bottomGrid.addToGrid(new JPanel(), 0, 2);
            bottomGrid.addToGrid(new JPanel(), 0, 3);
            bottomGrid.addToGrid(new JPanel(), 0, 4);
            bottomGrid.addToGrid(exitButton, 0, 5);

            GridPanel mainGrid = new GridPanel(2, 1);
            mainGrid.setFont(globalFont);
            mainGrid.addToGrid(tabbedPane, 0, 0, 1, 1, 1, 12);
            mainGrid.addToGrid(bottomGrid, 1, 0);

            frame.add(mainGrid);
            frame.setLocationRelativeTo(null);

            // Обработчики меню
            saveItem.addActionListener(e -> saveData(frame));
            loadItem.addActionListener(e -> loadData(frame, studentPanel, attendancePanel, reportPanel));
            exitItem.addActionListener(e -> exitApplication(frame));
            aboutAuthorItem.addActionListener(e -> {
                    JOptionPane.showMessageDialog(frame,
                    "Автор: Дмитриев Артур Александрович, студент БНТУ, ФИТР, гр. 10702423",
                    "Об авторе",
                    JOptionPane.INFORMATION_MESSAGE);});
            aboutProgramItem.addActionListener(e -> {
                JOptionPane.showMessageDialog(frame,
                        "Версия программы: 1.2\n" +
                                "• 1.2: Добавлена сортировка по столбцам во всех вкладках\n" +
                                "• 1.1: Добавлен модуль JCalendar для более удобного выбора даты\n" +
                                "• 1.0: Основной функционал реализован\n",
                        "О программе",
                        JOptionPane.INFORMATION_MESSAGE);
            });

            // Обработчик закрытия окна
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    exitApplication(frame);
                }
            });

            // Обработчик смены вкладок
            tabbedPane.addChangeListener(e -> {
                int index = tabbedPane.getSelectedIndex();
                String title = tabbedPane.getTitleAt(index);

                if ("Посещаемость".equals(title)) {
                    // при переходе во вкладку "Посещаемость"
                    attendancePanel.setTodayDate();
                }
            });


            frame.setVisible(true);
        });
    }

    private static List<Group> createDefaultGroups() {
        return new java.util.ArrayList<>();
    }

    private static void saveData(JFrame frame) {
        try {
            DataManager.saveData(groupPanel.getAllGroups());
            JOptionPane.showMessageDialog(frame,
                    "Данные успешно сохранены в файл attendance_data.xlsx!",
                    "Сохранение",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame,
                    "Ошибка сохранения данных: " + ex.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private static void loadData(JFrame frame, StudentPanel studentPanel,
                                 AttendancePanel attendancePanel, ReportPanel reportPanel) {
        int result = JOptionPane.showConfirmDialog(frame,
                "Загрузить данные из файла? Текущие несохранённые данные будут потеряны.",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            try {
                groups = DataManager.loadData();

                // Обновляем все панели
                groupPanel.reloadGroups(groups);
                studentPanel.setGroup(null);
                attendancePanel.setGroup(null);
                reportPanel.updateGroups(groups);

                JOptionPane.showMessageDialog(frame,
                        "Данные успешно загружены из файла!",
                        "Загрузка",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame,
                        "Ошибка загрузки данных: " + ex.getMessage(),
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private static void exitApplication(JFrame frame) {
        int result = JOptionPane.showConfirmDialog(frame,
                "Сохранить данные перед выходом?",
                "Выход",
                JOptionPane.YES_NO_CANCEL_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            saveData(frame);
            System.exit(0);
        } else if (result == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
        // CANCEL - ничего не делаем
    }
}