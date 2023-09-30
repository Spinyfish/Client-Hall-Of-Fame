package de.lirium.impl.module.impl.player;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.base.setting.impl.ComboBox;
import de.lirium.impl.events.PacketEvent;
import de.lirium.impl.module.ModuleFeature;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;

import java.util.ArrayList;
import java.util.List;

@ModuleFeature.Info(name = "Blink", description = "Be able to blink away when toggling off", category = ModuleFeature.Category.PLAYER)
public class BlinkFeature extends ModuleFeature {

    private final List<Packet<?>> packets = new ArrayList<>();
    private EntityOtherPlayerMP fakeEntity;

    @Value(name = "Mode")
    private final ComboBox<String> mode = new ComboBox<>("Default", new String[]{"Only move packets"});

    @Value(name = "Prevent keep alive sending")
    private final CheckBox preventKeepAlive = new CheckBox(false);

    @Value(name = "Prevent attacking")
    private final CheckBox preventAttacking = new CheckBox(false);

    @Value(name = "Dismount Vehicle")
    private final CheckBox dismountVehicle = new CheckBox(false);

    @EventHandler
    private final Listener<PacketEvent> packetEventListener = e -> {
        if (e.state == PacketEvent.State.SEND) {
            if(preventAttacking.getValue() && e.packet instanceof CPacketUseEntity) {
                e.setCancelled(true);
                return;
            }
            if (isValid(e.packet)) {
                packets.add(e.packet);
                e.setCancelled(true);
            }
        }
    };

    private boolean isValid(Packet<?> packet) {
        if (!preventKeepAlive.getValue() && packet instanceof CPacketKeepAlive)
            return false;
        switch (mode.getValue()) {
            case "Default":
                return true;
            case "Only move packets":
                return !(packet instanceof CPacketPlayer);
        }
        return false;
    }

    @Override
    public void onEnable() {
        fakeEntity = copy(getPlayer());
        spawn(fakeEntity);
        if (dismountVehicle.getValue() && getPlayer().isRiding()) {
            getPlayer().dismountRidingEntity();
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        packets.forEach(this::sendPacketUnlogged);
        packets.clear();
        if (fakeEntity != null) {
            getWorld().removeEntity(fakeEntity);
            fakeEntity = null;
        }
        super.onDisable();
    }
}
