package de.jcm.discordgamesdk;

import java.util.function.*;
import java.util.*;
import de.jcm.discordgamesdk.user.*;

public class UserManager
{
    private final long pointer;
    private final Core core;
    public static final int USER_FLAG_PARTNER = 2;
    public static final int USER_FLAG_HYPE_SQUAD_EVENTS = 4;
    public static final int USER_FLAG_HYPE_SQUAD_HOUSE1 = 64;
    public static final int USER_FLAG_HYPE_SQUAD_HOUSE2 = 128;
    public static final int USER_FLAG_HYPE_SQUAD_HOUSE3 = 256;
    
    UserManager(final long pointer, final Core core) {
        this.pointer = pointer;
        this.core = core;
    }
    
    public DiscordUser getCurrentUser() {
        final Object ret = this.core.execute(() -> this.getCurrentUser(this.pointer));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (DiscordUser)ret;
    }
    
    public void getUser(final long userId, final BiConsumer<Result, DiscordUser> callback) {
        this.core.execute(() -> this.getUser(this.pointer, userId, Objects.requireNonNull(callback)));
    }
    
    public PremiumType getCurrentUserPremiumType() {
        final Object ret = this.core.execute(() -> this.getCurrentUserPremiumType(this.pointer));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return PremiumType.values()[(int)ret];
    }
    
    public boolean currentUserHasFlag(final int flag) {
        final Object ret = this.core.execute(() -> this.currentUserHasFlag(this.pointer, flag));
        if (ret instanceof Result) {
            throw new GameSDKException((Result)ret);
        }
        return (boolean)ret;
    }
    
    private native Object getCurrentUser(final long p0);
    
    private native void getUser(final long p0, final long p1, final BiConsumer<Result, DiscordUser> p2);
    
    private native Object getCurrentUserPremiumType(final long p0);
    
    private native Object currentUserHasFlag(final long p0, final int p1);
}
