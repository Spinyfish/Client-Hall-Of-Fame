package dev.elysium.client;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import org.lwjgl.opengl.Display;

import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.ModManager;
import dev.elysium.client.command.CommandManager;
import dev.elysium.client.extensions.ConfigManager;
import dev.elysium.client.mods.ModManagerImpl;
import dev.elysium.client.mods.impl.settings.Targets;
import dev.elysium.client.scripting.ScriptManager;
import dev.elysium.client.ui.HUD;
import dev.elysium.client.ui.elements.HudManager;
import dev.elysium.client.ui.font.FontManager;
import dev.elysium.client.ui.widgets.WidgetManager;
import dev.elysium.client.utils.api.Hypixel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

public class Elysium {

	public static Elysium INSTANCE;
	public Preferences prefs = Preferences.userNodeForPackage(Elysium.class);
	private ModManager modManager;
	private WidgetManager widgetManager;
	private CommandManager cmdManager;
	private HudManager hudManager;
	public HUD hud = new HUD();
	public FontManager fm;
	public static String name = "Elysium";
	public static String version = "b1";
	public long timeLaunched = System.currentTimeMillis();

	public ServerData currentServer = null;
	
	public List<EntityLivingBase> targets = new ArrayList<EntityLivingBase>();
	public List<EntityLivingBase> entities = new ArrayList<EntityLivingBase>();

	public static Elysium newInstance() {
		return new Elysium();
	}
	public static Elysium getInstance() {
		return INSTANCE;
	}

	public void addTarget(EntityLivingBase e) {
		if(!targets.contains(e))
			targets.add(e);
	}

	public Mod byName(String name) {
		return getModManager().getModByName(name);
	}

	public void addChatMessage(Object o) {
		String message = o.toString();
		message = "[\2479" + name + "\247f] \2477" + message;
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(message));
	}

	public void addChatMessageConfig(Object o) {
		String message = o.toString();
		message = "[\247a" + "Config" + "\247f] \2477" + message;
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(message));
	}

	public Elysium() {
		INSTANCE = this;
		Hypixel.getBanStats();
		Display.setTitle(name + " " + version);
		fm = new FontManager();
		modManager = new ModManagerImpl();
		cmdManager = new CommandManager();
		hudManager = new HudManager();
		widgetManager = new WidgetManager();
		ConfigManager.createConfigDirectories();
	
		
		ScriptManager.loadScripts();
			
	}

	public WidgetManager getWidgetManager() {
		return widgetManager;
	}
	public ModManager getModManager() {
		return modManager;
	}
	public CommandManager getCmdManager() {
		return cmdManager;
	}
	public HudManager getHudManager() {
		return hudManager;
	}
	public FontManager getFontManager() {
		return fm;
	}

	public void clearTargets() {
		targets = new ArrayList<EntityLivingBase>();
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.currentScreen instanceof GuiChat) {
			if(!Elysium.getInstance().targets.contains(mc.thePlayer))
				Elysium.getInstance().targets.add(mc.thePlayer);
		}
	}

	public List<EntityLivingBase> getTargets() {
		Targets tar = (Targets)getModManager().getModByName("Targets");
		if(!tar.toggled) return new ArrayList<EntityLivingBase>();

		Minecraft mc = Minecraft.getMinecraft();

		entities = new ArrayList<EntityLivingBase>();


		for(Entity en : mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).collect(Collectors.toList())) {
			EntityLivingBase entity = (EntityLivingBase)en;
			if( entity != mc.thePlayer && (tar.Invisible.isEnabled() || !entity.isInvisible()) && (tar.Mobs.isEnabled() || !(entity instanceof EntityMob || entity instanceof EntityArmorStand || entity instanceof EntityVillager)) && (tar.Animals.isEnabled() || !(entity instanceof EntityAnimal || entity instanceof EntityBat)) && (tar.Players.isEnabled() || !(entity instanceof EntityPlayer)) && (tar.Dead.isEnabled() || !entity.isDead && entity.getHealth() > 0))
				entities.add(entity);

		}

		return entities;
	}

	public List<EntityLivingBase> getAllTargets() {
		Minecraft mc = Minecraft.getMinecraft();

		entities = new ArrayList<EntityLivingBase>();

		for(Entity en : mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).collect(Collectors.toList())) {
			EntityLivingBase entity = (EntityLivingBase)en;
			if(entity != mc.thePlayer && !entity.isInvisible() && !entity.isDead && entity.getHealth() > 0)
				entities.add(entity);

		}

		return entities;
	}
}
