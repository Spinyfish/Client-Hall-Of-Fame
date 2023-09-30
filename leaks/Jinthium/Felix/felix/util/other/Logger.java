
package felix.util.other;

import felix.Client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class Logger {
	
    public static void print(final String msg) {
        Minecraft.getMinecraft().thePlayer.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.GRAY + "[" + EnumChatFormatting.AQUA + "Felix" + EnumChatFormatting.GRAY + "]" + EnumChatFormatting.WHITE + " " + msg));
    }
}
