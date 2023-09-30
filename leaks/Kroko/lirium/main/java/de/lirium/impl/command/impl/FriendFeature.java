/*
 * Copyright Felix Hans from FriendCommand coded for haze.yt / Lirium . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited
 */

package de.lirium.impl.command.impl;

import de.lirium.impl.command.CommandFeature;
import me.felix.friends.Friend;
import me.felix.friends.FriendData;

@CommandFeature.Info(name = "Friend", alias = "friends")
public class FriendFeature extends CommandFeature {
    @Override
    public boolean execute(String[] args) {
        if (args.length > 1) {
            final String secondParameter = args[0];
            final String thirdParameter = args[1];
            switch (secondParameter.toLowerCase()) {
                case "add": {
                    final Friend friend = new Friend(thirdParameter, "");
                    FriendData.addFriend(friend);
                    sendMessage("Added friend \"" + friend.name + "\"");
                    break;
                }
                case "remove": {
                    if (FriendData.removeFriend(thirdParameter))
                        sendMessage("Removed friend \"" + thirdParameter + "\"");
                    else
                        sendMessage("Unable to find friend \"" + thirdParameter + "\"");
                    break;
                }
            }
        } else {
            if (!args[0].equalsIgnoreCase("list")) {
                switch (args[0].toLowerCase()) {
                    case "add":
                        sendMessage("Please enter a Player name -> .friend add \"name\"");
                        break;
                    case "remove":
                        sendMessage("Please enter a Player name to remove -> .friend remove \"name\"");
                        break;
                }
            } else {
                if (FriendData.friends.size() > 0) {
                    for (final Friend friend : FriendData.friends.values())
                        sendMessage(friend.name);
                } else {
                    sendMessage("You don´t have any friends.");
                }
            }
        }
        return true;
    }
}
