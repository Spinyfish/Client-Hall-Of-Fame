package me.wavelength.baseclient.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.PlayerControllerMP;

public class Wrapper {

	public static Minecraft mc = Minecraft.getMinecraft();
	public static FontRenderer fr = mc.fontRendererObj;
	
	public PlayerControllerMP getPlayerController() {
		return Minecraft.getMinecraft().playerController;
	}


	
}
