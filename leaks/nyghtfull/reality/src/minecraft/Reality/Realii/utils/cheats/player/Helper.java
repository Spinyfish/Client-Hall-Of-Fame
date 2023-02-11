/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.utils.cheats.player;

import Reality.Realii.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;

public class Helper {
    public static Minecraft mc = Minecraft.getMinecraft();

    public static void sendMessageOLD(String msg) {
        Object[] arrobject = new Object[2];
        Client.instance.getClass();
        arrobject[0] = (Object)((Object)EnumChatFormatting.DARK_GRAY) + Client.CLIENT_NAME + (Object)((Object)EnumChatFormatting.GRAY) + ": ";
        arrobject[1] = msg;
        Helper.mc.thePlayer.addChatMessage(new ChatComponentText(String.format("%s%s", arrobject)));
    }

    public static void sendMessage(String message) {
        new ChatUtils.ChatMessageBuilder(true, true).appendText(message).setColor(EnumChatFormatting.DARK_PURPLE).build().displayClientSided();
    }

    public static void sendMessageWithoutPrefix(String message) {
        new ChatUtils.ChatMessageBuilder(false, true).appendText(message).setColor(EnumChatFormatting.DARK_PURPLE).build().displayClientSided();
    }

    public static boolean onServer(String server) {
        if (!mc.isSingleplayer() && Helper.mc.getCurrentServerData().serverIP.toLowerCase().contains(server)) {
            return true;
        }
        return false;
    }

    public static int reAlpha(int color, float alpha) {
        Color c = new Color(color);
        float r = 0.003921569F * (float)c.getRed();
        float g = 0.003921569F * (float)c.getGreen();
        float b = 0.003921569F * (float)c.getBlue();
        return (new Color(r, g, b, alpha)).getRGB();
    }
}

