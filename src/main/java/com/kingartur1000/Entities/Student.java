package com.kingartur1000.Entities;

/**
 * Класс Student представляет студента учебной группы.
 * <p>Содержит ФИО, ссылку на группу и количество посещений.</p>
 * @author Артур
 * @version 1.9
 */
public class Student {
    /** Полное имя студента */
    private String fullName;
    /** Группа, к которой относится студент */
    private Group group;
    /** Количество посещений (счётчик) */
    private int attendanceCount;

    /**
     * Конструктор студента.
     *
     * @param fullName полное имя студента
     * @param group группа, к которой относится студент
     */
    public Student(String fullName, Group group) {
        this.fullName = fullName;
        this.group = group;
        this.attendanceCount = 0; // изначально посещений нет
    }

    /** @return полное имя студента */
    public String getFullName() {
        return fullName;
    }

    /** Установить новое имя студента */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /** @return группа студента */
    public Group getGroup() {
        return group;
    }

    /**
     * Получить название группы студента.
     *
     * @return название группы или пустая строка, если группа не установлена
     */
    public String getGroupName() {
        return group != null ? group.getName() : "";
    }

    /** Установить группу для студента */
    public void setGroup(Group group) {
        this.group = group;
    }

    /** @return количество посещений студента */
    public int getAttendanceCount() {
        return attendanceCount;
    }

    /*
     * Метод incrementAttendance.
     * Алгоритм:
     * 1. Увеличить счётчик посещений на 1.
     * Используется при отметке студента как PRESENT или LATE.
     */
    public void incrementAttendance() {
        attendanceCount++;
    }

    /**
     * Установить количество посещений вручную.
     *
     * @param count новое значение счётчика посещений
     */
    public void setAttendanceCount(int count) {
        this.attendanceCount = count;
    }
}
