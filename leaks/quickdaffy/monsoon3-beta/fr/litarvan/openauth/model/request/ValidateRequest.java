/*
 * Decompiled with CFR 0.152.
 */
package fr.litarvan.openauth.model.request;

public class ValidateRequest {
    private String accessToken;

    public ValidateRequest(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return this.accessToken;
    }
}

