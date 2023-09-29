/**
 * @project LiriumV4
 * @author Skush/Duzey
 * @at 03.09.2022
 */
package de.lirium.impl.module.impl.misc;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import com.mojang.realmsclient.gui.ChatFormatting;
import de.lirium.impl.events.PacketEvent;
import de.lirium.impl.module.ModuleFeature;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

@ModuleFeature.Info(name = "Chat Unblocker", description = "Bypass block drop's chat spamming checks", category = ModuleFeature.Category.MISCELLANEOUS)
public class ChatUnblockerFeature extends ModuleFeature {

    @EventHandler
    public final Listener<PacketEvent> eventListener = e -> {
        if (e.packet instanceof SPacketChat) {
            final SPacketChat packetChat = (SPacketChat) e.packet;

            final String unformatted = ChatFormatting.stripFormatting(packetChat.getChatComponent().getUnformattedText());

            if (unformatted.contains("(!) Please type") && unformatted.contains("to continue sending messages/commands.")) {
                try {
                    final String[] split = unformatted.split("'");
                    getPlayer().sendChatMessage(split[1]);
                    sendMessage("Sent: " + split[1]);
                } catch (Exception ignored) {
                }
            }
        }
    };
}