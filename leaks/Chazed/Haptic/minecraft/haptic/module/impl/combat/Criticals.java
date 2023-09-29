package haptic.module.impl.combat;

import com.mojang.realmsclient.gui.ChatFormatting;

import haptic.Haptic;
import haptic.event.Event;
import haptic.event.impl.EventMotionUpdate;
import haptic.event.impl.EventRender;
import haptic.event.impl.EventSendPacket;
import haptic.module.Category;
import haptic.module.ListeningType;
import haptic.module.Module;
import haptic.setting.impl.BooleanSetting;
import haptic.setting.impl.ModeSetting;
import haptic.util.network.PacketUtils;
import haptic.util.network.ServerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;

public class Criticals extends Module {
	
	public ModeSetting mode = new ModeSetting("Mode", "Packet", "Packet", "Edit", "NoGround");
	private BooleanSetting onlyOnGround = new BooleanSetting("Only on ground", true, this);
	
	private int counter;
	private double y;
	
	public Criticals() {
		super("Criticals", Category.COMBAT, ListeningType.ALWAYS);
		this.addSettings(mode, onlyOnGround);
	}
	
	public void onEvent(Event event) {
		
		if(event instanceof EventRender) {
			onlyOnGround.setShowed(mode.is("Packet") || mode.is("Edit"));
		}
		
		if(!this.isEnabled()) {
			return;
		}
		
		if(event instanceof EventMotionUpdate) {
			this.setDisplayName(this.getName() + " " + ChatFormatting.GRAY + mode.getMode());
			EventMotionUpdate e = (EventMotionUpdate) event;
			if(mode.is("NoGround") && Haptic.getModuleManager().getModuleByName("Killaura").isEnabled() && ((Killaura) Haptic.getModuleManager().getModuleByName("Killaura")).target != null && !Haptic.getModuleManager().getModuleByName("Fly").isEnabled()) {
				e.setOnGround(!mc.thePlayer.onGround && mc.thePlayer.motionY >= 0 && mc.thePlayer.motionY <= 0.16);
			}
			
			if(mode.is("Edit")) {
				if(Haptic.getModuleManager().getModuleByName("Killaura").isEnabled() && ((Killaura) Haptic.getModuleManager().getModuleByName("Killaura")).target != null) {
					Entity entity = ((Killaura) Haptic.getModuleManager().getModuleByName("Killaura")).target;
					if(entity instanceof EntityPlayer) {
						EntityPlayer target = (EntityPlayer) entity;
						
						if(ServerUtils.isOnHypixel()) {
							hypixelEditCrits(e, target);
						} else {
							EditCrits(e, target);
						}
					}
				}
			}
		} else if(event instanceof EventSendPacket) {
			EventSendPacket e = (EventSendPacket) event;
			if(e.getPacket() instanceof C02PacketUseEntity) {
				C02PacketUseEntity packet = (C02PacketUseEntity) e.getPacket();
				if(packet.getAction() == C02PacketUseEntity.Action.ATTACK) {
					if(packet.getEntity().hurtResistantTime < 4) { // It's not useful to do criticals when the entity can't be attacked lol
						if(mode.is("Packet") && (mc.thePlayer.onGround || !onlyOnGround.isEnabled())) { // Should kinda bypass ncp if only on ground is enabled
							PacketUtils.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.1, mc.thePlayer.posZ, false));
							PacketUtils.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
						}
					}
				}
			}
		}
	}
	
	private void hypixelEditCrits(EventMotionUpdate e, EntityPlayer target) {
		// Make your own !!!
	}
	
	private void EditCrits(EventMotionUpdate e, EntityPlayer target) {
		if(target.hurtTime < 5) {
			e.setOnGround(true);
		} else if(target.hurtTime < 3) {
			e.setY(e.getY() + 0.01);
			e.setOnGround(false);
		} else {
			e.setY(e.getY() + 1E-5);
			e.setOnGround(false);
		}
	}
	
}