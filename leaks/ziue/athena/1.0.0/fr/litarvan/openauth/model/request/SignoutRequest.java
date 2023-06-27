package fr.litarvan.openauth.model.request;

public class SignoutRequest
{
    private String username;
    private String password;
    
    public SignoutRequest(final String username, final String password) {
        this.username = username;
        this.password = password;
    }
    
    public void setUsername(final String username) {
        this.username = username;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public String getPassword() {
        return this.password;
    }
}
