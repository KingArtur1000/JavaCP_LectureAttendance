package com.kingartur1000.UI;

import com.kingartur1000.Entities.AttendanceTable.AttendanceMark;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Рендерер ячеек таблицы для отображения статуса посещаемости.
 * <p>Использует JCheckBox с цветной галочкой:
 * - Чёрная галочка для PRESENT,
 * - Красная галочка для LATE,
 * - Пустая ячейка для ABSENT.</p>
 * @author Роман
 * @version 1.9
 */
public class AttendanceMarkRenderer extends JCheckBox implements TableCellRenderer {

    /** Иконка чёрной галочки (присутствие) */
    private static final Icon BLACK_CHECK = new ColoredCheckIcon(Color.BLACK);
    /** Иконка красной галочки (опоздание) */
    private static final Icon RED_CHECK   = new ColoredCheckIcon(Color.RED);

    /**
     * Конструктор рендерера.
     * <p>Устанавливает выравнивание по центру и делает компонент непрозрачным.</p>
     */
    public AttendanceMarkRenderer() {
        setHorizontalAlignment(CENTER);
        setOpaque(true);
    }

    /*
     * Метод для рендеринга ячейки таблицы.
     * Алгоритм:
     * 1. Установить фон в зависимости от выделения строки.
     * 2. Проверить значение ячейки:
     *    - Если PRESENT -> отметить чекбокс и поставить чёрную галочку.
     *    - Если LATE -> отметить чекбокс и поставить красную галочку.
     *    - Если ABSENT -> снять чекбокс и убрать иконку.
     * 3. Вернуть компонент (JCheckBox) для отображения.
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

        if (value instanceof AttendanceMark mark) {
            switch (mark) {
                case PRESENT -> {
                    setSelected(true);
                    setIcon(BLACK_CHECK); // чёрная галочка
                }
                case LATE -> {
                    setSelected(true);
                    setIcon(RED_CHECK); // красная галочка
                }
                case ABSENT -> {
                    setSelected(false);
                    setIcon(null); // пустая ячейка
                }
            }
        }
        return this;
    }

    /**
     * Внутренний класс для рисования цветной галочки.
     * <p>Позволяет отобразить галочку заданного цвета.</p>
     */
    static class ColoredCheckIcon implements Icon {
        /** Цвет галочки */
        private final Color color;

        /**
         * Конструктор цветной иконки.
         *
         * @param color цвет галочки
         */
        public ColoredCheckIcon(Color color) {
            this.color = color;
        }

        /*
         * Метод для рисования иконки.
         * Алгоритм:
         * 1. Создать Graphics2D.
         * 2. Установить цвет и толщину линии.
         * 3. Нарисовать две линии в форме галочки.
         * 4. Освободить ресурсы Graphics2D.
         */
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

        /** @return высота иконки (16 пикселей) */
        @Override
        public int getIconWidth() {
            return 16;
        }

        /** @return высота иконки (16 пикселей) */
        @Override
        public int getIconHeight() {
            return 16;
        }
    }
}
