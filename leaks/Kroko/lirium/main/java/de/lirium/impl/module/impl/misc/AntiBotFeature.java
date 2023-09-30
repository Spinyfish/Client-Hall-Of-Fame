package de.lirium.impl.module.impl.misc;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.impl.events.PacketEvent;
import de.lirium.impl.module.ModuleFeature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketEntity;

import java.util.ArrayList;
import java.util.List;

@ModuleFeature.Info(name = "Anti Bot", description = "Prevents to attack bots", category = ModuleFeature.Category.MISCELLANEOUS)
public class AntiBotFeature extends ModuleFeature {
    @Value(name = "Hypixel Bots")
    private static final CheckBox hypixelBots = new CheckBox(false);

    @Value(name = "Universal Check")
    private static final CheckBox universalCheck = new CheckBox(true);

    @Value(name = "Valid Names")
    private static final CheckBox validNames = new CheckBox(false);

    private static final List<Entity> universalPlayers = new ArrayList<>();

    @EventHandler
    public Listener<PacketEvent> packetEventListener = e -> {
        if (universalCheck.getValue())
            handleUniversal(e);
    };

    public boolean isBot(Entity entity) {
        if (!isEnabled() || !(entity instanceof EntityPlayer)) return false;
        else {
            final EntityPlayer player = (EntityPlayer) entity;
            if (hypixelBots.getValue() && !hasPing(player, 1)) return true;
            if (validNames.getValue() && !isValidEntityName(entity)) return true;
            if(universalCheck.getValue() && !universalPlayers.contains(entity)) return true;
        }
        return false;
    }

    private void handleUniversal(PacketEvent event) {
        final Packet<?> packet = event.packet;
        if (event.state == PacketEvent.State.RECEIVE) {
            if (packet instanceof SPacketEntity.S15PacketEntityRelMove) {
                final SPacketEntity.S15PacketEntityRelMove relMove = (SPacketEntity.S15PacketEntityRelMove) packet;
                final Entity entity = relMove.getEntity(getWorld());
                if (!universalPlayers.contains(entity))
                    universalPlayers.add(entity);
            }
        }
    }

    @Override
    public void onEnable() {
        universalPlayers.clear();
        super.onEnable();
    }
}
