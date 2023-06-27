package net.minecraft.scoreboard;

import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import java.util.*;

public class ScoreHealthCriteria extends ScoreDummyCriteria
{
    public ScoreHealthCriteria(final String name) {
        super(name);
    }
    
    @Override
    public int func_96635_a(final List<EntityPlayer> p_96635_1_) {
        float f = 0.0f;
        for (final EntityPlayer entityplayer : p_96635_1_) {
            f += entityplayer.getHealth() + entityplayer.getAbsorptionAmount();
        }
        if (p_96635_1_.size() > 0) {
            f /= p_96635_1_.size();
        }
        return MathHelper.ceiling_float_int(f);
    }
    
    @Override
    public boolean isReadOnly() {
        return true;
    }
    
    @Override
    public IScoreObjectiveCriteria.EnumRenderType getRenderType() {
        return IScoreObjectiveCriteria.EnumRenderType.HEARTS;
    }
}
