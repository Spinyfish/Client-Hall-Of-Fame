package fr.litarvan.openauth.microsoft.model.request;

public class XboxLiveLoginProperties
{
    private final String AuthMethod;
    private final String SiteName;
    private final String RpsTicket;
    
    public XboxLiveLoginProperties(final String AuthMethod, final String SiteName, final String RpsTicket) {
        this.AuthMethod = AuthMethod;
        this.SiteName = SiteName;
        this.RpsTicket = RpsTicket;
    }
    
    public String getAuthMethod() {
        return this.AuthMethod;
    }
    
    public String getSiteName() {
        return this.SiteName;
    }
    
    public String getRpsTicket() {
        return this.RpsTicket;
    }
}
