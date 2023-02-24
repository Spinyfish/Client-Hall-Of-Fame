package me.kansio.client.utils.chat;

import me.kansio.client.utils.Util;
import net.minecraft.util.ChatComponentText;

public class ChatUtil extends Util {

    public static void log(String message) {
        mc.thePlayer.addChatMessage(new ChatComponentText("§bSleek §7» §f" + message));
    }

    public static void logNoPrefix(String message) {
        mc.thePlayer.addChatMessage(new ChatComponentText("§f" + message));
    }

    public static void logSleekCheater(String player, String check) {
        mc.thePlayer.addChatMessage(new ChatComponentText("§7[§b§lSleekAC§7] §f" + player + " §7might be using §b" + check));
    }

    public static void logVerusCheater(String player, String check, String vl) {
        mc.thePlayer.addChatMessage(new ChatComponentText("§9§lVerus §8§l> §f" + player + " §7failed §f " + check + " §7VL[§9" + vl + "§7]"));
    }

    public static String translateColorCodes(String toTranslate) {
        return toTranslate.replaceAll("&", "§");
    }

}
