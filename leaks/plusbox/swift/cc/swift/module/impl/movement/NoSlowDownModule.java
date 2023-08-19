/**
 * @project hakarware
 * @author CodeMan
 * @at 26.07.23, 22:38
 */

package cc.swift.module.impl.movement;

import cc.swift.events.EventState;
import cc.swift.events.ItemSlowDownEvent;
import cc.swift.events.PacketEvent;
import cc.swift.events.UpdateWalkingPlayerEvent;
import cc.swift.module.Module;
import cc.swift.value.impl.ModeValue;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.concurrent.ThreadLocalRandom;

public final class NoSlowDownModule extends Module {

    public final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.values());

    private boolean using, sendPacket, rotate;

    public NoSlowDownModule() {
        super("NoSlowDown", Category.MOVEMENT);
        this.registerValues(mode);
    }

    @Handler
    public final Listener<UpdateWalkingPlayerEvent> updateWalkingPlayerEventListener = event -> {
        if (!mc.thePlayer.isUsingItem()) {
            using = false;
            return;
        }
        boolean isSword = mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
        boolean isConsumable = mc.thePlayer.getHeldItem() != null && (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood || mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion);

        if (event.getState() == EventState.PRE) {
            switch (mode.getValue()) {
                case HYPIXEL:
                    if (isSword) {
                        int slot = mc.thePlayer.inventory.currentItem;
                        while (slot == mc.thePlayer.inventory.currentItem || slot == mc.thePlayer.inventory.currentItem + 1) {
                            slot = ThreadLocalRandom.current().nextInt(9);
                        }
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                        using = true;
                    } else if (isConsumable) {
                        if (this.rotate) {
                            this.rotate = false;
                            event.setPitch(90);
                        } else if (this.sendPacket) {
                            this.sendPacket = false;
                            mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getPosition().down(), 1, mc.thePlayer.getHeldItem(), 0, 0, 0), null);
                        }
                    }
                    break;
                case NCP:
                    if (using) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        using = false;
                    }
                    break;
            }
        } else {
            switch (mode.getValue()) {
                case NCP:
                    if (!using) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                        using = true;
                    }
                    break;
            }
        }
    };

    @Handler
    public final Listener<PacketEvent> packetEventListener = event -> {
        if (event.getDirection() != EventState.SEND || event.getState() != EventState.PRE) return;

        if (this.mode.getValue() != Mode.HYPIXEL) return;

        if (event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            boolean isConsumable = mc.thePlayer.getHeldItem() != null && (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood || (mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion && !ItemPotion.isSplash(mc.thePlayer.getHeldItem().getMetadata())));
            if (isConsumable) {
                event.setCancelled(true);
                this.sendPacket = true;
                this.rotate = true;
            }
        }
    };

    @Handler
    public final Listener<ItemSlowDownEvent> itemSlowDownEventListener = event -> {
        boolean isSword = mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
        boolean isConsumable = mc.thePlayer.getHeldItem() != null && (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood || mc.thePlayer.getHeldItem().getItem() instanceof ItemBow || mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion);

        if (isSword || isConsumable || mode.getValue() != Mode.HYPIXEL) {
            event.setForward(1);
            event.setStrafe(1);
            event.setSprinting(true);
        }
    };

    public enum Mode {
        VANILLA, NCP, HYPIXEL
    }
}
