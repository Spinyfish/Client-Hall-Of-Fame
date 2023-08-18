package ai.production.backend.messageHandler;

import ai.production.backend.messageHandler.container.MessageCommand;
import ai.production.backend.messageHandler.container.MessageDirection;
import ai.production.backend.messageHandler.container.MessageObject;


public class MessageHandler {
    public void handleMessage(String message){
        System.out.println(message);
    }

    public static MessageObject buildMessage(String username, MessageDirection messageDirection, MessageCommand messageCommand, Object... data){
        return new MessageObject(username, messageDirection, messageCommand, data);
    }
}
