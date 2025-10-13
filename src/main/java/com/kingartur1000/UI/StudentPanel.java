package com.kingartur1000.UI;

import Entities.Group;
import Entities.Student;
import Entities.StudentTable;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class StudentPanel extends GridPanel {
    private JTable table;
    private StudentTable studentTable;
    private JTextField searchField;
    private Group currentGroup;

    public StudentPanel() {
        super(3, 1);

        studentTable = new StudentTable(new java.util.ArrayList<>());
        table = new JTable(studentTable);

        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Добавить");
        JButton delBtn = new JButton("Удалить");
        JButton editBtn = new JButton("Редактировать");

        buttonPanel.add(addBtn);
        buttonPanel.add(delBtn);
        buttonPanel.add(editBtn);

        searchField = new JTextField("Поиск по фамилии...");

        addToGrid(buttonPanel, 0, 0);
        addToGrid(new JScrollPane(table), 1, 0);
        addToGrid(searchField, 2, 0);

        addBtn.addActionListener(this::onAddStudent);
        delBtn.addActionListener(this::onDeleteStudent);
        editBtn.addActionListener(this::onEditStudent);
        searchField.addActionListener(this::onSearch);
    }

    public void setGroup(Group g) {
        this.currentGroup = g;
        if (g != null) {
            studentTable = new StudentTable(g.getStudents());
            table.setModel(studentTable);
        } else {
            studentTable = new StudentTable(new java.util.ArrayList<>());
            table.setModel(studentTable);
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
        }
    }

    private void onDeleteStudent(ActionEvent e) {
        int row = table.getSelectedRow();
        if (row >= 0) {
            Student student = studentTable.getStudent(row);
            if (currentGroup != null) {
                currentGroup.removeStudent(student);
                // Уведомляем таблицу об изменении
                studentTable.refresh();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Выберите студента для удаления");
        }
    }

    private void onEditStudent(ActionEvent e) {
        int row = table.getSelectedRow();
        if (row >= 0) {
            Student s = studentTable.getStudent(row);
            String newName = JOptionPane.showInputDialog(this, "Новое ФИО:", s.getFullName());
            if (newName != null && !newName.isBlank()) {
                s.setFullName(newName);
                studentTable.refresh();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Выберите студента для редактирования");
        }
    }

    private void onSearch(ActionEvent e) {
        String query = searchField.getText().toLowerCase();
        for (int i = 0; i < studentTable.getRowCount(); i++) {
            String name = studentTable.getStudent(i).getFullName().toLowerCase();
            if (name.contains(query)) {
                table.setRowSelectionInterval(i, i);
                table.scrollRectToVisible(table.getCellRect(i, 0, true));
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Студент не найден");
    }
}