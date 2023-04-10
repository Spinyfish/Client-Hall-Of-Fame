// 
// Decompiled by Procyon v0.5.36
// 

package com.thealtening;

import com.thealtening.domain.Account;

import java.io.IOException;
import java.net.URLConnection;

import com.thealtening.utils.Utilities;

import java.net.URL;

import com.thealtening.domain.User;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

public final class TheAltening {
    private final String apiKey;
    private final String website = "http://api.thealtening.com/v1/";
    private final Gson gson;

    public TheAltening(final String apiKey) {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.apiKey = apiKey;
    }

    public User getUser() throws IOException {
        final StringBuilder sb = new StringBuilder();
        this.getClass();
        final URLConnection licenseEndpoint = new URL(this.attach(sb.append("http://api.thealtening.com/v1/").append("license").toString())).openConnection();
        final String userInfo = new String(Utilities.getInstance().readAllBytes(licenseEndpoint.getInputStream()));
        return (User) this.gson.fromJson(userInfo, (Class) User.class);
    }

    public Account generateAccount(final User user) throws IOException {
        final StringBuilder sb = new StringBuilder();
        this.getClass();
        final URLConnection generateEndpoint = new URL(this.attach(sb.append("http://api.thealtening.com/v1/").append("generate").toString())).openConnection();
        final String accountInfo = new String(Utilities.getInstance().readAllBytes(generateEndpoint.getInputStream()));
        if (user.isPremium()) {
            return (Account) this.gson.fromJson(accountInfo, (Class) Account.class);
        }
        return null;
    }

    public boolean favoriteAccount(final Account account) throws IOException {
        final StringBuilder sb = new StringBuilder();
        this.getClass();
        final URLConnection favoriteAccount = new URL(this.attachAccount(sb.append("http://api.thealtening.com/v1/").append("favorite").toString(), account)).openConnection();
        final String info = new String(Utilities.getInstance().readAllBytes(favoriteAccount.getInputStream()));
        return info.isEmpty();
    }

    public boolean privateAccount(final Account account) throws IOException {
        final StringBuilder sb = new StringBuilder();
        this.getClass();
        final URLConnection privateAccount = new URL(this.attachAccount(sb.append("http://api.thealtening.com/v1/").append("private").toString(), account)).openConnection();
        final String info = new String(Utilities.getInstance().readAllBytes(privateAccount.getInputStream()));
        return info.isEmpty();
    }

    private String attach(final String website) {
        return website + "?token=" + this.apiKey;
    }

    private String attachAccount(final String website, final Account account) {
        return website + "?token=" + this.apiKey + "&acctoken=" + account.getToken();
    }
}
