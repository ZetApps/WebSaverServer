package core;

import interfaces.DataBaseService;
import org.sqlite.SQLiteConnection;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseWork implements DataBaseService{
    private SQLiteConnection connection;
    private Statement statement;

    public DataBaseWork() {
        try {
            connection = makeConnection("org.sqlite.JDBC","jdbc:sqlite:src/main/resources/database/webserverdb.db");
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*@Override
    public SQLiteConnection makeConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        System.out.println("Сервер:Подключение к базе данных установлено!");
        return (SQLiteConnection) DriverManager.getConnection("jdbc:sqlite:main.db");
    }*/

    @Override
    public void stopConnection() {
        try {
            connection.close();
        }catch (SQLException e){}
        try {
            statement.close();
        }catch (SQLException e){}
    }

    @Override
    public void execSQL(String request) throws SQLException{
        statement.execute(request);
    }

    @Override
    public void updateSQL(String request) throws SQLException {
        System.out.println(request);
        connection.setAutoCommit(false);
        statement.executeUpdate(request);
        connection.setAutoCommit(true);

    }

    @Override
    public ResultSet selectstmt(String request) throws SQLException {
        return statement.executeQuery(request);
    }
}
