package core;

import interfaces.AuthAbility;
import userwork.ClientEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Authorisation implements AuthAbility {
    private Server server;
    private DataBaseWork dbwork;

    public Authorisation(Server server) {
        this.server = server;
        dbwork = server.getDataBase();
    }

    @Override
    public ClientEntity authorisation(String login, String pass) {
        try {
            System.out.println("SELECT * FROM USERS WHERE login = '" + login + "' and pass = '" + pass + "'");
            ResultSet rs = dbwork.selectstmt("SELECT * FROM USERS WHERE login = '" + login + "' and pass = '" + pass + "'");
            if (rs.next()){
                System.out.println("Все прошло хорошо!");
                return new ClientEntity(login, pass, rs.getString("nick"),rs.getInt("UID"));
            }else{
                System.out.println("Авторизация прошла неудачно...");
                return null;
            }
        }catch (SQLException e) {
            System.err.println(e.getStackTrace());
            return null;
        }
    }

    @Override
    public boolean registration(String login, String pass, String email, String nick) {
        try{
            dbwork.updateSQL("INSERT INTO USERS(login,pass,email,nick) VALUES('"+login+"','"+pass+"','"+email+"','"+nick+"')");
            System.out.println("УСПЕШНО!");
            return true;
        }
        catch (SQLException e){
            System.out.println("НЕ УСПЕШНО(");
            return false;
        }
    }

    @Override
    public void unauthorisation() {

    }

    public DataBaseWork getDbwork() {
        return dbwork;
    }
}
