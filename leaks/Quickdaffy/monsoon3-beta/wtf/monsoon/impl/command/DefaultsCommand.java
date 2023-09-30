/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.command;

import wtf.monsoon.Wrapper;
import wtf.monsoon.api.command.Command;
import wtf.monsoon.api.module.HUDModule;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;

public class DefaultsCommand
extends Command {
    public DefaultsCommand() {
        super("Defaults");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            PlayerUtil.sendClientMessage("Usage: .defaults <module name>");
            return;
        }
        String moduleName = args[0];
        Module module = Wrapper.getMonsoon().getModuleManager().getModuleByName(moduleName);
        if (module != null) {
            for (Setting<?> setting : module.getSettingHierarchy()) {
                if (setting.getName().equals("Keybinding")) continue;
                setting.setValue(setting.getDefaultValue());
            }
            PlayerUtil.sendClientMessage("Set all settings to default value.");
            if (module instanceof HUDModule) {
                HUDModule hudModule = (HUDModule)module;
                hudModule.setX(hudModule.getDefaultX());
                hudModule.setY(hudModule.getDefaultY());
                PlayerUtil.sendClientMessage("Set HUD Module's position back to default.");
            }
        } else {
            PlayerUtil.sendClientMessage("Error: Module doesn't exist. If a module name has spaces, do not include them (.defaults inventorymanager instead of .defaults inventory manager).");
        }
    }
}

