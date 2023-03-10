/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.utils.openauth.microsoft;

import lodomir.dev.utils.openauth.microsoft.model.response.MinecraftProfile;

public class MicrosoftAuthResult {
    private final MinecraftProfile profile;
    private final String accessToken;
    private final String refreshToken;

    public MicrosoftAuthResult(MinecraftProfile profile, String accessToken, String refreshToken) {
        this.profile = profile;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public MinecraftProfile getProfile() {
        return this.profile;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }
}

