package com.kingartur1000.UI;

import com.kingartur1000.Entities.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
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

public class ReportPanel extends GridPanel {
    private JTable fixedTable;
    private JTable mainTable;
    private FixedColumnModel fixedModel;
    private AttendanceDatesModel datesModel;
    private JLabel groupLabel = new JLabel("Выбранная группа: —");
    private JButton exportButton = new JButton("Экспорт");
    private JComboBox<String> sortBox = new JComboBox<>(new String[] {
            "По ФИО", "По количеству посещений"
    });
    private Group currentGroup;

    public ReportPanel(List<Group> groups) {
        super(3, 3);

        fixedModel = new FixedColumnModel();
        datesModel = new AttendanceDatesModel();

        fixedTable = new JTable(fixedModel);
        fixedTable.setRowHeight(30);
        fixedTable.setFont(globalFont);
        fixedTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        fixedTable.getTableHeader().setFont(globalFont);
        fixedTable.getColumnModel().getColumn(0).setPreferredWidth(250);

        mainTable = new JTable(datesModel);
        mainTable.setRowHeight(30);
        mainTable.setFont(globalFont);
        mainTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        mainTable.getTableHeader().setFont(globalFont);

        JScrollPane scrollFixed = new JScrollPane(fixedTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollFixed.setPreferredSize(new Dimension(250, scrollFixed.getPreferredSize().height));

        JScrollPane scrollMain = new JScrollPane(mainTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        scrollFixed.getVerticalScrollBar().setModel(scrollMain.getVerticalScrollBar().getModel());

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(scrollFixed, BorderLayout.WEST);
        tablePanel.add(scrollMain, BorderLayout.CENTER);

        // поля вокруг таблицы
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        groupLabel.setFont(globalFont);
        sortBox.setFont(globalFont);
        sortBox.addActionListener(e -> applySorting());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(groupLabel);
        JLabel sortLabel = new JLabel("Сортировка:");
        sortLabel.setFont(new Font(globalFont.getFontName(), Font.BOLD, globalFont.getSize()));
        topPanel.add(sortLabel);
        topPanel.add(sortBox);

        exportButton.setFont(globalFont);
        exportButton.setBackground(new Color(95, 212, 124));
        exportButton.setForeground(Color.WHITE);
        exportButton.addActionListener(this::onExport);

        addToGrid(topPanel, 0, 0, 1, 3);
        addToGrid(tablePanel, 1, 0, 1, 3);
        addToGrid(new JPanel(), 2,0,1,1, 2, 1);
        addToGrid(exportButton, 2, 1, 1, 1, 1, 1);
        addToGrid(new JPanel(), 2,2,1,1, 2,1);

    }

    public void setGroup(Group group) {
        this.currentGroup = group;
        groupLabel.setText("Выбранная группа: " + group.getName() + "                  ");
        applySorting();
    }

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
                return -count;
            }));
        }

        fixedModel.setGroup(currentGroup);
        fixedTable.getColumnModel().getColumn(0).setPreferredWidth(248);
        datesModel.setGroup(currentGroup);

        if (mainTable.getColumnModel().getColumnCount() > 0) {
            for (int i = 0; i < mainTable.getColumnCount(); i++) {
                mainTable.getColumnModel().getColumn(i).setPreferredWidth(150);

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
