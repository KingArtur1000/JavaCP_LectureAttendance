package com.kingartur1000.UI;

import javax.swing.*;
import java.awt.*;

/**
 * {@code GridPanel} — это расширение {@link JPanel}, предоставляющее
 * удобный способ размещения компонентов в виде таблицы, аналогичной
 * элементу Grid в WPF.
 *
 * <p>В основе используется {@link GridBagLayout}, что позволяет:</p>
 * <ul>
 *   <li>Размещать компоненты по строкам и столбцам.</li>
 *   <li>Объединять несколько ячеек с помощью параметров rowSpan и colSpan.</li>
 *   <li>Автоматически растягивать компоненты для заполнения доступного пространства.</li>
 * </ul>
 *
 * <p><b>Пример использования:</b></p>
 * <pre>{@code
 * GridPanel grid = new GridPanel(3, 3);
 * grid.addToGrid(new JButton("0,0"), 0, 0);
 * grid.addToGrid(new JButton("0,1-2 span"), 0, 1, 1, 2);
 * grid.addToGrid(new JButton("1,0-2 span"), 1, 0, 2, 1);
 * }</pre>
 *
 * В результате получится таблица 3x3, где некоторые кнопки занимают
 * несколько ячеек.
 *
 * @author Артур
 * @version 1.0
 */
public class GridPanel extends JPanel {

    public GridPanel(int rows, int cols) {
        super(new GridBagLayout());
        this.rows = rows;
        this.cols = cols;
    }

    private int rows;
    private int cols;

    public void addToGrid(Component comp, int row, int col) {
        addToGrid(comp, row, col, 1, 1);
    }

    public void addToGrid(Component comp, int row, int col, int rowSpan, int colSpan) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.gridwidth = colSpan;
        gbc.gridheight = rowSpan;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(comp, gbc);
    }

    public void addToGrid(Component comp, int row, int col, int rowSpan, int colSpan,
                          double weightx, double weighty) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.gridwidth = colSpan;
        gbc.gridheight = rowSpan;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        add(comp, gbc);
    }

}
