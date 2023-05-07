package client.util.chat;

import client.Client;
import client.util.interfaces.InstanceAccess;
import client.util.packet.PacketUtil;
import lombok.experimental.UtilityClass;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

@UtilityClass
public class ChatUtil implements InstanceAccess {

    public void display(final Object message, final Object... objects) {
        if (mc.thePlayer != null) {
            final String format = String.format(message.toString(), objects);

            mc.thePlayer.addChatMessage(new ChatComponentText(getPrefix() + format));
        }
    }

    public void displayNoPrefix(final Object message, final Object... objects) {
        if (mc.thePlayer != null) {
            final String format = String.format(message.toString(), objects);

            mc.thePlayer.addChatMessage(new ChatComponentText(getPrefix() + format));
        }
    }

    public void send(final Object message) {
        if (mc.thePlayer != null) {
            PacketUtil.send(new C01PacketChatMessage(message.toString()));
        }
    }

    private String getPrefix() {
        final String color = EnumChatFormatting.AQUA.toString();
        return EnumChatFormatting.BOLD + color + Client.NAME
                + EnumChatFormatting.RESET + color + " » "
                + EnumChatFormatting.RESET;
    }
}
