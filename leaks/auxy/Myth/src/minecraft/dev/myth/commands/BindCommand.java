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
import org.lwjgl.input.Keyboard;

@Command.Info(name = "bind")
public class BindCommand extends Command {

    @Override
    public void run(String[] args) {
        if(args.length < 2) {
            Logger.doLog(Logger.Type.ERROR, "Usage: bind <module> <key>");
            return;
        }
        Feature feature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(args[0]);
        if(feature == null) {
            Logger.doLog(Logger.Type.ERROR, "Module not found!");
            return;
        }
        feature.setKeyBind(Keyboard.getKeyIndex(args[1].toUpperCase()));
        Logger.doLog(Logger.Type.INFO, "Set keybind for " + feature.getName() + " to " + Keyboard.getKeyName(feature.getKeyBind()));
    }
}
