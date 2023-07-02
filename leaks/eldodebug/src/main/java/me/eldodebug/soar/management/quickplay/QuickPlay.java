package me.eldodebug.soar.management.quickplay;

import java.util.ArrayList;

import me.eldodebug.soar.utils.animation.simple.SimpleAnimation;
import net.minecraft.util.ResourceLocation;

public class QuickPlay {

	private String name;
	
	private ResourceLocation icon;
	
	private ArrayList<QuickPlayCommand> commands = new ArrayList<QuickPlayCommand>();
	
	public SimpleAnimation animation = new SimpleAnimation(0.0F);
	
	public QuickPlay(String name, String iconLocation) {
		this.name = name;
		this.icon = new ResourceLocation(iconLocation);
		this.addCommands();
	}
	
	public void addCommands() {}

	public ResourceLocation getIcon() {
		return icon;
	}

	public void setIcon(ResourceLocation icon) {
		this.icon = icon;
	}

	public ArrayList<QuickPlayCommand> getCommands() {
		return commands;
	}

	public void setCommands(ArrayList<QuickPlayCommand> commands) {
		this.commands = commands;
	}

	public String getName() {
		return name;
	}
}