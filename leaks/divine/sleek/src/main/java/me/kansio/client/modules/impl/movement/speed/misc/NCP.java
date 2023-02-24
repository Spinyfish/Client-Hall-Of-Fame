package me.kansio.client.modules.impl.movement.speed.misc;

import me.kansio.client.event.impl.MoveEvent;
import me.kansio.client.event.impl.PacketEvent;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.impl.movement.speed.SpeedMode;
import me.kansio.client.utils.player.PlayerUtil;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;

public class NCP extends SpeedMode {
    private int stage = 1, stageOG = 1;
    private double moveSpeed, lastDist, moveSpeedOG, lastDistOG;
    public NCP() {
        super("NCP");
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer == null) return;
        lastDist = 0;
        moveSpeed = 0;
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        final boolean tick = mc.thePlayer.ticksExisted % 2 == 0;
        lastDist = Math.sqrt(((mc.thePlayer.posX - mc.thePlayer.prevPosX) * (mc.thePlayer.posX - mc.thePlayer.prevPosX)) + ((mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * (mc.thePlayer.posZ - mc.thePlayer.prevPosZ)));
        if (lastDist > 5) lastDist = 0.0D;
    }

    @Override
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            lastDist = 0;
        }
    }

    @Override
    public void onMove(MoveEvent event) {
        switch (stage) {
            case 0:
                ++stage;
                lastDist = 0.0D;
                break;
            case 2:
                double motionY = 0.4025;
                if ((mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F) && mc.thePlayer.onGround) {
                    if (mc.thePlayer.isPotionActive(Potion.jump))
                        motionY += ((mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
                    event.setMotionY(mc.thePlayer.motionY = motionY);
                    moveSpeed *= 2;
                }
                break;
            case 3:
                moveSpeed = lastDist - (0.7 * (lastDist - getBaseMoveSpeed()));
                break;
            default:
                if ((mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, mc.thePlayer.motionY, 0.0D)).size() > 0 || mc.thePlayer.isCollidedVertically) && stage > 0) {
                    stage = mc.thePlayer.moveForward == 0.0F && mc.thePlayer.moveStrafing == 0.0F ? 0 : 1;
                }
                moveSpeed = lastDist - lastDist / 159.0D;
                break;
        }
        moveSpeed = Math.max(moveSpeed, getBaseMoveSpeed());
        PlayerUtil.setMotion(event, moveSpeed);
        ++stage;
    }

    private double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            final int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + (0.2 * amplifier);
        }
        return baseSpeed;
    }
}
