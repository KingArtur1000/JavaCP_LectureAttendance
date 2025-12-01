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

/**
 * Панель для отображения и редактирования посещаемости студентов.
 * <p>Содержит: выбор даты, таблицу студентов, статистику, справку и кнопку сохранения.</p>
 * <p>Обновляется при смене группы или даты, сохраняет данные в модель AttendanceRecord.</p>
 */
public class AttendancePanel extends GridPanel {
    /** Таблица студентов с их статусами */
    private JTable table;
    /** Модель таблицы посещаемости */
    private AttendanceTable attendanceTable;
    /** Текущая выбранная группа */
    private Group currentGroup;
    /** Запись посещаемости на выбранную дату */
    private AttendanceRecord currentRecord;
    /** Компонент выбора даты */
    private JDateChooser dateChooser;
    /** Текущая выбранная дата */
    private LocalDate currentDate;
    /** Форматтер для отображения дат */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    /** Метка с названием выбранной группы */
    private JLabel groupLabel;
    /** Метки статистики */
    private JLabel totalLabel, presentLabel, absentLabel;

    /**
     * Конструктор панели.
     * <p>Создаёт интерфейс: заголовок, выбор даты, таблицу, статистику, справку и кнопку сохранения.</p>
     */
    public AttendancePanel() {
        super(3, 3); // сетка 3×3

        // Панель выбора даты и группы
        JPanel datePanel = new JPanel();
        datePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // верхний отступ
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

        // Таблица посещаемости студентов
        table = new JTable();
        table.setFont(globalFont);
        table.setRowHeight(70);
        table.getTableHeader().setFont(new Font(globalFont.getFontName(), Font.BOLD, globalFont.getSize()));

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(300, 400)); // фиксированный размер
        tableWrapper.add(scrollPane, BorderLayout.CENTER);

        // Кнопка сохранения
        JButton saveButton = new JButton("Сохранить посещаемость");
        saveButton.setFont(globalFont);
        saveButton.setBackground(new Color(95, 212, 124));
        saveButton.setForeground(Color.WHITE);

        // Левая панель статистики
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Статистика"));
        totalLabel = new JLabel("Всего студентов: —");
        presentLabel = new JLabel("Присутствуют: —");
        absentLabel = new JLabel("Отсутствуют: —");
        for (JLabel label : new JLabel[]{totalLabel, presentLabel, absentLabel}) {
            label.setFont(globalFont);
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            statsPanel.add(label);
        }

        // Правая панель справки
        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBorder(BorderFactory.createTitledBorder("Справка"));
        legendPanel.setPreferredSize(new Dimension(180, 400)); // компактная ширина

        JLabel spravka = new JLabel("Как отмечать студентов:");
        spravka.setFont(new Font(globalFont.getFontName(), Font.BOLD, globalFont.getSize()));
        spravka.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel present = new JLabel("Присутствовал — нажать ЛКМ");
        JLabel late = new JLabel("Опоздал (красная галочка) — нажать ПКМ");
        JLabel absent = new JLabel("Отсутствовал — оставить пустым");

        for (JLabel label : new JLabel[]{spravka, present, late, absent}) {
            label.setFont(globalFont);
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            legendPanel.add(label);
        }

        // Добавление компонентов в сетку
        addToGrid(datePanel, 0, 0, 1, 3);
        addToGrid(statsPanel, 1, 0);
        addToGrid(tableWrapper, 1, 1, 1, 1, 1, 1);
        addToGrid(legendPanel, 1, 2);
        addToGrid(saveButton, 2, 1, 1, 1);

        // Обработчик смены даты
        dateChooser.getDateEditor().addPropertyChangeListener("date", evt -> {
            Date selectedDate = dateChooser.getDate();
            if (selectedDate != null) {
                currentDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                loadAttendanceForDate(currentDate);
            }
        });

        // Обработчик кнопки сохранения
        saveButton.addActionListener(this::onSaveAttendance);
    }

    /**
     * Установить выбранную группу.
     * <p>Загружает студентов, настраивает таблицу и обновляет статистику.</p>
     */
    public void setGroup(Group g) {
        this.currentGroup = g;
        if (g != null) {
            attendanceTable = new AttendanceTable(g.getStudents());
            table.setModel(attendanceTable);

            TableRowSorter<AttendanceTable> sorter = new TableRowSorter<>(attendanceTable);
            table.setRowSorter(sorter);
            sorter.toggleSortOrder(0);

            // Настройка столбцов
            table.getColumnModel().getColumn(0).setPreferredWidth(250);
            DefaultTableCellRenderer leftMarginRenderer = new DefaultTableCellRenderer();
            leftMarginRenderer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
            table.getColumnModel().getColumn(0).setCellRenderer(leftMarginRenderer);

            table.getColumnModel().getColumn(1).setPreferredWidth(50);
            table.getColumnModel().getColumn(1).setCellRenderer(new AttendanceMarkRenderer());

            // Обработка кликов по статусу
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
            totalLabel.setText("Всего студентов: " + g.getStudents().size());
            presentLabel.setText("Присутствуют: —");
            absentLabel.setText("Отсутствуют: —");
        } else {
            attendanceTable = new AttendanceTable(new ArrayList<>());
            table.setModel(attendanceTable);
            currentRecord = null;
            currentDate = null;
            groupLabel.setText("Выбранная группа: —");
            totalLabel.setText("Всего студентов: —");
            presentLabel.setText("Присутствуют: —");
            absentLabel.setText("Отсутствуют: —");
        }
    }

    /** @return текущая выбранная группа */
    public Group getCurrentGroup() {
        return currentGroup;
    }

    /** Установить сегодняшнюю дату в календарь и загрузить данные */
    public void setTodayDate() {
        Date today = new Date();
        dateChooser.setDate(today);
        currentDate = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        loadAttendanceForDate(currentDate);
    }

    /**
     * Загрузить посещаемость на выбранную дату.
     * <p>Если записи нет — таблица очищается. Если есть — данные загружаются.</p>
     */
    private void loadAttendanceForDate(LocalDate date) {
        if (currentGroup == null) return;

        currentRecord = currentGroup.getAttendanceRecordByDate(date);

        if (currentRecord == null) {
            attendanceTable.clear();
            presentLabel.setText("Присутствуют: 0");
            absentLabel.setText("Отсутствуют: " + currentGroup.getStudents().size());
            return;
        }

        attendanceTable.loadFromRecord(currentRecord);

        long presentCount = currentGroup.getStudents().stream()
                .filter(s -> currentRecord.isPresent(s))
                .count();

        presentLabel.setText("Присутствуют: " + presentCount);
        absentLabel.setText("Отсутствуют: " + (currentGroup.getStudents().size() - presentCount));
    }

    /**
     * Обработчик кнопки сохранения посещаемости.
     * <p>Сохраняет статусы студентов в запись, обновляет статистику и показывает итоговый отчёт.</p>
     * <p>Если никто не присутствовал — запись удаляется.</p>
     */
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

            // сохраняем статус студента в записи
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

        // если никто не присутствовал — удаляем запись
        if (presentCount == 0) {
            currentGroup.removeAttendanceRecord(currentRecord);
            currentRecord = null;
            JOptionPane.showMessageDialog(this, "Никого не было — лекция удалена из отчётов");
            presentLabel.setText("Присутствуют: 0");
            absentLabel.setText("Отсутствуют: " + currentGroup.getStudents().size());
            return;
        }

        // обновляем статистику
        presentLabel.setText("Присутствуют: " + presentCount);
        absentLabel.setText("Отсутствуют: " + (currentGroup.getStudents().size() - presentCount));

        // показываем итоговый отчёт
        JOptionPane.showMessageDialog(this, sb.toString());
    }
}
