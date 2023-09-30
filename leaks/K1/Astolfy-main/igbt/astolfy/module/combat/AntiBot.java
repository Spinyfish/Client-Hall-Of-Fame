package igbt.astolfy.module.combat;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.play.server.S40PacketDisconnect;
import org.lwjgl.input.Keyboard;

import igbt.astolfy.Astolfy;
import igbt.astolfy.events.Event;
import igbt.astolfy.events.listeners.EventPacket;
import igbt.astolfy.events.listeners.EventUpdate;
import igbt.astolfy.module.ModuleBase;
import igbt.astolfy.ui.Notifications.Notification;
import igbt.astolfy.ui.Notifications.NotificationType;
import io.netty.handler.timeout.TimeoutException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.status.server.S01PacketPong;

public class AntiBot extends ModuleBase {

	public AntiBot() {
		super("AntiBot", Keyboard.KEY_J, Category.COMBAT);
	}
	private static CopyOnWriteArrayList<Entity> entities = new CopyOnWriteArrayList<Entity>();
	
	public void onEvent(Event event) {
		if(event instanceof EventUpdate) {
			CopyOnWriteArrayList<Entity> entList = new CopyOnWriteArrayList<Entity>();
			for(Entity ent : mc.theWorld.loadedEntityList) {
					/*
					 * AntiBot (Hypixel) Removes Invisibles
					*/
					if(!ent.isInvisible() && ent != mc.thePlayer && ent instanceof EntityPlayer)
						entList.add(ent);
			}
			entities = entList;
		}
	}
	
	public void onSkipEvent(Event event) {
		if(event instanceof EventPacket) {
			EventPacket e = (EventPacket)event;
			if(e.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition){

			}
			if(e.getPacket() instanceof S01PacketJoinGame) {

				Astolfy.notificationManager.showNotification(new Notification(NotificationType.INFORMATION, "Loaded World", "Loading World..... L1", 5));
			}
			if(e.getPacket() instanceof C19PacketResourcePackStatus) {
			}
		}
		if(!this.isToggled()) {
			if(event instanceof EventUpdate) {
				CopyOnWriteArrayList<Entity> entList = new CopyOnWriteArrayList<Entity>();
				for(Entity ent : mc.theWorld.loadedEntityList) {
					if(ent != mc.thePlayer)
						entList.add(ent);
				}
				entities = entList;
				//System.out.println(entList.get(0));
			}
		}
	}
	
	private void writeAllPlayers() throws TimeoutException {
		
	}

	public static CopyOnWriteArrayList<Entity> getEntities() {
		return entities;
	}
	
}
