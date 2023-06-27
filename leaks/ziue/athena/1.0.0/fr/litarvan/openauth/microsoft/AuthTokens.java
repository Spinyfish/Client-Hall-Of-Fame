package fr.litarvan.openauth.microsoft;

public class AuthTokens
{
    private final String accessToken;
    private final String refreshToken;
    
    public AuthTokens(final String accessToken, final String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
    
    public String getAccessToken() {
        return this.accessToken;
    }
    
    public String getRefreshToken() {
        return this.refreshToken;
    }
}
