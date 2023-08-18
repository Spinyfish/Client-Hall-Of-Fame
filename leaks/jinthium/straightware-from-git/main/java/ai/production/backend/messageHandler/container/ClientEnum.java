package ai.production.backend.messageHandler.container;

public enum ClientEnum {
    GLOBAL(0),
    WEEDXVOIDED(1),
    STRAIGHTWARE(2);


    private int clientID;

    ClientEnum(int clientID){
        this.clientID = clientID;
    }

    public int getClientID() {
        return clientID;
    }

    public static ClientEnum getClientEnum(int clientID){
        for(ClientEnum clientEnum : ClientEnum.values()){
            if(clientEnum.getClientID() == clientID){
                return clientEnum;
            }
        }
        return null;
    }
}
