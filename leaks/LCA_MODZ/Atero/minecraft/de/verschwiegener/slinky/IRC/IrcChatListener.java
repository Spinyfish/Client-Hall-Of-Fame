package de.verschwiegener.slinky.IRC;

import de.liquiddev.ircclient.api.SimpleIrcApi;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class IrcChatListener extends SimpleIrcApi {

    @Override
    public void addChat(String message) {
        print(message.replace("�", "<"));
    }

    public static void print(String msg) {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.theWorld != null && msg != null) {
                mc.thePlayer.addChatMessage(new ChatComponentText(msg));
            }
        } catch (Exception ex) {
            System.out.println("Error by printing message: " + msg);
        }
    }
}
