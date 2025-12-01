package com.kingartur1000.Entities;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Модель таблицы для отображения посещаемости студентов по датам.
 * <p>Используется в Swing JTable для рендеринга данных.</p>
 */
public class AttendanceDatesModel extends AbstractTableModel {
    private List<Student> students = new ArrayList<>(); /** Список студентов выбранной группы */
    private List<LocalDate> dates = new ArrayList<>(); /** Список дат посещаемости */
    private Map<LocalDate, AttendanceRecord> records = new HashMap<>(); /** Карта: дата -> запись посещаемости */
    /** Форматтер для отображения дат в заголовках таблицы */
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");

    /**
     * Устанавливает группу для модели.
     * <p>Загружает студентов и записи посещаемости, сортирует даты и обновляет структуру таблицы.</p>
     *
     * @param group выбранная группа
     */
    public void setGroup(Group group) {
        this.students = group.getStudents();
        this.records.clear();
        this.dates.clear();

        /*
         * Алгоритм:
         * 1. Очистить текущие списки студентов, дат и записей.
         * 2. Пройтись по всем записям посещаемости группы.
         * 3. Добавить дату в список и сохранить запись в карту.
         * 4. Отсортировать даты по возрастанию.
         * 5. Уведомить таблицу о смене структуры.
         */
        for (AttendanceRecord record : group.getAttendanceRecords()) {
            LocalDate date = record.getDate();
            dates.add(date);
            records.put(date, record);
        }

        dates.sort(LocalDate::compareTo);
        fireTableStructureChanged(); // обновляем структуру JTable
    }

    /** @return количество строк (равно числу студентов) */
    @Override
    public int getRowCount() {
        return students.size();
    }
    /** @return количество колонок (равно числу дат) */
    @Override
    public int getColumnCount() {
        return dates.size();
    }
    /**
     * Возвращает название колонки (отформатированная дата).
     *
     * @param column индекс колонки
     * @return строка с датой в формате dd-MM-yy
     */
    @Override
    public String getColumnName(int column) {
        return formatter.format(dates.get(column));
    }
    /**
     * Возвращает значение ячейки таблицы.
     *
     * @param rowIndex индекс строки (студент)
     * @param columnIndex индекс колонки (дата)
     * @return статус посещаемости (AttendanceMark) или пустая строка
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Student s = students.get(rowIndex);
        LocalDate date = dates.get(columnIndex);
        AttendanceRecord record = records.get(date);

        if (record == null) return "";

        AttendanceTable.AttendanceMark mark = record.getMark(s);
        return mark; // возвращаем сам статус, а не текст
    }

    // Вспомогательные методы для рендерера JTable

    /**
     * Получает запись посещаемости по индексу колонки.
     *
     * @param columnIndex индекс колонки (дата)
     * @return объект AttendanceRecord или null
     */
    public AttendanceRecord getRecordAt(int columnIndex) {
        LocalDate date = dates.get(columnIndex);
        return records.get(date);
    }
    /**
     * Получает студента по индексу строки.
     *
     * @param rowIndex индекс строки
     * @return объект Student
     */
    public Student getStudentAt(int rowIndex) {
        return students.get(rowIndex);
    }
}
