package com.kingartur1000.UI;

import javax.swing.*;
import java.awt.*;

public class SplashScreenWindow extends JWindow {
    private final JProgressBar progressBar;

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
        setLocationRelativeTo(null);
    }

    public void showSplash(Runnable onFinish, Runnable loadTask) {
        setVisible(true);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                long start = System.currentTimeMillis();

                // выполняем загрузку данных
                loadTask.run();

                // гарантируем минимум 3 секунды
                long elapsed = System.currentTimeMillis() - start;
                if (elapsed < 3000) {
                    Thread.sleep(3000 - elapsed);
                }
                return null;
            }

            @Override
            protected void done() {
                setVisible(false);
                dispose();
                onFinish.run();
            }
        };
        worker.execute();
    }
}
