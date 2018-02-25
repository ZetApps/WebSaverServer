package interfaces;


import org.sqlite.SQLiteConnection;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface DataBaseService {

    default SQLiteConnection makeConnection(String className, String dbconstring) throws SQLException, ClassNotFoundException {
        Class.forName(className);
        System.out.println("Сервер:Подключение к базе данных установлено!");
        return (SQLiteConnection) DriverManager.getConnection(dbconstring);
    }

    void stopConnection() throws SQLException;
    void execSQL(String request)throws SQLException;
    void updateSQL(String request) throws SQLException;
    ResultSet selectstmt(String request) throws SQLException;

}
