package de.jcm.discordgamesdk.activity;

public enum ActivityActionType
{
    JOIN, 
    SPECTATE;
    
    private static final int OFFSET = 1;
    
    public int nativeValue() {
        return this.ordinal() + 1;
    }
}
