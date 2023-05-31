/**
 * @project LiriumV4
 * @author Skush/Duzey
 * @at 13.12.2022
 */
package de.lirium.impl.module.impl.player;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.helper.TimeHelper;
import de.lirium.base.setting.Dependency;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.ComboBox;
import de.lirium.base.setting.impl.SliderSetting;
import de.lirium.impl.events.PacketEvent;
import de.lirium.impl.events.UpdateEvent;
import de.lirium.impl.module.ModuleFeature;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketKeepAlive;

import java.util.concurrent.CopyOnWriteArrayList;

@ModuleFeature.Info(name = "Ping Spoof", description = "Spoof your ping", category = ModuleFeature.Category.PLAYER)
public class PingSpoofFeature extends ModuleFeature {

    @Value(name = "Mode")
    final ComboBox<String> mode = new ComboBox<>("Basic", new String[]{"Basic"});

    @Value(name = "Ping")
    final SliderSetting<Long> ping = new SliderSetting<>(1000L, 0L, 30000L);

    final TimeHelper timer = new TimeHelper();
    final CopyOnWriteArrayList<Packet<? extends INetHandler>> packets = new CopyOnWriteArrayList<>();

    @EventHandler
    public final Listener<UpdateEvent> updateEvent = e -> {

        switch (this.mode.getValue()) {
            case "Basic": {
                if (this.timer.hasReached(this.ping.getValue())) {
                    this.packets.forEach(this::sendPacketUnlogged);
                    this.packets.clear();
                    this.timer.reset();
                }
                break;
            }
        }
    };

    @EventHandler
    public final Listener<PacketEvent> packetEvent = e -> {
        final Packet<? extends INetHandler> packet = e.packet;

        if (mc.isSingleplayer()) return;

        switch (this.mode.getValue()) {
            case "Basic": {
                if (packet instanceof CPacketConfirmTransaction
                        || packet instanceof CPacketKeepAlive) {
                    this.packets.add(packet);
                    e.setCancelled(true);
                }
                break;
            }
        }
    };

    @Override
    public void onDisable() {
        super.onDisable();

        this.timer.reset();
        if (!this.packets.isEmpty()) {
            this.packets.forEach(this::sendPacketUnlogged);
            this.packets.clear();
        }
    }
}