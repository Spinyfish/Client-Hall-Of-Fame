package fr.litarvan.openauth.model.request;

import fr.litarvan.openauth.model.*;

public class AuthRequest
{
    private AuthAgent agent;
    private String username;
    private String password;
    private String clientToken;
    
    public AuthRequest(final AuthAgent agent, final String username, final String password, final String clientToken) {
        this.agent = agent;
        this.username = username;
        this.password = password;
        this.clientToken = clientToken;
    }
    
    public void setAgent(final AuthAgent agent) {
        this.agent = agent;
    }
    
    public AuthAgent getAgent() {
        return this.agent;
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
    
    public void setClientToken(final String clientToken) {
        this.clientToken = clientToken;
    }
    
    public String getClientToken() {
        return this.clientToken;
    }
}
