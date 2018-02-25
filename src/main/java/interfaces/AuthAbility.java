package interfaces;

import userwork.ClientEntity;

public interface AuthAbility {
    ClientEntity authorisation(String login, String pass);
    boolean registration(String login, String pass, String email, String nick);
    void unauthorisation();


}
