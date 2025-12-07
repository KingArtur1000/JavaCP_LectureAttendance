package com.kingartur1000.Data;

import com.kingartur1000.Entities.AttendanceRecord;
import com.kingartur1000.Entities.Group;
import com.kingartur1000.Entities.Student;
import com.kingartur1000.Entities.AttendanceTable.AttendanceMark;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Менеджер для сохранения и загрузки данных в/из Excel файла.
 * Использует Apache POI для работы с .xlsx файлами.
 * @author Артур И Роман
 * @version 1.9
 */
public class DataManager {
    private static final String FILE_NAME = "attendance_data.xlsx"; /** Имя файла для хранения данных */

    private static final String SHEET_GROUPS = "Группы"; /** Название листа с группами */

    private static final String SHEET_STUDENTS = "Студенты";  /** Название листа со студентами */

    private static final String SHEET_ATTENDANCE = "Посещаемость"; /** Название листа с посещаемостью */

    /**
     * Сохраняет данные о группах и студентах в Excel файл.
     *
     * @param groups список групп для сохранения
     * @throws IOException если произошла ошибка записи файла
     */
    public static void saveData(List<Group> groups) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);

        // Сохраняем три листа: группы, студенты, посещаемость
        saveGroups(workbook, groups, headerStyle);
        saveStudents(workbook, groups, headerStyle);
        saveAttendance(workbook, groups, headerStyle, dateStyle);

        try (FileOutputStream fileOut = new FileOutputStream(FILE_NAME)) {
            workbook.write(fileOut);
        }

        workbook.close();
    }

    /**
     * Загружает данные из Excel файла.
     *
     * @return список групп с данными студентов и посещаемости
     * @throws IOException если произошла ошибка чтения файла
     */
    public static List<Group> loadData() throws IOException {
        try (FileInputStream fileIn = new FileInputStream(FILE_NAME)) {
            Workbook workbook = new XSSFWorkbook(fileIn);

            Map<Integer, Group> groupMap = loadGroups(workbook);
            loadStudents(workbook, groupMap);
            loadAttendance(workbook, groupMap);

            workbook.close();
            return new ArrayList<>(groupMap.values());
        }
    }

    /*
     * Метод для сохранения информации о группах.
     * Алгоритм:
     * 1. Создать заголовки.
     * 2. Записать каждую группу в строку.
     * 3. Автоматически подогнать ширину колонок.
     */
    private static void saveGroups(Workbook workbook, List<Group> groups, CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet(SHEET_GROUPS);

        Row headerRow = sheet.createRow(0);
        createCell(headerRow, 0, "ID", headerStyle);
        createCell(headerRow, 1, "Название группы", headerStyle);
        createCell(headerRow, 2, "Количество студентов", headerStyle);

        int rowNum = 1;
        for (int i = 0; i < groups.size(); i++) {
            Group group = groups.get(i);
            Row row = sheet.createRow(rowNum++);

            // Записываем ID, название и количество студентов
            row.createCell(0).setCellValue(i);
            row.createCell(1).setCellValue(group.getName());
            row.createCell(2).setCellValue(group.getStudents().size());
        }

        // Автоматическая подгонка ширины колонок
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /*
     * Метод для сохранения студентов.
     * Алгоритм:
     * 1. Создать заголовки столбцов (ID студента, ФИО, ID группы, название группы, количество посещений).
     * 2. Для каждой группы пройтись по списку студентов.
     * 3. Для каждого студента записать его данные в новую строку:
     *    - уникальный ID студента,
     *    - полное имя,
     *    - ID группы,
     *    - название группы,
     *    - количество посещений.
     * 4. После заполнения таблицы автоматически подогнать ширину всех колонок.
     */
    private static void saveStudents(Workbook workbook, List<Group> groups, CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet(SHEET_STUDENTS);

        Row headerRow = sheet.createRow(0);
        createCell(headerRow, 0, "ID студента", headerStyle);
        createCell(headerRow, 1, "ФИО", headerStyle);
        createCell(headerRow, 2, "ID группы", headerStyle);
        createCell(headerRow, 3, "Название группы", headerStyle);
        createCell(headerRow, 4, "Посещений", headerStyle);

        int rowNum = 1;
        int studentId = 0;

        for (int groupId = 0; groupId < groups.size(); groupId++) {
            Group group = groups.get(groupId);
            for (Student student : group.getStudents()) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(studentId++);
                row.createCell(1).setCellValue(student.getFullName());
                row.createCell(2).setCellValue(groupId);
                row.createCell(3).setCellValue(group.getName());
                row.createCell(4).setCellValue(student.getAttendanceCount());
            }
        }
        // Автоматическая подгонка ширины колонок
        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }
    }


    /*
     * Метод для сохранения посещаемости.
     * Алгоритм:
     * 1. Для каждой группы пройтись по записям посещаемости.
     * 2. Для каждой записи сохранить дату, студента и статус.
     * 3. Автоматически подогнать ширину колонок.
     */
    private static void saveAttendance(Workbook workbook, List<Group> groups,
                                       CellStyle headerStyle, CellStyle dateStyle) {
        Sheet sheet = workbook.createSheet(SHEET_ATTENDANCE);

        Row headerRow = sheet.createRow(0);
        createCell(headerRow, 0, "ID группы", headerStyle);
        createCell(headerRow, 1, "Название группы", headerStyle);
        createCell(headerRow, 2, "Дата", headerStyle);
        createCell(headerRow, 3, "ФИО студента", headerStyle);
        createCell(headerRow, 4, "Присутствовал", headerStyle);

        int rowNum = 1;

        for (int groupId = 0; groupId < groups.size(); groupId++) {
            Group group = groups.get(groupId);

            for (AttendanceRecord record : group.getAttendanceRecords()) {
                for (Map.Entry<Student, AttendanceMark> entry : record.getMarks().entrySet()) {
                    Row row = sheet.createRow(rowNum++);

                    row.createCell(0).setCellValue(groupId);
                    row.createCell(1).setCellValue(group.getName());

                    Cell dateCell = row.createCell(2);
                    dateCell.setCellValue(Date.from(record.getDate()
                            .atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    dateCell.setCellStyle(dateStyle);

                    row.createCell(3).setCellValue(entry.getKey().getFullName());

                    String statusText = switch (entry.getValue()) {
                        case PRESENT -> "Да";
                        case LATE -> "Опоздал";
                        case ABSENT -> "Нет";
                    };
                    row.createCell(4).setCellValue(statusText);
                }
            }
        }

        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /*
     * Метод для загрузки информации о группах.
     * Алгоритм:
     * 1. Получить лист "Группы" из Excel.
     * 2. Пройтись по строкам начиная со второй (первая — заголовки).
     * 3. Считать ID группы и её название.
     * 4. Создать объект Group и положить его в карту groupMap.
     * 5. Вернуть карту всех загруженных групп.
     */
    private static Map<Integer, Group> loadGroups(Workbook workbook) {
        Map<Integer, Group> groupMap = new HashMap<>();
        Sheet sheet = workbook.getSheet(SHEET_GROUPS);

        if (sheet == null) return groupMap;

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            int groupId = (int) row.getCell(0).getNumericCellValue();
            String groupName = row.getCell(1).getStringCellValue();

            // Добавляем группу в карту по её ID
            groupMap.put(groupId, new Group(groupName));
        }

        return groupMap;
    }

    /*
     * Метод для загрузки студентов.
     * Алгоритм:
     * 1. Получить лист "Студенты".
     * 2. Пройтись по строкам начиная со второй.
     * 3. Считать ID группы, ФИО студента и количество посещений.
     * 4. Найти соответствующую группу в карте.
     * 5. Создать объект Student, установить количество посещений и добавить в группу.
     */
    private static void loadStudents(Workbook workbook, Map<Integer, Group> groupMap) {
        Sheet sheet = workbook.getSheet(SHEET_STUDENTS);

        if (sheet == null) return;

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            int groupId = (int) row.getCell(2).getNumericCellValue();
            String fullName = row.getCell(1).getStringCellValue();
            int attendanceCount = (int) row.getCell(4).getNumericCellValue();

            Group group = groupMap.get(groupId);
            if (group != null) {
                // Создаём студента и добавляем его в группу
                Student student = new Student(fullName, group);
                student.setAttendanceCount(attendanceCount);
                group.addStudent(student);
            }
        }
    }

    /*
     * Метод для загрузки посещаемости.
     * Алгоритм:
     * 1. Получить лист "Посещаемость".
     * 2. Пройтись по строкам начиная со второй.
     * 3. Считать ID группы, дату, имя студента и статус посещения.
     * 4. Определить AttendanceMark (Да, Опоздал, Нет).
     * 5. Найти группу и студента.
     * 6. Для каждой даты создать или получить AttendanceRecord.
     * 7. Добавить отметку посещаемости для студента.
     */
    private static void loadAttendance(Workbook workbook, Map<Integer, Group> groupMap) {
        Sheet sheet = workbook.getSheet(SHEET_ATTENDANCE);

        if (sheet == null) return;

        Map<Group, Map<LocalDate, AttendanceRecord>> recordsMap = new HashMap<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            int groupId = (int) row.getCell(0).getNumericCellValue();
            Date date = row.getCell(2).getDateCellValue();
            LocalDate localDate = date.toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            String studentName = row.getCell(3).getStringCellValue();
            String statusText = row.getCell(4).getStringCellValue();

            // Определяем статус посещаемости
            AttendanceMark mark = switch (statusText) {
                case "Да" -> AttendanceMark.PRESENT;
                case "Опоздал" -> AttendanceMark.LATE;
                default -> AttendanceMark.ABSENT;
            };

            Group group = groupMap.get(groupId);
            if (group == null) continue;

            // Ищем студента по ФИО
            Student student = group.getStudents().stream()
                    .filter(s -> s.getFullName().equals(studentName))
                    .findFirst()
                    .orElse(null);

            if (student == null) continue;

            recordsMap.putIfAbsent(group, new HashMap<>());
            Map<LocalDate, AttendanceRecord> dateRecords = recordsMap.get(group);

            AttendanceRecord record = dateRecords.get(localDate);
            if (record == null) {
                // Создаём новую запись посещаемости для даты
                record = new AttendanceRecord(localDate);
                dateRecords.put(localDate, record);
                group.addAttendanceRecord(record);
            }

            // сохраняем статус (три состояния: присутствовал, опоздал, отсутствовал)
            record.getMarks().put(student, mark);
        }
    }

// ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================

    /*
     * Метод createCell.
     * Назначение:
     * 1. Создать ячейку в указанной строке и колонке.
     * 2. Установить текстовое значение.
     * 3. Применить переданный стиль (например, заголовок).
     */
    private static void createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    /*
     * Метод createHeaderStyle.
     * Назначение:
     * 1. Создать стиль для заголовков таблицы.
     * 2. Сделать шрифт жирным, задать размер.
     * 3. Установить серый фон.
     * 4. Добавить рамки вокруг ячеек.
     * Используется для оформления первой строки (заголовков).
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    /*
     * Метод createDateStyle.
     * Назначение:
     * 1. Создать стиль для отображения дат.
     * 2. Установить формат "dd.MM.yyyy".
     * Используется для ячеек с датами посещаемости.
     */
    private static CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("dd.MM.yyyy"));
        return style;
    }

    /**
     * Проверяет, существует ли файл с данными.
     *
     * @return true если файл существует, иначе false
     */
    public static boolean dataFileExists() {
        // Проверяем наличие файла по имени FILE_NAME
        return new java.io.File(FILE_NAME).exists();
    }
}

