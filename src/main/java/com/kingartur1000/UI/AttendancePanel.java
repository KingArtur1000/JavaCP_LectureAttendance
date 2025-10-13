package com.kingartur1000.UI;

import Entities.AttendanceRecord;
import Entities.AttendanceTable;
import Entities.Group;
import Entities.Student;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AttendancePanel extends GridPanel {
    private JTable table;
    private AttendanceTable attendanceTable;
    private Group currentGroup;
    private AttendanceRecord currentRecord;
    private JTextField dateField;
    private LocalDate currentDate;

    public AttendancePanel() {
        super(3, 1);

        JPanel datePanel = new JPanel();
        dateField = new JTextField("ДД.ММ.ГГГГ", 10);
        JButton pickBtn = new JButton("Выбрать");
        datePanel.add(new JLabel("Дата:"));
        datePanel.add(dateField);
        datePanel.add(pickBtn);

        table = new JTable();
        JButton saveButton = new JButton("Сохранить посещаемость");

        addToGrid(datePanel, 0, 0);
        addToGrid(new JScrollPane(table), 1, 0);
        addToGrid(saveButton, 2, 0);

        pickBtn.addActionListener(this::onPickDate);
        saveButton.addActionListener(this::onSaveAttendance);
    }

    public void setGroup(Group g) {
        this.currentGroup = g;
        if (g != null) {
            attendanceTable = new AttendanceTable(g.getStudents());
            table.setModel(attendanceTable);

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

    private void onPickDate(ActionEvent e) {
        try {
            LocalDate date = LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            currentDate = date;
            loadAttendanceForDate(date);
            JOptionPane.showMessageDialog(this, "Выбрана дата: " + date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Неверный формат даты. Используйте ДД.ММ.ГГГГ");
        }
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
                " на дату " + currentDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ":\n\n");

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