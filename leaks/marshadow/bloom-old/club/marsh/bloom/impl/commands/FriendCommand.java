package club.marsh.bloom.impl.commands;

import club.marsh.bloom.api.command.Command;
import club.marsh.bloom.impl.mods.player.MiddleClickFriends;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;

public class FriendCommand extends Command {

	public FriendCommand() {
		super("friend");
	}

	@Override
	public void onCommand(String arg, String[] args) throws Exception {
		if(!arg.isEmpty() && !arg.startsWith(" ")) {
			try
			{
				EntityPlayer player = null;
				
				for (NetworkPlayerInfo networkPlayerInfo : mc.getNetHandler().getPlayerInfoMap())
				{
					player = mc.theWorld.getPlayerEntityByUUID(networkPlayerInfo.getGameProfile().getId());

					if (player.getName().equalsIgnoreCase(args[0]) && player.getName().equals(mc.thePlayer.getName()))
					{
						throw new ArithmeticException();
					}
					
					if (!player.getName().equalsIgnoreCase(args[0]))
					{
						player = null;
					}
				}
				
	        	if (player == null)
	        	{
					addMessage("Not a valid player name.");
	        	}
	        	
				else
				{
					MiddleClickFriends.friends.add(player);
					addMessage("Added " + args[0] + " as a friend.");
				}
			}
			
			catch (Exception e)
			{
				if (e instanceof NullPointerException)
				{
					addMessage("Not a valid player name.");
				}
				
				else if (e instanceof ArithmeticException)
				{
					addMessage("You cannot friend yourself!");
				}
			}
		} else {
            addMessage(".friend [playername]");
        }
	}
	

}
