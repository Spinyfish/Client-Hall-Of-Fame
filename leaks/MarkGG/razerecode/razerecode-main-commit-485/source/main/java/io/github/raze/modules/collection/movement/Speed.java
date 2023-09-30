package io.github.raze.modules.collection.movement;

import io.github.raze.Raze;
import io.github.raze.events.collection.motion.EventMotion;
import io.github.raze.events.system.BaseEvent;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.settings.collection.ArraySetting;
import io.github.raze.settings.collection.BooleanSetting;
import io.github.raze.settings.collection.NumberSetting;
import io.github.raze.utilities.collection.MoveUtil;
import io.github.raze.utilities.collection.math.TimeUtil;
import io.github.raze.utilities.collection.visual.ChatUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPosition;
import net.minecraft.util.EnumFacing;

public class Speed extends BaseModule {

    public ArraySetting mode;
    public BooleanSetting damageBoost;
    public NumberSetting speed;

    private final TimeUtil timer;

    private boolean iceMessageSaid;

    public Speed() {
        super("Speed", "Makes you faster", ModuleCategory.MOVEMENT);

        Raze.INSTANCE.MANAGER_REGISTRY.SETTING_REGISTRY.register(
                mode = new ArraySetting(this, "Mode", "Legit", "Legit", "Vanilla", "AAC3", "NCP Latest", "Vulcan", "Vulcan Disabler", "Old NCP", "BlockDrop", "BlocksMC", "WatchDog", "HVH", "Verus", "Lowhop", "Ice"),
                speed = new NumberSetting(this, "Speed", 0.1, 5, 0.5),
                damageBoost = new BooleanSetting(this, "Damage Boost", false)
        );

        timer = new TimeUtil();

    }

    @Override
    public void onEnable() {
        mc.timer.timerSpeed = 1f;
        if(mode.compare("Ice") && !iceMessageSaid) {
            ChatUtil.addChatMessage("This mode only works while on ice blocks.", true);
            iceMessageSaid = true;
        }
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1f;
    }

    private int ticks;

    @SubscribeEvent
    private void onMotion(EventMotion eventMotion) {

        if (!MoveUtil.isMoving())
            return;

        if (eventMotion.getState() == BaseEvent.State.PRE) {

            if (damageBoost.get() && !mode.compare("Watchdog") && !mode.compare("BlocksMC")) {
                if (mc.thePlayer.hurtTime > 1) {
                    mc.thePlayer.motionX *= 1.02;
                    mc.thePlayer.motionZ *= 1.02;
                }
            }

            switch (mode.get()) {
                case "Legit":
                    if (mc.thePlayer.onGround)
                        mc.thePlayer.jump();
                    break;

                case "Vanilla":
                    MoveUtil.setSpeed(speed.get().doubleValue());
                    break;

                case "AAC3":
                    mc.timer.timerSpeed = (float) 1.75;
                    if (mc.thePlayer.onGround)
                        mc.thePlayer.jump();

                    if(mc.thePlayer.isAirBorne) {
                        MoveUtil.setSpeed(0.4);
                        mc.timer.timerSpeed = (float) 1.0;
                    }
                    break;

                case "NCP Latest":
                    if (mc.thePlayer.fallDistance > 0.7)
                        mc.timer.timerSpeed = 1.07F;
                    else
                        mc.timer.timerSpeed = 1.0F;

                    if (mc.thePlayer.onGround) {
                        if (MoveUtil.isMoving()) {
                            mc.thePlayer.jump();
                        } else {
                            mc.thePlayer.motionX = 0;
                            mc.thePlayer.motionZ = 0;
                        }
                    }

                    MoveUtil.strafe(MoveUtil.getSpeed());
                    break;

                case "Vulcan":
                    ticks++;

                    if (mc.thePlayer.onGround) {
                        MoveUtil.strafe((float) MoveUtil.getBaseMoveSpeed() * 1.15F);
                        ticks = 0;
                    }

                    if (ticks >= 4) {
                        mc.thePlayer.motionY = -0.2F;
                    }

                    if(MoveUtil.isMoving())
                        mc.thePlayer.jump();

                    break;

                case "Vulcan Disabler":
                    if (MoveUtil.getSpeed() < 0.29) {
                        mc.thePlayer.motionX *= 1.0005F;
                        mc.thePlayer.motionZ *= 1.0005F;
                    }

                    mc.thePlayer.jumpMovementFactor = 0.0242F;
                        MoveUtil.strafe((float) (MoveUtil.getBaseMoveSpeed() * 1.005F));

                    if (mc.thePlayer.onGround && MoveUtil.isMoving()) {
                        MoveUtil.strafe(MoveUtil.getSpeed());
                        mc.thePlayer.jump();
                    }

                    break;

                case "Old NCP":
                    if(MoveUtil.isMoving()){
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.jump();
                            mc.thePlayer.speedInAir = 0.0223f;
                        }
                        MoveUtil.strafe((float) MoveUtil.getBaseMoveSpeed());
                    } else {
                        final double rotation = Math.toRadians(mc.thePlayer.rotationYaw), x = Math.sin(rotation), z = Math.cos(rotation);
                        mc.thePlayer.setPosition(mc.thePlayer.posX - x * 0.005, mc.thePlayer.posY, mc.thePlayer.posZ + z * 0.005);
                    }
                    break;

                case "BlockDrop":
                    if (timer.elapsed(1000, false))
                        mc.timer.timerSpeed = (float) 1.08;
                    else if (timer.elapsed(1200, true))
                        mc.timer.timerSpeed = 1;

                    if (MoveUtil.isMoving()) {
                        MoveUtil.strafe((float) 0.26);
                       mc.thePlayer.setSprinting(true);
                        mc.thePlayer.jump();
                    } else {
                        mc.thePlayer.motionX = 0.0;
                        mc.thePlayer.motionZ = 0.0;
                    }
                    break;

                case "BlocksMC":
                    if(mc.thePlayer.hurtTime != 0 && damageBoost.get()) {
                        mc.thePlayer.motionX *= 1.0005F;
                        mc.thePlayer.motionZ *= 1.0005F;
                        MoveUtil.strafe(MoveUtil.getSpeed() * 1.03F);
                    }
                    if (timer.elapsed(1000, true)) {
                        mc.thePlayer.motionX *= 1.0065F;
                        mc.thePlayer.motionZ *= 1.0065F;
                    }

                    if (MoveUtil.isMoving() && !mc.thePlayer.isCollidedHorizontally) {
                        final double rotation = Math.toRadians(mc.thePlayer.rotationYaw), x = Math.sin(rotation), z = Math.cos(rotation);
                        mc.thePlayer.setPosition(mc.thePlayer.posX - x * 0.005673, mc.thePlayer.posY, mc.thePlayer.posZ + z * 0.005673);
                    }

                    if (mc.thePlayer.fallDistance > 0.7)
                        mc.timer.timerSpeed = 1.1F;
                    else
                        mc.timer.timerSpeed = 1.0F;

                    if (mc.thePlayer.onGround) {
                        if (MoveUtil.isMoving()) {
                            mc.thePlayer.jump();
                        } else {
                            mc.thePlayer.motionX = 0;
                            mc.thePlayer.motionZ = 0;
                        }
                    }

                    MoveUtil.strafe((float) (MoveUtil.getSpeed() * 1.01));
                    break;

                case "WatchDog":
                    if (mc.thePlayer.isBlocking() || mc.thePlayer.isUsingItem() || mc.thePlayer.isEating()) {
                        if (!Raze.INSTANCE.MANAGER_REGISTRY.MODULE_REGISTRY.getModule(NoSlow.class).isEnabled())
                            return;
                    }
                    float multiplier = 1.075F;
                    if (damageBoost.get()) {
                        if (mc.thePlayer.hurtTime >= 3) {
                            mc.thePlayer.motionY -= 0.0007;
                            multiplier = 1.085F;
                            mc.thePlayer.motionX *= 1.003F;
                            mc.thePlayer.motionZ *= 1.003F;
                        }

                        if (mc.thePlayer.hurtTime > 8) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, new BlockPosition(mc.thePlayer), EnumFacing.UP));
                            mc.thePlayer.motionX *= 1.005;
                            mc.thePlayer.motionZ *= 1.005;
                        }

                    } else
                        multiplier = 1.06F;

                    boolean strafe = false;

                    if (MoveUtil.getSpeed() > 0.287)
                        multiplier = 0.8F;

                    if (mc.thePlayer.onGround) {
                        strafe = true;
                    }

                    if (strafe) {
                        MoveUtil.strafe(MoveUtil.getSpeed() * multiplier);
                        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, new BlockPosition(mc.thePlayer), EnumFacing.UP));
                    }

                    if (mc.thePlayer.onGround && MoveUtil.isMoving())
                        mc.thePlayer.jump();
                    break;

                case "HVH":
                    MoveUtil.strafe((float) (MoveUtil.getBaseMoveSpeed() * 2.1212132414157823087F));
                    if (mc.thePlayer.onGround && MoveUtil.isMoving())
                        mc.thePlayer.jump();
                    break;

                case "Verus":
                    mc.timer.timerSpeed = 1F;
                    if(MoveUtil.isMoving() && mc.thePlayer.onGround)
                        if(timer.elapsed(100, true))
                            mc.thePlayer.jump();

                    if(MoveUtil.isMoving()) {

                        mc.thePlayer.setSprinting(true);

                        if(mc.thePlayer.onGround) {
                            if(mc.thePlayer.moveForward > 0)
                                MoveUtil.setSpeed(0.19);
                            else
                                MoveUtil.setSpeed(0.14);
                        } else {
                            if(mc.thePlayer.moveForward > 0)
                                MoveUtil.setSpeed(0.294);
                            else
                                MoveUtil.setSpeed(0.291);
                        }

                    }

                    break;

                case "Lowhop":
                    if(!mc.thePlayer.isUsingItem())
                        mc.timer.timerSpeed = (float) 1.31;

                    if(mc.thePlayer.onGround && MoveUtil.isMoving() && !mc.thePlayer.isCollidedHorizontally)
                        mc.thePlayer.motionY = 0.12;

                    if((mc.thePlayer.isCollidedHorizontally || mc.gameSettings.keyBindJump.pressed) && mc.thePlayer.onGround && MoveUtil.isMoving())
                        mc.thePlayer.jump();

                    MoveUtil.setSpeed(speed.get().doubleValue());
                    break;

                case "Ice":
                    Blocks.ice.slipperiness = 0.4F;
                    Blocks.packed_ice.slipperiness = 0.4F;
                    break;
            }
        }
    }
}