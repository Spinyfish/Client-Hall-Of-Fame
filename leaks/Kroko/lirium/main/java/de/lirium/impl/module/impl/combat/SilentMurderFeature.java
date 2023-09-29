package de.lirium.impl.module.impl.combat;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.impl.events.AttackEntityEvent;
import de.lirium.impl.events.AttackEvent;
import de.lirium.impl.events.ItemSyncEvent;
import de.lirium.impl.events.PacketEvent;
import de.lirium.impl.module.ModuleFeature;
import net.minecraft.network.play.client.CPacketHeldItemChange;

@ModuleFeature.Info(name = "Silent Murder", description = "Kill players with your hand as a murderer", category = ModuleFeature.Category.COMBAT)
public class SilentMurderFeature extends ModuleFeature {

    private final int slot = 1;

    @EventHandler
    public Listener<AttackEntityEvent> attackEntityEventListener = e -> {
        switch (e.state) {
            case BEFORE:
                sendPacket(new CPacketHeldItemChange(slot));
                break;
            case AFTER:
                sendPacket(new CPacketHeldItemChange(getPlayer().inventory.currentItem));
                break;
        }
    };

    @EventHandler
    public Listener<ItemSyncEvent> itemSyncEventListener = e -> {
        if (e.slot == slot)
            e.setCancelled(true);
    };
}
