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
 * @author Артур
 * @version 1.9
 */
public class GridPanel extends JPanel {

    /** Количество строк в сетке */
    private int rows;
    /** Количество столбцов в сетке */
    private int cols;

    /**
     * Конструктор панели сетки.
     *
     * @param rows количество строк
     * @param cols количество столбцов
     */
    public GridPanel(int rows, int cols) {
        super(new GridBagLayout()); // используем GridBagLayout как основу
        this.rows = rows;
        this.cols = cols;
    }

    /**
     * Добавить компонент в сетку в указанную ячейку.
     * <p>По умолчанию занимает одну строку и один столбец.</p>
     *
     * @param comp компонент
     * @param row строка
     * @param col столбец
     */
    public void addToGrid(Component comp, int row, int col) {
        addToGrid(comp, row, col, 1, 1);
    }

    /*
     * Метод для добавления компонента с возможностью указать rowSpan и colSpan.
     * Алгоритм:
     * 1. Создать объект GridBagConstraints.
     * 2. Установить координаты (row, col).
     * 3. Установить размеры ячейки (rowSpan, colSpan).
     * 4. Включить растягивание компонента по обеим осям.
     * 5. Установить веса по умолчанию (1.0).
     * 6. Добавить компонент в панель.
     */
    public void addToGrid(Component comp, int row, int col, int rowSpan, int colSpan) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.gridwidth = colSpan;
        gbc.gridheight = rowSpan;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; // равномерное распределение по горизонтали
        gbc.weighty = 1.0; // равномерное распределение по вертикали
        add(comp, gbc);
    }

    /**
     * Добавить компонент в сетку с указанием rowSpan, colSpan и весов.
     *
     * @param comp компонент
     * @param row строка
     * @param col столбец
     * @param rowSpan количество строк, которые занимает компонент
     * @param colSpan количество столбцов, которые занимает компонент
     * @param weightx вес по горизонтали (распределение свободного пространства)
     * @param weighty вес по вертикали (распределение свободного пространства)
     */
    public void addToGrid(Component comp, int row, int col, int rowSpan, int colSpan,
                          double weightx, double weighty) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.gridwidth = colSpan;
        gbc.gridheight = rowSpan;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = weightx; // задаём вес по горизонтали
        gbc.weighty = weighty; // задаём вес по вертикали
        add(comp, gbc);
    }

}
