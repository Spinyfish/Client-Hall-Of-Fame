package com.srt.module.combat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;

import com.srt.events.Event;
import com.srt.events.listeners.EventPacket;
import com.srt.events.listeners.EventUpdate;
import com.srt.module.ModuleBase;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S38PacketPlayerListItem;

public class AntiBot extends ModuleBase {

	public AntiBot() {
		super("AntiBot", Keyboard.KEY_J, Category.COMBAT);
		setDisplayName("Anti Bot");
	}
	private static CopyOnWriteArrayList<Entity> entities = new CopyOnWriteArrayList<Entity>();
	
	public void onEvent(Event event) {
		if(event instanceof EventUpdate) {
			
			CopyOnWriteArrayList<Entity> entList = new CopyOnWriteArrayList<Entity>();
			for(Entity ent : mc.theWorld.loadedEntityList) {
				/*
				 * AntiBot (Hypixel) Removes Invisibles
				*/
				
				if(!ent.isInvisible() && ent.ticksExisted > 75)
					entList.add(ent);
            }
            entities = entList;
		}

		if(event instanceof EventPacket) {
			EventPacket packetEvent = (EventPacket)event;
			CopyOnWriteArrayList<Entity> entList = new CopyOnWriteArrayList<Entity>();
			
			if (packetEvent.getPacket() instanceof S38PacketPlayerListItem && mc.getNetHandler() != null && mc.thePlayer.ticksExisted > 250) {
                final S38PacketPlayerListItem packetPlayerListItem = (S38PacketPlayerListItem) packetEvent.getPacket();
                for (final Object playerData : packetPlayerListItem.func_179767_a()) {
                    S38PacketPlayerListItem.AddPlayerData addPlayerData = (S38PacketPlayerListItem.AddPlayerData) playerData;
                    if (packetPlayerListItem.func_179768_b() == S38PacketPlayerListItem.Action.ADD_PLAYER) {
                        final NetworkPlayerInfo networkplayerinfo = new NetworkPlayerInfo(addPlayerData);
                        if (((Collection<NetworkPlayerInfo>) mc.getNetHandler().getPlayerInfoMap()).stream().anyMatch(i -> i.getGameProfile().getName().equals(networkplayerinfo.getGameProfile().getName()))) {
                            //packetEvent.setCancelled(true);
                        }
                    }
                }

            }
		}
	}
	
	public void onSkipEvent(Event event) {
		if(event instanceof EventUpdate) {

			CopyOnWriteArrayList<Entity> entList = new CopyOnWriteArrayList<Entity>();
			for(Entity ent : mc.theWorld.loadedEntityList) {
				entList.add(ent);
			}
			entities = entList;
			//System.out.println(entList.get(0));
		}
	}
	
	public static CopyOnWriteArrayList<Entity> getEntities() {
		return entities;
	}
	
}
