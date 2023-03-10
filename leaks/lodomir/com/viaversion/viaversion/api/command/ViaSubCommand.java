/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.command;

import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.util.ChatColorUtil;
import java.util.Collections;
import java.util.List;

public abstract class ViaSubCommand {
    public abstract String name();

    public abstract String description();

    public String usage() {
        return this.name();
    }

    public String permission() {
        return "viaversion.admin";
    }

    public abstract boolean execute(ViaCommandSender var1, String[] var2);

    public List<String> onTabComplete(ViaCommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    public static String color(String s) {
        return ChatColorUtil.translateAlternateColorCodes(s);
    }

    public static void sendMessage(ViaCommandSender sender, String message, Object ... args) {
        sender.sendMessage(ViaSubCommand.color(args == null ? message : String.format(message, args)));
    }
}

