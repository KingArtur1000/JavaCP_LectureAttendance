package com.kingartur1000.Data;

import com.kingartur1000.Entities.AttendanceRecord;
import com.kingartur1000.Entities.Group;
import com.kingartur1000.Entities.Student;
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
 */
public class DataManager {
    private static final String FILE_NAME = "attendance_data.xlsx";

    // Названия листов
    private static final String SHEET_GROUPS = "Группы";
    private static final String SHEET_STUDENTS = "Студенты";
    private static final String SHEET_ATTENDANCE = "Посещаемость";

    /**
     * Сохраняет все данные (группы, студентов и посещаемость) в Excel файл.
     *
     * @param groups список групп для сохранения
     * @throws IOException если произошла ошибка записи файла
     */
    public static void saveData(List<Group> groups) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);

        saveGroups(workbook, groups, headerStyle);
        saveStudents(workbook, groups, headerStyle);
        saveAttendance(workbook, groups, headerStyle, dateStyle);

        try (FileOutputStream fileOut = new FileOutputStream(FILE_NAME)) {
            workbook.write(fileOut);
        }

        workbook.close();
    }

    /**
     * Загружает все данные (группы, студентов и посещаемость) из Excel файла.
     *
     * @return список загруженных групп
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

    // ==================== СОХРАНЕНИЕ ====================

    /**
     * Сохраняет список групп в отдельный лист Excel.
     *
     * @param workbook    рабочая книга Excel
     * @param groups      список групп
     * @param headerStyle стиль для заголовков
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

            row.createCell(0).setCellValue(i);
            row.createCell(1).setCellValue(group.getName());
            row.createCell(2).setCellValue(group.getStudents().size());
        }

        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Сохраняет список студентов в отдельный лист Excel.
     *
     * @param workbook    рабочая книга Excel
     * @param groups      список групп
     * @param headerStyle стиль для заголовков
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

        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Сохраняет записи посещаемости в отдельный лист Excel.
     *
     * @param workbook    рабочая книга Excel
     * @param groups      список групп
     * @param headerStyle стиль для заголовков
     * @param dateStyle   стиль для отображения дат
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
                for (Map.Entry<Student, Boolean> entry : record.getMarks().entrySet()) {
                    Row row = sheet.createRow(rowNum++);

                    row.createCell(0).setCellValue(groupId);
                    row.createCell(1).setCellValue(group.getName());

                    Cell dateCell = row.createCell(2);
                    dateCell.setCellValue(Date.from(record.getDate()
                            .atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    dateCell.setCellStyle(dateStyle);

                    row.createCell(3).setCellValue(entry.getKey().getFullName());
                    row.createCell(4).setCellValue(entry.getValue() ? "Да" : "Нет");
                }
            }
        }

        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // ==================== ЗАГРУЗКА ====================

    /**
     * Загружает список групп из Excel.
     *
     * @param workbook рабочая книга Excel
     * @return карта групп по их ID
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

            groupMap.put(groupId, new Group(groupName));
        }

        return groupMap;
    }

    /**
     * Загружает студентов из Excel и добавляет их в соответствующие группы.
     *
     * @param workbook рабочая книга Excel
     * @param groupMap карта групп
     */
    private static void loadStudents(Workbook workbook, Map<Integer, Group> groupMap) {
        Sheet sheet = workbook.getSheet(SHEET_STUDENTS);

        if (sheet == null) return;

        Map<Integer, Student> studentMap = new HashMap<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            int studentId = (int) row.getCell(0).getNumericCellValue();
            String fullName = row.getCell(1).getStringCellValue();
            int groupId = (int) row.getCell(2).getNumericCellValue();
            int attendanceCount = (int) row.getCell(4).getNumericCellValue();

            Group group = groupMap.get(groupId);
            if (group != null) {
                Student student = new Student(fullName, group);
                student.setAttendanceCount(attendanceCount);
                group.addStudent(student);
                studentMap.put(studentId, student);
            }
        }
    }

    /**
     * Загружает записи посещаемости из Excel и добавляет их в соответствующие группы.
     *
     * @param workbook рабочая книга Excel
     * @param groupMap карта групп
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
            boolean present = row.getCell(4).getStringCellValue().equals("Да");

            Group group = groupMap.get(groupId);
            if (group == null) continue;

            Student student = group.getStudents().stream()
                    .filter(s -> s.getFullName().equals(studentName))
                    .findFirst()
                    .orElse(null);

            if (student == null) continue;

            recordsMap.putIfAbsent(group, new HashMap<>());
            Map<LocalDate, AttendanceRecord> dateRecords = recordsMap.get(group);

            AttendanceRecord record = dateRecords.get(localDate);
            if (record == null) {
                record = new AttendanceRecord(localDate);
                dateRecords.put(localDate, record);
                group.addAttendanceRecord(record);
            }

            record.getMarks().put(student, present);
        }
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================

    /**
     * Создаёт ячейку с заданным текстом и стилем.
     *
     * @param row    строка, в которую добавляется ячейка
     * @param column индекс колонки
     * @param value  текстовое значение
     * @param style  стиль ячейки
     */
    private static void createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    /**
     * Создаёт стиль для заголовков таблиц (жирный шрифт, серый фон, рамки).
     *
     * @param workbook рабочая книга Excel
     * @return стиль для заголовков
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

    /**
     * Создаёт стиль для отображения дат в формате dd.MM.yyyy.
     *
     * @param workbook рабочая книга Excel
     * @return стиль для ячеек с датами
     */
    private static CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("dd.MM.yyyy"));
        return style;
    }

    /**
     * Проверяет существование файла с данными.
     *
     * @return true, если файл существует, иначе false
     */
    public static boolean dataFileExists() {
        return new java.io.File(FILE_NAME).exists();
    }
}

