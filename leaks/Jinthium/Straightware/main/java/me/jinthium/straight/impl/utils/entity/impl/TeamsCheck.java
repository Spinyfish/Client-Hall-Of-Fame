package me.jinthium.straight.impl.utils.entity.impl;

import me.jinthium.straight.impl.utils.entity.ICheck;
import me.jinthium.straight.impl.utils.player.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public final class TeamsCheck implements ICheck {
    private final boolean teams;

    public TeamsCheck(final boolean teams) {
        this.teams = teams;
    }

    @Override
    public boolean validate(Entity entity) {
        if (entity instanceof EntityPlayer) {
            return !PlayerUtil.isTeammate((EntityPlayer) entity) || !teams;
        }
        return true;
    }
}