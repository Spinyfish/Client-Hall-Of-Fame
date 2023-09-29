package ai.production.backend.messageHandler.container;

public enum MessageCommand {
    SEXO("SEXO"),
    REQUEST_UID("REQUEST_UID"),
    REQUEST_UID_RESPONSE("REQUEST_UID_RESPONSE"),

    REQUEST_CLIENT_ENUM("REQUEST_CLIENT_ENUM"),

    REQUEST_CLIENT_ENUM_RESPONSE("REQUEST_CLIENT_ENUM_RESPONSE"),

    IRC_MESSAGE("IRC_MESSAGE");

    private String stringCommand;

    MessageCommand(String stringCommand){
        this.stringCommand = stringCommand;
    }

    public String getStringCommand() {
        return stringCommand;
    }
}
