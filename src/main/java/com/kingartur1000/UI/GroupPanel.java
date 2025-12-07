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

/**
 * Панель для управления учебными группами.
 * <p>Отображает список групп в таблице, позволяет добавлять и удалять группы,
 * а также синхронизирует выбранную группу с панелями студентов, посещаемости и отчётов.</p>
 * @author Роман
 * @version 1.9
 */
public class GroupPanel extends GridPanel {
    /** Таблица для отображения групп */
    private JTable table;
    /** Модель таблицы групп */
    private GroupTable groupTable;
    /** Панель студентов, связанная с выбранной группой */
    private StudentPanel studentPanel;
    /** Панель посещаемости, связанная с выбранной группой */
    private AttendancePanel attendancePanel;
    /** Панель отчётов, связанная с выбранной группой */
    private ReportPanel reportPanel;

    /**
     * Конструктор панели групп.
     *
     * @param studentPanel панель студентов
     * @param attendancePanel панель посещаемости
     * @param groups список групп
     */
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

        // Панель кнопок управления
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Добавить");
        addBtn.setFont(globalFont);
        addBtn.setBackground(new Color(95, 212, 124));
        addBtn.setForeground(new Color(255, 255, 255));
        addBtn.setToolTipText("Выводит окно, для добавления группы");
        JButton delBtn = new JButton("Удалить");
        delBtn.setFont(globalFont);
        delBtn.setBackground(new Color(216, 53, 53));
        delBtn.setForeground(new Color(255, 255, 255));
        delBtn.setToolTipText("Удаляет выбранную группу, требует подтверждения");

        buttonPanel.add(addBtn);
        buttonPanel.add(delBtn);

        // Добавление элементов в сетку
        addToGrid(buttonPanel, 0, 0, 1, 5);
        addToGrid(new JScrollPane(table), 1, 1, 1, 1, 5, 5);
        addToGrid(new GridPanel(1, 1), 1, 0); // отступ слева
        addToGrid(new GridPanel(1, 1), 1, 4); // отступ справа
        addToGrid(new GridPanel(1, 1), 2, 0);  // нижний отступ

        // Обработчики кнопок
        addBtn.addActionListener(this::onAddGroup);
        delBtn.addActionListener(this::onDeleteGroup);

        /*
         * Обработчик выбора группы:
         * 1. Получаем выбранную строку.
         * 2. Конвертируем индекс из представления в модель.
         * 3. Получаем группу и передаём её в связанные панели.
         */
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

        // Создаём сортировщик и включаем сортировку по первому столбцу
        TableRowSorter<GroupTable> sorter = new TableRowSorter<>(groupTable);
        table.setRowSorter(sorter);

        // Включаем сортировку по первому столбцу (Номер группы) по возрастанию
        sorter.toggleSortOrder(0);
    }

    /** Установить панель отчётов */
    public void setReportPanel(ReportPanel reportPanel) {
        this.reportPanel = reportPanel;
    }

    /*
     * Обработчик кнопки "Добавить".
     * Алгоритм:
     * 1. Запросить название группы у пользователя.
     * 2. Если введено корректное название — добавить группу.
     */
    private void onAddGroup(ActionEvent e) {
        String name = JOptionPane.showInputDialog(this, "Введите название группы:");
        if (name != null && !name.isBlank()) {
            groupTable.addGroup(new Group(name));

        }
    }

    /*
     * Обработчик кнопки "Удалить".
     * Алгоритм:
     * 1. Проверить, выбрана ли строка.
     * 2. Запросить подтверждение удаления.
     * 3. Если подтверждено — удалить группу и очистить связанные панели.
     */
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
            }
        }
    }

    /** @return список всех групп */
    public List<Group> getAllGroups() {
        return groupTable.getAllGroups();
    }

    /**
     * Перезагрузить список групп.
     * <p>Создаёт новую модель таблицы и переподключает обработчик выбора.</p>
     *
     * @param groups список групп
     */
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
