package club.pulsive.impl.util.entity.impl;

import club.pulsive.impl.property.Property;
import club.pulsive.impl.util.entity.ICheck;
import club.pulsive.impl.util.player.PlayerUtil;
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
            if (PlayerUtil.isTeammate((EntityPlayer) entity) && teams) {
                return false;
            }
        }
        return true;
    }
}
