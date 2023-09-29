package de.fanta.module.impl.miscellaneous;

import java.awt.Color;

import org.lwjgl.input.Mouse;

import de.fanta.events.Event;
import de.fanta.module.Module;
import de.fanta.utils.ChatUtil;
import de.fanta.utils.FriendSystem;
import de.fanta.utils.TimeUtil;
import net.minecraft.entity.Entity;import net.minecraft.entity.ai.EntityAIDefendVillage;
import net.minecraft.entity.player.EntityPlayer;

public class MiddleClickFriends extends Module{

	public MiddleClickFriends() {
		super("MCF", 0, Type.Misc, Color.cyan);
	}
	
	private long milliSeconds = System.currentTimeMillis();

	@Override
	public void onEvent(Event event) {
		if(Mouse.isButtonDown(2)) {
			Entity entity = mc.objectMouseOver.entityHit;
			if(entity != null && entity instanceof EntityPlayer) {
				if(System.currentTimeMillis()-milliSeconds > 300) {
					if(FriendSystem.getFriends().contains(entity.getName())) {
						FriendSystem.removeFriend(entity.getName());
						ChatUtil.sendChatMessageWithPrefix("Removed "+entity.getName()+" from your friend list!");
						milliSeconds = System.currentTimeMillis();
					}else {
						FriendSystem.addFriend(entity.getName());
						ChatUtil.sendChatMessageWithPrefix("Added "+entity.getName()+" to your friend list!");
						milliSeconds = System.currentTimeMillis();				
					}
				}
			}
		}
	}

}
