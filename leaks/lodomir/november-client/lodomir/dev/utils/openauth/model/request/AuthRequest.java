/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.utils.openauth.model.request;

import lodomir.dev.utils.openauth.model.AuthAgent;

public class AuthRequest {
    private AuthAgent agent;
    private String username;
    private String password;
    private String clientToken;

    public AuthRequest(AuthAgent agent, String username, String password, String clientToken) {
        this.agent = agent;
        this.username = username;
        this.password = password;
        this.clientToken = clientToken;
    }

    public void setAgent(AuthAgent agent) {
        this.agent = agent;
    }

    public AuthAgent getAgent() {
        return this.agent;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public void setClientToken(String clientToken) {
        this.clientToken = clientToken;
    }

    public String getClientToken() {
        return this.clientToken;
    }
}

