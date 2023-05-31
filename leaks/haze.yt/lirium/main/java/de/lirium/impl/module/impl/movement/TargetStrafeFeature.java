package de.lirium.impl.module.impl.movement;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.Client;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.SliderSetting;
import de.lirium.impl.events.PlayerUpdateEvent;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.impl.module.impl.combat.AuraFeature;
import de.lirium.util.rotation.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

@ModuleFeature.Info(name = "Target Strafe", description = "Strafing around the target", category = ModuleFeature.Category.MOVEMENT)
public class TargetStrafeFeature extends ModuleFeature {

    @Value(name = "Distance")
    private final SliderSetting<Double> distance = new SliderSetting<>(3.0, 0.0, 6.0);

    private AuraFeature auraFeature;
    private double direction = 1;
    private boolean strafing;
    private float strafeYaw;

    @EventHandler
    public final Listener<PlayerUpdateEvent> playerUpdateEventListener = e -> {
        if (e.getState().equals(PlayerUpdateEvent.State.UPDATE)) {
            if (auraFeature == null)
                auraFeature = Client.INSTANCE.getModuleManager().get(AuraFeature.class);
            strafeYaw = getYaw();
            strafing = false;
            if (auraFeature.isEnabled() && auraFeature.targets.size() > 0) {
                Vec3d middlePoint = new Vec3d(0, 0, 0);
                for (Entity entity : auraFeature.targets) {
                    middlePoint = middlePoint.add(entity.getPositionVector())
                            .addVector(-Math.sin(Math.toRadians(direction * 90)) * distance.getValue(), 0,
                                    Math.cos(Math.toRadians(direction * 90)) * distance.getValue());
                }
                middlePoint = middlePoint.scale(1.0 / auraFeature.targets.size());
                //middlePoint.addVector(-Math.sin(Math.toRadians(90 * direction)) * distance.getValue(), 0, Math.cos(Math.toRadians(90 * direction)) * distance.getValue());
                strafeYaw = (float) Math.toDegrees(Math.atan2(middlePoint.zCoord - getPlayer().posZ, middlePoint.xCoord - getPlayer().posX)) - 90.0F;
                strafing = true;
                direction += Math.hypot(mc.player.posX - mc.player.prevPosX, mc.player.posZ - mc.player.prevPosZ) / 4.0;
                if (direction > 4)
                    direction = 0;
            }
        }
    };

    public boolean isStrafing() {
        return strafing;
    }

    public float getStrafeYaw() {
        return strafeYaw;
    }

}