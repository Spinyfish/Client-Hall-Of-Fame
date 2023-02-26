package ooo.cpacket.ruby.command;

import net.minecraft.client.Minecraft;

public abstract class Command {
	public String name;
	private String[] aliases;
	protected Minecraft mc = Minecraft.getMinecraft();

	public Command(String name, String... aliases) {
		this.name = name;
		this.aliases = aliases;
	}

	public abstract void run(String[] args);

}
