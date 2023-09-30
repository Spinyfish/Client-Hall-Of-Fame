package felix;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

import com.google.common.eventbus.EventBus;
import com.thealtening.AltService;

import felix.api.annotations.Handler;
import felix.api.bus.Bus;
import felix.api.bus.BusImpl;
import felix.command.Command;
import felix.events.Event;
import felix.events.packet.EventPacketSend;
import felix.events.player.EventKeyPress;
import felix.events.player.EventMotionUpdate;
import felix.events.player.EventSendMessage;
import felix.gui.alt.system.AccountManager;
import felix.management.CommandManager;
import felix.management.ConfigManager;
import felix.management.FontManager;
import felix.management.ModuleManager;
import felix.module.Module;
import felix.util.other.Logger;
import felix.util.other.PlayerUtil;
import felix.util.other.TimeHelper;
import felix.value.Value;
import felix.value.impl.BooleanValue;
import felix.value.impl.EnumValue;
import felix.value.impl.NumberValue;
import net.halozy.Protection;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C18PacketSpectate;

public enum Client {
	
	INSTANCE;

	private ModuleManager moduleManager;
	
	private Bus<Event> eventapi;

	public String user = "";

	private FontManager fontManager;
	
    private AccountManager accountManager;
    
    private AltService altService = new AltService();
    
    private File directory;
    
    private File configDirectory;
    
    private CommandManager commandManager;
    
    private ConfigManager configManager;
    
    private File dataFile;
    
    public String build = "030121";
    
	
	public void start() throws UnsupportedEncodingException, NoSuchAlgorithmException {
		directory = new File(Minecraft.getMinecraft().mcDataDir, "Felix");
		configDirectory = new File(directory, "configs");
        if (!directory.exists()) {
            directory.mkdir();
        }
        if (!configDirectory.exists()) {
        	configDirectory.mkdir();
        }
        dataFile = new File(directory, "modules.txt");
		eventapi = new BusImpl<Event>();
		moduleManager = new ModuleManager();
		configManager = new ConfigManager();
		commandManager = new CommandManager();
	    
		fontManager = new FontManager();
		accountManager = new AccountManager(directory);
		configManager.loadConfigs();
		moduleManager.loadModules(dataFile);
		//protection.hook();
		eventapi.register(this);
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public void stop() {
        accountManager.save();
        if (!dataFile.exists()) {
        	try {
				dataFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        moduleManager.saveModules(dataFile);
		eventapi.unregister(this);
	}
	
	@Handler
	public void onKeyPress(final EventKeyPress event) {
		moduleManager.getModules().stream().filter(module -> module.getKey() == event.getKey()).forEach(module -> module.toggle());
	}
	
	@Handler
	public void onSendMessage(final EventSendMessage eventChat) {
		for (final Command command : commandManager.getComands()) {
			String chatMessage = eventChat.getMessage();
			String formattedMessage = chatMessage.replace(".", "");
			String[] regexFormattedMessage = formattedMessage.split(" ");
			if (regexFormattedMessage[0].equalsIgnoreCase(command.getCommandName())) {
				ArrayList<String> list = new ArrayList<>(Arrays.asList(regexFormattedMessage));
				list.remove(command.getCommandName());
				regexFormattedMessage = list.toArray(new String[0]);
				command.executeCommand(regexFormattedMessage);
			}
		}
		if (eventChat.getMessage().startsWith(".")) {
			eventChat.setCancelled(true);
		}
	}
	
	public ModuleManager getModuleManager() {
		return moduleManager;
	}
	
	public Bus getEventManager() {
		return eventapi;
	}
	
	public FontManager getFontManager() {
		return fontManager;
	}

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public void switchToMojang() {
        try {
            altService.switchService(AltService.EnumAltService.MOJANG);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.out.println("Couldnt switch to modank altservice");
        }
    }

    public void switchToTheAltening() {
        try {
            altService.switchService(AltService.EnumAltService.THEALTENING);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.out.println("Couldnt switch to altening altservice");
        }
    }

	public File getConfigDirectory() {
		return configDirectory;
	}
	
	public File getDirectory() {
		return directory;
	}
}
