package com.kingartur1000;

import com.kingartur1000.Data.DataManager;
import com.kingartur1000.Entities.Group;
import com.kingartur1000.UI.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;



public class MainWindow {
    private static List<Group> groups;
    private static GroupPanel groupPanel;
    public static Font globalFont = new Font("Arial", Font.PLAIN, 20);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SplashScreenWindow splash = new SplashScreenWindow();
            splash.showSplash(
                    MainWindow::createAndShowMainWindow, // что делать после загрузки
                    MainWindow::loadInitialData           // сама загрузка
            );
        });
    }

    private static void loadInitialData() {
        if (DataManager.dataFileExists()) {
            try {
                groups = DataManager.loadData();
            } catch (Exception e) {
                groups = createDefaultGroups();
            }
        } else {
            groups = createDefaultGroups();
        }
    }

    private static void createAndShowMainWindow() {
        JFrame frame = new JFrame("Курсовой проект Дмитриева А.А. и Мосейко Р.А. - Учет посещаемости лекционных занятий");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(960, 600);

        // меню
        JMenuBar menuBar = new JMenuBar();
        menuBar.setFont(globalFont);
        JMenu fileMenu = new JMenu("Файл");
        JMenu whatMenu = new JMenu("?");
        fileMenu.setFont(new Font("Arial", Font.BOLD, 20));
        whatMenu.setFont(new Font("Arial", Font.BOLD, 20));

        JMenuItem saveItem = new JMenuItem("Сохранить");
        saveItem.setFont(globalFont);
        JMenuItem loadItem = new JMenuItem("Загрузить");
        loadItem.setFont(globalFont);
        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.setFont(globalFont);

        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenuItem aboutAuthorItem = new JMenuItem("Об авторах");
        JMenuItem aboutProgramItem = new JMenuItem("О программе");
        aboutAuthorItem.setFont(globalFont);
        aboutProgramItem.setFont(globalFont);
        whatMenu.add(aboutAuthorItem);
        whatMenu.add(aboutProgramItem);

        menuBar.add(fileMenu);
        menuBar.add(whatMenu);
        frame.setJMenuBar(menuBar);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(globalFont);

        StudentPanel studentPanel = new StudentPanel();
        AttendancePanel attendancePanel = new AttendancePanel();

        groupPanel = new GroupPanel(studentPanel, attendancePanel, groups);
        ReportPanel reportPanel = new ReportPanel(groupPanel.getAllGroups());
        groupPanel.setReportPanel(reportPanel);

        tabbedPane.addTab("Группы", groupPanel);
        tabbedPane.addTab("Студенты", studentPanel);
        tabbedPane.addTab("Посещаемость", attendancePanel);
        tabbedPane.addTab("Отчёты", reportPanel);

        GridPanel bottomGrid = new GridPanel(1, 6);
        bottomGrid.setFont(globalFont);

        // 🔹 Таймер
        JLabel timerLabel = new JLabel("Время работы: 00:00");
        timerLabel.setFont(globalFont);
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottomGrid.addToGrid(timerLabel, 0, 0, 1,1,3,1); // ставим в центр

        long startTime = System.currentTimeMillis();
        new javax.swing.Timer(1000, e -> {
            long elapsed = (System.currentTimeMillis() - startTime) / 1000;
            long minutes = elapsed / 60;
            long seconds = elapsed % 60;
            timerLabel.setText(String.format("Время работы: %02d:%02d", minutes, seconds));
        }).start();

        // Кнопка выхода
        JButton exitButton = new JButton("Выход");
        exitButton.setFont(globalFont);
        exitButton.setForeground(Color.BLACK);
        exitButton.setToolTipText("Закрывает программу, перед этим предлагает сохранить данные в Excel файл");
        exitButton.addActionListener(e -> exitApplication(frame));
        bottomGrid.addToGrid(exitButton, 0, 1,1,1,1,1);

        GridPanel mainGrid = new GridPanel(2, 1);
        mainGrid.setFont(globalFont);
        mainGrid.addToGrid(tabbedPane, 0, 0, 1, 1, 1, 12);
        mainGrid.addToGrid(bottomGrid, 1, 0);

        frame.add(mainGrid);
        frame.setLocationRelativeTo(null);

        // обработчики меню
        saveItem.addActionListener(e -> saveData(frame));
        loadItem.addActionListener(e -> loadData(frame, studentPanel, attendancePanel, reportPanel));
        exitItem.addActionListener(e -> exitApplication(frame));
        aboutAuthorItem.addActionListener(e -> {
            // Загружаем изображения из ресурсов
            ImageIcon arturIcon = new ImageIcon(MainWindow.class.getResource("/Images/Artur.jpg"));
            ImageIcon romanIcon = new ImageIcon(MainWindow.class.getResource("/Images/Roman.jpg"));

            // Масштабируем изображения
            Image scaledArtur = arturIcon.getImage().getScaledInstance(256, 256, Image.SCALE_SMOOTH);
            Image scaledRoman = romanIcon.getImage().getScaledInstance(256, 328, Image.SCALE_SMOOTH);

            // Создаём иконки
            ImageIcon arturScaledIcon = new ImageIcon(scaledArtur);
            ImageIcon romanScaledIcon = new ImageIcon(scaledRoman);

            // Панель с вертикальным расположением
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            // Панель Артура
            JPanel arturPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel arturLabel = new JLabel(arturScaledIcon);
            arturPanel.add(arturLabel);
            arturPanel.add(new JLabel("<html><b>Дмитриев Артур Александрович</b><br>студент БНТУ, ФИТР, гр. 10702423</html>"));

            // Панель Романа
            JPanel romanPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel romanLabel = new JLabel(romanScaledIcon);
            romanPanel.add(romanLabel);
            romanPanel.add(new JLabel("<html><b>Мосейко Роман Андреевич</b><br>студент БНТУ, ФИТР, гр. 10702423</html>"));

            // Добавляем всё в основную панель
            panel.add(arturPanel);
            panel.add(romanPanel);

            // 👉 Добавляем обработчики клика по картинкам
            arturLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    playSound("/Sounds/artursound.wav"); // звук для Артура
                }
            });

            romanLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    playSound("/Sounds/romansound.wav"); // звук для Романа
                }
            });

            // Показываем диалог
            JOptionPane.showMessageDialog(frame, panel, "Об авторах", JOptionPane.INFORMATION_MESSAGE);
        });



        aboutProgramItem.addActionListener(e -> {
            // Загружаем картинку котов-программистов
            ImageIcon catsIcon = new ImageIcon(MainWindow.class.getResource("/Images/cats_programmers.jpg"));
            Image scaledCats = catsIcon.getImage().getScaledInstance(600, 375, Image.SCALE_SMOOTH);
            ImageIcon catsScaledIcon = new ImageIcon(scaledCats);

            // Панель с вертикальным расположением
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            // Картинка сверху
            JLabel catsLabel = new JLabel(catsScaledIcon);
            catsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(catsLabel);

            // Текст снизу
            JTextArea textArea = new JTextArea(
                    "Версия программы: 1.8\n" +
                            "--------------------------------------\n" +
                            " Истрия изменений:\n" +
                            "• 1.8: Переделаны отчёты и посещаемость\n" +
                            "• 1.7: Переделаны границы столбоцов\n" +
                            "• 1.6: Переделана панель о программе\n" +
                            "• 1.5: Добавлено звуковое сопровождение\n" +
                            "• 1.4: Добавлен таймер времени работы программы\n" +
                            "• 1.3: SplashScreen с прогресс-баром\n" +
                            "• 1.2: Сортировка по столбцам\n" +
                            "• 1.1: JCalendar для выбора даты\n" +
                            "• 1.0: Основной функционал"
            );
            textArea.setFont(globalFont);
            textArea.setEditable(false);
            textArea.setOpaque(false);
            textArea.setAlignmentX(Component.CENTER_ALIGNMENT);

            panel.add(Box.createRigidArea(new Dimension(0, 10))); // отступ
            panel.add(textArea);

            // Показываем диалог
            JOptionPane.showMessageDialog(frame, panel, "О программе", JOptionPane.INFORMATION_MESSAGE);
        });


        // закрытие окна
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                exitApplication(frame);
            }
        });

        // смена вкладок
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            String title = tabbedPane.getTitleAt(index);

            if ("Посещаемость".equals(title)) {
                attendancePanel.setTodayDate();
            } else if ("Отчёты".equals(title)) {
                // 👉 обновляем ReportPanel
                if (attendancePanel.getCurrentGroup() != null) {
                    reportPanel.setGroup(attendancePanel.getCurrentGroup());
                }
            }
        });


        frame.setVisible(true);
    }


    private static List<Group> createDefaultGroups() {
        return new java.util.ArrayList<>();
    }

    private static void saveData(JFrame frame) {
        try {
            DataManager.saveData(groupPanel.getAllGroups());
            JOptionPane.showMessageDialog(frame,
                    "Данные успешно сохранены в файл attendance_data.xlsx!",
                    "Сохранение", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame,
                    "Ошибка сохранения данных: " + ex.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private static void loadData(JFrame frame, StudentPanel studentPanel,
                                 AttendancePanel attendancePanel, ReportPanel reportPanel) {
        int result = JOptionPane.showConfirmDialog(frame,
                "Загрузить данные из файла? Текущие несохранённые данные будут потеряны.",
                "Подтверждение", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            try {
                groups = DataManager.loadData();
                groupPanel.reloadGroups(groups);
                studentPanel.setGroup(null);
                attendancePanel.setGroup(null);

                JOptionPane.showMessageDialog(frame,
                        "Данные успешно загружены из файла!",
                        "Загрузка", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame,
                        "Ошибка загрузки данных: " + ex.getMessage(),
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private static void exitApplication(JFrame frame) {
        int result = JOptionPane.showConfirmDialog(frame,
                "Сохранить данные перед выходом?",
                "Выход", JOptionPane.YES_NO_CANCEL_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            saveData(frame);
            System.exit(0);
        } else if (result == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
        // CANCEL — ничего не делаем
    }

    private static void playSound(String soundPath) {
        try {
            java.net.URL url = MainWindow.class.getResource(soundPath);
            if (url == null) {
                System.err.println("Не найден файл: " + soundPath);
                return;
            }
            javax.sound.sampled.AudioInputStream audioIn = javax.sound.sampled.AudioSystem.getAudioInputStream(url);
            javax.sound.sampled.Clip clip = javax.sound.sampled.AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
