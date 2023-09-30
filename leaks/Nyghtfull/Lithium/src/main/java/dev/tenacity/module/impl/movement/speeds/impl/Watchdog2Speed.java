package dev.tenacity.module.impl.movement.speeds.impl;

import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.event.impl.player.movement.correction.StrafeEvent;
import dev.tenacity.module.impl.movement.speeds.SpeedMode;
import dev.tenacity.utils.player.MovementUtils;
import net.minecraft.block.Block;
import net.minecraft.potion.Potion;
import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPosition;

public class Watchdog2Speed extends SpeedMode {
    private float angle;
    private int offGroundTicks;

    private int ticksSinceVelocity;

    public Watchdog2Speed() {
        super("Watchdog 2");

    }

    @Override
    public void onEnable() {
        offGroundTicks = 0;
        ticksSinceVelocity = 0;
        super.onEnable();
    }
    public Block blockRelativeToPlayer(final double offsetX, final double offsetY, final double offsetZ) {
        return mc.theWorld.getBlockState(new BlockPosition(mc.thePlayer).add(offsetX, offsetY, offsetZ)).getBlock();
    }


    @Override
    public void onMotionEvent(MotionEvent event) {
        if (mc.thePlayer.onGround) {
            offGroundTicks = 0;

        } else {
            offGroundTicks++;
        }

        if (mc.thePlayer.hurtTime>1) {
            ticksSinceVelocity = 0;

        } else {
            ticksSinceVelocity++;
        }
        if (!(blockRelativeToPlayer(0, -1, 0) instanceof BlockAir) && ticksSinceVelocity > 20) {
            event.setOnGround(true);
        }
        super.onMotionEvent(event);
    }


    @Override
    public void onStrafeEvent(StrafeEvent event) {

        if (!MovementUtils.isMoving() || MovementUtils.isInLiquid()) {
            return;
        }


        if (!(MovementUtils.blockRelativeToPlayer(0, -1, 0) instanceof BlockAir) || !(MovementUtils.blockRelativeToPlayer(0, -1.1, 0) instanceof BlockAir)) {
            angle = MovementUtils.simulationStrafeAngle(angle, ticksSinceVelocity < 40 ? 39.9f : 19.9f);
        }

        if (ticksSinceVelocity <= 20 || mc.thePlayer.onGround) {
            angle = MovementUtils.simulationStrafeAngle(angle, 360);
        }

        //PacketUtils.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, new BlockPos(mc.thePlayer), EnumFacing.UP));

        if (ticksSinceVelocity > 20) {
            switch (offGroundTicks) {
                case 1:
                    mc.thePlayer.motionY -= 0.005;
                    break;

                case 2:
                case 3:
                    mc.thePlayer.motionY -= 0.001;
                    break;
            }
        }


        if (mc.thePlayer.onGround) {
            double lastAngle = Math.atan(mc.thePlayer.lastMotionX / mc.thePlayer.lastMotionZ) * (180 / Math.PI);

            MovementUtils.strafe((float) MovementUtils.getAllowedHorizontalDistance() - (float)Math.random() / 1000);
            mc.thePlayer.jump();

            double angle = Math.atan(mc.thePlayer.motionX / mc.thePlayer.motionZ) * (180 / Math.PI);

            if (Math.abs(lastAngle - angle) > 20 && ticksSinceVelocity > 20) {
                int speed = mc.thePlayer.isPotionActive(Potion.moveSpeed) ? mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 : 0;

                switch (speed) {
                    case 0:
                        MovementUtils.moveFlying(-0.004);
                        break;

                    case 1:
                        MovementUtils.moveFlying(-0.025);
                        break;

                    default:
                        MovementUtils.moveFlying(-0.04);
                        break;
                }
            }
        }

        super.onStrafeEvent(event);
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;

        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;

        super.onDisable();
    }
}
