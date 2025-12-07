package com.kingartur1000;

import com.kingartur1000.Data.DataManager;
import com.kingartur1000.Entities.Group;
import com.kingartur1000.UI.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Главный класс приложения.
 * <p>Создаёт основное окно, вкладки, меню и управляет загрузкой/сохранением данных.</p>
 *  @author Роман и Артур
 *  @version 1.9
 */
public class MainWindow {
    /** Список всех групп */
    private static List<Group> groups;
    /** Панель управления группами */
    private static GroupPanel groupPanel;
    /** Глобальный шрифт для интерфейса */
    public static Font globalFont = new Font("Arial", Font.PLAIN, 20);

    /**
     * Точка входа в программу.
     * <p>Запускает SplashScreen, затем создаёт главное окно.</p>
     */

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Показываем SplashScreen
            SplashScreenWindow splash = new SplashScreenWindow();
            splash.showSplash(
                    // 2. После загрузки данных открываем WelcomeWindow
                    () -> {
                        WelcomeWindow welcome = new WelcomeWindow(MainWindow::createAndShowMainWindow);
                        welcome.setVisible(true);
                    },
                    // 3. Задача загрузки данных
                    MainWindow::loadInitialData
            );
        });
    }

    /**
     * Загрузка начальных данных.
     * <p>Если файл данных существует — загружаем, иначе создаём пустой список групп.</p>
     */
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

    /**
     * Создание и отображение главного окна приложения.
     * <p>Настраивает меню, вкладки, таймер и обработчики.</p>
     */
    private static void createAndShowMainWindow() {
        JFrame frame = new JFrame("Курсовой проект Дмитриева А.А. и Мосейко Р.А. - Учет посещаемости лекционных занятий");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(960, 600);

        // Меню
        JMenuBar menuBar = new JMenuBar();
        menuBar.setFont(globalFont);
        JMenu fileMenu = new JMenu("Файл");
        JMenu helpMenu = new JMenu("?");
        fileMenu.setFont(new Font("Arial", Font.BOLD, 20));
        helpMenu.setFont(new Font("Arial", Font.BOLD, 20));

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
        JMenuItem versionItem = new JMenuItem("Версия программы 1.9");
        aboutAuthorItem.setFont(globalFont);
        aboutProgramItem.setFont(globalFont);
        versionItem.setFont(globalFont);

        helpMenu.add(aboutAuthorItem);
        helpMenu.add(aboutProgramItem);
        helpMenu.add(versionItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        frame.setJMenuBar(menuBar);

        // Вкладки
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

        // Нижняя панель с таймером и кнопкой выхода
        GridPanel bottomGrid = new GridPanel(1, 6);
        bottomGrid.setFont(globalFont);

        JLabel timerLabel = new JLabel("Время работы: 00:00");
        timerLabel.setFont(globalFont);
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottomGrid.addToGrid(timerLabel, 0, 0, 1,1,3,1);

        long startTime = System.currentTimeMillis();
        new javax.swing.Timer(1000, e -> {
            long elapsed = (System.currentTimeMillis() - startTime) / 1000;
            long minutes = elapsed / 60;
            long seconds = elapsed % 60;
            timerLabel.setText(String.format("Время работы: %02d:%02d", minutes, seconds));
        }).start();

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

        // Обработчики меню
        saveItem.addActionListener(e -> saveData(frame));
        loadItem.addActionListener(e -> loadData(frame, studentPanel, attendancePanel, reportPanel));
        exitItem.addActionListener(e -> exitApplication(frame));

        // Диалог "Об авторах"
        aboutAuthorItem.addActionListener(e -> {
            ImageIcon arturIcon = new ImageIcon(MainWindow.class.getResource("/Images/Artur.jpg"));
            ImageIcon romanIcon = new ImageIcon(MainWindow.class.getResource("/Images/Roman.jpg"));

            Image scaledArtur = arturIcon.getImage().getScaledInstance(256, 256, Image.SCALE_SMOOTH);
            Image scaledRoman = romanIcon.getImage().getScaledInstance(256, 328, Image.SCALE_SMOOTH);

            ImageIcon arturScaledIcon = new ImageIcon(scaledArtur);
            ImageIcon romanScaledIcon = new ImageIcon(scaledRoman);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            JPanel arturPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel arturLabel = new JLabel(arturScaledIcon);
            arturPanel.add(arturLabel);
            arturPanel.add(new JLabel("<html><b>Дмитриев Артур Александрович</b><br>студент БНТУ, ФИТР, гр. 10702423</html>"));

            JPanel romanPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel romanLabel = new JLabel(romanScaledIcon);
            romanPanel.add(romanLabel);
            romanPanel.add(new JLabel("<html><b>Мосейко Роман Андреевич</b><br>студент БНТУ, ФИТР, гр. 10702423</html>"));

            panel.add(arturPanel);
            panel.add(romanPanel);

            arturLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    playSound("/Sounds/artursound.wav");
                }
            });

            romanLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    playSound("/Sounds/romansound.wav");
                }
            });

            JOptionPane.showMessageDialog(frame, panel, "Об авторах", JOptionPane.INFORMATION_MESSAGE);
        });

        // Диалог "О программе" (руководство пользователя)
        aboutProgramItem.addActionListener(e -> {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            JLabel titleLabel = new JLabel("Руководство пользователя");
            titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(titleLabel);

            panel.add(Box.createRigidArea(new Dimension(0, 10)));

            JTextArea textArea = new JTextArea(
                    "Добро пожаловать в программу учета посещаемости!\n\n" +
                            "Основные возможности:\n" +
                            "• Управление списком учебных групп и их добавление\n" +
                            "• Добавление, редактирование и удаление студентов\n" +
                            "• Отметка присутствия,отсутствия и опоздания студентов на занятиях\n" +
                            "• Формирование отчётов и возможность их сохранения в памяти устройства\n\n" +
                            "Дополнительно:\n" +
                            "• Сохранение и загрузка данных через меню 'Файл'\n" +
                            "• Таймер работы программы\n" +
                            "• Кнопка 'Выход' завершает работу программы\n\n" +
                            "Совет: перед выходом сохраняйте данные, чтобы не потерять изменения."
            );
            textArea.setFont(globalFont);
            textArea.setEditable(false);
            textArea.setOpaque(false);
            textArea.setAlignmentX(Component.CENTER_ALIGNMENT);

            panel.add(textArea);

            JOptionPane.showMessageDialog(frame, panel, "О программе", JOptionPane.INFORMATION_MESSAGE);
        });

        // Диалог "Версия программы 1.9"
        versionItem.addActionListener(e -> {
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

            panel.add(Box.createRigidArea(new Dimension(0, 10))); // отступ

            // Заголовок
            JLabel titleLabel = new JLabel("История изменений");
            titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(titleLabel);

            panel.add(Box.createRigidArea(new Dimension(0, 10)));

            // Текст с историей версий
            JTextArea textArea = new JTextArea(
                    "Версия программы: 1.9\n" +
                            "--------------------------------------\n" +
                            "• 1.9: Добавлен WelcomeWindow (титульный лист) и комментарии\n" +
                            "• 1.8: Переделаны отчёты и посещаемость\n" +
                            "• 1.7: Переделаны границы столбцов\n" +
                            "• 1.6: Переделана панель 'О программе'\n" +
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

            panel.add(textArea);

            // Показываем диалог
            JOptionPane.showMessageDialog(frame, panel, "Версия программы 1.9", JOptionPane.INFORMATION_MESSAGE);
        });


        // Обработчик закрытия окна
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                exitApplication(frame);
            }
        });

        // Обработчик смены вкладок
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            String title = tabbedPane.getTitleAt(index);

            if ("Посещаемость".equals(title)) {
                attendancePanel.setTodayDate(); // при открытии вкладки ставим сегодняшнюю дату
            } else if ("Отчёты".equals(title)) {
                if (attendancePanel.getCurrentGroup() != null) {
                    reportPanel.setGroup(attendancePanel.getCurrentGroup());
                }
            }
        });

        frame.setVisible(true);
    }

    /**
     * Создать список групп по умолчанию (пустой).
     */
    private static List<Group> createDefaultGroups() {
        return new java.util.ArrayList<>();
    }

    /**
     * Сохранить данные в файл.
     * <p>Использует DataManager для записи в Excel, показывает сообщение об успехе или ошибке.</p>
     */
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

    /**
     * Загрузить данные из файла.
     * <p>Подтверждает действие, затем загружает данные через DataManager и обновляет панели.</p>
     */
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

    /**
     * Завершение работы приложения.
     * <p>Предлагает сохранить данные перед выходом.</p>
     */
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

    /**
     * Воспроизвести звук из ресурсов.
     * @param soundPath путь к файлу звука
     */
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
