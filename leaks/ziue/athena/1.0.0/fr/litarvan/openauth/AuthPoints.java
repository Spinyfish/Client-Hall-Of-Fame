package fr.litarvan.openauth;

public class AuthPoints
{
    public static final AuthPoints NORMAL_AUTH_POINTS;
    private String authenticatePoint;
    private String refreshPoint;
    private String validatePoint;
    private String signoutPoint;
    private String invalidatePoint;
    
    public AuthPoints(final String authenticatePoint, final String refreshPoint, final String validatePoint, final String signoutPoint, final String invalidatePoint) {
        this.authenticatePoint = authenticatePoint;
        this.refreshPoint = refreshPoint;
        this.validatePoint = validatePoint;
        this.signoutPoint = signoutPoint;
        this.invalidatePoint = invalidatePoint;
    }
    
    public String getAuthenticatePoint() {
        return this.authenticatePoint;
    }
    
    public String getRefreshPoint() {
        return this.refreshPoint;
    }
    
    public String getValidatePoint() {
        return this.validatePoint;
    }
    
    public String getSignoutPoint() {
        return this.signoutPoint;
    }
    
    public String getInvalidatePoint() {
        return this.invalidatePoint;
    }
    
    static {
        NORMAL_AUTH_POINTS = new AuthPoints("authenticate", "refresh", "validate", "signout", "invalidate");
    }
}
