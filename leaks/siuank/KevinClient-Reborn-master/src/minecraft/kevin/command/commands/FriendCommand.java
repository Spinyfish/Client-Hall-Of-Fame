package kevin.command.commands;

import kevin.module.modules.misc.ClientFriend;
import kevin.utils.ChatUtils;

import java.util.LinkedList;

public class FriendCommand implements kevin.command.ICommand {
    @Override
    public void run(String[] args) {
        if (args == null || args.length == 0) {
            ChatUtils.INSTANCE.messageWithStart("§cUsage: §c.friend §c<add/remove> <player> §cor .friend§c list");
            return;
        }
        int length = args.length;
        String sub = args[0].toLowerCase();

        LinkedList<String> friends = ClientFriend.INSTANCE.getFriendsName();
        if ("add".equalsIgnoreCase(sub)) {
            if (length == 1) {
                ChatUtils.INSTANCE.messageWithStart("§cUsage: §c.friend§c add <player>");
                return;
            }
            friends.add(args[1].toLowerCase());
            return;
        } else if ("remove".equalsIgnoreCase(sub)) {
            if (length == 1) {
                ChatUtils.INSTANCE.messageWithStart("§cUsage: §c.friend§c remove <player>");
                return;
            }
            friends.remove(args[1].toLowerCase());
            return;
        } else if ("list".equalsIgnoreCase(sub)) {
            if (friends.isEmpty()) {
                ChatUtils.INSTANCE.messageWithStart("§cYou don't have any friend currently!");
                return;
            }
            ChatUtils.INSTANCE.messageWithStart("§cYour friends:\n");
            for (String s : friends) {
                ChatUtils.INSTANCE.message("  §7- §3" + s + "\n");
            }
            return;
        }
        ChatUtils.INSTANCE.messageWithStart("§cUsage: §c.friend §c<add/remove> <player> §cor .friend§c list");
    }
}
