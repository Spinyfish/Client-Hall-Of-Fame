package tech.dort.dortware.impl.utils.player;

import net.minecraft.util.ChatComponentText;
import tech.dort.dortware.api.util.Util;

public class ChatUtil implements Util {

    public static void displayChatMessage(final String message) {
        if (mc.thePlayer == null) {
            System.out.println(String.join(" ", "[Meme]", message));
        } else {
            mc.thePlayer.addChatMessage(new ChatComponentText(String.join(" ", "\u00a77[\u00a7cMemeClient\u00a77]\u00a7f", message)));
        }
    }

    public static void displayFlag(final String name, final String cheat, final int vl) {
        if (mc.thePlayer == null) {
            System.out.println(String.join(" ", "[Meme]", name + " flagged " + cheat + " - VL: " + vl + "."));
        } else {
            mc.thePlayer.addChatMessage(new ChatComponentText(String.join(" ", "\u00a77[\u00a7cMemeClient\u00a77]", name + " flagged \u00a7c" + cheat + "\u00a77 - VL: \u00a7c" + vl + "\u00a77.")));
        }
    }
}
