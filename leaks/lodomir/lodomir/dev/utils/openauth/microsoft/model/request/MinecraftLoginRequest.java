/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.utils.openauth.microsoft.model.request;

public class MinecraftLoginRequest {
    private final String identityToken;

    public MinecraftLoginRequest(String identityToken) {
        this.identityToken = identityToken;
    }

    public String getIdentityToken() {
        return this.identityToken;
    }
}

