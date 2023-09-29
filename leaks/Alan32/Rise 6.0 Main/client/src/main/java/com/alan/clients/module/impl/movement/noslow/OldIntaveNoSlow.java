package com.alan.clients.module.impl.movement.noslow;

import com.alan.clients.component.impl.hypixel.InventoryDeSyncComponent;
import com.alan.clients.component.impl.player.SlotComponent;
import com.alan.clients.module.impl.movement.NoSlow;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PostMotionEvent;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.motion.SlowDownEvent;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.value.Mode;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public class OldIntaveNoSlow extends Mode<NoSlow> {

    @EventLink
    public final Listener<PreMotionEvent> onPreMotion = event -> {
//        InventoryDeSyncComponent.setActive("/booster");

        if (mc.thePlayer.isUsingItem()) {
            PacketUtil.send(new C09PacketHeldItemChange(SlotComponent.getItemIndex() % 8 + 1));
            PacketUtil.send(new C09PacketHeldItemChange(SlotComponent.getItemIndex()));
        }
    };

    @EventLink
    public final Listener<PostMotionEvent> onPostMotion = event -> {
        if (mc.thePlayer.isUsingItem() && InventoryDeSyncComponent.isDeSynced()) {
            PacketUtil.send(new C08PacketPlayerBlockPlacement(SlotComponent.getItemStack()));
        }
    };

    @EventLink
    public final Listener<SlowDownEvent> onSlowDown = event -> {
        event.setCancelled(true);
    };

    public OldIntaveNoSlow(String name, NoSlow parent) {
        super(name, parent);
    }
}