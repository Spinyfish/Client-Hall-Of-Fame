package crispy.features.commands.impl;

import crispy.Crispy;
import crispy.features.commands.Command;
import crispy.features.commands.CommandInfo;
import crispy.util.player.SpeedUtils;

@CommandInfo(name = "hclip", description = "HClips you forward", alias = "hclip", syntax = ".hclip [distance]")
public class HclipCommand extends Command {

    @Override
    public void onCommand(String arg, String[] args) throws Exception {
        if(args.length > 0) {
            SpeedUtils.setPositionNoUp(Double.parseDouble(args[0]));
            message("Hclip forward " + args[0], true);
        } else {
            message(getSyntax(), true);
        }
    }
}
