package com.kingartur1000.UI;

import com.kingartur1000.Entities.*;
import com.kingartur1000.Entities.AttendanceTable.AttendanceMark;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
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
    private JLabel groupLabel;

    public AttendancePanel() {
        super(3, 3);

        // 🔹 Панель выбора даты
        JPanel datePanel = new JPanel();
        JLabel dateLabel = new JLabel("Дата: ");
        dateLabel.setFont(globalFont);

        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd.MM.yyyy");
        dateChooser.setFont(globalFont);
        dateChooser.setPreferredSize(new Dimension(200, 40));
        dateChooser.setDate(new Date());

        groupLabel = new JLabel("Выбранная группа: —");
        groupLabel.setFont(new Font(globalFont.getFontName(), Font.BOLD, 20));
        datePanel.add(groupLabel);
        datePanel.add(dateLabel);
        datePanel.add(dateChooser);

        // 🔹 Таблица посещаемости
        table = new JTable();
        table.setFont(globalFont);
        table.setRowHeight(70);
        table.getTableHeader().setFont(new Font(globalFont.getFontName(), Font.BOLD, globalFont.getSize()));

        // 🔹 Обёртка таблицы с отступами
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(300, 400));
        tableWrapper.add(scrollPane, BorderLayout.CENTER);

        // 🔹 Кнопка сохранения
        JButton saveButton = new JButton("Сохранить посещаемость");
        saveButton.setFont(globalFont);
        saveButton.setBackground(new Color(95, 212, 124));
        saveButton.setForeground(Color.WHITE);

        // 🔹 Добавление в сетку
        addToGrid(datePanel, 0, 0, 1, 3);
        addToGrid(new GridPanel(1, 1), 1, 0); // отступ слева
        addToGrid(tableWrapper, 1, 1, 1, 1, 1, 1);
        addToGrid(new GridPanel(1, 1), 1, 2); // отступ справа
        addToGrid(saveButton, 2, 1, 1, 1);

        // 🔹 Обработчик смены даты
        dateChooser.getDateEditor().addPropertyChangeListener("date", evt -> {
            Date selectedDate = dateChooser.getDate();
            if (selectedDate != null) {
                currentDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                loadAttendanceForDate(currentDate);
            }
        });

        saveButton.addActionListener(this::onSaveAttendance);
    }

    public void setGroup(Group g) {
        this.currentGroup = g;
        if (g != null) {
            attendanceTable = new AttendanceTable(g.getStudents());
            table.setModel(attendanceTable);

            TableRowSorter<AttendanceTable> sorter = new TableRowSorter<>(attendanceTable);
            table.setRowSorter(sorter);
            sorter.toggleSortOrder(0);

            // 🔹 Столбец ФИО
            table.getColumnModel().getColumn(0).setPreferredWidth(250);

            // 🔹 Столбец Статус
            table.getColumnModel().getColumn(1).setPreferredWidth(50);
            table.getColumnModel().getColumn(1).setCellRenderer(new AttendanceMarkRenderer());

            // 🔹 Обработка кликов
            table.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mousePressed(java.awt.event.MouseEvent e) {
                    int row = table.rowAtPoint(e.getPoint());
                    int col = table.columnAtPoint(e.getPoint());
                    if (col == 1 && row >= 0) {
                        Student s = attendanceTable.getStudent(row);
                        AttendanceMark next = switch (e.getButton()) {
                            case java.awt.event.MouseEvent.BUTTON1 -> AttendanceMark.PRESENT;
                            case java.awt.event.MouseEvent.BUTTON3 -> AttendanceMark.LATE;
                            case java.awt.event.MouseEvent.BUTTON2 -> AttendanceMark.ABSENT;
                            default -> AttendanceMark.ABSENT;
                        };
                        attendanceTable.setMark(s, next);
                    }
                }
            });

            if (currentDate != null) {
                loadAttendanceForDate(currentDate);
            }

            groupLabel.setText("Выбранная группа: " + currentGroup.getName());
        } else {
            attendanceTable = new AttendanceTable(new ArrayList<>());
            table.setModel(attendanceTable);
            currentRecord = null;
            currentDate = null;
            groupLabel.setText("Выбранная группа: —");
        }
    }

    public Group getCurrentGroup() {
        return currentGroup;
    }

    public void setTodayDate() {
        Date today = new Date();
        dateChooser.setDate(today);
        currentDate = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        loadAttendanceForDate(currentDate);
    }

    private void loadAttendanceForDate(LocalDate date) {
        if (currentGroup == null) return;

        currentRecord = currentGroup.getAttendanceRecordByDate(date);

        if (currentRecord == null) {
            attendanceTable.clear();
            return;
        }

        attendanceTable.loadFromRecord(currentRecord);
    }

    private void onSaveAttendance(ActionEvent e) {
        if (currentGroup == null || currentDate == null) {
            JOptionPane.showMessageDialog(this, "Сначала выберите группу и дату");
            return;
        }

        if (currentRecord == null) {
            currentRecord = new AttendanceRecord(currentDate);
            currentGroup.addAttendanceRecord(currentRecord);
        }

        int presentCount = 0;
        StringBuilder sb = new StringBuilder("Посещаемость для группы " + currentGroup.getName() +
                " на дату " + currentDate.format(DATE_FORMATTER) + ":\n\n");

        for (int i = 0; i < attendanceTable.getRowCount(); i++) {
            Student s = attendanceTable.getStudent(i);
            AttendanceMark mark = attendanceTable.getMark(s);

            currentRecord.mark(s, mark);

            boolean present = (mark == AttendanceMark.PRESENT || mark == AttendanceMark.LATE);
            if (present) presentCount++;

            sb.append(s.getFullName())
                    .append(" — ")
                    .append(mark == AttendanceMark.PRESENT ? "Присутствовал" :
                            mark == AttendanceMark.LATE ? "Опоздал" : "Отсутствовал")
                    .append(" (всего посещений: ")
                    .append(s.getAttendanceCount())
                    .append(")\n");
        }

        if (presentCount == 0) {
            currentGroup.removeAttendanceRecord(currentRecord);
            currentRecord = null;
            JOptionPane.showMessageDialog(this, "Никого не было — лекция удалена из отчётов");
            return;
        }

        JOptionPane.showMessageDialog(this, sb.toString());
    }
}
