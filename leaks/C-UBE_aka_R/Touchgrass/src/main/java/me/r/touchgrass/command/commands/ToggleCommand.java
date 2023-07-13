package me.r.touchgrass.command.commands;

import me.r.touchgrass.touchgrass;
import me.r.touchgrass.file.files.ModuleConfig;
import me.r.touchgrass.module.Module;
import me.r.touchgrass.command.Command;

/**
 * Created by r on 14/03/2021
 */
public class ToggleCommand extends Command {

    public void execute(String[] args) {
        if (args.length != 1) {
            msg(getAll());
        } else {
            String module = args[0];
            Module mod = touchgrass.getClient().moduleManager.getModulebyName(module);
            if (mod == null) {
                msg("§cThe requested module was not found!");
            } else {
                touchgrass.getClient().moduleManager.getModulebyName(module).toggle();
                msg(String.format("§b%s §7has been %s", touchgrass.getClient().moduleManager.getModulebyName(module).getName(), touchgrass.getClient().moduleManager.getModulebyName(module).isEnabled() ? "§aenabled" : "§cdisabled."));
                ModuleConfig moduleConfig = new ModuleConfig();
                moduleConfig.saveConfig();
            }
        }
    }

    public String getName() {
        return "t";
    }

    public String getDesc() {
        return "Toggles modules.";
    }

    public String getSyntax() {
        return ".t";
    }

    public String getAll() {
        return getSyntax() + " - " + getDesc();
    }

}
