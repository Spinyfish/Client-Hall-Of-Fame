/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.api.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.player.EntityPlayer;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.manager.friend.Friend;

public class FriendManager {
    private final List<Friend> friends = new ArrayList<Friend>();

    public void addFriend(String username) {
        Friend friend = new Friend(username);
        if (Wrapper.getMinecraft().theWorld.playerEntities.stream().anyMatch(entityPlayer -> entityPlayer.getDisplayName().getUnformattedText().equalsIgnoreCase(username))) {
            friend.setPlayer((EntityPlayer)Wrapper.getMinecraft().theWorld.playerEntities.stream().filter(entityPlayer -> entityPlayer.getDisplayName().getUnformattedText().equalsIgnoreCase(username)).collect(Collectors.toList()).get(0));
        }
        this.friends.add(friend);
    }

    public boolean removeFriend(String username) {
        if (this.friends.stream().anyMatch(friend -> friend.getName().equalsIgnoreCase(username))) {
            this.friends.removeIf(friend -> friend.getName().equalsIgnoreCase(username));
            return true;
        }
        return false;
    }

    public boolean isFriend(String username) {
        return this.friends.stream().anyMatch(friend -> friend.getName().equalsIgnoreCase(username));
    }

    public List<Friend> getFriends() {
        return this.friends;
    }
}

