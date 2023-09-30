package axolotl.cheats.commands.impl;

import axolotl.Axolotl;
import axolotl.cheats.commands.Command;

public class HelpCmd extends Command {

    public HelpCmd() {
        super("help", "Tells you all the commands you can use");
    }


    @Override
    public String onCommand(String[] args, String message) {
        String out = "Command list:\n\247d[----------------]";

        for(Command command : Axolotl.INSTANCE.cmdManager.commands) {
            out += "\n\247d" + command.name + " - " + command.description;
        }

        out += "\n\247d[----------------]";

        return out;

    }

}
