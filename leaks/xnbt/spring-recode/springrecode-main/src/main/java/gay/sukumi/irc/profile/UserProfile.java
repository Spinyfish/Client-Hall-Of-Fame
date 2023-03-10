package gay.sukumi.irc.profile;

public class UserProfile {

    private String username;
    private String uuid;
    private String mcUsername;
    private String currentServer;
    private Rank rank;
    private Client client;

    public UserProfile() {
    }

    public UserProfile(String username, String uuid, String mcUsername, String currentServer, Rank rank, Client client) {
        this.username = username;
        this.uuid = uuid;
        this.mcUsername = mcUsername;
        this.currentServer = currentServer;
        this.rank = rank;
        this.client = client;
    }

    public String getUsername() {
        return username;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMcUsername() {
        return mcUsername;
    }

    public void setMcUsername(String mcUsername) {
        this.mcUsername = mcUsername;
    }

    public String getCurrentServer() {
        return currentServer;
    }

    public void setCurrentServer(String currentServer) {
        this.currentServer = currentServer;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public enum Rank {
        ADMIN, USER
    }

    public enum Client {
        SUKUMI, SPRING
    }

}
