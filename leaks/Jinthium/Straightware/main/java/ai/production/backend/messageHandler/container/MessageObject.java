package ai.production.backend.messageHandler.container;


import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class MessageObject implements Serializable {
    private String username;

    private MessageDirection messageDirection;

    private MessageCommand messageCommand;

    private List<Object> data;

    private ClientEnum clientEnum;

    public MessageObject(String username, MessageDirection messageDirection, MessageCommand messageCommand, Object... data) {
        this.username = username;
        this.messageDirection = messageDirection;
        this.messageCommand = messageCommand;
        this.data = (data == null) ? null : Arrays.asList(data);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public MessageDirection getMessageDirection() {
        return messageDirection;
    }

    public void setMessageDirection(MessageDirection messageDirection) {
        this.messageDirection = messageDirection;
    }

    public MessageCommand getMessageCommand() {
        return messageCommand;
    }

    public void setMessageCommand(MessageCommand messageCommand) {
        this.messageCommand = messageCommand;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object... data) {
        this.data = Arrays.asList(data);
    }

    public ClientEnum getClientEnum() {
        return clientEnum;
    }

    public void setClientEnum(ClientEnum clientEnum) {
        this.clientEnum = clientEnum;
    }
}
