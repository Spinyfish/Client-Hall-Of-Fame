package com.alan.clients.module.impl.movement.noslow;

import com.alan.clients.component.impl.player.BlinkComponent;

import com.alan.clients.module.impl.movement.NoSlow;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.motion.SlowDownEvent;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.value.Mode;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * @author Alan
 * @since 13.03.2022
 */
public class IntaveNoSlow extends Mode<NoSlow> {

    boolean usingItem;

    public IntaveNoSlow(String name, NoSlow parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        final Item item = InstanceAccess.mc.thePlayer.getCurrentEquippedItem().getItem();

        if (InstanceAccess.mc.thePlayer.isUsingItem()) {
            if (item instanceof ItemSword) {
                BlinkComponent.blinking = true;

                if (InstanceAccess.mc.thePlayer.ticksExisted % 5 == 0) {
                    PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    BlinkComponent.dispatch();
                    InstanceAccess.mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(InstanceAccess.mc.thePlayer.getCurrentEquippedItem()));
                }

            } else if (item instanceof ItemFood || item instanceof ItemBow) {
                BlinkComponent.blinking = true;
            }

            usingItem = true;
        } else if (usingItem) {
            usingItem = false;

            BlinkComponent.blinking = false;
        }
    };

    @EventLink()
    public final Listener<SlowDownEvent> onSlowDown = event -> {
        event.setCancelled(true);
    };
}
