package dev.africa.pandaware.impl.module.misc;

import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.setting.EnumSetting;
import dev.africa.pandaware.utils.math.TimeHelper;
import lombok.AllArgsConstructor;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ModuleInfo(name = "Anti Desync")
public class AntiDesyncModule extends Module {
    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.ALL_C0F);

    private final List<Packet<?>> packetList = new CopyOnWriteArrayList<>();
    private final TimeHelper timer = new TimeHelper();
    private int queuedTimes;

    public AntiDesyncModule() {
        registerSettings(this.mode);
    }

    @Override
    public void onEnable() {
        this.timer.reset();
    }

    @Override
    public void onDisable() {
        this.queuedTimes = 0;
        for (Packet<?> packet : this.packetList) {
            this.packetList.remove(packet);
            mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(packet);
        }
    }

    @EventHandler
    EventCallback<MotionEvent> onPre = event -> {
        if (event.getEventState() == Event.EventState.PRE) {
            if (this.timer.reach(100L)) {
                if (this.queuedTimes++ > 20) {
                    this.queuedTimes = 0;
                    this.packetList.clear();
                }

                for (Packet<?> packet : this.packetList) {
                    mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(packet);
                }

                this.timer.reset();
            }
        }
    };

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
            C0FPacketConfirmTransaction c0FPacketConfirmTransaction = (C0FPacketConfirmTransaction)
                    event.getPacket();
            short action = c0FPacketConfirmTransaction.getUid();

            boolean blockPacket = false;

            switch (this.mode.getValue()) {
                case VERUS_C0F: {
                    blockPacket = action > 0 && action < 70;
                    break;
                }

                case ALL_C0F: {
                    blockPacket = true;
                    break;
                }
            }

            if (blockPacket) {
                this.packetList.add(event.getPacket());
            }
        }
    };

    @AllArgsConstructor
    public enum Mode {
        VERUS_C0F("Verus C0F"),
        ALL_C0F("All C0F");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }
}