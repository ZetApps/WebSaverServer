package interfaces;

import core.Authorisation;
import core.DataBaseWork;
import userwork.ClientHandler;

public interface ServerService{
    Authorisation getAuthorisationClass();
    DataBaseWork getDataBase();
    void subscribe(ClientHandler o);
    void unsubscribe(ClientHandler o);
}
