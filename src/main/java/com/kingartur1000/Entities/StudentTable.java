package com.kingartur1000.Entities;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Модель таблицы для отображения списка студентов.
 * <p>Колонки: ФИО, название группы, количество посещений (с процентом).</p>
 */
public class StudentTable extends AbstractTableModel {
    /** Список студентов */
    private final List<Student> students;
    /** Заголовки колонок таблицы */
    private final String[] columns = {"ФИО", "Группа", "Посещений"};

    /**
     * Конструктор таблицы студентов.
     *
     * @param students список студентов
     */
    public StudentTable(List<Student> students) {
        this.students = students;
    }

    /** @return количество строк (равно числу студентов) */
    @Override
    public int getRowCount() {
        return students.size();
    }

    /** @return количество колонок (ФИО, группа, посещения) */
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
     * 1. Получить студента по индексу строки.
     * 2. Если колонка = 0 -> вернуть ФИО.
     * 3. Если колонка = 1 -> вернуть название группы (или пустую строку).
     * 4. Если колонка = 2 -> вычислить количество посещений и процент:
     *    - Если лекций нет -> вернуть "0 (0%)".
     *    - Иначе рассчитать процент посещений и вернуть строку вида "X (Y%)".
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Student s = students.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> s.getFullName(); // ФИО
            case 1 -> s.getGroup() != null ? s.getGroup().getName() : ""; // название группы
            case 2 -> {
                int totalLectures = s.getGroup() != null ? s.getGroup().getAttendanceRecords().size() : 0;
                int attended = s.getAttendanceCount();
                if (totalLectures == 0) {
                    yield "0 (0%)"; // если лекций нет
                } else {
                    int percent = (int) Math.round((attended * 100.0) / totalLectures);
                    yield attended + " (" + percent + "%)"; // посещения + процент
                }
            }
            default -> null;
        };
    }

    /**
     * Определяет тип данных для всех колонок.
     * <p>Все колонки возвращают строки.</p>
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    /**
     * Получить студента по индексу строки.
     *
     * @param index индекс строки
     * @return объект Student
     */
    public Student getStudent(int index) {
        return students.get(index);
    }

    /**
     * Обновить таблицу (перерисовать данные).
     */
    public void refresh() {
        fireTableDataChanged(); // уведомляем JTable об изменении данных
    }
}
