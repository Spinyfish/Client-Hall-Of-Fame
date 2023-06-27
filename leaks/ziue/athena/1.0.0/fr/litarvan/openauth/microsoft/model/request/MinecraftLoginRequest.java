package fr.litarvan.openauth.microsoft.model.request;

public class MinecraftLoginRequest
{
    private final String identityToken;
    
    public MinecraftLoginRequest(final String identityToken) {
        this.identityToken = identityToken;
    }
    
    public String getIdentityToken() {
        return this.identityToken;
    }
}
