package de.fanta.utils;

import net.minecraft.entity.Entity;

import java.util.ArrayList;

public class FriendSystem {

    public static ArrayList<String> friends = new ArrayList<>();

    public static void addFriend(String name) {
        System.out.println("Added " + name);
        friends.add(name);
    }

    public static void removeFriend(String name) {
        System.out.println("Removed " + name);
        friends.remove(name);
    }

    public static ArrayList<String> getFriends() {
        return friends;
    }

    public static boolean isFriendEntity(Entity e) {
        return friends.contains(e.getName());
    }

    public static boolean isFriendString(String name) {
        return friends.contains(name);
    }

    public static void clearFriends() {
        System.out.println("Cleared friend list");
        friends.clear();
    }

}
