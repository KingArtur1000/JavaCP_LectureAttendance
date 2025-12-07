package com.kingartur1000.UI;

import com.kingartur1000.Entities.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import static com.kingartur1000.MainWindow.globalFont;

/**
 * Панель для отображения отчёта по посещаемости.
 * <p>Содержит таблицу студентов, таблицу дат, сортировку и экспорт в Excel.</p>
 * @author Роман
 * @version 1.9
 */
public class ReportPanel extends GridPanel {
    /** Таблица с ФИО студентов */
    private JTable fixedTable;
    /** Таблица с посещаемостью по датам */
    private JTable mainTable;
    /** Модель ФИО */
    private FixedColumnModel fixedModel;
    /** Модель дат и отметок */
    private AttendanceDatesModel datesModel;
    /** Метка с названием выбранной группы */
    private JLabel groupLabel = new JLabel("Выбранная группа: —");
    /** Кнопка экспорта в Excel */
    private JButton exportButton = new JButton("Экспорт");
    /** Выпадающий список сортировки */
    private JComboBox<String> sortBox = new JComboBox<>(new String[] {
            "По ФИО", "По количеству посещений"
    });
    /** Текущая выбранная группа */
    private Group currentGroup;

    /**
     * Конструктор панели отчёта.
     * <p>Создаёт таблицы, сортировку, кнопку экспорта и размещает всё в сетке.</p>
     */
    public ReportPanel(List<Group> groups) {
        super(3, 3); // сетка 3 на 3

        // Инициализация моделей
        fixedModel = new FixedColumnModel();
        datesModel = new AttendanceDatesModel();

        // Таблица ФИО
        fixedTable = new JTable(fixedModel);
        fixedTable.setRowHeight(30);
        fixedTable.setFont(globalFont);
        fixedTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        fixedTable.getTableHeader().setFont(globalFont);
        fixedTable.getColumnModel().getColumn(0).setPreferredWidth(250);

        // Таблица дат
        mainTable = new JTable(datesModel);
        mainTable.setRowHeight(30);
        mainTable.setFont(globalFont);
        mainTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        mainTable.getTableHeader().setFont(globalFont);

        // Обёртки таблиц
        JScrollPane scrollFixed = new JScrollPane(fixedTable,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollFixed.setPreferredSize(new Dimension(250, scrollFixed.getPreferredSize().height));

        JScrollPane scrollMain = new JScrollPane(mainTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Синхронизация вертикальной прокрутки
        scrollFixed.getVerticalScrollBar().setModel(scrollMain.getVerticalScrollBar().getModel());

        // Панель с таблицами
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(scrollFixed, BorderLayout.WEST);
        tablePanel.add(scrollMain, BorderLayout.CENTER);

        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Верхняя панель с группой и сортировкой
        groupLabel.setFont(globalFont);
        sortBox.setFont(globalFont);
        sortBox.addActionListener(e -> applySorting());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(groupLabel);
        JLabel sortLabel = new JLabel("Сортировка:");
        sortLabel.setFont(new Font(globalFont.getFontName(), Font.BOLD, globalFont.getSize()));
        topPanel.add(sortLabel);
        topPanel.add(sortBox);

        // Кнопка экспорта
        exportButton.setFont(globalFont);
        exportButton.setBackground(new Color(95, 212, 124));
        exportButton.setForeground(Color.WHITE);
        exportButton.addActionListener(this::onExport);

        // Размещение в сетке
        addToGrid(topPanel, 0, 0, 1, 3);
        addToGrid(tablePanel, 1, 0, 1, 3);
        addToGrid(new JPanel(), 2,0,1,1, 2, 1); // отступ слева
        addToGrid(exportButton, 2, 1, 1, 1, 1, 1);
        addToGrid(new JPanel(), 2,2,1,1, 2,1); // отступ справа
    }

    /**
     * Установить текущую группу и применить сортировку.
     * @param group выбранная группа
     */
    public void setGroup(Group group) {
        this.currentGroup = group;
        groupLabel.setText("Выбранная группа: " + group.getName() + "                  ");
        applySorting();
    }

    /**
     * Применить сортировку к списку студентов и обновить таблицы.
     * <p>Сортировка по ФИО или по количеству посещений.</p>
     */
    private void applySorting() {
        if (currentGroup == null) return;

        List<Student> students = currentGroup.getStudents();

        switch (sortBox.getSelectedIndex()) {
            case 0 -> students.sort(Comparator.comparing(Student::getFullName, String.CASE_INSENSITIVE_ORDER));
            case 1 -> students.sort(Comparator.comparingInt(s -> {
                int count = 0;
                for (AttendanceRecord r : currentGroup.getAttendanceRecords()) {
                    if (r.isPresent(s)) count++;
                }
                return -count; // по убыванию
            }));
        }

        // Обновляем модели
        fixedModel.setGroup(currentGroup);
        datesModel.setGroup(currentGroup);

        // Автоматическая ширина столбца ФИО
        int maxWidth = 0;
        FontMetrics fm = fixedTable.getFontMetrics(globalFont);
        for (int i = 0; i < fixedModel.getRowCount(); i++) {
            String name = fixedModel.getValueAt(i, 0).toString();
            int width = fm.stringWidth(name);
            if (width > maxWidth) maxWidth = width;
        }
        int finalWidth = Math.max(maxWidth + 40, 250); // минимум 250, чтобы не было слишком узко
        fixedTable.getColumnModel().getColumn(0).setPreferredWidth(finalWidth);

        // Обновление ширины scrollFixed
        JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, fixedTable);
        if (scrollPane != null) {
            scrollPane.setPreferredSize(new Dimension(finalWidth, scrollPane.getPreferredSize().height));
            scrollPane.revalidate();
        }

        // Устанавливаем ширину и рендеринг для столбцов с датами
        if (mainTable.getColumnModel().getColumnCount() > 0) {
            for (int i = 0; i < mainTable.getColumnCount(); i++) {
                mainTable.getColumnModel().getColumn(i).setPreferredWidth(90);

                mainTable.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                    @Override
                    public void setValue(Object value) {
                        if (value instanceof AttendanceTable.AttendanceMark mark) {
                            switch (mark) {
                                case ABSENT -> {
                                    setText("•");
                                    setForeground(Color.BLACK);
                                }
                                case LATE -> {
                                    setText("•");
                                    setForeground(Color.RED);
                                }
                                case PRESENT -> {
                                    setText("");
                                    setForeground(Color.BLACK);
                                }
                            }
                        } else {
                            setText("");
                            setForeground(Color.BLACK);
                        }
                        setHorizontalAlignment(SwingConstants.CENTER);
                    }
                });
            }
        }
    }

    /**
     * Экспорт отчёта в Excel.
     * <p>Создаёт файл .xlsx с таблицей посещаемости и сохраняет его на диск.</p>
     */
    private void onExport(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Сохранить отчёт");
        chooser.setSelectedFile(new File("attendance_report.xlsx"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Отчёт");

                // Заголовок
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("ФИО");
                for (int i = 0; i < datesModel.getColumnCount(); i++) {
                    header.createCell(i + 1).setCellValue(datesModel.getColumnName(i));
                }

                // Данные
                for (int row = 0; row < fixedModel.getRowCount(); row++) {
                    Row excelRow = sheet.createRow(row + 1);
                    excelRow.createCell(0).setCellValue(fixedModel.getValueAt(row, 0).toString());
                    for (int col = 0; col < datesModel.getColumnCount(); col++) {
                        Object value = datesModel.getValueAt(row, col);
                        if (value instanceof AttendanceTable.AttendanceMark mark) {
                            String text = switch (mark) {
                                case ABSENT -> "Отсутствовал";
                                case LATE -> "Опоздал"; // в Excel просто точка, цвет не сохраняем
                                case PRESENT -> "";
                            };
                            excelRow.createCell(col + 1).setCellValue(text);
                        } else {
                            excelRow.createCell(col + 1).setCellValue("");
                        }
                    }
                }

                // Автоматическая подгонка ширины столбцов
                for (int i = 0; i <= datesModel.getColumnCount(); i++) {
                    sheet.autoSizeColumn(i);
                }

                // Запись файла на диск
                try (FileOutputStream out = new FileOutputStream(file)) {
                    workbook.write(out);
                }

                JOptionPane.showMessageDialog(this, "Отчёт сохранён: " + file.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage());
            }
        }
    }
}
