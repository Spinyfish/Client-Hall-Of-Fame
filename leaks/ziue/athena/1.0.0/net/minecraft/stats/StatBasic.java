package net.minecraft.stats;

import net.minecraft.util.*;

public class StatBasic extends StatBase
{
    public StatBasic(final String statIdIn, final IChatComponent statNameIn, final IStatType typeIn) {
        super(statIdIn, statNameIn, typeIn);
    }
    
    public StatBasic(final String statIdIn, final IChatComponent statNameIn) {
        super(statIdIn, statNameIn);
    }
    
    @Override
    public StatBase registerStat() {
        super.registerStat();
        StatList.generalStats.add(this);
        return this;
    }
}
