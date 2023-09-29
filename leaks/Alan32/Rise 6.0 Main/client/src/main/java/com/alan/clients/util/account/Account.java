package com.alan.clients.util.account;

import com.alan.clients.util.SkinUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Account {

    private final String email;
    private final String password;
    private String username;
    private String uuid;
    private String refreshToken;

    public Account(final String email, final String password) {
        this.email = email;
        this.password = password;
        this.username = SkinUtil.uuidOf("Steve");
    }

    public Account(String email, String password, String username, String uuid, String refreshToken) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.uuid = uuid;
        this.refreshToken = refreshToken;
    }

    public void setUsername(String username) {
        this.username = username;
        this.uuid = SkinUtil.uuidOf(username);
    }
}
