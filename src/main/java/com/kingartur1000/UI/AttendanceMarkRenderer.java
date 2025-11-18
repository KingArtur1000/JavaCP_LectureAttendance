package com.kingartur1000.UI;

import com.kingartur1000.Entities.AttendanceTable.AttendanceMark;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class AttendanceMarkRenderer extends JCheckBox implements TableCellRenderer {

    private static final Icon BLACK_CHECK = new ColoredCheckIcon(Color.BLACK);
    private static final Icon RED_CHECK   = new ColoredCheckIcon(Color.RED);

    public AttendanceMarkRenderer() {
        setHorizontalAlignment(CENTER);
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

        if (value instanceof AttendanceMark mark) {
            switch (mark) {
                case PRESENT -> {
                    setSelected(true);
                    setIcon(BLACK_CHECK);
                }
                case LATE -> {
                    setSelected(true);
                    setIcon(RED_CHECK);
                }
                case ABSENT -> {
                    setSelected(false);
                    setIcon(null);
                }
            }
        }
        return this;
    }

    /**
     * Внутренний класс для рисования цветной галочки.
     */
    static class ColoredCheckIcon implements Icon {
        private final Color color;

        public ColoredCheckIcon(Color color) {
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2));

            // простая галочка
            int w = getIconWidth();
            int h = getIconHeight();
            g2.drawLine(x + 2, y + h / 2, x + w / 3, y + h - 2);
            g2.drawLine(x + w / 3, y + h - 2, x + w - 2, y + 2);

            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return 16;
        }

        @Override
        public int getIconHeight() {
            return 16;
        }
    }
}
