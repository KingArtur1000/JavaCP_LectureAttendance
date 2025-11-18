package com.kingartur1000.UI;

import com.kingartur1000.Entities.AttendanceTable.AttendanceMark;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class AttendanceMarkRenderer extends JCheckBox implements TableCellRenderer {

    public AttendanceMarkRenderer() {
        setHorizontalAlignment(CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        setOpaque(true);
        setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

        if (value instanceof AttendanceMark mark) {
            switch (mark) {
                case PRESENT -> {
                    setSelected(true);
                    setForeground(Color.BLACK);
                }
                case LATE -> {
                    setSelected(true);
                    setForeground(Color.RED);
                }
                case ABSENT -> {
                    setSelected(false);
                    setForeground(Color.BLACK);
                }
            }
        }

        return this;
    }
}
