package com.kingartur1000.UI;

import com.kingartur1000.Entities.Group;
import com.kingartur1000.Entities.Student;
import com.kingartur1000.Entities.StudentTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.kingartur1000.MainWindow.globalFont;

/**
 * Панель для управления студентами выбранной группы.
 * <p>Содержит таблицу студентов, кнопки добавления/удаления/редактирования и информацию о группе.</p>
 */
public class StudentPanel extends GridPanel {
    /** Таблица студентов */
    private JTable table;
    /** Модель таблицы студентов */
    private StudentTable studentTable;
    /** Метка с названием выбранной группы */
    private JLabel groupLabel;
    /** Текущая выбранная группа */
    private Group currentGroup;

    /**
     * Конструктор панели студентов.
     * <p>Создаёт таблицу, кнопки управления и панель информации о группе.</p>
     */
    public StudentPanel() {
        super(3, 3); // сетка 3 на 3

        // Инициализация пустой таблицы
        studentTable = new StudentTable(new java.util.ArrayList<>());
        table = new JTable(studentTable);
        table.setAutoCreateRowSorter(true); // включаем сортировку по умолчанию
        table.getTableHeader().setFont(new Font(globalFont.getFontName(), Font.BOLD, globalFont.getSize()));
        table.setFont(globalFont);
        table.setRowHeight(50);
        table.getColumnModel().setColumnMargin(15);

        // Выравнивание по правому краю в колонках -Группа-, -Посещений-
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);

        // Панель кнопок управления
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Добавить");
        addBtn.setBackground(new Color(95, 212, 124));
        addBtn.setForeground(new Color(255, 255, 255));
        addBtn.setToolTipText("Выводит окно, для добавления студента в выбранную группу");

        JButton delBtn = new JButton("Удалить");
        delBtn.setBackground(new Color(216, 53, 53));
        delBtn.setForeground(new Color(255, 255, 255));
        delBtn.setToolTipText("Удаляет выбранного студента из группы, требует подтверждения");

        JButton editBtn = new JButton("Редактировать");
        editBtn.setBackground(new Color(239, 167, 59));
        editBtn.setForeground(new Color(255, 255, 255));
        editBtn.setToolTipText("Выводит окно, где можно отредактировать ФИО выбранного студента");

        addBtn.setFont(globalFont);
        delBtn.setFont(globalFont);
        editBtn.setFont(globalFont);

        buttonPanel.add(addBtn);
        buttonPanel.add(delBtn);
        buttonPanel.add(editBtn);

        // Панель с информацией о группе
        JPanel infoPanel = new JPanel();
        groupLabel = new JLabel("Группа не выбрана");
        groupLabel.setFont(globalFont);
        JLabel chosenGroupLabel = new JLabel("Выбранная группа: ");
        chosenGroupLabel.setFont(new Font(globalFont.getFontName(), Font.BOLD, globalFont.getSize()));
        infoPanel.add(chosenGroupLabel);
        infoPanel.add(groupLabel);

        // Размещение элементов в сетке
        addToGrid(buttonPanel, 0, 0, 1, 3);
        addToGrid(new JPanel(), 1, 0, 1, 1, 1, 1);
        addToGrid(new JScrollPane(table), 1, 1, 1, 1, 10, 10);
        addToGrid(new JPanel(), 1, 2, 1, 1, 1, 1);
        addToGrid(infoPanel, 2, 0, 1, 3);

        // Обработчики кнопок
        addBtn.addActionListener(this::onAddStudent);
        delBtn.addActionListener(this::onDeleteStudent);
        editBtn.addActionListener(this::onEditStudent);
    }

    /**
     * Установить текущую группу для панели.
     * <p>Загружает студентов группы в таблицу, включает сортировку и обновляет метку.</p>
     */
    public void setGroup(Group g) {
        this.currentGroup = g;
        if (g != null) {
            studentTable = new StudentTable(g.getStudents());
            table.setModel(studentTable);

            // Создаём сортировщик и включаем сортировку по ФИО
            TableRowSorter<StudentTable> sorter = new TableRowSorter<>(studentTable);
            table.setRowSorter(sorter);

            // Включаем сортировку по первому столбцу (ФИО) по возрастанию
            sorter.toggleSortOrder(0);

            // Выравнивание по правому краю в колонках -Группа-, -Посещений-
            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
            rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
            table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);

            // Настройка ширины колонок
            table.getColumnModel().getColumn(0).setPreferredWidth(250);
            table.getColumnModel().getColumn(1).setPreferredWidth(75);
            table.getColumnModel().getColumn(2).setPreferredWidth(30);

            groupLabel.setFont(globalFont);
            groupLabel.setText(g.getName() + " (" + g.getStudents().size() + " студентов)");

        } else {
            studentTable = new StudentTable(new java.util.ArrayList<>());
            table.setModel(studentTable);
            groupLabel.setFont(globalFont);
            groupLabel.setText("Группа не выбрана");
        }
    }

    /**
     * Добавить нового студента в текущую группу.
     * <p>Открывает диалог для ввода ФИО, добавляет студента и обновляет таблицу.</p>
     */
    private void onAddStudent(ActionEvent e) {
        if (currentGroup == null) {
            JOptionPane.showMessageDialog(this, "Сначала выберите группу");
            return;
        }
        String name = JOptionPane.showInputDialog(this, "Введите ФИО:");
        if (name != null && !name.isBlank()) {
            Student student = new Student(name, currentGroup);
            // Добавляем только через группу - таблица уже ссылается на список группы
            currentGroup.addStudent(student);
            // Уведомляем таблицу об изменении
            studentTable.refresh();
            // Обновляем счётчик студентов
            groupLabel.setText(currentGroup.getName() + " (" + currentGroup.getStudents().size() + " студентов)");
        }
    }

    /**
     * Удалить выбранного студента из группы.
     * <p>Требует подтверждения, обновляет таблицу и счётчик студентов.</p>
     */
    private void onDeleteStudent(ActionEvent e) {
        int viewRow = table.getSelectedRow();
        if (viewRow >= 0) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            Student student = studentTable.getStudent(modelRow);

            if (currentGroup != null) {
                int result = JOptionPane.showConfirmDialog(
                        this,
                        "Удалить студента \"" + student.getFullName() + "\"?",
                        "Подтверждение удаления",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (result == JOptionPane.YES_OPTION) {
                    currentGroup.removeStudent(student);
                    studentTable.refresh();
                    groupLabel.setText(currentGroup.getName() + " (" + currentGroup.getStudents().size() + " студентов)");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Выберите студента для удаления");
        }
    }

    /**
     * Редактировать ФИО выбранного студента.
     * <p>Открывает диалог, сохраняет новое имя и обновляет таблицу.</p>
     */
    private void onEditStudent(ActionEvent e) {
        int viewRow = table.getSelectedRow();
        if (viewRow >= 0) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            Student s = studentTable.getStudent(modelRow);
            String newName = JOptionPane.showInputDialog(this, "Новое ФИО:", s.getFullName());
            if (newName != null && !newName.isBlank()) {
                s.setFullName(newName);
                studentTable.refresh();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Выберите студента для редактирования");
        }
    }
}