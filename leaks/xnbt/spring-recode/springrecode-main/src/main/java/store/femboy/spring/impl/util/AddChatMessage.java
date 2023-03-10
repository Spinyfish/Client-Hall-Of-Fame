package store.femboy.spring.impl.util;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class AddChatMessage {
    public static void print(String message) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.GREEN + "[Spring] "+ ChatFormatting.GRAY + message));
    }
}
