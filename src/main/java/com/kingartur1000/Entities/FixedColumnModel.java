package com.kingartur1000.Entities;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Модель таблицы с фиксированной одной колонкой.
 * <p>Отображает только список студентов (ФИО).</p>
 * @author Артур
 * @version 1.9
 */
public class FixedColumnModel extends AbstractTableModel {
    /** Список студентов выбранной группы */
    private List<Student> students = new ArrayList<>();

    /**
     * Устанавливает группу для модели.
     * <p>Загружает студентов и обновляет структуру таблицы.</p>
     *
     * @param group выбранная группа
     */
    public void setGroup(Group group) {
        this.students = group.getStudents();
        fireTableStructureChanged(); // уведомляем JTable о смене структуры
    }

    /** @return количество строк (равно числу студентов) */
    @Override
    public int getRowCount() {
        return students.size();
    }

    /*
     * Метод getColumnCount.
     * Алгоритм:
     * 1. Возвращает фиксированное значение 1.
     * 2. Единственная колонка — ФИО студента.
     */
    @Override
    public int getColumnCount() {
        return 1; // Только ФИО
    }

    /**
     * Возвращает название колонки.
     *
     * @param column индекс колонки
     * @return строка "ФИО"
     */
    @Override
    public String getColumnName(int column) {
        return "ФИО";
    }

    /**
     * Возвращает значение ячейки таблицы.
     *
     * @param rowIndex индекс строки (студент)
     * @param columnIndex индекс колонки (всегда 0)
     * @return ФИО студента
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        // Получаем студента по индексу строки и возвращаем его ФИО
        return students.get(rowIndex).getFullName();
    }
}
