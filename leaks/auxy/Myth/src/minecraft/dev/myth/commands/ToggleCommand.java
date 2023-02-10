/**
 * @project Myth
 * @author CodeMan
 * @at 05.08.22, 16:03
 */
package dev.myth.commands;

import dev.myth.api.command.Command;
import dev.myth.api.feature.Feature;
import dev.myth.api.logger.Logger;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;

@Command.Info(name = "toggle")
public class ToggleCommand extends Command {

    @Override
    public void run(String[] args) {
        if (args.length < 1) {
            Logger.doLog(Logger.Type.ERROR, "Usage: toggle <module>");
            return;
        }
        Feature feature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(args[0]);
        if (feature == null) {
            Logger.doLog(Logger.Type.ERROR, "Module not found!");
            return;
        }
        feature.toggle();
        Logger.doLog(Logger.Type.INFO, (feature.isEnabled() ? "Enabled " : "Disabled ") + feature.getName());
    }
}
