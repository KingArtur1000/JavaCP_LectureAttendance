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

public class StudentPanel extends GridPanel {
    private JTable table;
    private StudentTable studentTable;
    private JLabel groupLabel;
    private Group currentGroup;

    public StudentPanel() {
        super(3, 3);

        studentTable = new StudentTable(new java.util.ArrayList<>());
        table = new JTable(studentTable);
        table.setAutoCreateRowSorter(true); // включаем сортировку
        table.getTableHeader().setFont(new Font(globalFont.getFontName(), Font.BOLD, globalFont.getSize()));
        table.setFont(globalFont);
        table.setRowHeight(50);
        table.getColumnModel().setColumnMargin(15);

        // Выравнивание по правому краю в колонках -Группа-, -Посещений-
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);


        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Добавить");
        JButton delBtn = new JButton("Удалить");
        JButton editBtn = new JButton("Редактировать");
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

        addToGrid(buttonPanel, 0, 0, 1, 3);
        addToGrid(new JPanel(), 1, 0, 1, 1, 1, 1);
        addToGrid(new JScrollPane(table), 1, 1, 1, 1, 10, 10);
        addToGrid(new JPanel(), 1, 2, 1, 1, 1, 1);
        addToGrid(infoPanel, 2, 0, 1, 3);

        addBtn.addActionListener(this::onAddStudent);
        delBtn.addActionListener(this::onDeleteStudent);
        editBtn.addActionListener(this::onEditStudent);
    }

    public void setGroup(Group g) {
        this.currentGroup = g;
        if (g != null) {
            studentTable = new StudentTable(g.getStudents());
            table.setModel(studentTable);

            // Создаём сортировщик
            TableRowSorter<StudentTable> sorter = new TableRowSorter<>(studentTable);
            table.setRowSorter(sorter);

            // Включаем сортировку по первому столбцу (ФИО) по возрастанию
            sorter.toggleSortOrder(0);

            // Выравнивание по правому краю в колонках -Группа-, -Посещений-
            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
            rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
            table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);


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

    private void onDeleteStudent(ActionEvent e) {
        int viewRow = table.getSelectedRow();
        if (viewRow >= 0) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            Student student = studentTable.getStudent(modelRow);
            if (currentGroup != null) {
                currentGroup.removeStudent(student);
                studentTable.refresh();
                groupLabel.setText(currentGroup.getName() + " (" + currentGroup.getStudents().size() + " студентов)");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Выберите студента для удаления");
        }
    }

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