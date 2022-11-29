package ru.edu.JFrame;

import ru.edu.Database.Database;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.sql.*;

public class WindowApp extends JFrame implements ActionListener {

    private JTextArea tArea;
    private JPanel fPanel, sPanel, thPanel;
    private JTextField fField, thField;
    private JLabel fPrompt, thPrompt, condPrompt;
    private JButton btnOpen, btnConnect, btnClean, btnPerf, btnDisc;
    private JMenuBar mMainMenu;
    private JMenu[] mMenu;
    private String[][] mTitles = {{"Файл", "Открыть базу данных", "-", "Выход"},
            {"Сервис", "Подключиться к базе данных", "Отключиться от базы данных","Выполнить SQL запрос"},
            {"Справка", "О программе"}};
    private String filePath;
    private String status = "База данных не подключена";
    private String showTables;
    private Database database = new Database(this);


    public WindowApp() {

        // главное окно
        JFrame frm = new JFrame();
        frm.setTitle("Приложение для работы с базой данных");
        frm.setSize(700, 500);
        frm.setLocation(500, 300);
        frm.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frm.setIconImage(getToolkit().getImage("src/main/java/ru/edu/image/Server@2x_4.png"));

        // панель меню
        mMainMenu = new JMenuBar();
        mMenu = new JMenu[mTitles.length];
        for (int i = 0; i < mMenu.length; i++) {
            mMenu[i] = new JMenu(mTitles[i][0]);
            for (int j = 1; j < mTitles[i].length; j++) {
                if (mTitles[i][j].equals("-")) {
                    mMenu[i].add(new JSeparator());
                } else {
                    mMenu[i].add(new JMenuItem(mTitles[i][j])).addActionListener(this);
                }
            }
            mMainMenu.add(mMenu[i]);
        }
        ImageIcon open = new ImageIcon("src/main/java/ru/edu/image/open.png");
        mMenu[0].getItem(0).setIcon(open);
        mMenu[0].getItem(0).setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK)
        );
        ImageIcon exit = new ImageIcon("src/main/java/ru/edu/image/door.png");
        mMenu[0].getItem(2).setIcon(exit);
        mMenu[0].getItem(2).setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK)
        );
        ImageIcon about = new ImageIcon("src/main/java/ru/edu/image/info.png");
        mMenu[2].getItem(0).setIcon(about);
        frm.setJMenuBar(mMainMenu);

        // первая панель
        fPanel = new JPanel();
        fPanel.setPreferredSize(new Dimension(getWidth(), 110));
        fPanel.setBorder(BorderFactory.createEtchedBorder());

        // текстовая панель на первой панели
        fPrompt = new JLabel();
        fPrompt.setText("Укажите полный путь к базе данных или выберите файл, нажав на кнопку \"Открыть файл\"");
        fPrompt.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        fPrompt.setBounds(10, 10, 125, 15);
        fPanel.add(fPrompt, BorderLayout.NORTH);

        // поле ввода пути к базе данных
        fField = new JTextField(30);
        fField.setSize(700, 50);
        fField.setPreferredSize(new Dimension(700, 26));
        fField.setBounds(fPrompt.getX(), fPrompt.getY()+200, fPrompt.getWidth(), 100);
        fField.addActionListener(e -> {
            filePath = fField.getText();
        });
        fPanel.add(fField, BorderLayout.WEST);

        // кнопка открыть на первой панели
        btnOpen = new JButton("Открыть файл");
        btnOpen.setPreferredSize(new Dimension(120, 25));
        btnOpen.addActionListener(e -> {
            try {
                onLoad();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        fPanel.add(btnOpen, BorderLayout.CENTER);

        // кнопка подключить на первой панели
        btnConnect = new JButton("Подключить");
        btnConnect.setPreferredSize(new Dimension(120, 25));
        btnConnect.addActionListener(e -> {
            database.connectionDB(filePath);
        });
        fPanel.add(btnConnect, BorderLayout.EAST);

        // панель состояния подключения на первой панели
        condPrompt = new JLabel();
        condPrompt.setText(status);
        condPrompt.setBorder(BorderFactory.createEtchedBorder());
        condPrompt.setFont(new Font(Font.DIALOG, Font.BOLD, 15));
        condPrompt.setHorizontalAlignment(0);
        condPrompt.setPreferredSize(new Dimension(430, 25));
        condPrompt.setBounds(10, 10, 225, 26);
        fPanel.add(condPrompt, BorderLayout.SOUTH);

        // кнопка отключения на первой панели
        btnDisc = new JButton("Отключить");
        btnDisc.setPreferredSize(new Dimension(120, 25));
        btnDisc.setBounds(10,10,275,25);
        btnDisc.addActionListener(e -> {
            try {
                database.disconnectDB();
            } catch (SQLException ex) {
                throw new RuntimeException();
            }
        });
        fPanel.add(btnDisc, BorderLayout.SOUTH);
        frm.add(fPanel, BorderLayout.NORTH);



        // вторая панель
        sPanel = new JPanel();
        sPanel.setPreferredSize(new Dimension(getWidth(), 100));
        sPanel.setBorder(BorderFactory.createEtchedBorder());

        tArea = new JTextArea();
        tArea.setFont(new Font(Font.DIALOG, Font.BOLD, 13));
        tArea.setLineWrap(true);
        tArea.setWrapStyleWord(true);
        JScrollPane spane = new JScrollPane(tArea);
        frm.add(spane, BorderLayout.CENTER);



        // третья панель
        thPanel = new JPanel();
        thPanel.setPreferredSize(new Dimension(getWidth(), 90));
        thPanel.setBorder(BorderFactory.createEtchedBorder());

        // текстовая панель на третьей панели
        thPrompt = new JLabel();
        thPrompt.setText("Введите SQL запрос");
        thPrompt.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        thPrompt.setBounds(10, 10, getWidth(), getHeight());
        thPanel.add(thPrompt, BorderLayout.NORTH);

        // поле ввода запроса на третьей панели
        thField = new JTextField();
        thField.setPreferredSize(new Dimension(500, 25));
        thField.setBounds(thPrompt.getX(), thPrompt.getY()+200, thPrompt.getWidth(), 100);
        thPanel.add(thField, BorderLayout.NORTH);

        // кнопка выполнения запроса на третьей панели
        btnPerf = new JButton("Выполнить запрос");
        btnPerf.setSize(50,50);
        btnPerf.setPreferredSize(new Dimension(150, 25));
        btnPerf.addActionListener(e -> {
                showTable();
                database.requestSQL(thField.getText());
        });
        thPanel.add(btnPerf, BorderLayout.CENTER);


        // кнопка очистки на третьей панели
        btnClean = new JButton("Очистить");
        btnClean.setSize(50,50);
        btnClean.setPreferredSize(new Dimension(150, 25));
        btnClean.addActionListener(e -> {
            thField.setText("");
        });
        thPanel.add(btnClean, BorderLayout.EAST);
        frm.add(thPanel, BorderLayout.SOUTH);

        //отобразить фрейм
        frm.setVisible(true);
    }

    // окно результата sql-запроса
    private void showTable() {
        JFrame tableFrame = new JFrame("Результат");
        tableFrame.setIconImage(getToolkit().getImage("src/main/java/ru/edu/image/Server@2x_4.png"));
        tableFrame.setSize(new Dimension(500, 480));
        tableFrame.setLocation(600, 400);
        tableFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        tableFrame.setLocationRelativeTo(null);
        tableFrame.setLayout(new GridBagLayout());

        JButton btnCancel = new JButton("Close");
        btnCancel.addActionListener(e -> {
            tableFrame.setVisible(false);
        });

        // создание и заполнение таблицы результата sql запроса
        JTable table = new JTable();
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension( 400, 400));

        try {
            ResultSet resultSet = database.requestSQL(thField.getText());
            ResultSetMetaData rsmd = resultSet.getMetaData();

            // добавили название столбцов
            String[] columnNames = new String[rsmd.getColumnCount()];
            for (int i = 1; i <= columnNames.length ; i++) {
                columnNames[i-1] = rsmd.getColumnLabel(i);
            }

            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            String[] rowTable = columnNames;
            String[] data = new String[columnNames.length];

            while (resultSet.next()) {

                for (int i = 1; i <= columnNames.length ; i++) {
                    columnNames[i-1] = resultSet.getString(rsmd.getColumnLabel(i));
                    data[i-1] = columnNames[i-1];
                }

                tableModel.addRow(data);
            }

            table.setModel(tableModel);
            resultSet.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        tableFrame.add(tableScrollPane, new GridBagConstraints(0, 0, 1, 1, 1 ,1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(1, 1, 1, 1), 0, 0));

        tableFrame.add(btnCancel, new GridBagConstraints(0, 1, 1, 1, 1 ,1,
                GridBagConstraints.NORTH, GridBagConstraints.CENTER,
                new Insets(1, 1, 1, 1), 0, 0));

        tableFrame.setVisible(true);
        tableFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    /**
     * Обработка событий кнопок на верхней панели
     * @param e the event to be processed
     */

    @Override
    public void actionPerformed(ActionEvent e) {
        String res = e.getActionCommand();

        // нажатие кнопки "открыть базу данных"
        if(res.equals("Открыть базу данных")) {
            try {
                onLoad();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            return;
        }

        // нажатие кнопки "выход"
        if(res.equals("Выход")) {
            onExit();
        }

        // нажатие кнопки подключить БД
        if(res.equals("Подключиться к базе данных")) {
            database.connectionDB(filePath);
        }

        // нажатие кнопки отключить БД
        if(res.equals("Отключиться от базы данных")) {
            try {
                database.disconnectDB();
            } catch (SQLException ex) {
                throw new RuntimeException();
            }
        }

        if(res.equals("Выполнить SQL запрос")) {
            database.requestSQL(thField.getText());
        }

        // нажатие кнопки "about"
        if(res.equals(("О программе"))) {
            String text = "<html> Работа с базой данных SQLite<br>" +
                    "<br>Разработано средствами Java Swing<br>" +
                    "в качестве тестового задания<br>" +
                    "<br>Russia, Saint-Petersburg" +
                    "<br>Vdovyko Alksei, 2022</html>";
            JOptionPane.showMessageDialog(
                    this,
                    text,
                    "About",
                    JOptionPane.PLAIN_MESSAGE,
                    new ImageIcon("src/main/java/ru/edu/image/about.png")
            );
            return;
        }
    }

    /**
     * нажатие на кнопку "открыть базу данных"
     * @throws SQLException
     */
    private void onLoad() throws SQLException {
        JFileChooser fChoose = new JFileChooser();
        FileNameExtensionFilter flt = new FileNameExtensionFilter(
                "База данных",
                "db"
        );
        fChoose.setFileFilter(flt);
        int res = fChoose.showOpenDialog(this);
        if(res==JFileChooser.APPROVE_OPTION) {
            filePath=fChoose.getSelectedFile().getAbsolutePath();
        }
    }

    /**
     * окно ошибки выполнения sql-запроса
     */
    public void showError() {
        String messageErr = "<html>SQL запрос неверный.<br>" +
                "<br>Проверьте корректность введенных данных<br>" +
                " и подключение к базе данных</html>";
        JOptionPane.showMessageDialog(this, messageErr, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * выход и закрытие программы
     */
    private void onExit() {
        System.exit(0);
    }


    /**
     * геттеры и сеттеры только для полей используемых за пределами класса
     */
    public JTextArea gettArea() {
        return tArea;
    }

    public JLabel getCondPrompt() {
        return condPrompt;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getStatus() {
        return status;
    }

    public void setShowTables(String showTables) {
        this.showTables = showTables;
    }
}
