package fr.litarvan.openauth.model.request;

public class RefreshRequest
{
    private String accessToken;
    private String clientToken;
    
    public RefreshRequest(final String accessToken, final String clientToken) {
        this.accessToken = accessToken;
        this.clientToken = clientToken;
    }
    
    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getAccessToken() {
        return this.accessToken;
    }
    
    public void setClientToken(final String clientToken) {
        this.clientToken = clientToken;
    }
    
    public String getClientToken() {
        return this.clientToken;
    }
}
