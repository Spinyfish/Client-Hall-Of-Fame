package ai.production.backend.messageHandler.container;

public enum MessageDirection {
    SERVER_TO_CLIENT("SERVER_TO_CLIENT"),
    CLIENT_TO_SERVER("CLIENT_TO_SERVER");

    private String stringDirection;

    MessageDirection(String stringDirection){
        this.stringDirection = stringDirection;
    }

    public String getStringDirection() {
        return stringDirection;
    }
}
