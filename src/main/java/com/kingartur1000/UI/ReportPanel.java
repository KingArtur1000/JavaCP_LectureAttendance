package com.kingartur1000.UI;

import com.kingartur1000.Entities.*;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import static com.kingartur1000.MainWindow.globalFont;

public class ReportPanel extends GridPanel {
    private JTable fixedTable;
    private JTable mainTable;
    private FixedColumnModel fixedModel;
    private AttendanceDatesModel datesModel;
    private JLabel groupLabel = new JLabel("Выбранная группа: —");
    private JButton exportButton = new JButton("Экспорт");

    public ReportPanel(List<Group> groups) {
        super(3, 3);

        fixedModel = new FixedColumnModel();
        datesModel = new AttendanceDatesModel();

        // Таблица ФИО (фиксированная)
        fixedTable = new JTable(fixedModel);
        fixedTable.setRowHeight(30);
        fixedTable.setFont(globalFont);
        fixedTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        fixedTable.getTableHeader().setFont(globalFont);
        TableColumn col = fixedTable.getColumnModel().getColumn(0);
        col.setPreferredWidth(180); // компактнее

        // Таблица дат
        mainTable = new JTable(datesModel);
        mainTable.setRowHeight(30);
        mainTable.setFont(globalFont);
        mainTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        mainTable.getTableHeader().setFont(globalFont);

        // Скроллы
        JScrollPane scrollFixed = new JScrollPane(fixedTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollFixed.setPreferredSize(new Dimension(180, scrollFixed.getPreferredSize().height));

        JScrollPane scrollMain = new JScrollPane(mainTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // синхронизация вертикального скролла
        scrollFixed.getVerticalScrollBar().setModel(scrollMain.getVerticalScrollBar().getModel());

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(scrollFixed, BorderLayout.WEST);
        tablePanel.add(scrollMain, BorderLayout.CENTER);

        // Верхняя панель
        groupLabel.setFont(globalFont);
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(groupLabel);

        // Кнопка экспорта
        exportButton.setFont(globalFont);
        exportButton.setBackground(new Color(95, 212, 124));
        exportButton.setForeground(Color.WHITE);
        exportButton.addActionListener(this::onExport);

        // Сборка интерфейса
        addToGrid(topPanel, 0, 0, 1, 3);
        addToGrid(tablePanel, 1, 0, 1, 3);
        addToGrid(exportButton, 2, 2);
    }

    public void setGroup(Group group) {
        groupLabel.setText("Выбранная группа: " + group.getName());

        // Дефолтная сортировка по ФИО на уровне данных (затрагивает обе таблицы)
        group.getStudents().sort(Comparator.comparing(Student::getFullName, String.CASE_INSENSITIVE_ORDER));

        fixedModel.setGroup(group);
        datesModel.setGroup(group);

        // Ширина колонок дат — после загрузки модели
        if (mainTable.getColumnModel().getColumnCount() > 0) {
            for (int i = 0; i < mainTable.getColumnModel().getColumnCount(); i++) {
                mainTable.getColumnModel().getColumn(i).setPreferredWidth(150);
            }
        }
    }

    private void onExport(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Сохранить отчёт");
        chooser.setSelectedFile(new File("attendance_report.xlsx"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Отчёт");

                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("ФИО");
                for (int i = 0; i < datesModel.getColumnCount(); i++) {
                    header.createCell(i + 1).setCellValue(datesModel.getColumnName(i));
                }

                for (int row = 0; row < fixedModel.getRowCount(); row++) {
                    Row excelRow = sheet.createRow(row + 1);
                    excelRow.createCell(0).setCellValue(fixedModel.getValueAt(row, 0).toString());
                    for (int col = 0; col < datesModel.getColumnCount(); col++) {
                        Object value = datesModel.getValueAt(row, col);
                        excelRow.createCell(col + 1).setCellValue(value != null ? value.toString() : "");
                    }
                }

                for (int i = 0; i <= datesModel.getColumnCount(); i++) {
                    sheet.autoSizeColumn(i);
                }

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
