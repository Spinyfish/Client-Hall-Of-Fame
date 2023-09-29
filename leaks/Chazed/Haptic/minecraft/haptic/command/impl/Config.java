package haptic.command.impl;

import java.io.IOException;

import haptic.Haptic;
import haptic.command.Command;
import haptic.config.SaveLoad;

public class Config extends Command {
    public Config() {
        super("Config", "config issue", ".config <args>", "cfg");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if(args.length > 1) {
            String action = args[0];

            if(args[1] == null) return;

            if(action.equalsIgnoreCase("load")) {
                SaveLoad saveLoad1 = new SaveLoad(args[1], false);
                saveLoad1.load(false);
                Haptic.addChatMessage("Loaded config " + args[1]);
            }
            if(action.equalsIgnoreCase("save")) {
                SaveLoad saveLoad2 = new SaveLoad(args[1], false);
                saveLoad2.save();
                Haptic.addChatMessage("Saved config " + args[1]);
            }
            if(action.equalsIgnoreCase("")) {
                SaveLoad saveLoad2 = new SaveLoad("default", false);
                saveLoad2.save();
                Haptic.addChatMessage("Saved config default");
            }
        }
    }
}
