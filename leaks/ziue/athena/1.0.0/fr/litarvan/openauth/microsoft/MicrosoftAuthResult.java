package fr.litarvan.openauth.microsoft;

import fr.litarvan.openauth.microsoft.model.response.*;

public class MicrosoftAuthResult
{
    private final MinecraftProfile profile;
    private final String accessToken;
    private final String refreshToken;
    private final String xuid;
    private final String clientId;
    
    public MicrosoftAuthResult(final MinecraftProfile profile, final String accessToken, final String refreshToken, final String xuid, final String clientId) {
        this.profile = profile;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.xuid = xuid;
        this.clientId = clientId;
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
    
    public String getXuid() {
        return this.xuid;
    }
    
    public String getClientId() {
        return this.clientId;
    }
}
