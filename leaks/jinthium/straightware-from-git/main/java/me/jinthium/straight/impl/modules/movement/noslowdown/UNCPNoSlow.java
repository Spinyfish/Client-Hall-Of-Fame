package me.jinthium.straight.impl.modules.movement.noslowdown;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.components.BadPacketsComponent;
import me.jinthium.straight.impl.event.game.TeleportEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.modules.combat.KillAura;
import me.jinthium.straight.impl.modules.movement.NoSlowDown;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

import java.util.Objects;

@ModeInfo(name = "UpdatedNCP", parent = NoSlowDown.class)
public class UNCPNoSlow extends ModuleMode<NoSlowDown> {
    private int ticks;

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {
        if(event.isPre()){
            this.ticks++;
            if (mc.thePlayer.isUsingItem() && this.ticks > 10 && !BadPacketsComponent.bad(false,
                    true, true, false, false) && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBow)) {
                PacketUtil.sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                PacketUtil.sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }
        }
    };

    @Callback
    final EventCallback<TeleportEvent> teleportEventEventCallback = event -> ticks = 0;
}
