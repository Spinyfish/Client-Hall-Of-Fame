/**
 * @project hakarware
 * @author CodeMan
 * @at 25.07.23, 19:27
 */

package cc.swift.util;

import lombok.experimental.UtilityClass;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

@UtilityClass
public class ChatUtil implements IMinecraft {
    public void printChatMessage(Object message) {
        mc.thePlayer.addChatMessage(new ChatComponentText("[" + EnumChatFormatting.AQUA + "Swift" + EnumChatFormatting.RESET + "] " + message.toString()));
    }
}
