package club.pulsive.altmanager;

public class Alt {
    String username;
    String password;
    long creationdate;

    public Alt(String username, String password) {
        this(username, password, System.currentTimeMillis());
    }

    public Alt(String username, String password, long creationdate) {
        this.username = username;
        this.password = password;
        this.creationdate = creationdate;
    }

    public boolean isCracked() {
        return password.isEmpty();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getCreationdate() {
        return creationdate;
    }

    public void setCreationdate(long creationdate) {
        this.creationdate = creationdate;
    }
}
