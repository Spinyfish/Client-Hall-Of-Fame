/*
 * Decompiled with CFR 0.152.
 */
package fr.litarvan.openauth.microsoft.model.response;

public class MicrosoftRefreshResponse {
    private final String token_type;
    private final long expires_in;
    private final String scope;
    private final String access_token;
    private final String refresh_token;
    private final String user_id;

    public MicrosoftRefreshResponse(String token_type, long expires_in, String scope, String access_token, String refresh_token, String user_id) {
        this.token_type = token_type;
        this.expires_in = expires_in;
        this.scope = scope;
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.user_id = user_id;
    }

    public String getTokenType() {
        return this.token_type;
    }

    public long getExpiresIn() {
        return this.expires_in;
    }

    public String getScope() {
        return this.scope;
    }

    public String getAccessToken() {
        return this.access_token;
    }

    public String getRefreshToken() {
        return this.refresh_token;
    }

    public String getUserId() {
        return this.user_id;
    }
}

