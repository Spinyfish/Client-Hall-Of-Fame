package crispy.features.commands.impl;

import crispy.Crispy;
import crispy.features.commands.Command;
import crispy.features.commands.CommandInfo;
import crispy.features.script.ScriptModule;

@CommandInfo(name = "Script", description = "Allows the user to reload scripts and load scripts", syntax = ".script load [script], .script reload [script]", alias = "script")
public class ScriptCommand extends Command {
    @Override
    public void onCommand(String arg, String[] args) throws Exception {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                Crispy.INSTANCE.getHackManager().getHacks().removeIf(hack -> hack instanceof ScriptModule);
                Crispy.INSTANCE.getSaver().loadScripts();

                Crispy.addChatMessage("Loaded ");


            }
        } else {
            Crispy.addChatMessage(getSyntax());
        }
    }
}
