import java.sql.*;

class DataBaseService {

    // Объект, в котором будет храниться соединение с БД
    private static Connection connection;
    // Statement используется для того, чтобы выполнить sql-запрос
    private static Statement stmt;

    static void connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            //Выполняем подключение к базе данных, "jdbc:sqlite:userDB.db" - адрес подключения
            connection = DriverManager.getConnection("jdbc:sqlite:Cloud_storage_Database.db");
            stmt = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static String getIdByLoginAndPass(String login, String pass) throws SQLException {
        String sql = String.format("SELECT id FROM main where " +
                "login = '%s' and password = '%s'", login, pass);
        // В resultSet будет храниться результат нашего запроса,
        // который выполняется командой statement.executeQuery()
        ResultSet rs = stmt.executeQuery(sql);

        if (rs.next()) {
            return rs.getString(1);
        }
        return null;
    }
}
