package com.kingartur1000.Entities;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Модель таблицы для отображения списка учебных групп.
 * <p>Первая колонка содержит название группы, вторая — количество студентов.</p>
 * @author Артур
 * @version 1.9
 */
public class GroupTable extends AbstractTableModel {
    /** Список групп */
    private final List<Group> groups;
    /** Заголовки колонок таблицы */
    private final String[] columns = {"Номер группы", "Кол-во студ."};

    /**
     * Конструктор таблицы групп.
     *
     * @param groups список групп
     */
    public GroupTable(List<Group> groups) {
        this.groups = groups;
    }

    /** @return количество строк (равно числу групп) */
    @Override
    public int getRowCount() {
        return groups.size();
    }

    /** @return количество колонок (название группы и количество студентов) */
    @Override
    public int getColumnCount() {
        return columns.length;
    }

    /**
     * Возвращает название колонки.
     *
     * @param column индекс колонки
     * @return название колонки
     */
    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    /*
     * Метод getValueAt.
     * Алгоритм:
     * 1. Получить группу по индексу строки.
     * 2. Если колонка = 0 -> вернуть название группы.
     * 3. Если колонка = 1 -> вернуть количество студентов.
     * 4. Если колонка не определена -> вернуть null.
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Group g = groups.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> g.getName();
            case 1 -> g.getStudents().size();
            default -> null;
        };
    }

    /**
     * Добавить новую группу в таблицу.
     *
     * @param g группа для добавления
     */
    public void addGroup(Group g) {
        groups.add(g);
        // уведомляем JTable о добавлении новой строки
        fireTableRowsInserted(groups.size() - 1, groups.size() - 1);
    }

    /**
     * Удалить группу по индексу.
     *
     * @param index индекс группы для удаления
     */
    public void removeGroup(int index) {
        groups.remove(index);
        // уведомляем JTable об удалении строки
        fireTableRowsDeleted(index, index);
    }

    /**
     * Получить группу по индексу.
     *
     * @param index индекс строки
     * @return объект Group
     */
    public Group getGroup(int index) {
        return groups.get(index);
    }

    /**
     * Получить все группы.
     *
     * @return список всех групп
     */
    public List<Group> getAllGroups() {
        return groups;
    }
}
