package net.minecraft.scoreboard;

import java.util.*;
import net.minecraft.entity.player.*;
import com.google.common.collect.*;
import net.minecraft.util.*;

public interface IScoreObjectiveCriteria
{
    public static final Map<String, IScoreObjectiveCriteria> INSTANCES = Maps.newHashMap();
    public static final IScoreObjectiveCriteria DUMMY = new ScoreDummyCriteria("dummy");
    public static final IScoreObjectiveCriteria TRIGGER = new ScoreDummyCriteria("trigger");
    public static final IScoreObjectiveCriteria deathCount = new ScoreDummyCriteria("deathCount");
    public static final IScoreObjectiveCriteria playerKillCount = new ScoreDummyCriteria("playerKillCount");
    public static final IScoreObjectiveCriteria totalKillCount = new ScoreDummyCriteria("totalKillCount");
    public static final IScoreObjectiveCriteria health = new ScoreHealthCriteria("health");
    public static final IScoreObjectiveCriteria[] field_178792_h = { new GoalColor("teamkill.", EnumChatFormatting.BLACK), new GoalColor("teamkill.", EnumChatFormatting.DARK_BLUE), new GoalColor("teamkill.", EnumChatFormatting.DARK_GREEN), new GoalColor("teamkill.", EnumChatFormatting.DARK_AQUA), new GoalColor("teamkill.", EnumChatFormatting.DARK_RED), new GoalColor("teamkill.", EnumChatFormatting.DARK_PURPLE), new GoalColor("teamkill.", EnumChatFormatting.GOLD), new GoalColor("teamkill.", EnumChatFormatting.GRAY), new GoalColor("teamkill.", EnumChatFormatting.DARK_GRAY), new GoalColor("teamkill.", EnumChatFormatting.BLUE), new GoalColor("teamkill.", EnumChatFormatting.GREEN), new GoalColor("teamkill.", EnumChatFormatting.AQUA), new GoalColor("teamkill.", EnumChatFormatting.RED), new GoalColor("teamkill.", EnumChatFormatting.LIGHT_PURPLE), new GoalColor("teamkill.", EnumChatFormatting.YELLOW), new GoalColor("teamkill.", EnumChatFormatting.WHITE) };
    public static final IScoreObjectiveCriteria[] field_178793_i = { new GoalColor("killedByTeam.", EnumChatFormatting.BLACK), new GoalColor("killedByTeam.", EnumChatFormatting.DARK_BLUE), new GoalColor("killedByTeam.", EnumChatFormatting.DARK_GREEN), new GoalColor("killedByTeam.", EnumChatFormatting.DARK_AQUA), new GoalColor("killedByTeam.", EnumChatFormatting.DARK_RED), new GoalColor("killedByTeam.", EnumChatFormatting.DARK_PURPLE), new GoalColor("killedByTeam.", EnumChatFormatting.GOLD), new GoalColor("killedByTeam.", EnumChatFormatting.GRAY), new GoalColor("killedByTeam.", EnumChatFormatting.DARK_GRAY), new GoalColor("killedByTeam.", EnumChatFormatting.BLUE), new GoalColor("killedByTeam.", EnumChatFormatting.GREEN), new GoalColor("killedByTeam.", EnumChatFormatting.AQUA), new GoalColor("killedByTeam.", EnumChatFormatting.RED), new GoalColor("killedByTeam.", EnumChatFormatting.LIGHT_PURPLE), new GoalColor("killedByTeam.", EnumChatFormatting.YELLOW), new GoalColor("killedByTeam.", EnumChatFormatting.WHITE) };
    
    String getName();
    
    int func_96635_a(final List<EntityPlayer> p0);
    
    boolean isReadOnly();
    
    EnumRenderType getRenderType();
    
    public enum EnumRenderType
    {
        INTEGER("integer"), 
        HEARTS("hearts");
        
        private static final Map<String, EnumRenderType> field_178801_c;
        private final String field_178798_d;
        
        private EnumRenderType(final String p_i45548_3_) {
            this.field_178798_d = p_i45548_3_;
        }
        
        public String func_178796_a() {
            return this.field_178798_d;
        }
        
        public static EnumRenderType func_178795_a(final String p_178795_0_) {
            final EnumRenderType iscoreobjectivecriteria$enumrendertype = EnumRenderType.field_178801_c.get(p_178795_0_);
            return (iscoreobjectivecriteria$enumrendertype == null) ? EnumRenderType.INTEGER : iscoreobjectivecriteria$enumrendertype;
        }
        
        static {
            field_178801_c = Maps.newHashMap();
            for (final EnumRenderType iscoreobjectivecriteria$enumrendertype : values()) {
                EnumRenderType.field_178801_c.put(iscoreobjectivecriteria$enumrendertype.func_178796_a(), iscoreobjectivecriteria$enumrendertype);
            }
        }
    }
}
