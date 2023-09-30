package axolotl.cheats.commands.impl;

import axolotl.Axolotl;
import axolotl.cheats.commands.Command;
import axolotl.cheats.modules.Module;
import org.lwjgl.input.Keyboard;

import java.util.Locale;

public class BindCmd extends Command {

    public BindCmd() {
        super("bind", "Binds a module to a keybind");
    }

    @Override
    public String onCommand(String[] args, String message) {
        if(args[0] != null) {
            Module m = Axolotl.INSTANCE.moduleManager.getModule(args[0]);
            Axolotl.INSTANCE.sendMessage(args[0]);
            if(m != null) {
                if(args[1] != null) {
                    int keyID = Keyboard.getKeyIndex(args[1]);
                    if(keyID != 0) {
                        m.keybindSetting.setCode(keyID);
                        return msg(args[1], m.name);
                    } else {
                        if(args[1].equalsIgnoreCase("NONE")) {
                            m.keybindSetting.setCode(0);
                            return msg(args[1], m.name);
                        } else return "Enter a valid keybind!";
                    }
                } else return "Enter the key to bind it to!";
            } else return "That is not a module!";
        } else return "Enter a module name!";
    }

    private String msg(String arg, String module) {
        return "Successfully binded " + module + " to " + arg + ".";
    }

}
