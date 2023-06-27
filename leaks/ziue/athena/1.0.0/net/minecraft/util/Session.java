package net.minecraft.util;

import com.mojang.authlib.*;
import com.mojang.util.*;
import java.util.*;
import com.google.common.collect.*;

public class Session
{
    private final String username;
    private final String playerID;
    private final String token;
    private final Type sessionType;
    
    public Session(final String usernameIn, final String playerIDIn, final String tokenIn, final String sessionTypeIn) {
        this.username = usernameIn;
        this.playerID = playerIDIn;
        this.token = tokenIn;
        this.sessionType = Type.setSessionType(sessionTypeIn);
    }
    
    public String getSessionID() {
        return "token:" + this.token + ":" + this.playerID;
    }
    
    public String getPlayerID() {
        return this.playerID;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public String getToken() {
        return this.token;
    }
    
    public GameProfile getProfile() {
        try {
            final UUID uuid = UUIDTypeAdapter.fromString(this.getPlayerID());
            return new GameProfile(uuid, this.getUsername());
        }
        catch (IllegalArgumentException var2) {
            return new GameProfile((UUID)null, this.getUsername());
        }
    }
    
    public Type getSessionType() {
        return this.sessionType;
    }
    
    public enum Type
    {
        LEGACY("legacy"), 
        MOJANG("mojang");
        
        private static final Map<String, Type> SESSION_TYPES;
        private final String sessionType;
        
        private Type(final String sessionTypeIn) {
            this.sessionType = sessionTypeIn;
        }
        
        public static Type setSessionType(final String sessionTypeIn) {
            return Type.SESSION_TYPES.get(sessionTypeIn.toLowerCase());
        }
        
        static {
            SESSION_TYPES = Maps.newHashMap();
            for (final Type session$type : values()) {
                Type.SESSION_TYPES.put(session$type.sessionType, session$type);
            }
        }
    }
}
