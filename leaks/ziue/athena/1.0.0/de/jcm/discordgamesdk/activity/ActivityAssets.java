package de.jcm.discordgamesdk.activity;

public class ActivityAssets
{
    private final long pointer;
    
    ActivityAssets(final long pointer) {
        this.pointer = pointer;
    }
    
    public void setLargeImage(final String assetKey) {
        if (assetKey.getBytes().length >= 128) {
            throw new IllegalArgumentException("max length is 127");
        }
        this.setLargeImage(this.pointer, assetKey);
    }
    
    public String getLargeImage() {
        return this.getLargeImage(this.pointer);
    }
    
    public void setLargeText(final String text) {
        if (text.getBytes().length >= 128) {
            throw new IllegalArgumentException("max length is 127");
        }
        this.setLargeText(this.pointer, text);
    }
    
    public String getLargeText() {
        return this.getLargeText(this.pointer);
    }
    
    public void setSmallImage(final String assetKey) {
        if (assetKey.getBytes().length >= 128) {
            throw new IllegalArgumentException("max length is 127");
        }
        this.setSmallImage(this.pointer, assetKey);
    }
    
    public String getSmallImage() {
        return this.getSmallImage(this.pointer);
    }
    
    public void setSmallText(final String text) {
        if (text.getBytes().length >= 128) {
            throw new IllegalArgumentException("max length is 127");
        }
        this.setSmallText(this.pointer, text);
    }
    
    public String getSmallText() {
        return this.getSmallText(this.pointer);
    }
    
    private native void setLargeImage(final long p0, final String p1);
    
    private native String getLargeImage(final long p0);
    
    private native void setLargeText(final long p0, final String p1);
    
    private native String getLargeText(final long p0);
    
    private native void setSmallImage(final long p0, final String p1);
    
    private native String getSmallImage(final long p0);
    
    private native void setSmallText(final long p0, final String p1);
    
    private native String getSmallText(final long p0);
}
