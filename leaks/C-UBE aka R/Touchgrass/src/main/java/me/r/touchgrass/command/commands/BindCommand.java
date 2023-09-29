package me.r.touchgrass.command.commands;

import me.r.touchgrass.touchgrass;
import me.r.touchgrass.file.files.ModuleConfig;
import me.r.touchgrass.module.Module;
import me.r.touchgrass.utils.KeybindUtil;
import me.r.touchgrass.command.Command;

/**
 * Created by r on 13/03/2021
 */
public class BindCommand extends Command {

    public void execute(String[] args) {
        if (args.length == 0) {
            msg(getAll());
        } else if (args.length == 2) {
            String key = args[0];
            String value = args[1];
            KeybindUtil mgr = touchgrass.getClient().keybindManager;
            if (key.equalsIgnoreCase("reset")) {
                Module mod = touchgrass.getClient().moduleManager.getModulebyName(value);
                if (mod == null) {
                    msg(String.format("§cThe module §b%s §cwas not found!", value));
                } else {
                    mgr.unbind(mod);
                    msg("§7Succesfully reset the bind for §b" + mod.getName() + "§7!");
                }
                return;
            }
            Module mod = touchgrass.getClient().moduleManager.getModulebyName(key);
            if (mod == null) {
                msg(String.format("§cThe specified key or module §cwas not found!", value));
            } else {
                mgr.bind(mod, mgr.toInt(value));
                msg(String.format("§7Bound §b%s §7to §b%s§7!", mod.getName(), value));
                ModuleConfig moduleConfig = new ModuleConfig();
                moduleConfig.saveConfig();
            }
        } else {
            msg("§cSyntax Error.");
        }
    }


    public String getName() {
        return "bind";
    }

    public String getSyntax() {
        return ".bind <module> <key>";
    }

    public String getDesc() {
        return "Sets binds for modules.";
    }

    public String getAll() {
        return getSyntax() + " - " + getDesc();
    }
}
