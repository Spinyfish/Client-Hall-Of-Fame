package io.github.raze.modules.collection.player;

import io.github.raze.Raze;
import io.github.raze.events.collection.motion.EventMotion;
import io.github.raze.events.collection.network.EventPacketSend;
import io.github.raze.events.system.BaseEvent;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.settings.collection.BooleanSetting;
import io.github.raze.utilities.collection.math.TimeUtil;
import io.github.raze.utilities.collection.visual.ChatUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.util.BlockPosition;
import net.minecraft.util.EnumFacing;

public class Disabler extends BaseModule {

    public BooleanSetting vulcan_autoblock,vulcan_sprint,negativity,verus_ground,vulcan_strafe,verus_void,lagback,debug,timerDisabler;

    private boolean teleported = false;
    private int voidCheckInt;
    private boolean clearPacket;

    private final TimeUtil timer;

    public Disabler() {
        super("Disabler", "Disables Anti-Cheat solutions.", ModuleCategory.PLAYER);

        Raze.INSTANCE.MANAGER_REGISTRY.SETTING_REGISTRY.register(

                // Vulcan
                vulcan_autoblock = new BooleanSetting(this, "Vulcan AutoBlock", false),
                vulcan_sprint    = new BooleanSetting(this, "Vulcan Sprint", false),
                vulcan_strafe    = new BooleanSetting(this, "Vulcan Strafe", false),

                // Verus
                verus_ground = new BooleanSetting(this, "Verus Ground", false),
                verus_void   = new BooleanSetting(this, "Verus Semi", false),

                // Negativity
                negativity = new BooleanSetting(this, "Negativity", false),

                //Lag
                lagback = new BooleanSetting(this, "Lagback", false),

                // Timer (Beta)
                timerDisabler = new BooleanSetting(this, "Timer", false),

                //debug
                debug = new BooleanSetting(this, "Debug Mode", false)

        );

        timer = new TimeUtil();
    }

    @SubscribeEvent
    private void onMotion(EventMotion eventMotion) {
        if (eventMotion.getState() == BaseEvent.State.PRE) {

            if (vulcan_autoblock.get()) {
                mc.getNetHandler().addToSendQueueSilent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                mc.getNetHandler().addToSendQueueSilent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
            }

            if (negativity.get()) {
                if (mc.thePlayer.ticksExisted % 20 == 0) {
                    mc.thePlayer.sendQueue.addToSendQueueSilent(new C03PacketPlayer.C06PacketPlayerPosLook());
                    teleported = true;
                }
            }

            if (verus_ground.get())
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 14.37, mc.thePlayer.posZ, true));
        }
        if(vulcan_strafe.get()) {
            if (mc.thePlayer.ticksExisted % 4 == 0) {
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, new BlockPosition(mc.thePlayer.posX, mc.thePlayer.posY - 2, mc.thePlayer.posZ), EnumFacing.DOWN));
            }
        }
        if(verus_void.get()) {
            if(mc.theWorld.isAirBlock(new BlockPosition(mc.thePlayer.posX, (mc.thePlayer.posY - 15) - voidCheckInt, mc.thePlayer.posZ))) {
                if(mc.thePlayer.ticksExisted % 40 == 0) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, ((mc.thePlayer.posY - 15) - voidCheckInt) - 1, mc.thePlayer.posZ, mc.thePlayer.onGround));
                    if(debug.get()) {
                        ChatUtil.addChatMessage("Teleported to void", true);
                    }
                }
            } else {
                voidCheckInt--;
                if(mc.thePlayer.ticksExisted % 10 == 0) {
                    if (debug.get()) {
                        ChatUtil.addChatMessage("Finding void.....", true);
                    }
                }
            }
        }
        if(lagback.get()){
            double oldPosX =  mc.thePlayer.posX, oldPosY = mc.thePlayer.posY, oldPosZ = mc.thePlayer.posZ;
            if(timer.elapsed(999, true)) {
                mc.thePlayer.posX = oldPosX;
                mc.thePlayer.posY = oldPosY;
                mc.thePlayer.posZ = oldPosZ;
                mc.thePlayer.motionX *= 0.001F;
                mc.thePlayer.motionY *= -0.5F;
                mc.thePlayer.motionZ *= 0.001F;
            }
        }
        if(timerDisabler.get()) {
            int chance = (int) (100 / mc.timer.timerSpeed);
            if(chance < Math.random() * 100) {
                clearPacket = true;
            }
        }
    }

    @SubscribeEvent
    private void onPacketSend(EventPacketSend event) {
        Packet <?> packet = event.getPacket();

        if(verus_void.get()) {
            if(event.getPacket() instanceof C0FPacketConfirmTransaction) {
                event.setCancelled(true);
            }
        }

        if (negativity.get()) {
            if (event.getPacket() instanceof S08PacketPlayerPosLook && teleported) {
                event.setCancelled(true);
                teleported = false;

                S08PacketPlayerPosLook position = (S08PacketPlayerPosLook) packet;

                mc.thePlayer.sendQueue.addToSendQueueSilent(new C03PacketPlayer.C06PacketPlayerPosLook(
                                position.getY(),
                                position.getZ(),
                                position.getX(),
                                position.getYaw(),
                                position.getPitch(),
                                true
                ));
            }
        }

        if (vulcan_autoblock.get()) {
            if (packet instanceof C17PacketCustomPayload)
                event.setCancelled(true);
        }
        if(timerDisabler.get()) {
            if(clearPacket) {
                if(event.getPacket() != null) {
                    if(event.getPacket() instanceof C03PacketPlayer) {
                        if(debug.get())
                            ChatUtil.addChatMessage("Saved a C03");
                    } else if(event.getPacket() instanceof C00PacketKeepAlive) {
                        if(debug.get()) {
                            ChatUtil.addChatMessage("Saved a KeepAlive packet.");
                        }
                    } else if(!(event.getPacket() instanceof C00PacketKeepAlive) && !(event.getPacket() instanceof C03PacketPlayer)) {
                        event.setCancelled(true);
                        clearPacket = false;
                        if(debug.get()) {
                            ChatUtil.addChatMessage("Removed a Packet.");
                        }
                    }
                }
            }
        }
    }

    public void onEnable() {
        teleported = false;
        if(timerDisabler.get()) {
            ChatUtil.addChatMessage("NOTE: You might get lagged back.");
        }
    }
}
