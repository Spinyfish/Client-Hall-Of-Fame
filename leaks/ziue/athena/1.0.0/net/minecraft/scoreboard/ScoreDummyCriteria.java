package net.minecraft.scoreboard;

import java.util.*;
import net.minecraft.entity.player.*;

public class ScoreDummyCriteria implements IScoreObjectiveCriteria
{
    private final String dummyName;
    
    public ScoreDummyCriteria(final String name) {
        this.dummyName = name;
        IScoreObjectiveCriteria.INSTANCES.put(name, this);
    }
    
    @Override
    public String getName() {
        return this.dummyName;
    }
    
    @Override
    public int func_96635_a(final List<EntityPlayer> p_96635_1_) {
        return 0;
    }
    
    @Override
    public boolean isReadOnly() {
        return false;
    }
    
    @Override
    public EnumRenderType getRenderType() {
        return EnumRenderType.INTEGER;
    }
}
