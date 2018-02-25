package userwork;

public class ClientEntity {
    private String login;
    private String pass;
    private String nick;
    private int dbid;

    public ClientEntity(String login, String pass, int dbid) {
        this.login = login;
        this.pass = pass;
        this.dbid = dbid;
    }

    public ClientEntity(String login, String pass, String nick, int dbid) {
        this.login = login;
        this.pass = pass;
        this.nick = nick;
        this.dbid = dbid;
    }

    public ClientEntity(String login, String pass, String nick) {
        this.login = login;
        this.pass = pass;
        this.nick = nick;
    }

    @Override
    public String toString() {
        return login + ","+pass+","+nick;
    }

    public String getLogin(){return nick;}
}
