package net.minecraft.scoreboard;

import java.util.*;
import com.google.common.collect.*;

public abstract class Team
{
    public boolean isSameTeam(final Team other) {
        return other != null && this == other;
    }
    
    public abstract String getRegisteredName();
    
    public abstract String formatString(final String p0);
    
    public abstract boolean getSeeFriendlyInvisiblesEnabled();
    
    public abstract boolean getAllowFriendlyFire();
    
    public abstract EnumVisible getNameTagVisibility();
    
    public abstract Collection<String> getMembershipCollection();
    
    public abstract EnumVisible getDeathMessageVisibility();
    
    public enum EnumVisible
    {
        ALWAYS("always", 0), 
        NEVER("never", 1), 
        HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2), 
        HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);
        
        private static Map<String, EnumVisible> field_178828_g;
        public final String field_178830_e;
        public final int field_178827_f;
        
        public static String[] func_178825_a() {
            return EnumVisible.field_178828_g.keySet().toArray(new String[EnumVisible.field_178828_g.size()]);
        }
        
        public static EnumVisible func_178824_a(final String p_178824_0_) {
            return EnumVisible.field_178828_g.get(p_178824_0_);
        }
        
        private EnumVisible(final String p_i45550_3_, final int p_i45550_4_) {
            this.field_178830_e = p_i45550_3_;
            this.field_178827_f = p_i45550_4_;
        }
        
        static {
            EnumVisible.field_178828_g = (Map<String, EnumVisible>)Maps.newHashMap();
            for (final EnumVisible team$enumvisible : values()) {
                EnumVisible.field_178828_g.put(team$enumvisible.field_178830_e, team$enumvisible);
            }
        }
    }
}
