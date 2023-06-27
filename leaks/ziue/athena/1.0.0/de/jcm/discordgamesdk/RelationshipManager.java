package de.jcm.discordgamesdk;

import java.util.function.*;
import java.util.*;
import de.jcm.discordgamesdk.activity.*;
import de.jcm.discordgamesdk.user.*;

public class RelationshipManager
{
    public static final Predicate<Relationship> NO_FILTER;
    public static final Predicate<Relationship> FRIEND_FILTER;
    public static final Predicate<Relationship> ONLINE_FILTER;
    public static final Predicate<Relationship> OFFLINE_FILTER;
    public static final Predicate<Relationship> SPECIAL_FILTER;
    private final long pointer;
    private final Core core;
    
    RelationshipManager(final long pointer, final Core core) {
        this.pointer = pointer;
        this.core = core;
    }
    
    public Relationship getWith(final long userId) {
        final Object ret = this.core.execute(() -> this.get(this.pointer, userId));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (Relationship)ret;
    }
    
    public void filter(final Predicate<Relationship> filter) {
        this.core.execute(() -> this.filter(this.pointer, Objects.requireNonNull(filter)));
    }
    
    public int count() {
        final Object ret = this.core.execute(() -> this.count(this.pointer));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (int)ret;
    }
    
    public Relationship getAt(final int index) {
        final Object ret = this.core.execute(() -> this.getAt(this.pointer, index));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (Relationship)ret;
    }
    
    public List<Relationship> asList() {
        final int count = this.count();
        final Relationship[] relationships = new Relationship[count];
        for (int i = 0; i < relationships.length; ++i) {
            relationships[i] = this.getAt(i);
        }
        return Arrays.asList(relationships);
    }
    
    private native void filter(final long p0, final Predicate<Relationship> p1);
    
    private native Object count(final long p0);
    
    private native Object get(final long p0, final long p1);
    
    private native Object getAt(final long p0, final int p1);
    
    static {
        NO_FILTER = (r -> true);
        FRIEND_FILTER = (r -> r.getType() == RelationshipType.FRIEND);
        ONLINE_FILTER = (r -> r.getPresence().getStatus() == OnlineStatus.ONLINE);
        OFFLINE_FILTER = (r -> r.getPresence().getStatus() == OnlineStatus.OFFLINE);
        SPECIAL_FILTER = (r -> (r.getPresence().getActivity().getType() == ActivityType.PLAYING && r.getPresence().getActivity().getApplicationId() != 0L) || r.getPresence().getActivity().getType() != ActivityType.PLAYING);
    }
}
