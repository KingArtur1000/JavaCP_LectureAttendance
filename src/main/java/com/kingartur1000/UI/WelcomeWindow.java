package com.kingartur1000.UI;

import javax.swing.*;
import java.awt.*;

/**
 * Приветственное окно с титульным листом и кнопками "Выход" и "Далее".
 * Отображается после SplashScreen и перед основным окном.
 * Содержит информацию о курсовой работе, авторах и преподавателе.
 * @author Роман
 * @version 1.9
 */
public class WelcomeWindow extends JFrame {

    /** Основной контейнер */
    private Container container;

    /** Панели для структурирования интерфейса */
    private JPanel jpnlUniversity, jpnlFaculty, jpnlCourseWork, jpnlCourseWorkInfo,
            jpnlInfo, jpnlAuthorInfo, jpnlTeacherInfo, jpnlIcon, jpnlButtons;

    /** Метки заголовка и описания */
    private JLabel jlblUniversity, jlblFaculty, jlblDepartment;
    private JLabel jlblCourseWork, jlblDiscipline, jlblTtheme;

    /** Метки авторов и преподавателя */
    private JLabel jlblAuthor, jlblAuthorName, jlblTeacher, jlblTeacherName;

    /** Метка города и года */
    private JLabel jlblMinsk2025;

    /** Метка с изображением */
    private JLabel jlblImage;

    /** Кнопки управления */
    private JButton jbtnStart, jbtnExit;

    /** Таймер авто-выхода */
    private Timer timer;

    /**
     * Конструктор окна приветствия
     * @param onContinue действие, выполняемое при нажатии кнопки "Далее"
     */
    public WelcomeWindow(Runnable onContinue){
        setTitle("Курсовая работа");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setPreferredSize(new Dimension(1100, 720));

        container = getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        /* === Инициализация панелей === */
        jpnlUniversity = new JPanel();
        jpnlFaculty = new JPanel();
        jpnlCourseWork = new JPanel();
        jpnlCourseWork.setLayout(new BoxLayout(jpnlCourseWork, BoxLayout.Y_AXIS));
        jpnlCourseWorkInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        jpnlInfo = new JPanel();
        jpnlInfo.setLayout(new BoxLayout(jpnlInfo, BoxLayout.Y_AXIS));
        jpnlAuthorInfo = new JPanel();
        jpnlAuthorInfo.setLayout(new BoxLayout(jpnlAuthorInfo, BoxLayout.Y_AXIS));
        jpnlTeacherInfo = new JPanel();
        jpnlTeacherInfo.setLayout(new BoxLayout(jpnlTeacherInfo, BoxLayout.Y_AXIS));
        jpnlIcon = new JPanel();
        jpnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));

        /* === Заголовок и описание === */
        jlblUniversity = new JLabel("МИНИСТЕРСТВО ОБРАЗОВАНИЯ РЕСПУБЛИКИ БЕЛАРУСЬ");
        jlblUniversity.setFont(new Font("Times New Roman", Font.PLAIN, 24));
        jlblUniversity.setAlignmentX(Component.CENTER_ALIGNMENT);

        jlblFaculty = new JLabel("БЕЛОРУССКИЙ НАЦИОНАЛЬНЫЙ ТЕХНИЧЕСКИЙ УНИВЕРСИТЕТ");
        jlblFaculty.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        jlblFaculty.setAlignmentX(Component.CENTER_ALIGNMENT);

        jlblDepartment = new JLabel("Факультет информационных технологий и робототехники");
        jlblDepartment.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        jlblDepartment.setAlignmentX(Component.CENTER_ALIGNMENT);

        jlblCourseWork = new JLabel("Курсовая работа");
        jlblCourseWork.setFont(new Font("Times New Roman", Font.PLAIN, 30));
        jlblCourseWork.setAlignmentX(Component.CENTER_ALIGNMENT);

        jlblDiscipline = new JLabel("по дисциплине «Программирование на Java»");
        jlblDiscipline.setFont(new Font("Times New Roman", Font.PLAIN, 25));
        jlblDiscipline.setAlignmentX(Component.CENTER_ALIGNMENT);

        jlblTtheme = new JLabel("Журнал посещаемости студентов");
        jlblTtheme.setFont(new Font("Times New Roman", Font.PLAIN, 40));
        jlblTtheme.setAlignmentX(Component.CENTER_ALIGNMENT);

        /* === Авторство и преподаватель === */
        jlblAuthor = new JLabel("Выполнили: студенты группы 10702423");
        jlblAuthor.setFont(new Font("Times New Roman", Font.PLAIN, 20)); // однострочный комментарий: подпись авторов

        jlblAuthorName = new JLabel("Мосейко Роман Андреевич, Дмитриев Артур Александрович");
        jlblAuthorName.setFont(new Font("Times New Roman", Font.PLAIN, 20));

        jlblTeacher = new JLabel("Преподаватель: к.ф.-м.н., доц.");
        jlblTeacher.setFont(new Font("Times New Roman", Font.PLAIN, 20));

        jlblTeacherName = new JLabel("Сидорик Валерий Владимирович");
        jlblTeacherName.setFont(new Font("Times New Roman", Font.PLAIN, 20));

        jlblMinsk2025 = new JLabel("Минск 2025");
        jlblMinsk2025.setFont(new Font("Times New Roman", Font.PLAIN, 25));
        jlblMinsk2025.setAlignmentX(Component.CENTER_ALIGNMENT);

        /* === Картинка === */
        jlblImage = new JLabel(loadScaledIcon("/Images/jurnalicon.jpg", 240, 240));
        jpnlIcon.add(jlblImage);

        /* === Кнопки === */
        jbtnStart = new JButton("Далее");
        jbtnStart.setFont(new Font("Times New Roman", Font.BOLD, 16));
        jbtnStart.addActionListener(e -> {
            dispose(); // закрываем окно
            timer.stop(); // останавливаем таймер
            if (onContinue != null) onContinue.run(); // запускаем основное окно
        });

        jbtnExit = new JButton("Выход");
        jbtnExit.setFont(new Font("Times New Roman", Font.BOLD, 16));
        jbtnExit.addActionListener(e -> System.exit(0)); // выход из программы

        timer = new Timer(60000, e -> System.exit(0)); // таймер авто-выхода
        timer.setRepeats(false);
        timer.start();

        /* === Сборка интерфейса === */
        jpnlUniversity.add(jlblUniversity);
        jpnlFaculty.add(jlblFaculty);
        jpnlFaculty.add(jlblDepartment);
        jpnlCourseWork.add(jlblCourseWork);
        jpnlCourseWork.add(jlblDiscipline);
        jpnlCourseWork.add(jlblTtheme);
        jpnlAuthorInfo.add(jlblAuthor);
        jpnlAuthorInfo.add(jlblAuthorName);
        jpnlTeacherInfo.add(jlblTeacher);
        jpnlTeacherInfo.add(jlblTeacherName);
        jpnlInfo.add(jpnlAuthorInfo);
        jpnlInfo.add(jpnlTeacherInfo);
        jpnlCourseWorkInfo.add(jpnlIcon);
        jpnlCourseWorkInfo.add(jpnlInfo);
        jpnlButtons.add(jbtnStart);
        jpnlButtons.add(jbtnExit);

        container.add(jpnlUniversity);
        container.add(jpnlFaculty);
        container.add(jpnlCourseWork);
        container.add(jpnlCourseWorkInfo);
        container.add(jlblMinsk2025);
        container.add(jpnlButtons);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Загружает изображение из ресурсов и масштабирует его до заданных размеров.
     * @param path путь к изображению
     * @param width ширина
     * @param height высота
     * @return масштабированная иконка
     */
    private ImageIcon loadScaledIcon(String path, int width, int height) {
        java.net.URL url = getClass().getResource(path);
        if (url == null) {
            System.err.println("Не найден ресурс: " + path);
            return new ImageIcon(); // возвращаем пустую иконку
        }
        ImageIcon icon = new ImageIcon(url);
        Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}
