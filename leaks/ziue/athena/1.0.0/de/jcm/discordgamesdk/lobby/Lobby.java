package de.jcm.discordgamesdk.lobby;

public class Lobby
{
    private final long id;
    private final LobbyType type;
    private final long ownerId;
    private final String secret;
    private final int capacity;
    private final boolean locked;
    
    public Lobby(final long id, final int type, final long ownerId, final String secret, final int capacity, final boolean locked) {
        this.id = id;
        this.type = LobbyType.values()[type - 1];
        this.ownerId = ownerId;
        this.secret = secret;
        this.capacity = capacity;
        this.locked = locked;
        if (this.secret.getBytes().length >= 128) {
            throw new IllegalArgumentException("max secret length is 127");
        }
    }
    
    public long getId() {
        return this.id;
    }
    
    public LobbyType getType() {
        return this.type;
    }
    
    public long getOwnerId() {
        return this.ownerId;
    }
    
    public String getSecret() {
        return this.secret;
    }
    
    public int getCapacity() {
        return this.capacity;
    }
    
    public boolean isLocked() {
        return this.locked;
    }
    
    @Override
    public String toString() {
        return "Lobby{id=" + this.id + ", type=" + this.type + ", ownerId=" + this.ownerId + ", secret='" + this.secret + '\'' + ", capacity=" + this.capacity + ", locked=" + this.locked + '}';
    }
}
