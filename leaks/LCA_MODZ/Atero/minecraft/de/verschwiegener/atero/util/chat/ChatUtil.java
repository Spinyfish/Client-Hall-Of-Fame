package de.verschwiegener.atero.util.chat;

import java.awt.Color;
import java.util.ArrayList;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.util.files.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class ChatUtil {

	public static void sendMessageWithPrefix(String message) {
		Minecraft.getMinecraft().thePlayer
				.addChatMessage(new ChatComponentText("§9" + Management.instance.CLIENT_NAME + ": §f" + message));
	}

	public static void sendMessage(String message) {
		Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
	}

	public static void addIChatComponent(IChatComponent components) {
		Minecraft.getMinecraft().ingameGUI.getChatGUI().addChatLine(components);
	}

	public static void addLoadChat2() {
		IChatComponent ichatcomponent = new ChatComponentText("§c❤❤❤❤❤❥§f❤❤❤❤");
		ichatcomponent.getChatStyle().setColor(EnumChatFormatting.GREEN);
		ichatcomponent.getChatStyle()
				.setChatHoverEvent(new HoverEvent(HoverEvent.Action.CLIENT, new ChatComponentText("test_test2_test3")));
		ichatcomponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.CLIENT, "bind_del_test"));

		IChatComponent ichatcomponent2 = new ChatComponentText("Test2");
		ichatcomponent2.getChatStyle().setColor(EnumChatFormatting.GREEN);
		ichatcomponent2.getChatStyle()
				.setChatHoverEvent(new HoverEvent(HoverEvent.Action.CLIENT, new ChatComponentText("test_test2_test4")));

		IChatComponent test = new ChatComponentText("Test ");
		test.appendSibling(ichatcomponent);
		test.appendSibling(ichatcomponent2);

		Minecraft.getMinecraft().ingameGUI.getChatGUI().addChatLine(test);
	}

	public static void addBindsMessage(String module) {
		IChatComponent message = new ChatComponentText("§r" + module + ": ");

		IChatComponent componentremove = new ChatComponentText("Remove");
		componentremove.getChatStyle().setColor(EnumChatFormatting.RED);
		componentremove.getChatStyle()
				.setChatClickEvent(new ClickEvent(ClickEvent.Action.CLIENT, "bind_del_" + module));
		message.appendSibling(componentremove);

		Minecraft.getMinecraft().ingameGUI.getChatGUI().addChatLine(message);
	}

	public static void addConfigMessage(Config config) {
		IChatComponent message = new ChatComponentText("§r" + config.getName() + (config.getDescription() != "" ? " | " + config.getDescription() : " "));

		IChatComponent componentload = new ChatComponentText("[Load]");
		componentload.getChatStyle().setColor(EnumChatFormatting.GREEN);
		componentload.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.CLIENT, "config_load_" + config.getName()));
		message.appendSibling(componentload);

		Minecraft.getMinecraft().ingameGUI.getChatGUI().addChatLine(message);
	}

	public static void sendConfigJoinMessage() {
		try {
			ArrayList<Config> configs = Management.instance.configmgr
					.getConfigsforServer(Minecraft.getMinecraft().getCurrentServerData().serverIP);
			if (!configs.isEmpty()) {
				IChatComponent joinmessage = new ChatComponentText("Available Configs: ");
				for (int i = 0; i < configs.size(); i++) {
					Config config = configs.get(i);
					IChatComponent ichatcomponent2 = new ChatComponentText(config.getName());
					ichatcomponent2.getChatStyle().setColor(EnumChatFormatting.GREEN);
					ichatcomponent2.getChatStyle().setChatClickEvent(
							new ClickEvent(ClickEvent.Action.CLIENT, "config_load_" + config.getName()));
					joinmessage.appendSibling(ichatcomponent2);
					if (i != configs.size() - 1) {
						IChatComponent ichatcomponent3 = new ChatComponentText(", ");
						ichatcomponent3.getChatStyle().setColor(EnumChatFormatting.WHITE);
						joinmessage.appendSibling(ichatcomponent3);
					}
				}
				if (!joinmessage.getSiblings().isEmpty()) {
					Minecraft.getMinecraft().ingameGUI.getChatGUI().addChatLine(joinmessage);
				}
			}
		} catch (Exception e) {

		}
	}

}
