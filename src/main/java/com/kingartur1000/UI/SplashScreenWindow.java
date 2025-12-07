package com.kingartur1000.UI;

import javax.swing.*;
import java.awt.*;

/**
 * Окно SplashScreen для отображения экрана загрузки.
 * <p>Показывает сообщение "Загрузка данных..." и индикатор прогресса.</p>
 * <p>Используется для имитации процесса загрузки перед запуском основного окна.</p>
 * @author Артур и Роман
 * @version 1.9
 */
public class SplashScreenWindow extends JWindow {
    /** Индикатор прогресса (бегущая полоска) */
    private final JProgressBar progressBar;

    /**
     * Конструктор окна SplashScreen.
     * <p>Создаёт панель с текстом и прогресс-баром.</p>
     */
    public SplashScreenWindow() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);

        JLabel label = new JLabel("Загрузка данных...", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        content.add(label, BorderLayout.CENTER);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true); // «бегущий» прогресс
        content.add(progressBar, BorderLayout.SOUTH);

        setContentPane(content);
        setSize(400, 200);
        setLocationRelativeTo(null); // центрируем окно на экране
    }

    /**
     * Отобразить SplashScreen и выполнить задачу загрузки.
     * <p>После завершения загрузки окно автоматически закрывается.</p>
     *
     * @param onFinish действие, выполняемое после закрытия окна
     * @param loadTask задача загрузки (например, чтение данных)
     */
    public void showSplash(Runnable onFinish, Runnable loadTask) {
        setVisible(true);

        /*
         * Используем SwingWorker для выполнения задачи в фоновом потоке.
         * Алгоритм:
         * 1. Запустить задачу loadTask.
         * 2. Засечь время выполнения.
         * 3. Если задача заняла меньше 3 секунд — добавить задержку.
         * 4. После завершения скрыть и уничтожить окно.
         * 5. Вызвать действие onFinish.
         */
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                long start = System.currentTimeMillis();

                // выполняем загрузку данных
                loadTask.run();

                // гарантируем минимум 3 секунды отображения SplashScreen
                long elapsed = System.currentTimeMillis() - start;
                if (elapsed < 3000) {
                    Thread.sleep(3000 - elapsed);
                }
                return null;
            }

            @Override
            protected void done() {
                setVisible(false); // скрываем окно
                dispose();         // освобождаем ресурсы
                onFinish.run();    // выполняем действие после загрузки
            }
        };
        worker.execute(); // запускаем фоновую задачу
    }
}
