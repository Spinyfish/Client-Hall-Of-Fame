package crispy.features.commands.impl;

import crispy.Crispy;
import crispy.features.commands.Command;
import crispy.features.commands.CommandInfo;
import crispy.features.hacks.Hack;

@CommandInfo(name = "t",description = "Toggles a hack", syntax = ".t [hack]", alias = "t")
public class TToggleCommand extends Command {
    @Override
    public void onCommand(String arg, String[] args) throws Exception {
        if(args.length == 1) {
            Hack hack = Crispy.INSTANCE.getHackManager().getModule(args[0], false);
            if(hack != null) {
                hack.toggle();
                Crispy.addChatMessage("Toggled " + args[0]);
            } else {
                Crispy.addChatMessage("Invalid hack name " + args[0]);
            }


        } else {
            Crispy.addChatMessage(getSyntax());
        }
    }
}
