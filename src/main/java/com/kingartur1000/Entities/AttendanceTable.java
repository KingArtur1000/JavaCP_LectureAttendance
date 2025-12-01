package com.kingartur1000.Entities;

import javax.swing.table.AbstractTableModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Модель таблицы для отображения текущего статуса посещаемости студентов.
 * <p>Первая колонка содержит ФИО студента, вторая — его статус (ABSENT, PRESENT, LATE).</p>
 */
public class AttendanceTable extends AbstractTableModel {
    /**
     * Перечисление возможных статусов посещаемости:
     * ABSENT — отсутствовал,
     * PRESENT — присутствовал,
     * LATE — опоздал.
     */
    public enum AttendanceMark {
        ABSENT, PRESENT, LATE
    }

    /** Список студентов */
    private final List<Student> students;
    /** Заголовки колонок таблицы */
    private final String[] columns = {"ФИО", "Статус"};
    /** Карта: студент -> статус посещаемости */
    private final Map<Student, AttendanceMark> marks;

    /**
     * Конструктор таблицы посещаемости.
     * <p>Инициализирует всех студентов со статусом ABSENT.</p>
     *
     * @param students список студентов
     */
    public AttendanceTable(List<Student> students) {
        this.students = students;
        this.marks = new HashMap<>();
        for (Student s : students) {
            marks.put(s, AttendanceMark.ABSENT); // по умолчанию все отсутствуют
        }
    }

    /*
     * Метод для загрузки данных из записи посещаемости.
     * Алгоритм:
     * 1. Для каждого студента получить статус из AttendanceRecord.
     * 2. Если записи нет, установить статус ABSENT.
     * 3. Обновить таблицу вызовом fireTableDataChanged().
     */
    public void loadFromRecord(AttendanceRecord record) {
        for (Student s : students) {
            AttendanceMark mark = AttendanceMark.ABSENT;
            if (record != null) {
                mark = record.getMark(s); // сохраняем точный статус: PRESENT / LATE / ABSENT
            }
            marks.put(s, mark);
        }
        fireTableDataChanged();
    }

    /**
     * Очистить таблицу: установить всем студентам статус ABSENT.
     */
    public void clear() {
        for (Student s : students) {
            marks.put(s, AttendanceMark.ABSENT);
        }
        fireTableDataChanged();
    }

    /**
     * Получить статус посещаемости студента.
     *
     * @param s студент
     * @return статус посещаемости (по умолчанию ABSENT)
     */
    public AttendanceMark getMark(Student s) {
        return marks.getOrDefault(s, AttendanceMark.ABSENT);
    }

    /**
     * Установить статус посещаемости студенту.
     *
     * @param s студент
     * @param mark статус посещаемости
     */
    public void setMark(Student s, AttendanceMark mark) {
        marks.put(s, mark);
        fireTableDataChanged(); // обновляем таблицу
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

    /** @return количество строк (равно числу студентов) */
    @Override
    public int getRowCount() {
        return students.size();
    }

    /** @return количество колонок (ФИО и статус) */
    @Override
    public int getColumnCount() {
        return columns.length;
    }

    /** @return название колонки по индексу */
    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    /**
     * Определяет тип данных для колонки.
     * <p>Первая колонка — String (ФИО), вторая — AttendanceMark (статус).</p>
     *
     * @param columnIndex индекс колонки
     * @return класс данных для колонки
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 1 ? AttendanceMark.class : String.class;
    }

    /**
     * Возвращает значение ячейки таблицы.
     *
     * @param rowIndex индекс строки (студент)
     * @param columnIndex индекс колонки (ФИО или статус)
     * @return ФИО студента или его статус посещаемости
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Student s = students.get(rowIndex);
        return columnIndex == 0 ? s.getFullName() : getMark(s);
    }

    /**
     * Определяет, можно ли редактировать ячейку.
     * <p>В данной модели все ячейки нередактируемые.</p>
     *
     * @return false всегда
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
