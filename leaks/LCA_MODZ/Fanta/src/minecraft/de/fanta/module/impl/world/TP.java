package de.fanta.module.impl.world;



import java.awt.Color;

import de.fanta.Client;
import de.fanta.command.impl.Spec;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventUpdate;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.module.impl.movement.Speed;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.Slider;
import de.fanta.utils.ChatUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C03PacketPlayer;

public class TP extends Module {
	public TP() {
		super("TP", 0, Type.World, Color.WHITE);
		this.settings.add(new Setting("Command", new CheckBox(false)));
		this.settings.add(new Setting("Boost", new CheckBox(false)));
		this.settings.add(new Setting("MineplexFast", new CheckBox(false)));
		//this.settings.add(new Setting("BoostSpeed", new Slider(0.1, 8, 0.1, 4)));
	}
	public static double BoostSpeed;
	
	@Override
	public void onEvent(Event event) {
		mc.thePlayer.onGround = true;
		if (event instanceof EventUpdate) {
			mc.thePlayer.motionY = 0F;

			if (((CheckBox) this.getSetting("Command").getSetting()).state) {
				for (Entity entity : mc.theWorld.loadedEntityList) {
					final String name = Spec.name;
					if (entity.getName().equalsIgnoreCase(name)) {
					
							//BoostSpeed = ((Slider) this.getSetting("BoostSpeed").getSetting()).curValue;
						double forward = 2.01;
						mc.thePlayer.setPosition(entity.posX + -Math.sin(Math.toRadians(entity.rotationYaw)) * forward,
								entity.posY, entity.posZ + Math.cos(Math.toRadians(entity.rotationYaw)) * forward
								
						);
						}
					}
				
//			if (mc.thePlayer.ticksExisted % 25 == 0) {
//				mc.thePlayer.sendChatMessage("/spec " + Spec.name);	
//				}
			}
			for (Entity entity : mc.theWorld.loadedEntityList) {
				final String name = Spec.name;
				if (entity.getName().equalsIgnoreCase(name)) {
					if (((CheckBox) this.getSetting("MineplexFast").getSetting()).state) {
					//	BoostSpeed = ((Slider) this.getSetting("BoostSpeed").getSetting()).curValue;
					double forward = 3.27;
					//ChatUtil.messageWithoutPrefix("" +forward);
					mc.thePlayer.setPosition(entity.posX + -Math.sin(Math.toRadians(entity.rotationYaw)) * forward,
							entity.posY, entity.posZ + Math.cos(Math.toRadians(entity.rotationYaw)) * forward
							
					);
					}else {
						
						mc.thePlayer.setPositionAndUpdate(entity.posX, entity.posY, entity.posZ);
						mc.getNetHandler().addToSendQueue(
								new C03PacketPlayer.C04PacketPlayerPosition(entity.posX, entity.posY, entity.posZ, false));
						mc.getNetHandler().addToSendQueue(
								new C03PacketPlayer.C04PacketPlayerPosition(entity.posX , entity.posY, entity.posZ , false));
						mc.getNetHandler().addToSendQueue(
								new C03PacketPlayer.C04PacketPlayerPosition(entity.posX , entity.posY, entity.posZ, false));
					}
					
				}
			}
		}
	}
}
