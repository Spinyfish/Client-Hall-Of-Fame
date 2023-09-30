package club.pulsive.impl.module.impl.player.autopot;

import net.minecraft.client.entity.EntityPlayerSP;

public interface Requirement {
    boolean test(EntityPlayerSP player, float healthTarget, int currentAmplifier, int potionId);
}