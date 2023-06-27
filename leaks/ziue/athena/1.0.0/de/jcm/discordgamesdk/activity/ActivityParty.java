package de.jcm.discordgamesdk.activity;

public class ActivityParty
{
    private final long pointer;
    private final ActivityPartySize size;
    
    ActivityParty(final long pointer) {
        this.pointer = pointer;
        this.size = new ActivityPartySize(this.getSize(pointer));
    }
    
    public void setID(final String id) {
        if (id.getBytes().length >= 128) {
            throw new IllegalArgumentException("max length is 127");
        }
        this.setID(this.pointer, id);
    }
    
    public String getID() {
        return this.getID(this.pointer);
    }
    
    public ActivityPartySize size() {
        return this.size;
    }
    
    private native void setID(final long p0, final String p1);
    
    private native String getID(final long p0);
    
    private native long getSize(final long p0);
}
