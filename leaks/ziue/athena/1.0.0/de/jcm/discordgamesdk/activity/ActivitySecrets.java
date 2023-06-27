package de.jcm.discordgamesdk.activity;

public class ActivitySecrets
{
    private final long pointer;
    
    ActivitySecrets(final long pointer) {
        this.pointer = pointer;
    }
    
    public void setMatchSecret(final String secret) {
        if (secret.getBytes().length >= 128) {
            throw new IllegalArgumentException("max length is 128");
        }
        this.setMatchSecret(this.pointer, secret);
    }
    
    public String getMatchSecret() {
        return this.getMatchSecret(this.pointer);
    }
    
    public void setJoinSecret(final String secret) {
        if (secret.getBytes().length >= 128) {
            throw new IllegalArgumentException("max length is 128");
        }
        this.setJoinSecret(this.pointer, secret);
    }
    
    public String getJoinSecret() {
        return this.getJoinSecret(this.pointer);
    }
    
    public void setSpectateSecret(final String secret) {
        if (secret.getBytes().length >= 128) {
            throw new IllegalArgumentException("max length is 128");
        }
        this.setSpectateSecret(this.pointer, secret);
    }
    
    public String getSpectateSecret() {
        return this.getSpectateSecret(this.pointer);
    }
    
    private native void setMatchSecret(final long p0, final String p1);
    
    private native String getMatchSecret(final long p0);
    
    private native void setJoinSecret(final long p0, final String p1);
    
    private native String getJoinSecret(final long p0);
    
    private native void setSpectateSecret(final long p0, final String p1);
    
    private native String getSpectateSecret(final long p0);
}
