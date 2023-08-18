package me.jinthium.straight.api.client;




public class User {
    private String username, uid;

    public User(String username, String uid) {
        this.username = username;
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
