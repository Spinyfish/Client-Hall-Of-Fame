/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.commands.defaultsubs;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.command.ViaSubCommand;
import com.viaversion.viaversion.api.configuration.ConfigurationProvider;

public class AutoTeamSubCmd
extends ViaSubCommand {
    @Override
    public String name() {
        return "autoteam";
    }

    @Override
    public String description() {
        return "Toggle automatically teaming to prevent colliding.";
    }

    @Override
    public boolean execute(ViaCommandSender sender, String[] args) {
        ConfigurationProvider provider = Via.getPlatform().getConfigurationProvider();
        boolean newValue = !Via.getConfig().isAutoTeam();
        provider.set("auto-team", newValue);
        provider.saveConfig();
        AutoTeamSubCmd.sendMessage(sender, "&6We will %s", newValue ? "&aautomatically team players" : "&cno longer auto team players");
        AutoTeamSubCmd.sendMessage(sender, "&6All players will need to re-login for the change to take place.", new Object[0]);
        return true;
    }
}

