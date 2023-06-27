package de.jcm.discordgamesdk.user;

import de.jcm.discordgamesdk.activity.*;

public class Presence
{
    private final OnlineStatus status;
    private final Activity activity;
    
    Presence(final OnlineStatus status, final Activity activity) {
        this.status = status;
        this.activity = activity;
    }
    
    public OnlineStatus getStatus() {
        return this.status;
    }
    
    public Activity getActivity() {
        return this.activity;
    }
    
    @Override
    public String toString() {
        return "Presence{status=" + this.status + ", activity=" + this.activity + '}';
    }
}
