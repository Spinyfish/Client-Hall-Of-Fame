/**
 * @project Myth
 * @author CodeMan
 * @at 07.01.23, 15:41
 */
package dev.myth.commands;

import dev.myth.api.command.Command;
import dev.myth.api.logger.Logger;
import dev.myth.main.ClientMain;
import dev.myth.managers.FriendManager;

@Command.Info(name = "friend")
public class FriendCommand extends Command {

    @Override
    public void run(String[] args) {
        if (args.length < 1) {
            doLog(Logger.Type.ERROR, "Usage: friend <add/remove> <name> | friend list");
            return;
        }

        FriendManager friendManager = ClientMain.INSTANCE.manager.getManager(FriendManager.class);

        switch (args[0].toLowerCase()) {
            case "add":
                if (args.length < 2) {
                    doLog(Logger.Type.ERROR, "Usage: friend add <name>");
                    return;
                }
                if (friendManager.isFriend(args[1])) {
                    doLog(Logger.Type.ERROR, "That player is already your friend!");
                    return;
                }
                friendManager.addFriend(args[1]);
                doLog(Logger.Type.INFO, "Added friend " + args[1]);
                break;
            case "remove":
                if (args.length < 2) {
                    doLog(Logger.Type.ERROR, "Usage: friend remove <name>");
                    return;
                }
                if (!friendManager.isFriend(args[1])) {
                    doLog(Logger.Type.ERROR, "That player is not your friend!");
                    return;
                }
                friendManager.removeFriend(args[1]);
                doLog(Logger.Type.INFO, "Removed friend " + args[1]);
                break;
            case "list":
                if (!friendManager.getFriends().isEmpty()) {
                    doLog(Logger.Type.INFO, "Friends: ");
                    StringBuilder sb = new StringBuilder();
                    for (String friend : friendManager.getFriends()) {
                        sb.append(friend).append(", ");
                    }
                    doLog(Logger.Type.INFO, sb.toString());
                } else {
                    doLog(Logger.Type.INFO, "You have no friends!");
                }
                break;
        }
    }
}
