package ooo.cpacket.ruby.command.commands;

import ooo.cpacket.ruby.ClientBase;
import ooo.cpacket.ruby.command.Command;
import ooo.cpacket.ruby.manager.FriendManager;

public class Friend extends Command {

	public Friend() {
		super("friend", "af");
	}

	@Override
	public void run(String[] args) {
		try {
			if (!FriendManager.isFriend(args[1])) {
				if (args.length >= 3) {
					FriendManager.friends.put(args[1], args[2]);
					ClientBase.INSTANCE.chat("Friend " + args[1] + " added as " + args[2] + ".");
				}
				else {
					FriendManager.friends.put(args[1], args[1]);
					ClientBase.INSTANCE.chat("Friend " + args[1] + " added.");
				}
			}
			else {
				FriendManager.friends.remove(args[1]);
				ClientBase.INSTANCE.chat("Friend " + args[1] + " removed.");
			}
		}catch (Exception e){
			ClientBase.INSTANCE.chat("Error (Invalid arguments?)");
		}
	}

}
