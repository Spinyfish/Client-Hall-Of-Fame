package de.verschwiegener.atero.util.chat;

import java.nio.charset.StandardCharsets;

import org.lwjgl.input.Keyboard;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.util.files.config.ConfigType;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.IChatComponent;

public class ChatExecute {

    public static void executeHover(HoverEvent hoverevent) {
	String[] args = hoverevent.getValue().getUnformattedText().split("_");
	// System.out.println("Args: " + args[0]);
	switch (args[0]) {
	case "config":
	    switch (args[1]) {
	    case "load":
		// System.out.println("Config load: " + args[2]);
		break;
	    }
	    break;
	case "bind":
	    switch (args[1]) {
	    case "del":
		// System.out.println("Bind del: " + args[2]);
		break;
	    case "edit":
		// System.out.println("Bind edit: " + args[2]);
		break;
	    }
	    break;
	case "friend":
	    switch (args[1]) {
	    case "del":
		// System.out.println("Friend del: " + args[2]);
		break;
	    }
	}
    }

    public static void executeClick(ClickEvent clickevent) {
	String[] args = new String(clickevent.getValue().getBytes()).split("_");
	switch (args[0]) {
	case "config":
	    switch (args[1]) {
	    case "load":
		Management.instance.configmgr.getConfigByName(args[2]).loadConfig();
		ChatUtil.sendMessageWithPrefix("Config loaded: " + args[2]);
		break;
	    }
	    break;
	case "bind":
	    switch (args[1]) {
	    case "del":
		Management.instance.modulemgr.getModuleByName(args[2]).setKey(Keyboard.KEY_NONE);
		ChatUtil.sendMessageWithPrefix("Removed Bind from Module: \"" + args[2] + "\"");
		break;
	    case "edit":
		System.out.println("Bind edit: " + args[2]);
		break;
	    }
	    break;
	case "friend":
	    switch (args[1]) {
	    case "del":
		System.out.println("Friend del: " + args[2]);
		break;
	    }
	}
    }

}
