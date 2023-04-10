package crispy.features.commands.impl;

import crispy.Crispy;
import crispy.features.commands.Command;
import crispy.features.commands.CommandInfo;
import crispy.features.hacks.Hack;
import org.lwjgl.input.Keyboard;

import javax.swing.*;

@CommandInfo(name = "Bind", description = "Binds a key to a hack", syntax = ".bind [hack] [key]", alias = "bind")
public class BindCommand extends Command {

    public void onCommand(String command, String[] args) throws Exception {
        if(args.length > 1) {
            args[1] = args[1].toUpperCase();
            for (Hack hack : Crispy.INSTANCE.getHackManager().getHacks()) {
                if (hack.getName().equalsIgnoreCase(args[0])) {
                    hack.setKey(Keyboard.getKeyIndex(args[1]));
                    message("Binded hack to " + args[1], true);
                }
            }
        } else {
            message(getSyntax(), true);
        }
    }
}
