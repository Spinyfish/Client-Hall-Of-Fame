package dev.africa.pandaware.impl.module.movement;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.EnumSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.utils.client.Timer;
import lombok.AllArgsConstructor;
import lombok.var;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;

import java.util.concurrent.ConcurrentLinkedQueue;

@ModuleInfo(name = "Blink", category = Category.MOVEMENT)
public class BlinkModule extends Module {
    private final EnumSetting<BlinkMode> blinkMode = new EnumSetting<>("Mode", BlinkMode.TWITTER);
    private final NumberSetting delayTimer = new NumberSetting("Delay Seconds", 30, 1, 5, 0.5,
            () -> blinkMode.getValue() == BlinkMode.DELAY);

    private final BooleanSetting c04 = new BooleanSetting("C04", true);
    private final BooleanSetting c05 = new BooleanSetting("C05", true);
    private final BooleanSetting c06 = new BooleanSetting("C06", true);
    private final BooleanSetting c07 = new BooleanSetting("C07", false);
    private final BooleanSetting c08 = new BooleanSetting("C08", false);
    private final BooleanSetting c09 = new BooleanSetting("C09", false);
    private final BooleanSetting c00 = new BooleanSetting("C00", false);
    private final BooleanSetting c0f = new BooleanSetting("C0F", false);

    private final Timer timer = new Timer();
    private final ConcurrentLinkedQueue<Packet<?>> packetList = new ConcurrentLinkedQueue<>();

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        var packet = event.getPacket();
        switch (blinkMode.getValue()) {
            case TWITTER:
                if (packet instanceof C03PacketPlayer.C04PacketPlayerPosition && c04.getValue()) event.cancel();
                if (packet instanceof C03PacketPlayer.C05PacketPlayerLook && c05.getValue()) event.cancel();
                if (packet instanceof C03PacketPlayer.C06PacketPlayerPosLook && c06.getValue()) event.cancel();
                if (packet instanceof C07PacketPlayerDigging && c07.getValue()) event.cancel();
                if (packet instanceof C08PacketPlayerBlockPlacement && c08.getValue()) event.cancel();
                if (packet instanceof C09PacketHeldItemChange && c09.getValue()) event.cancel();
                if (packet instanceof C00PacketKeepAlive && c00.getValue()) event.cancel();
                if (packet instanceof C0FPacketConfirmTransaction && c0f.getValue()) event.cancel();
                break;
            case DELAY:
                if (c04.getValue()) {
                    if (event.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition) {
                        this.packetList.add(event.getPacket());
                        event.cancel();
                    }
                }
                if (c05.getValue()) {
                    if (packet instanceof C03PacketPlayer.C05PacketPlayerLook) {
                        this.packetList.add(packet);
                        event.cancel();
                    }
                }
                if (c06.getValue()) {
                    if (packet instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
                        this.packetList.add(packet);
                        event.cancel();
                    }
                }
                if (c07.getValue()) {
                    if (packet instanceof C07PacketPlayerDigging) {
                        this.packetList.add(packet);
                        event.cancel();
                    }
                }
                if (c08.getValue()) {
                    if (packet instanceof C08PacketPlayerBlockPlacement) {
                        this.packetList.add(packet);
                        event.cancel();
                    }
                }
                if (c09.getValue()) {
                    if (packet instanceof C09PacketHeldItemChange) {
                        this.packetList.add(packet);
                        event.cancel();
                    }
                }
                if (c00.getValue()) {
                    if (packet instanceof C00PacketKeepAlive) {
                        this.packetList.add(packet);
                        event.cancel();
                    }
                }
                if (c0f.getValue()) {
                    if (packet instanceof C0FPacketConfirmTransaction) {
                        this.packetList.add(packet);
                        event.cancel();
                    }
                }

                if (timer.hasReached(delayTimer.getValue().doubleValue() * 1000) && mc.thePlayer != null && mc.theWorld != null) {
                    packetList.forEach(packet1 -> mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(this.packetList.poll()));
                    this.timer.reset();
                }
                break;
        }
    };

    public BlinkModule() {
        this.registerSettings(
                this.blinkMode,
                this.delayTimer,
                this.c04,
                this.c05,
                this.c06,
                this.c07,
                this.c08,
                this.c09,
                this.c00,
                this.c0f
        );
    }

    @Override
    public void onDisable() {
        packetList.forEach(packet1 -> mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(this.packetList.poll()));
        this.timer.reset();
    }

    @AllArgsConstructor
    private enum BlinkMode {
        TWITTER("Cancel"),
        DELAY("Delay");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }
}
