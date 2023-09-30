/*
 * Decompiled with CFR 0.152.
 */
package fr.litarvan.openauth.microsoft.model.response;

public class MinecraftLoginResponse {
    private final String username;
    private final String access_token;
    private final String token_type;
    private final long expires_in;

    public MinecraftLoginResponse(String username, String access_token, String token_type, long expires_in) {
        this.username = username;
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
    }

    public String getUsername() {
        return this.username;
    }

    public String getAccessToken() {
        return this.access_token;
    }

    public String getTokenType() {
        return this.token_type;
    }

    public long getExpiresIn() {
        return this.expires_in;
    }
}

