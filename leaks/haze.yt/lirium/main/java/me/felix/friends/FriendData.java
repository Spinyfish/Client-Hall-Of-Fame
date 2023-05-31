/*
 * Copyright Felix Hans from FriendData coded for haze.yt / Lirium . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited
 */

package me.felix.friends;

import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class FriendData {

    public static HashMap<String, Friend> friends = new HashMap<>();

    public static void addFriend(final String name, String... nickname) {
        final Friend friend;
        if (!nickname[0].isEmpty())
            friend = new Friend(name, nickname);
        else
            friend = new Friend(name, "");
        friends.put(name, friend);
    }

    public static void addFriend(final Friend friend) {
        friends.put(friend.name, friend);
    }

    public static boolean removeFriend(final String name) {
        if(friends.containsKey(name) || friends.containsValue(name)) {
            friends.remove(name);
            return true;
        }
        return false;
    }

    public static boolean isAlreadyFriend(Entity entity) {
        return friends.containsKey(entity.getName());
    }

    public static boolean isAlreadyFriend(Friend selectedFriend) {
        for (Friend friend : friends.values()) {
            if (selectedFriend.name.equalsIgnoreCase(friend.name) || selectedFriend.nickname.equalsIgnoreCase(friend.nickname))
                return true;
        }
        return false;
    }
}
