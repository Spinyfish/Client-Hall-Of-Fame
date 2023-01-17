package com.alan.clients.module.impl.movement.noslow;

import com.alan.clients.Client;
import com.alan.clients.component.impl.player.BadPacketsComponent;
import com.alan.clients.component.impl.player.SlotComponent;
import com.alan.clients.module.impl.combat.KillAura;
import com.alan.clients.module.impl.movement.NoSlow;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.motion.SlowDownEvent;
import com.alan.clients.newevent.impl.other.TeleportEvent;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.value.Mode;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

/**
 * @author Auth
 * @since 18/11/2021
 */

public class WatchdogNoSlow extends Mode<NoSlow> {

    private int disable;

    public WatchdogNoSlow(String name, NoSlow parent) {
        super(name, parent);
    }

    @EventLink
    public final Listener<PreMotionEvent> onPreMotion = event -> {
        this.disable++;
        if (InstanceAccess.mc.thePlayer.isUsingItem() && this.disable > 10 && !BadPacketsComponent.bad(false,
                true, true, false, false) && Client.INSTANCE.getModuleManager().get(KillAura.class).target == null) {
            PacketUtil.send(new C09PacketHeldItemChange(SlotComponent.getItemIndex() % 8 + 1));
            PacketUtil.send(new C09PacketHeldItemChange(SlotComponent.getItemIndex()));
        }
    };

    @EventLink
    public final Listener<SlowDownEvent> onSlowDown = event -> {
        if(Client.INSTANCE.getModuleManager().get(KillAura.class).target == null) event.setCancelled(true);
    };


    @EventLink()
    public final Listener<TeleportEvent> onTeleport = event -> {
        this.disable = 0;
    };
}