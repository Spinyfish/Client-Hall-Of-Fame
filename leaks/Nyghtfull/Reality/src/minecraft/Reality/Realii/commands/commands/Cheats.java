/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.commands.commands;

import Reality.Realii.Client;
import Reality.Realii.commands.Command;
import Reality.Realii.managers.ModuleManager;
import Reality.Realii.mods.Module;
import Reality.Realii.utils.cheats.player.Helper;
import net.minecraft.util.EnumChatFormatting;

public class Cheats
extends Command {
    public Cheats() {
        super("Cheats", new String[]{"mods"}, "", "sketit");
    }

    @Override
    public String execute(String[] args) {
        if (args.length == 0) {
            Client.instance.getModuleManager();
            StringBuilder list = new StringBuilder(String.valueOf(ModuleManager.getModules().size()) + " Cheats - ");
            Client.instance.getModuleManager();
            for (Module cheat : ModuleManager.getModules()) {
                list.append((Object)(cheat.isEnabled() ? EnumChatFormatting.GREEN : EnumChatFormatting.RED)).append(cheat.getName()).append(", ");
            }
            Helper.sendMessage("> " + list.toString().substring(0, list.toString().length() - 2));
        } else {
            Helper.sendMessage("> Correct usage .cheats");
        }
        return null;
    }
}

