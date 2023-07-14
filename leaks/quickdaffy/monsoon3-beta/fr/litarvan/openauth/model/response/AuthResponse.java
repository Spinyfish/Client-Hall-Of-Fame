/*
 * Decompiled with CFR 0.152.
 */
package fr.litarvan.openauth.model.response;

import fr.litarvan.openauth.model.AuthProfile;

public class AuthResponse {
    private String accessToken;
    private String clientToken;
    private AuthProfile[] availableProfiles;
    private AuthProfile selectedProfile;

    public AuthResponse(String accessToken, String clientToken, AuthProfile[] availableProfiles, AuthProfile selectedProfile) {
        this.accessToken = accessToken;
        this.clientToken = clientToken;
        this.availableProfiles = availableProfiles;
        this.selectedProfile = selectedProfile;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getClientToken() {
        return this.clientToken;
    }

    public AuthProfile[] getAvailableProfiles() {
        return this.availableProfiles;
    }

    public AuthProfile getSelectedProfile() {
        return this.selectedProfile;
    }
}

