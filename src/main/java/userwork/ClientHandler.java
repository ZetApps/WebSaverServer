package userwork;



import core.Authorisation;
import core.Server;
import ru.zetapps.filework.FileWorkMain;
import ru.zetapps.websavermessages.MessageType;
import ru.zetapps.websavermessages.Messages.*;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.rmi.server.ExportException;
import java.sql.SQLException;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private ClientEntity client = null;
    private Authorisation authorisation;
    private MessageHandler messageHandler;
    private boolean waitcmd = true;
    private ObjectInputStream in;
    private FileWorkMain fw;


    private void waitMessage() throws IOException {
        authorisation = server.getAuthorisationClass();
        messageHandler = new MessageHandler(socket);
        while(waitcmd){
            //in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

            try {
                //Object msg = in.readObject();
                Object msg = messageHandler.getMessage();
                if (AuthMessage.class == msg.getClass()){
                    System.out.println("Авторизация клиента");
                    client = authmessageAction((AuthMessage) msg);
                    if (!(client == null)){
                        messageHandler.sendMessage(new AnswerMessage(MessageType.AUTHORISATION,"good"));
                        System.out.println(client.toString());
                    }else{
                        messageHandler.sendMessage(new AnswerMessage(MessageType.AUTHORISATION,"bad"));
                        System.out.println("Ошибка авторизации((");
                    }
                }else if(CommandMessage.class == msg.getClass()){
                    commandmessageAction((CommandMessage)msg);
                    System.out.println("Команда клиента");

                }else if (FileMessage.class == msg.getClass()){
                    filemessageAction((FileMessage) msg);
                    System.out.println("Файл клиента");
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        fw = new FileWorkMain("I:/CourceWork/ServerPath");
        try {
            waitMessage();
        }catch (IOException e){
        }
    }

    private ClientEntity authmessageAction(AuthMessage msg){
        String login="",pass="",nick="",email="";
        System.out.println(msg.getContent());
        if(msg.getType() == MessageType.REGISTRATION) {
            System.out.println("Регистрируем...");
            login = msg.getContent().split(",")[0];
            pass = msg.getContent().split(",")[1];
            nick = msg.getContent().split(",")[2];
            email = msg.getContent().split(",")[3];
            boolean res = authorisation.registration(login, pass, email, nick);
            try {
                if (res){
                    messageHandler.sendMessage(new AnswerMessage(MessageType.REGISTRATION,"good"));
                    fw.directories.makeUserDir(login);
                }else{
                    messageHandler.sendMessage(new AnswerMessage(MessageType.REGISTRATION,"bad"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }else if(msg.getType()==MessageType.AUTHORISATION){
            System.out.println("Авторизуем...");
            login = msg.getContent().split(",")[0];
            pass = msg.getContent().split(",")[1];
            return authorisation.authorisation(login,pass);
        }
        return null;
    }

    private void filemessageAction(FileMessage msg){

        if (msg.getType() == MessageType.UPLOAD) {
            boolean msgres = fw.files.saveFile(msg.getContent(),client.getLogin()+"\\"+msg.getFilepath(),msg.getName());
            try {
                if (msgres) {
                    messageHandler.sendMessage(new AnswerMessage(MessageType.UPLOAD, "good"));
                    authorisation.getDbwork().updateSQL("INSERT INTO fileinfo(ULogin, filename, filepath, filesize, lastchange) " +
                                                                "Values('" + client.getLogin() + "','" + msg.getName() + "','" + msg.getFilepath() + "', " +
                                                                 msg.getSize() + "," + msg.getLastchange() + ")");
                } else {
                    messageHandler.sendMessage(new AnswerMessage(MessageType.UPLOAD, "bad"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

    private void commandmessageAction(CommandMessage msg){
        boolean res = false;
        if (msg.getType()==MessageType.FOLDER_CREATE){
             res = fw.directories.makeDir(msg.getContent());
        }
        if (msg.getType() == MessageType.FOLDER_DELETE){
            File fl = new File(msg.getContent());
            res = fw.directories.delete(msg.getContent());
            try{
                authorisation.getDbwork().updateSQL("DELETE FROM fileinfo WHERE filepath='" + fl.getName() + "' and ULogin='" + client.getLogin() + "'");
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        if (msg.getType() == MessageType.FILE_RENAME){
            System.out.println("Переименование файла");
            String oldname = msg.getContent().split(";")[0];
            String newname = msg.getContent().split(";")[1];
            res = fw.files.rename(oldname,newname);
            try {
                authorisation.getDbwork().updateSQL("UPDATE fileinfo SET filename='" + newname + "' WHERE filename='" + oldname + "' and ULogin='" + client.getLogin() + "'");
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

        if (msg.getType() == MessageType.FILE_DELETE){
            File fl = new File(msg.getContent());
            res = fw.files.delete(msg.getContent());
            try{
                authorisation.getDbwork().updateSQL("DELETE FROM fileinfo WHERE filename='" + fl.getName() + "' and ULogin='" + client.getLogin() + "'");
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

        if (msg.getType() == MessageType.SYNC_GET){
            System.out.println("Получил сообщение о синхронизации!!!");
            try {
                messageHandler.sendMessage(new AnswerMessage(msg.getType(),fw.getDataList(msg.getContent(),client.getLogin())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (msg.getType() == MessageType.DOWNLOAD){
            try {
                File file = new File(fw.files.getAbsPath(msg.getContent()));
                messageHandler.sendMessage(new FileMessage(MessageType.DOWNLOAD,file.getPath(),fw.files.getCloudPath(file.getParent(),client.getLogin()),file.getName(),file.length(),file.lastModified()));
            }catch (IOException e){

            }
        }


        try {
            if (msg.getType() != MessageType.SYNC_GET && msg.getType() != MessageType.DOWNLOAD) {
                if (res) {
                    messageHandler.sendMessage(new AnswerMessage(msg.getType(), "good"));
                } else {
                    if (!res) {
                        messageHandler.sendMessage(new AnswerMessage(msg.getType(), "bad"));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
