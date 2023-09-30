package alos.stella.module.modules.movement;

import alos.stella.event.EventState;
import alos.stella.event.EventTarget;
import alos.stella.event.events.*;
import alos.stella.module.Module;
import alos.stella.module.ModuleCategory;
import alos.stella.module.ModuleInfo;
import alos.stella.utils.MovementUtils;
import alos.stella.utils.PacketUtils;
import alos.stella.utils.packets.BadPacketsComponent;
import alos.stella.value.BoolValue;
import alos.stella.value.FloatValue;
import alos.stella.value.ListValue;
import net.minecraft.block.BlockAir;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;

@ModuleInfo(name = "Fly", description = "Allows you to move faster.", category = ModuleCategory.MOVEMENT)
public class Fly extends Module {
    double startY = 0.0;
    private int ticks;
    boolean down;
    private final FloatValue speed = new FloatValue("HSpeed",1f, 0.1f, 9.5f);
    private final FloatValue vSpeed = new FloatValue("VSpeed",1f, 0.1f, 9.5f);
    private final BoolValue sendFlying = new BoolValue("Send Flying", false);
    
    private final BoolValue antikick = new BoolValue("MotionAntiKick", false);

    private final BoolValue motionreset = new BoolValue("MotionReset", false);

    private final BoolValue disablebypass = new BoolValue("DisableBypass", false);

    public final ListValue modeValue = new ListValue("Mode", new String[]{
            "SmoothVanilla", "SmoothVanillaFast", "Damage", "DamageHighJump", "Mushmc", "Nebula", "NcpPacket", "BufferAbuse","MotionAbuse", "S08Jump"}, "SmoothVanilla");

    @Override
    public void onDisable(){
        if (motionreset.get()) {
            mc.thePlayer.motionX=0;
            mc.thePlayer.motionY=0;
            mc.thePlayer.motionZ=0;
        }
        down = false;
        mc.thePlayer.capabilities.isFlying = false;
        mc.timer.timerSpeed=1;
    }
    @Override
    public void onEnable(){
        if (modeValue.get().equalsIgnoreCase("MotionAbuse")) {
            ticks = 0;
            PacketUtils.send(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY - 2, mc.thePlayer.posZ,
                    mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
        }
        if (modeValue.get().equalsIgnoreCase("S08Jump")) {
        }
        if (modeValue.get().equalsIgnoreCase("Damage")) {
            verusDamageMoment();
        }
        if (modeValue.get().equalsIgnoreCase("DamageHighJump")) {
            verusDamageMoment();
        }
    }
    @EventTarget
    public void onUpdate(final UpdateEvent event) {
    	if (antikick.get()) {
            if (mc.thePlayer.ticksExisted % 2 == 0) {
                mc.thePlayer.motionY+=0.1;
            }else{
                mc.thePlayer.motionY-=0.1;
            }
    	}
        if (modeValue.get().equalsIgnoreCase("SmoothVanilla")) {
            mc.thePlayer.capabilities.isFlying = true;
        }
        if (modeValue.get().equalsIgnoreCase("SmoothVanillaFast")) {
            mc.thePlayer.capabilities.isFlying = true;
            MovementUtils.strafe(1f);
        }
        if (modeValue.get().equalsIgnoreCase("Damage")) {
            if (mc.thePlayer.hurtTime != 0) {
                mc.thePlayer.jump();
                MovementUtils.strafe(1f);
                mc.timer.timerSpeed = 0.1f;
                mc.thePlayer.motionY=0.4;
                mc.thePlayer.onGround=true;
                setState(false);
            }
        }
        if (modeValue.get().equalsIgnoreCase("DamageHighJump")) {
            if (mc.thePlayer.hurtTime != 0) {
                mc.thePlayer.motionY = 2;
                mc.timer.timerSpeed = 0.1f;
                mc.thePlayer.onGround=true;
                setState(false);
            }
        }
        if (modeValue.get().equalsIgnoreCase("Mushmc")) {
            final float speed = this.speed.getValue().floatValue();
            final float vSpeed = this.vSpeed.getValue().floatValue();

            MovementUtils.strafe(speed);

            mc.thePlayer.motionY = -1E-10D
                    + (mc.gameSettings.keyBindJump.isKeyDown() ? vSpeed : 0.0D)
                    - (mc.gameSettings.keyBindSneak.isKeyDown() ? vSpeed : 0.0D);

            if (mc.thePlayer.getDistance(mc.thePlayer.lastReportedPosX, mc.thePlayer.lastReportedPosY, mc.thePlayer.lastReportedPosZ) <= 10 - speed - 0.15) {
                event.cancelEvent(true);
            }
            if (mc.thePlayer.ticksExisted % 2 == 0) {
                mc.thePlayer.motionY+=0.1;
                mc.thePlayer.fallDistance= 0;
            }else{
                mc.thePlayer.motionY-=0.1;
                mc.thePlayer.fallDistance= 0;
            }
        }
        if (modeValue.get().equalsIgnoreCase("NcpPacket")) {
            if (mc.thePlayer.ticksExisted % 3 == 0) {
                mc.getNetHandler().addToSendQueue(
                        new C03PacketPlayer.C04PacketPlayerPosition(
                        mc.thePlayer.posX + -MathHelper.sin((float) Math.toRadians(mc.thePlayer.rotationYaw)) * 0.2873,
                                mc.thePlayer.posY,
                        mc.thePlayer.posZ + MathHelper.cos((float) Math.toRadians(mc.thePlayer.rotationYaw)) * 0.2873,
                        false
                        )
                );
                mc.getNetHandler().addToSendQueue(
                        new C03PacketPlayer.C04PacketPlayerPosition(
                                mc.thePlayer.posX + -MathHelper.sin((float) Math.toRadians(mc.thePlayer.rotationYaw)) * 0.2873,
                        mc.thePlayer.posY - 1000,
                                        mc.thePlayer.posZ + MathHelper.cos((float) Math.toRadians(mc.thePlayer.rotationYaw)) * 0.2873,
                        true
                        )
                );
            }
        }
    }


    @EventTarget
    public void onBB(BlockBBEvent event) {
        if (mc.thePlayer == null) return;
        if (event.getBlock() instanceof BlockAir && (modeValue.get().equalsIgnoreCase("Mushmc") && (event.y < mc.thePlayer.posY))) {
                {
                event.boundingBox = AxisAlignedBB.fromBounds(
                        event.x,
                        event.y,
                        event.z,
                        event.x + 1.0,
                        startY,
                        event.z + 1.0
                );
            }
        }

    }

    private void verusDamageMoment() {
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.05, mc.thePlayer.posZ, false));
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+0.41999998688697815, mc.thePlayer.posZ, true));
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (modeValue.get().equalsIgnoreCase("S08Jump")) {
        }
    }

    @EventTarget
    public void onMove(MoveEvent event) {
        if (modeValue.get().equalsIgnoreCase("BufferAbuse")) {
            mc.gameSettings.keyBindSneak.setPressed(false);
        }
        if (modeValue.get().equalsIgnoreCase("MotionAbuse")) {
            mc.gameSettings.keyBindSneak.setPressed(false);
        }
    }
    @EventTarget
    public void onPacketRise(PacketEvent event) {
        if (!sendFlying.getValue()) {
            Packet<?> packet = event.getPacket();

            if (packet instanceof C03PacketPlayer) {
                C03PacketPlayer c03PacketPlayer = ((C03PacketPlayer) packet);

                if (!c03PacketPlayer.isMoving() && !BadPacketsComponent.bad()) {
                    event.cancelEvent(true);
                }
            }
        }
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        if (event.getEventState() == EventState.PRE) {
            if (modeValue.get().equalsIgnoreCase("MotionAbuse")) {
                final float speed = this.speed.getValue().floatValue();
                final float vSpeed = this.vSpeed.getValue().floatValue();

                mc.thePlayer.motionY = -1E-10D
                        + (mc.gameSettings.keyBindJump.isKeyDown() ? vSpeed : 0.0D)
                        - (mc.gameSettings.keyBindSneak.isKeyDown() ? vSpeed : 0.0D);
                if (disablebypass.get()) {
                    if (ticks >= 60) {
                        MovementUtils.stop();
                        setState(false);
                    }
                }
                if (mc.thePlayer.getDistance(mc.thePlayer.lastReportedPosX, mc.thePlayer.lastReportedPosY, mc.thePlayer.lastReportedPosZ) <= 10 - speed - 0.15) {
                    event.cancelEvent(true);
                } else {
                    ticks++;

                    if (ticks >= 8) {
                        MovementUtils.stop();
                        getState();
                    }
                }
                if (mc.thePlayer.getDistance(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ) <= 10 - speed - 0.15) {
                    event.cancelEvent(true);
                } else {
                    ticks++;

                    if (ticks >= 8) {
                        MovementUtils.stop();
                        getState();
                    }
                }
            }
            if (modeValue.get().equalsIgnoreCase("BufferAbuse")) {
                final float speed = this.speed.getValue().floatValue();
                final float vSpeed = this.vSpeed.getValue().floatValue();

                mc.thePlayer.motionY = -1E-10D
                        + (mc.gameSettings.keyBindJump.isKeyDown() ? vSpeed : 0.0D)
                        - (mc.gameSettings.keyBindSneak.isKeyDown() ? vSpeed : 0.0D);

                if (mc.thePlayer.getDistance(mc.thePlayer.lastReportedPosX, mc.thePlayer.lastReportedPosY, mc.thePlayer.lastReportedPosZ) <= 10 - speed - 0.15) {
                    event.cancelEvent(true);
                }
            }
            if (modeValue.get().equalsIgnoreCase("Nebula")) {
                final float speed = this.speed.getValue().floatValue();
                final float vSpeed = this.vSpeed.getValue().floatValue();
                mc.thePlayer.motionY = 0;
                if (mc.thePlayer.getDistance(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ) <= 1 - speed - 0.15) {
                    event.cancelEvent(true);
                    mc.thePlayer.isAirBorne = false;
                    MovementUtils.strafe(0.5f);

                } else {
                    mc.thePlayer.isAirBorne = true;
                    MovementUtils.strafe(0.3f);
                    mc.timer.timerSpeed = 2f;
                }

                if (mc.thePlayer.ticksExisted % 2 == 0) {
                    mc.thePlayer.motionY += 0.1;
                    mc.thePlayer.onGround = false;
                } else {
                    mc.thePlayer.motionY -= 0.1;
                    mc.thePlayer.onGround = true;
                }

                if (mc.thePlayer.ticksExisted % 5 == 0) {
                    mc.timer.timerSpeed = 0.4f;
                } else {
                    mc.timer.timerSpeed = 0.4f;
                }
                if (mc.thePlayer.ticksExisted % 8 == 0) {
                    mc.timer.timerSpeed = 4f;
                } else {
                    mc.timer.timerSpeed = 20f;
                }
                mc.thePlayer.forceSpawn = true;
                mc.thePlayer.isCollided = true;
                mc.timer.updateTimer();
                mc.thePlayer.onGround = false;
                mc.thePlayer.positionUpdateTicks = 0;
            }
            if (modeValue.get().equalsIgnoreCase("S08Jump")) {
                final float speed = this.speed.getValue().floatValue();
                final float vSpeed = this.vSpeed.getValue().floatValue();
                   if (mc.thePlayer.getDistance(mc.thePlayer.lastReportedPosX, mc.thePlayer.lastReportedPosY, mc.thePlayer.lastReportedPosZ) <= 10 - speed - 0.15) {
                       event.cancelEvent(true);
                   }
                   if (mc.thePlayer.onGround) {
                       mc.thePlayer.motionY = vSpeed;
                   }
            }
        }
    }
    @EventTarget
    public void onStrafe(StrafeEvent event) {
        if (modeValue.get().equalsIgnoreCase("BufferAbuse")) {
            final float speed = this.speed.getValue().floatValue();
            event.setSpeed(speed);
        }
        if (modeValue.get().equalsIgnoreCase("MotionAbuse")) {
            final float speed = this.speed.getValue().floatValue();
            event.setSpeed(speed);
        }
        if (modeValue.get().equalsIgnoreCase("S08Jump")) {
            final float speed = this.speed.getValue().floatValue();
        	event.setSpeed(speed);
        }
    }

}
