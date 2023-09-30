package de.verschwiegener.atero.friend;

import de.verschwiegener.atero.Management;

import java.util.concurrent.CopyOnWriteArrayList;

public class FriendManager {

    public CopyOnWriteArrayList<String> friends = new CopyOnWriteArrayList<>();

    public void addFriend(String name) {
        this.friends.add(name);
    }

    public boolean isFriend(String name) {
        return this.friends.contains(name) && !Management.instance.modulemgr.getModuleByName("NoFriends").isEnabled();
    }

    public boolean removeFriend(String name) {
        return friends.remove(name);
    }
}
