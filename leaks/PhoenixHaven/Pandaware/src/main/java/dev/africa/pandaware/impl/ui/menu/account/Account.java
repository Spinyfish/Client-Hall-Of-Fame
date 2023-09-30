package dev.africa.pandaware.impl.ui.menu.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class Account {
    private String email, username, password, refreshToken, uuid;
    private boolean cracked, loggedIn, microsoft;

    private final Map<String, Long> bannedServers = new LinkedHashMap<>();
}