package cafe.corrosion.command.impl;

import cafe.corrosion.attributes.CommandAttributes;
import cafe.corrosion.command.ICommand;
import cafe.corrosion.util.player.PlayerUtil;
import java.util.ArrayList;
import java.util.List;

@CommandAttributes(
    name = "friend"
)
public class FriendCommand implements ICommand {
    public static final List<String> FRIEND_NAMES = new ArrayList();

    public void handle(String[] args) {
        if (args.length != 1) {
            PlayerUtil.sendMessage("Try -friend (player)");
        } else {
            String friend = args[0].toLowerCase();
            if (FRIEND_NAMES.contains(friend)) {
                FRIEND_NAMES.remove(friend);
                PlayerUtil.sendMessage("Removed " + friend + " from your friends!");
            } else {
                FRIEND_NAMES.add(friend);
                PlayerUtil.sendMessage("Added " + friend + " to your friends!");
            }

        }
    }
}
