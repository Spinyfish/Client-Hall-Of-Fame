/*
 * Copyright Felix Hans from Account coded for haze.yt / Lirium . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited
 */

package me.felix.altmanager.data;

import me.felix.altmanager.group.AccountGroup;

import java.util.UUID;

public class Account implements IResponse {
    private String email, password;
    private UUID uuid;
    private final Type type;

    public AccountGroup group;

    public String response;

    public Account moveToGroup(final AccountGroup accountGroup) {
        this.group = accountGroup;
        return this;
    }

    public Account(Type type) {
        this.type = type;
    }

    public Account setupUUID(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public Account setupEmail(String email) {
        this.email = email;
        return this;
    }

    public Account setupPassword(String password) {
        this.password = password;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String response() {
        return response;
    }

    @Override
    public boolean deleteOnError() {
        return false;
    }

    public enum Type {
        ALTENING, MICROSOFT, CRACKED, SHADOW_GEN, MC_LEAKS, EASY_MC
    }

}
