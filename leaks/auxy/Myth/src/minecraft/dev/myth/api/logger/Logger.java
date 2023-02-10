/**
 * @project Myth
 * @author Skush/Duzey
 * @at 05.08.2022
 */
package dev.myth.api.logger;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class Logger {

    public static void doLog(final Object obj) {
        doLog(Type.INFO, obj);
    }

    public static void doLog(final Type type, final Object obj) {
        if (Minecraft.getMinecraft().thePlayer == null) {
            System.out.println(obj);
            return;
        }
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(type.getPrefix() + ": " + obj));
    }

    public enum Type {
        INFO("§7INFO"), WARNING("§eWARNING"), ERROR("§cERROR");

        @Getter
        private final String prefix;

        Type(final String prefix) {
            this.prefix = prefix;
        }
    }
}
