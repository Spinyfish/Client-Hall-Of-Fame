package me.jinthium.straight.api.irc;

import ai.production.backend.messageHandler.MessageHandler;
import ai.production.backend.messageHandler.container.ClientEnum;
import ai.production.backend.messageHandler.container.MessageCommand;
import ai.production.backend.messageHandler.container.MessageDirection;
import ai.production.backend.messageHandler.container.MessageObject;
import lombok.SneakyThrows;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.utils.ChatUtil;
import me.jinthium.straight.impl.utils.Multithreading;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectedClient implements Runnable {

    private String username;
    private Socket socket;


    private ObjectInputStream objectInputStream;

    private ObjectOutputStream objectOutputStream;


    public static ConnectedClient INSTANCE;

    public ConnectedClient(Socket socket) {
        this.username = Client.INSTANCE.getUser().getUsername();
        this.socket = socket;
        INSTANCE = this;
    }


    public void setUsername(String UID) {
        this.username = UID;
    }


    public String getUsername() {
        return username;
    }

    @SneakyThrows
    public void run() {
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.flush();
        objectInputStream = new ObjectInputStream(socket.getInputStream());

        while (true) {
            Object receivedObject = objectInputStream.readObject();
           // System.out.println("DEBUG: Message was received with command: " + ((MessageObject) receivedObject).getMessageCommand());

            handleMessagesObject(receivedObject);

        }
    }


    @SneakyThrows
    public void handleMessagesObject(Object message) {
        if (message instanceof MessageObject messageObject) {
            if (messageObject.getMessageCommand() == MessageCommand.REQUEST_UID) {
                objectOutputStream.writeObject(MessageHandler.buildMessage(getUsername(), MessageDirection.CLIENT_TO_SERVER, MessageCommand.REQUEST_UID_RESPONSE, getUsername()));
            }

            if(messageObject.getMessageCommand() == MessageCommand.REQUEST_CLIENT_ENUM) {
               objectOutputStream.writeObject(MessageHandler.buildMessage(getUsername(), MessageDirection.CLIENT_TO_SERVER, MessageCommand.REQUEST_CLIENT_ENUM_RESPONSE, ClientEnum.STRAIGHTWARE.getClientID()));
            }

            if (messageObject.getMessageCommand() == MessageCommand.IRC_MESSAGE) {
                String dataString = messageObject.getData().toString();
                if (dataString.equals("[-client-updateplayers]")) {
                    Multithreading.runAsync(Client.INSTANCE.getFunnyFuck());
                    return;
                }
                ChatUtil.print(dataString.replace("[", "").replace("]", ""));

            }
        }

    }

    public Socket getSocket() {
        return socket;
    }

    public void sendMessageObject(MessageObject messageObject) {
        try {
            objectOutputStream.writeObject(messageObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
