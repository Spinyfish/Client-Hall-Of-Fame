/*
 * Decompiled with CFR 0.152.
 */
package fr.litarvan.openauth.model.request;

public class InvalidateRequest {
    private String accessToken;
    private String clientToken;

    public InvalidateRequest(String accessToken, String clientToken) {
        this.accessToken = accessToken;
        this.clientToken = clientToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setClientToken(String clientToken) {
        this.clientToken = clientToken;
    }

    public String getClientToken() {
        return this.clientToken;
    }
}

