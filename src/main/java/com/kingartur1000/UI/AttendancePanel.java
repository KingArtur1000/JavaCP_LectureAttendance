package com.kingartur1000.UI;

import com.kingartur1000.Entities.*;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import static com.kingartur1000.MainWindow.globalFont;

public class AttendancePanel extends GridPanel {
    private JTable table;
    private AttendanceTable attendanceTable;
    private Group currentGroup;
    private AttendanceRecord currentRecord;
    private JDateChooser dateChooser;
    private LocalDate currentDate;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public AttendancePanel() {
        super(3, 3);

        // Панель выбора даты
        JPanel datePanel = new JPanel();
        JLabel dateLabel = new JLabel("Дата: ");
        dateLabel.setFont(globalFont);

        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd.MM.yyyy");
        dateChooser.setFont(globalFont);
        dateChooser.setMinimumSize(new Dimension(150, 30));
        dateChooser.setPreferredSize(new Dimension(200, 40));
        dateChooser.getCalendarButton().setMargin(new Insets(0, 20, 0, 20));
        dateChooser.setDate(new Date()); // сегодняшняя дата по умолчанию

        datePanel.add(dateLabel);
        datePanel.add(dateChooser);

        // Таблица посещаемости
        table = new JTable();
        table.setFont(globalFont);
        table.setAutoCreateRowSorter(true); // включаем сортировку
        table.getTableHeader().setFont(new Font(globalFont.getFontName(), Font.BOLD, globalFont.getSize()));
        table.setRowHeight(40);
        table.getColumnModel().setColumnMargin(15);

        JButton saveButton = new JButton("Сохранить посещаемость");
        saveButton.setFont(globalFont);
        saveButton.setBackground(new Color(95, 212, 124));
        saveButton.setForeground(new Color(255, 255, 255));

        addToGrid(datePanel, 0, 0, 1, 3);
        addToGrid(new JPanel(), 1, 0);
        addToGrid(new JScrollPane(table), 1, 1, 1, 1, 10, 10);
        addToGrid(new JPanel(), 1, 2);

        GridPanel buttonPanel = new GridPanel(1, 3);
        buttonPanel.addToGrid(new JPanel(), 0, 0);
        buttonPanel.addToGrid(new JPanel(), 1, 0, 1, 1, 2, 1);
        buttonPanel.addToGrid(saveButton, 1, 1, 1, 1, 1, 1);
        buttonPanel.addToGrid(new JPanel(), 1, 2, 1, 1, 2, 1);
        addToGrid(buttonPanel, 2, 0, 1, 3);

        // Автоматическая реакция на выбор даты
        dateChooser.getDateEditor().addPropertyChangeListener("date", evt -> {
            Date selectedDate = dateChooser.getDate();
            if (selectedDate != null) {
                LocalDate localDate = selectedDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                currentDate = localDate;
                loadAttendanceForDate(localDate);
            }
        });

        saveButton.addActionListener(this::onSaveAttendance);

        dateChooser.setDate(new Date()); // сегодняшняя дата
    }

    public void setGroup(Group g) {
        this.currentGroup = g;
        if (g != null) {
            attendanceTable = new AttendanceTable(g.getStudents());
            table.setModel(attendanceTable);

            // Создаём сортировщик
            TableRowSorter<AttendanceTable> sorter = new TableRowSorter<>(attendanceTable);
            table.setRowSorter(sorter);

            // Включаем сортировку по первому столбцу (ФИО) по возрастанию
            sorter.toggleSortOrder(0);

            // Если дата уже выбрана, загружаем данные для этой даты
            if (currentDate != null) {
                loadAttendanceForDate(currentDate);
            }
        } else {
            attendanceTable = new AttendanceTable(new ArrayList<>());
            table.setModel(attendanceTable);
            currentRecord = null;
            currentDate = null;
        }
    }


    public void setTodayDate() {
        Date today = new Date();
        dateChooser.setDate(today);

        // сразу обновляем currentDate и данные
        currentDate = today.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        loadAttendanceForDate(currentDate);
    }



    private void loadAttendanceForDate(LocalDate date) {
        if (currentGroup == null) {
            return;
        }

        // Ищем существующую запись для этой даты
        currentRecord = currentGroup.getAttendanceRecordByDate(date);

        if (currentRecord == null) {
            // Создаём новую запись
            currentRecord = new AttendanceRecord(date);
            currentGroup.addAttendanceRecord(currentRecord);
        }

        // Загружаем данные в таблицу
        attendanceTable.loadFromRecord(currentRecord);
    }

    private void onSaveAttendance(ActionEvent e) {
        if (currentGroup == null) {
            JOptionPane.showMessageDialog(this, "Сначала выберите группу");
            return;
        }
        if (currentRecord == null || currentDate == null) {
            JOptionPane.showMessageDialog(this, "Сначала выберите дату");
            return;
        }

        StringBuilder sb = new StringBuilder("Посещаемость для группы " + currentGroup.getName() +
                " на дату " + currentDate.format(DATE_FORMATTER) + ":\n\n");

        for (int i = 0; i < attendanceTable.getRowCount(); i++) {
            Student s = attendanceTable.getStudent(i);
            boolean present = attendanceTable.isPresent(s);
            currentRecord.mark(s, present);
            sb.append(s.getFullName())
                    .append(" — ")
                    .append(present ? "Присутствовал" : "Отсутствовал")
                    .append(" (всего посещений: ")
                    .append(s.getAttendanceCount())
                    .append(")\n");
        }

        JOptionPane.showMessageDialog(this, sb.toString());
    }
}
