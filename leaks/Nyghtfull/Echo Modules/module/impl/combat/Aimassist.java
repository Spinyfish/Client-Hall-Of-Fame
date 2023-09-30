package dev.echo.module.impl.combat;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.player.MotionEvent;
import dev.echo.listener.event.impl.render.Render2DEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.settings.impl.BooleanSetting;
import dev.echo.module.settings.impl.NumberSetting;
import dev.echo.utils.time.TimerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class Aimassist extends Module {
    private static final NumberSetting range = new NumberSetting("Range", 3, 6, 3, 0.1);
    private static final NumberSetting speed = new NumberSetting("Speed", 1, 25, 1, 0.50);
    private static final BooleanSetting vertical = new BooleanSetting("Vertical", false);

    private Entity target;
    private final TimerUtil timer = new TimerUtil();

    public Aimassist() {
        super("Aim assist", Category.COMBAT, "Muie");
        addSettings(range, speed, vertical);
    }

    @Link
    private final Listener<MotionEvent> motionEventListener = e -> this.setSuffix(String.valueOf(range.getValue()));

    @Link
    private final Listener<Render2DEvent> onRender2DEvent = e -> {
        if (target == null || !canAttack(target)) {
            target = getClosest(range.getValue());
        }

        if (target == null) {
            timer.reset();
            return;
        }

        if (mc.gameSettings.keyBindAttack.isKeyDown()) {
            faceEntity(target, Math.min(2.0 * speed.getValue(), speed.getValue() * 2.0 * Math.max(0.2, getDistanceFromMouse(target) / mc.gameSettings.fovSetting)));

            mc.thePlayer.rotationYaw += 0.5 - 0.25;

            if (vertical.isEnabled()) {
                mc.thePlayer.rotationPitch += Math.random() * 0.2 - 0.1;
                mc.thePlayer.rotationPitch = Math.max(mc.thePlayer.rotationPitch, -90);
                mc.thePlayer.rotationPitch = Math.min(mc.thePlayer.rotationPitch, 90);
            }
        }

        timer.reset();
    };

    private boolean canAttack(Entity target) {
        if (target instanceof EntityLivingBase) {
            if (!mc.thePlayer.canEntityBeSeen(target)) {
                return false;
            }
            return target != mc.thePlayer && mc.thePlayer.isEntityAlive() && mc.thePlayer.getDistanceToEntity(target) <= range.getValue() && mc.thePlayer.ticksExisted > 100;
        }

        return false;
    }

    private EntityLivingBase getClosest(double range) {
        double dist = range;
        EntityLivingBase target1 = null;

        for (Entity object : mc.theWorld.loadedEntityList) {
            if (object instanceof EntityLivingBase) {
                EntityLivingBase player = (EntityLivingBase) object;
                if (canAttack(player)) {
                    double currentDist = mc.thePlayer.getDistanceToEntity(player);
                    if (currentDist <= dist) {
                        dist = currentDist;
                        target1 = player;
                    }
                }
            }
        }

        return target1;
    }

    private static float wrapAngleTo180_float(float value) {
        return MathHelper.wrapAngleTo180_float(value);
    }

    private int getDistanceFromMouse(Entity entity) {
        float[] neededRotations = getRotationsNeeded(entity);
        if (neededRotations != null) {
            float distanceFromMouse = MathHelper.sqrt_float(
                    (mc.thePlayer.rotationYaw - neededRotations[0]) * (mc.thePlayer.rotationYaw - neededRotations[0]) +
                            (mc.thePlayer.rotationPitch - neededRotations[1]) * (mc.thePlayer.rotationPitch - neededRotations[1])
            );
            return (int) distanceFromMouse;
        }
        return -1;
    }

    private float[] getRotationsNeeded(Entity entity) {
        if (entity == null) {
            return null;
        }

        double diffX = entity.posX - mc.thePlayer.posX;
        double diffY = (entity instanceof EntityLivingBase) ?
                ((EntityLivingBase) entity).posY + ((EntityLivingBase) entity).getEyeHeight() * 0.9 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight()) :
                (entity.boundingBox.minY + entity.boundingBox.maxY) / 2.0 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double diffZ = entity.posZ - mc.thePlayer.posZ;
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (-(Math.atan2(diffY, dist) * 180.0 / Math.PI));
        return new float[]{mc.thePlayer.rotationYaw + wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw), mc.thePlayer.rotationPitch + wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch)};
    }

    private void faceEntity(Entity entity, double speed) {
        float[] rotations = getRotationsNeeded(entity);
        if (rotations != null) {
            mc.thePlayer.rotationYaw = (float) limitAngleChange(mc.thePlayer.prevRotationYaw, rotations[0], speed);
        }
    }

    private double limitAngleChange(double current, double intended, double speed) {
        double change = intended - current;
        if (change > speed) {
            change = speed;
        } else if (change < -speed) {
            change = -speed;
        }
        return current + change;
    }
}
