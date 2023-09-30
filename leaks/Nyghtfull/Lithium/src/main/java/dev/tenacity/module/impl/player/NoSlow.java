package dev.tenacity.module.impl.player;

import dev.tenacity.event.impl.network.PacketSendEvent;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.event.impl.player.movement.SlowdownEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.utils.player.MovementUtils;
import dev.tenacity.utils.server.PacketUtils;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.BlockPosition;
import net.minecraft.util.EnumFacing;

public class NoSlow extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Watchdog", "Vanilla","Dev","Exploit","MMC", "NCP","Switch", "Watchdog");
    private boolean synced;

    public NoSlow() {
        super("NoSlow", Category.PLAYER, "prevent item slowdown");
        this.addSettings(mode);
    }

    @Override
    public void onSlowDownEvent(SlowdownEvent event) {
        event.cancel();
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        switch (mode.getMode()) {
            case"Exploit":
                if(mc.thePlayer.isUsingItem()) {
                    if (event.getPacket() instanceof C07PacketPlayerDigging) {
                        final C07PacketPlayerDigging wrapper = (C07PacketPlayerDigging) event.getPacket();

                        if (wrapper.getClass().equals(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM)) {
                            event.cancel();


                        }
                    }
                }


                break;

            case"Dev":
               // if(mc.thePlayer.isUsingItem()) {
                //    if (event.getPacket() instanceof C0BPacketEntityAction) {
              //          final C0BPacketEntityAction packetEntityAction = (C0BPacketEntityAction) event.getPacket();
              //          if (packetEntityAction.getAction().equals(C0BPacketEntityAction.Action.START_SNEAKING)) {
             //               event.cancel();
             //           }
            //        }
            //    }

                break;
        }

    }

    @Override
    public void onMotionEvent(MotionEvent e) {
        this.setSuffix(mode.getMode());
        switch (mode.getMode()) {
            case "Watchdog":
                if (mc.thePlayer.onGround && mc.thePlayer.isUsingItem() && MovementUtils.isMoving()) {
                    if (e.isPre()) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        synced = true;
                    } else {
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem < 8 ? mc.thePlayer.inventory.currentItem + 1 : mc.thePlayer.inventory.currentItem - 1));
                        synced = false;
                    }
                }
                if (!synced) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    synced = true;
                }
                break;


            case"Dev":
                if(mc.thePlayer.isBlocking()) {

                  // mc.gameSettings.keyBindSneak.pressed = true;
                    mc.thePlayer.sendQueue.addToSendQueue( new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9));
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                }

                break;


            case"MMC":
                if(mc.thePlayer.isUsingItem()) {
                    mc.gameSettings.keyBindSprint.pressed = false;
                    mc.thePlayer.setSprinting(false);
                } break;

            case"Switch":
                if (MovementUtils.isMoving() && mc.thePlayer.isUsingItem()) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                }
                break;
            case "NCP":

                if (MovementUtils.isMoving() && mc.thePlayer.isUsingItem()) {
                    if (e.isPre()) {
                        PacketUtils.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPosition.ORIGIN, EnumFacing.DOWN));
                    } else {
                        PacketUtils.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
                    }

                }
                break;
        }
    }

}
