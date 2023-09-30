package club.pulsive.impl.module.impl.misc;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.impl.event.network.PacketEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.property.implementations.EnumProperty;
import club.pulsive.impl.util.client.Logger;
import club.pulsive.impl.util.client.TimerUtil;
import lombok.AllArgsConstructor;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S45PacketTitle;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static club.pulsive.api.minecraft.MinecraftUtil.mc;

@ModuleInfo(name = "Auto Hypixel", category = Category.MISC)
public class AutoHypixel extends Module {
    private EnumProperty<MODE> mode = new EnumProperty<MODE>("Mode", MODE.SOLO);
    @EventHandler
    private final Listener<PacketEvent> packetEventListener = event -> {
        switch(event.getEventState()) {
            case RECEIVING:
                if (event.getPacket() instanceof S45PacketTitle) {
                    S45PacketTitle s45 = (S45PacketTitle) event.getPacket();
                    if (s45.getMessage() != null) {
                        String message = s45.getMessage().getFormattedText().toLowerCase();

                        if (message.contains("you died") || message.contains("game over")) {
                            Logger.print("You Lost Mah boi, player bettah");
                            mc.thePlayer.sendChatMessage("/play solo_insane");
                        }
                        else if (message.contains("you win") || message.contains("victory")) {
                            Logger.print("You Won! Pretty decent job mah retarded friend");
                            mc.thePlayer.sendChatMessage("/play solo_insane");
                        }
                    }
                }
                break;
        }
    };

    @AllArgsConstructor
    private enum MODE {
        SOLO("Solo"),
        DOUBLES("Doubles");

        private final String modeName;

        @Override
        public String toString() {return modeName;}
    }
}
