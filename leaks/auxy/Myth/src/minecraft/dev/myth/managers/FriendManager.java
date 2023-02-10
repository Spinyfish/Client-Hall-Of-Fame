/**
 * @project Myth
 * @author CodeMan
 * @at 07.01.23, 15:32
 */
package dev.myth.managers;

import dev.myth.api.interfaces.IMethods;
import dev.myth.api.manager.Manager;
import dev.myth.api.utils.FileUtil;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;

public class FriendManager implements Manager, IMethods {

    @Getter private final ArrayList<String> friends = new ArrayList<>();
    private final File friendFile = new File(MC.mcDataDir, "myth/friends.txt");

    @Override
    public void run() {
        loadFriends();
    }

    @Override
    public void shutdown() {
        saveFriends();
        friends.clear();
    }

    private void loadFriends() {
        friends.clear();
        if(friendFile.exists()) {
            friends.addAll(FileUtil.readLinesFromFile(friendFile.getAbsolutePath()));
        }
    }

    private void saveFriends() {
        FileUtil.writeLinesToFile(friendFile.getAbsolutePath(), friends);
    }

    public void addFriend(String name) {
        if(!friends.contains(name)) {
            friends.add(name);
        }
    }

    public void removeFriend(String name) {
        friends.remove(name);
    }

    public boolean isFriend(String name) {
        return friends.contains(name);
    }

}
