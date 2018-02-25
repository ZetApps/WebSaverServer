package core;

import com.sun.corba.se.internal.corba.ORBSingleton;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import interfaces.ServerService;
import userwork.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;

public class Server implements ServerService{
    private static final int SERVER_PORT = 8080;

    private boolean ServerWorkStatus = true;
    private Authorisation authorisation;
    private ServerSocket serverSocket;
    private Socket socket = null;
    private Vector<ClientHandler> clients;
    private DataBaseWork dbwork;

    private void init() throws IOException, SQLException {
        this.serverSocket = new ServerSocket(SERVER_PORT);
        this.clients = new Vector<>();
        this.dbwork = new DataBaseWork();
        this.authorisation = new Authorisation(this );

        System.out.println("Сервер запущен!");
    }

    private void waitUser() throws IOException{
        while(this.ServerWorkStatus){
            System.out.println("Ожидание клиента!");
            this.socket = serverSocket.accept();
            subscribe(new ClientHandler(this, this.socket));
            System.out.println("Клиент подключился!");
        }
    }

    public Server() {
        try{
            init();
            waitUser();
        }catch (IOException e){}
        catch (SQLException sqle){
            sqle.fillInStackTrace();}
    }

    @Override
    public void subscribe(ClientHandler o){this.clients.add(o);}

    @Override
    public void unsubscribe(ClientHandler o){this.clients.remove(o);}

    @Override
    public Authorisation getAuthorisationClass() {
        return authorisation;
    }

    @Override
    public DataBaseWork getDataBase() {
        return dbwork;
    }
}
