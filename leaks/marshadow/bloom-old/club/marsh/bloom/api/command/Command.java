package club.marsh.bloom.api.command;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public abstract class Command {
	public String name;
	public Minecraft mc = Minecraft.getMinecraft();
	public Command(String name) {
		this.name = name;
	}
	public void addMessage(String message) {
		 mc.thePlayer.addChatComponentMessage(new ChatComponentText("\247dBloom \2479>>\247a " + message));
	}
	public abstract void onCommand(String arg, String[] args) throws Exception;
}
