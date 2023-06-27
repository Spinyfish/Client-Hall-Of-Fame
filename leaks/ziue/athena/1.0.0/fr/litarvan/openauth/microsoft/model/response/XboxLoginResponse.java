package fr.litarvan.openauth.microsoft.model.response;

public class XboxLoginResponse
{
    private final String IssueInstant;
    private final String NotAfter;
    private final String Token;
    private final XboxLiveLoginResponseClaims DisplayClaims;
    
    public XboxLoginResponse(final String IssueInstant, final String NotAfter, final String Token, final XboxLiveLoginResponseClaims DisplayClaims) {
        this.IssueInstant = IssueInstant;
        this.NotAfter = NotAfter;
        this.Token = Token;
        this.DisplayClaims = DisplayClaims;
    }
    
    public String getIssueInstant() {
        return this.IssueInstant;
    }
    
    public String getNotAfter() {
        return this.NotAfter;
    }
    
    public String getToken() {
        return this.Token;
    }
    
    public XboxLiveLoginResponseClaims getDisplayClaims() {
        return this.DisplayClaims;
    }
    
    public static class XboxLiveLoginResponseClaims
    {
        private final XboxLiveUserInfo[] xui;
        
        public XboxLiveLoginResponseClaims(final XboxLiveUserInfo[] xui) {
            this.xui = xui;
        }
        
        public XboxLiveUserInfo[] getUsers() {
            return this.xui;
        }
    }
    
    public static class XboxLiveUserInfo
    {
        private final String uhs;
        
        public XboxLiveUserInfo(final String uhs) {
            this.uhs = uhs;
        }
        
        public String getUserHash() {
            return this.uhs;
        }
    }
}
