package de.jcm.discordgamesdk.user;

import de.jcm.discordgamesdk.activity.*;

public class Relationship
{
    private final RelationshipType type;
    private final DiscordUser user;
    private final Presence presence;
    
    private Relationship(final RelationshipType type, final DiscordUser user, final Presence presence) {
        this.type = type;
        this.user = user;
        this.presence = presence;
    }
    
    public RelationshipType getType() {
        return this.type;
    }
    
    public DiscordUser getUser() {
        return this.user;
    }
    
    public Presence getPresence() {
        return this.presence;
    }
    
    @Override
    public String toString() {
        return "Relationship{type=" + this.type + ", user=" + this.user + ", presence=" + this.presence + '}';
    }
    
    static Relationship createRelationship(final int type, final DiscordUser user, final int status, final long activity) {
        final RelationshipType type2 = RelationshipType.values()[type];
        final OnlineStatus status2 = OnlineStatus.values()[status];
        final Activity activity2 = new Activity(activity);
        final Presence presence = new Presence(status2, activity2);
        return new Relationship(type2, user, presence);
    }
}
