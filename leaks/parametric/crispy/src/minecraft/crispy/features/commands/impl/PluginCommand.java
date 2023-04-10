package crispy.features.commands.impl;

import crispy.Crispy;
import crispy.features.commands.Command;
import crispy.features.commands.CommandInfo;
import crispy.features.hacks.impl.misc.Plugin;

@CommandInfo(name = "Plugin", alias = "plugin", syntax = ".plugin", description = "View the servers plugins")
public class PluginCommand extends Command {
    @Override
    public void onCommand(String arg, String[] args) throws Exception {
        Crispy.INSTANCE.getHackManager().getHack(Plugin.class).setState(true);
    }
}
