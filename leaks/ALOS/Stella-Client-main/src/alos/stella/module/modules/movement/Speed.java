package alos.stella.module.modules.movement;

import alos.stella.Stella;
import alos.stella.event.EventState;
import alos.stella.event.EventTarget;
import alos.stella.event.events.*;
import alos.stella.module.Module;
import alos.stella.module.ModuleCategory;
import alos.stella.module.ModuleInfo;
import alos.stella.module.modules.combat.Velocity;
import alos.stella.utils.BlockUtils;
import alos.stella.utils.MovementUtils;
import alos.stella.utils.PacketUtils;
import alos.stella.value.BoolValue;
import alos.stella.value.IntegerValue;
import alos.stella.value.ListValue;
import net.minecraft.block.BlockCarpet;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

@ModuleInfo(name = "Speed", description = "Allows you to move faster.", category = ModuleCategory.MOVEMENT)
public class Speed extends Module {

    public final ListValue modeValue = new ListValue("Mode", new String[]{"MatrixFast", "WatchdogHard", "AAC3.3.10", "Mushmc", "Vulcan","Test", "Eat"}, "WatchdogHard");
    private final IntegerValue wdground = new IntegerValue("WatchdogCustomOnGround",1, 0, 10);

    public final BoolValue lagbackCheck = new BoolValue("LagBackCheck", true);
    boolean wasTimer = false;
    private int ticks;

    private int groundTick;
    private ItemStack itemToRender;

    public void onDisable() {
        mc.timer.timerSpeed = 1.0f;
        if (modeValue.get().equalsIgnoreCase("Vulcan")) {
        }
    }
    public void onEnable() {
        if (modeValue.get().equalsIgnoreCase("Vulcan")) {
	        ticks = 0;
	        PacketUtils.send(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY - 2, mc.thePlayer.posZ,
	                mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
        }
    }



    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        if (modeValue.get().equalsIgnoreCase("Eat")) {
            if (mc.thePlayer.onGround){
                mc.thePlayer.motionY = MovementUtils.getJumpBoostModifier(0.41999998688698, true);
            }
        }
        if (modeValue.get().equalsIgnoreCase("Mushmc")) {
            if (MovementUtils.isMoving()) {
                MovementUtils.strafe();
                if(mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                    MovementUtils.accelerate(0.5f);
                }
            }
            mc.timer.timerSpeed = 1f;
        }
        if (modeValue.get().equalsIgnoreCase("WatchdogHard")) {
            if (MovementUtils.isMoving()) {
                mc.timer.timerSpeed = 1.2f;
                if (mc.thePlayer.onGround) {
                    if (groundTick >= wdground.get()) {
                        mc.timer.timerSpeed = 2.0f;
                        MovementUtils.strafe(0.43f);
                        mc.thePlayer.motionY = MovementUtils.getJumpBoostModifier(0.41999998688698, true);
                        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                            MovementUtils.strafe(0.63f);
                        }
                    }
                    groundTick++;
                } else{
                    groundTick = 0;
                }
                if (mc.thePlayer.hurtTime > 0 || mc.thePlayer.fallDistance > 0.0) {
                    MovementUtils.strafe();
                }
                if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    mc.thePlayer.jumpMovementFactor = 0.03f;
                }
            }
        }
        if (modeValue.get().equalsIgnoreCase("MatrixFast")) {
            if (wasTimer) {
                wasTimer = false;
                mc.timer.timerSpeed = 1.0f;
            }
            mc.thePlayer.motionY -= 0.00348;
            mc.thePlayer.jumpMovementFactor = 0.026f;
            mc.gameSettings.keyBindRight.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindRight);
            if (MovementUtils.isMoving() && mc.thePlayer.onGround) {
                mc.gameSettings.keyBindRight.pressed = false;
                mc.timer.timerSpeed = 1.35f;
                wasTimer = true;
                mc.thePlayer.jump();
                MovementUtils.strafe();
            } else if (MovementUtils.getSpeed() < 0.215) {
                MovementUtils.strafe(0.215f);

            }
        }
        if (modeValue.get().equalsIgnoreCase("AAC3.3.10")) {
            if (!MovementUtils.isMoving() || mc.thePlayer.isInWater() || mc.thePlayer.isInLava() ||
                    mc.thePlayer.isOnLadder() || mc.thePlayer.isRiding() || mc.thePlayer.hurtTime > 0)
                return;

            if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically) {
                float yawRad = mc.thePlayer.rotationYaw * 0.017453292F;
                mc.thePlayer.motionX -= MathHelper.sin(yawRad) * 0.202f;
                mc.thePlayer.motionZ += MathHelper.cos(yawRad) * 0.202f;
                mc.thePlayer.motionY = 0.405F;
                Stella.eventManager.callEvent(new JumpEvent(0.405F));
                MovementUtils.strafe();
            } else if (mc.thePlayer.fallDistance < 0.31F) {
                if (BlockUtils.getBlock(mc.thePlayer.getPosition()) instanceof BlockCarpet) // why?
                    return;

                mc.thePlayer.jumpMovementFactor = mc.thePlayer.moveStrafing == 0F ? 0.027F : 0.021F;
                mc.thePlayer.motionX *= 1.001;
                mc.thePlayer.motionZ *= 1.001;

                // Mb maximum
                if (!mc.thePlayer.isCollidedHorizontally)
                    mc.thePlayer.motionY -= 0.0027675;
            } else
                mc.thePlayer.jumpMovementFactor = 0.02F;
        }
    }

    @EventTarget
    public void onMove(final MoveEvent event) {
    }
    @EventTarget
    public void onMotion(MotionEvent event) {
        if (event.getEventState() == EventState.PRE) {
            if (modeValue.get().equalsIgnoreCase("Vulcan")) {
                ticks++;
                if (ticks >= 66) {
                    MovementUtils.stop();
                    setState(false);
                }
                if (mc.thePlayer.getDistance(mc.thePlayer.lastReportedPosX, mc.thePlayer.lastReportedPosY, mc.thePlayer.lastReportedPosZ) <= 10 - 1 - 0.15) {
                    event.cancelEvent(true);
                } else {
                    ticks++;
                    if (ticks >= 8) {
                        MovementUtils.stop();
                        getState();
                    }
                }
            }
        }
    }
    @EventTarget
    public void onStrafe(StrafeEvent event) {
        if (modeValue.get().equalsIgnoreCase("Vulcan")) {
            event.setSpeed(1);
        }
        if (modeValue.get().equalsIgnoreCase("Test")) {
        	if (MovementUtils.isMoving()) {
        		if (mc.thePlayer.onGround) {
        			mc.thePlayer.jump();
                	MovementUtils.strafe(0.5f);
        		}
        	}
        }
    }

    @EventTarget
    public void onPacket(final PacketEvent event) {
        if (modeValue.get().equalsIgnoreCase("Eat")) {
            if (event.getPacket() instanceof S12PacketEntityVelocity) {
                if (!Stella.moduleManager.getModule(Velocity.class).getState()) {
                        event.cancelEvent();
                    }
                }
            }
        if (lagbackCheck.get() && event.getPacket() instanceof S08PacketPlayerPosLook)
            this.setState(false);
    }

}
