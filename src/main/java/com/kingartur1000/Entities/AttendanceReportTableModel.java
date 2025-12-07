package com.kingartur1000.Entities;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.*;

/**
 * Модель таблицы для отображения отчёта по посещаемости студентов.
 * <p>Первая колонка содержит ФИО студента, остальные — даты посещаемости.</p>
 * @author Артур
 * @version 1.9
 */
public class AttendanceReportTableModel extends AbstractTableModel {
    /** Список студентов выбранной группы */
    private List<Student> students = new ArrayList<>();
    /** Список дат посещаемости */
    private List<LocalDate> dates = new ArrayList<>();
    /** Карта: дата -> запись посещаемости */
    private Map<LocalDate, AttendanceRecord> records = new HashMap<>();

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

    /** @return количество колонок (ФИО + количество дат) */
    @Override
    public int getColumnCount() {
        return 1 + dates.size(); // ФИО + даты
    }

    /**
     * Возвращает название колонки.
     * <p>Первая колонка — "ФИО", остальные — даты.</p>
     *
     * @param column индекс колонки
     * @return название колонки
     */
    @Override
    public String getColumnName(int column) {
        return column == 0 ? "ФИО" : dates.get(column - 1).toString();
    }

    /**
     * Возвращает значение ячейки таблицы.
     *
     * @param rowIndex индекс строки (студент)
     * @param columnIndex индекс колонки (ФИО или дата)
     * @return ФИО студента или статус посещаемости
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Student s = students.get(rowIndex);
        if (columnIndex == 0) return s.getFullName(); // первая колонка — ФИО

        LocalDate date = dates.get(columnIndex - 1);
        AttendanceRecord record = records.get(date);
        if (record == null) return "";

        // Возвращаем сам статус, чтобы рендерер мог покрасить точку
        return record.getMark(s);
    }

    // Вспомогательные методы для рендерера JTable

    /**
     * Получает запись посещаемости по индексу колонки.
     *
     * @param columnIndex индекс колонки (дата)
     * @return объект AttendanceRecord или null
     */
    public AttendanceRecord getRecordAt(int columnIndex) {
        LocalDate date = dates.get(columnIndex - 1);
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
