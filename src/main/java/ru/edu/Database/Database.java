package ru.edu.Database;

import ru.edu.JFrame.WindowApp;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Database extends JFrame{

    private Connection connection;
    private ResultSet resultSet;
    private Statement statement;
    private final WindowApp app;

    public Database(WindowApp app) {
        this.app = app;
    }

    /**
     * Подключение к базе данных
     * @param filePath путь к базе данных
     * @return Statement результат подключения к БД.
     */
    public Statement connectionDB(String filePath) {

        statement = null;
        if (filePath.equals("")) {
            throw new RuntimeException();
        }

        try {
            String jdbcUrl = "jdbc:sqlite:" + filePath;
            connection = DriverManager.getConnection(jdbcUrl);
            System.out.println("connection: " + connection);
            statement = connection.createStatement();
            app.getCondPrompt().setText("Success. База данных подключена. OK");
            app.getCondPrompt().setForeground(Color.BLUE);

            //добавление информации о таблицах в БД
            String allTables = "SELECT name FROM sqlite_master WHERE type='table';";
            ResultSet resultSet = statement.executeQuery(allTables);
            String tablesName = "Таблицы в базе данных:\n";

            while (resultSet.next()) {
                tablesName += resultSet.getString(1);
                tablesName += "\n";
            }
            app.gettArea().setText(tablesName);

        } catch (SQLException ex) {
            app.getCondPrompt().setText(app.getStatus());
            app.getCondPrompt().setForeground(Color.black);
            app.gettArea().setText("");
        }

        return statement;
    }

    /**+
     * Отключение от базы данных
     * @throws SQLException
     */
    public void disconnectDB() throws SQLException {
        connection.close();
        app.setFilePath("");
        app.getCondPrompt().setText(app.getStatus());
        app.getCondPrompt().setForeground(Color.BLACK);
        app.setShowTables("");
        app.gettArea().setText("");
    }

    /**
     * Выполнение sql-запроса в БД
     * @param request запрос с базу данных
     * @return результат запроса в базу данных
     */
    public ResultSet requestSQL(String request) {
        // примеры
//        String request = "select * from albums";
//        String request = "select artists.Name, albums.Title from albums inner join artists on albums.ArtistId  = artists.ArtistId";

        try {
            resultSet = statement.executeQuery(request);
        } catch (NullPointerException | SQLException ex) {
            app.showError();
            connectionDB(app.getFilePath());
        }
        return resultSet;
    }
}
