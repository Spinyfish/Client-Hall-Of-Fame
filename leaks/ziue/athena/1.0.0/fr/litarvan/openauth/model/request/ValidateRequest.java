package fr.litarvan.openauth.model.request;

public class ValidateRequest
{
    private String accessToken;
    
    public ValidateRequest(final String accessToken) {
        this.accessToken = accessToken;
    }
    
    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getAccessToken() {
        return this.accessToken;
    }
}
