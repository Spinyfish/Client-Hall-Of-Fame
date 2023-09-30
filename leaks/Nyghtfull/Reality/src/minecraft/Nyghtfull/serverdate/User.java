package Nyghtfull.serverdate;

public class User {
    public String name, password, hwid, rank, gameID;
    public long lastTime;

    public User(String name, String password, String hwid) {
        this.name = name;
        this.password = password;
        this.hwid = hwid;
        this.lastTime = System.currentTimeMillis();
    }
}
