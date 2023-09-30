package dev.africa.pandaware.impl.module.misc;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.setting.EnumSetting;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

@ModuleInfo(name = "Client Spoofer", category = Category.MISC)
public class ClientBrandChangerModule extends Module {
    private final EnumSetting<Brand> mode = new EnumSetting<>("Brand", Brand.FORGE);

    public ClientBrandChangerModule() {
        this.registerSettings(this.mode);
    }

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (event.getPacket() instanceof C17PacketCustomPayload) {
            final C17PacketCustomPayload packet = event.getPacket();
            if (packet.getChannelName().equals("MC|Brand")) {
                event.cancel();
                switch (mode.getValue()) {
                    case COLOR_CODES:

                        StringBuilder sb = new StringBuilder("§c§l§k");

                        for (int i = 0; i < 50; i++) sb.append("#|");

                        mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent((new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString(sb.toString()))));
                        break;
                    case FORGE:
                        mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent((new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("FML"))));
                        break;
                    case LUNAR:
                        mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent((new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("Lunar-Client"))));
                        break;
                    case PVPLOUNGE:
                        mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent((new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("PLC18"))));
                        break;
                    case CHEATBREAKER:
                        mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent((new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("CB"))));
                        break;
                    case FEATHER_FORGE:
                        mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent((new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("Feather Forge"))));
                        break;
                    case FEATHER_FABRIC:
                        mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent((new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("Feather Fabric"))));
                        break;
                    case PANDAWARE:
                        mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent((new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("Pandaware"))));
                        break;
                }
            }
        }
    };

    @AllArgsConstructor
    private enum Brand {
        FORGE("Forge"),
        LUNAR("Lunar"),
        PVPLOUNGE("PVP Lounge"),
        CHEATBREAKER("Cheatbreaker"),
        COLOR_CODES("Color Codes"),
        FEATHER_FORGE("Feather (Forge)"),
        FEATHER_FABRIC("Feather (Fabric)"),
        PANDAWARE("Pandaware");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }
}
