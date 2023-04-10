package crispy.friend;

import crispy.Crispy;
import crispy.util.file.Filer;
import net.minecraft.util.StringUtils;

import java.util.ArrayList;

/**
 * @author - Wizard
 * A terrible friend system I made 2 years ago
 */
public class FriendManager {

	public static Filer friendFile = new Filer("friends", "Crispy");
	public static ArrayList<Friend> friends = new ArrayList<Friend>();

	public boolean isFriend(String name) {
		for(Friend friend : getFriends()) {
			if(friend.getName().equalsIgnoreCase(StringUtils.stripControlCodes(name))) {
				//Found Friend!
				return true;
			} 
		}
		return false;
	}
	
	
	public void load() {
		for(String s : friendFile.read()) {
			FriendManager.friends.add(new Friend(s));
		}
	}

	public void addFriend(String name) {
		FriendManager.friends.add(new Friend(name));
		Crispy.addChatMessage("Friend now added " + name );
		save();
	}
	
	public void removeFriend(String name) {	
		for(int i = 0;  i < Crispy.INSTANCE.getFriendManager().getFriends().size(); i++) {
			for(Friend ignored : Crispy.INSTANCE.getFriendManager().getFriends()) {
				if(FriendManager.friends.get(i).getName().equalsIgnoreCase(name)) {
					Crispy.addChatMessage("Friend " + name + " is now removed!");
					FriendManager.friends.remove(i);
					save();
				}
			}
	
		}


	}
	
	public void save() {
		friendFile.clear();
		for(Friend friend : Crispy.INSTANCE.getFriendManager().getFriends()) {
			friendFile.write(friend.getName());
		}
	}
	
	public ArrayList<Friend> getFriends() {
		return friends;
	}
}
