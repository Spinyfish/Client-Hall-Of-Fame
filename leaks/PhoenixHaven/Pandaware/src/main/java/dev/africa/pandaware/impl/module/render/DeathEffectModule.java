package dev.africa.pandaware.impl.module.render;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.game.TickEvent;
import dev.africa.pandaware.impl.module.combat.KillAuraModule;
import dev.africa.pandaware.impl.module.combat.TPAuraModule;
import dev.africa.pandaware.utils.math.apache.ApacheMath;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ModuleInfo(name = "Death Effect", category = Category.VISUAL)
public class DeathEffectModule extends Module {
    private final List<EntityPlayer> entityPlayers = new CopyOnWriteArrayList<>();

    @Override
    public void onEnable() {
        this.entityPlayers.clear();
    }

    @EventHandler
    EventCallback<TickEvent> onTick = event -> {
        if (mc.theWorld != null && mc.thePlayer != null && mc.thePlayer.ticksExisted > 40) {
            mc.theWorld.playerEntities.stream()
                    .filter(entityPlayer -> {
                        KillAuraModule killAuraModule = Client.getInstance().getModuleManager().getByClass(KillAuraModule.class);
                        TPAuraModule tpAuraModule = Client.getInstance().getModuleManager().getByClass(TPAuraModule.class);

                        return (killAuraModule.getData().isEnabled() &&
                                killAuraModule.getTarget() != null || tpAuraModule.getData().isEnabled() && tpAuraModule.getTarget() != null) &&
                                !Client.getInstance().getIgnoreManager().isIgnore(entityPlayer, false) &&
                                !entityPlayer.isDead && entityPlayer.isEntityAlive() &&
                                (entityPlayer == killAuraModule.getTarget() || tpAuraModule.getTarget() == entityPlayer);
                    }).forEach(entity -> {
                        if (!this.entityPlayers.contains(entity)) {
                            this.entityPlayers.add(entity);
                        }
                    });

            this.entityPlayers.forEach(entityPlayer -> {
                if (entityPlayer.isDead || !entityPlayer.isEntityAlive()) {
                    if (entityPlayer.getDistanceToEntity(mc.thePlayer) < 50 && entityPlayer.ticksExisted > 20) {
                        mc.getSoundHandler().playSound(PositionedSoundRecord.create(
                                new ResourceLocation("mob.ghast.fireball"), 1.3F));

                        double add = 0;
                        for (int i = 0; i < 360; i++) {
                            mc.theWorld.spawnParticle(EnumParticleTypes.FLAME,
                                    entityPlayer.posX + ApacheMath.cos(i) * (1.5 + (add / 2f)),
                                    entityPlayer.posY + 0.5 + add,
                                    entityPlayer.posZ + ApacheMath.sin(i) * (1.5 + (add / 2f)),
                                    0.0D, 0.0D, 0.0D, 20);
                            add += 0.004;
                        }
                    }

                    this.entityPlayers.remove(entityPlayer);
                }
            });
        } else {
            this.entityPlayers.clear();
        }
    };
}
