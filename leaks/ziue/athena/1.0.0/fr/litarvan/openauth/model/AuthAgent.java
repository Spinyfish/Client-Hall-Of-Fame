package fr.litarvan.openauth.model;

public class AuthAgent
{
    public static final AuthAgent MINECRAFT;
    public static final AuthAgent SCROLLS;
    private String name;
    private int version;
    
    public AuthAgent(final String name, final int version) {
        this.name = name;
        this.version = version;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setVersion(final int version) {
        this.version = version;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    static {
        MINECRAFT = new AuthAgent("Minecraft", 1);
        SCROLLS = new AuthAgent("Scrolls", 1);
    }
}
