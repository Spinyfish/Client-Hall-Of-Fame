package crispy.features.commands.impl;

import crispy.Crispy;
import crispy.features.commands.Command;
import crispy.features.commands.CommandInfo;
import crispy.friend.Friend;


@CommandInfo(name = "friend", alias = "friend", description = "Adds a player as a friend", syntax = ".friend add [name] | .friend remove [name] | .friend list")
public class FriendCommand extends Command {
    @Override
    public void onCommand(String arg, String[] args) throws Exception {
        if(args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "add": {
                    if(args.length > 1) {
                        if(!Crispy.INSTANCE.getFriendManager().isFriend(args[1])) {
                            Crispy.INSTANCE.getFriendManager().addFriend(args[1]);
                        } else {
                            message("Already a friend", true);
                        }
                    } else message(getSyntax(), true);
                    break;
                }
                case "remove": {
                    if(args.length > 1) {
                        if(Crispy.INSTANCE.getFriendManager().isFriend(args[1])) {
                            Crispy.INSTANCE.getFriendManager().removeFriend(args[1]);
                        } else {
                            message("There is no friend named: " + args[1], true);
                        }
                    } else message(getSyntax(), true);
                    break;
                }
                case "list": {
                    message("Friends:", true);
                    for(Friend friend : Crispy.INSTANCE.getFriendManager().getFriends()) {
                        Crispy.addChatMessage(friend.getName());
                    }
                    break;
                }
                case "": {
                    message(getSyntax(), true);
                    break;
                }
            }
        } else {
            message(getSyntax(), true);
        }
    }
}
