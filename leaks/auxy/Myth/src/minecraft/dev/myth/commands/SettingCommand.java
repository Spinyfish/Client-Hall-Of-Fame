/**
 * @project Myth
 * @author CodeMan
 * @at 05.08.22, 17:23
 */
package dev.myth.commands;

import dev.myth.api.command.Command;
import dev.myth.api.feature.Feature;
import dev.myth.api.logger.Logger;
import dev.myth.api.setting.Setting;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;

@Command.Info(name = "setting")
public class SettingCommand extends Command {

    @Override
    public void run(String[] args) {
        if(args.length < 3) {
            Logger.doLog(Logger.Type.ERROR, "Usage: setting <module> <setting> <value>");
            return;
        }
        Feature feature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(args[0]);
        if(feature == null) {
            Logger.doLog(Logger.Type.ERROR, "Module not found!");
            return;
        }
        Setting<?> setting = feature.getSettingByName(args[1]);
        if(setting == null) {
            Logger.doLog(Logger.Type.ERROR, "Setting not found!");
            return;
        }
        setting.setValueFromString(args[2]);
        Logger.doLog(Logger.Type.INFO, "Set setting " + setting.getName() + " for " + feature.getName() + " to " + setting.getValue());
    }
}
