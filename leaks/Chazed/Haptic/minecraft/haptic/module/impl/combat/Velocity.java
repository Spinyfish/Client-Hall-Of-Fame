package haptic.module.impl.combat;

import com.mojang.realmsclient.gui.ChatFormatting;

import haptic.Haptic;
import haptic.event.Event;
import haptic.event.impl.EventReceivePacket;
import haptic.event.impl.EventRender;
import haptic.event.impl.EventUpdate;
import haptic.module.Category;
import haptic.module.ListeningType;
import haptic.module.Module;
import haptic.setting.impl.ModeSetting;
import haptic.setting.impl.NumberSetting;
import haptic.util.network.PacketUtils;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class Velocity extends Module {
	
	public NumberSetting horizontal = new NumberSetting("Horizontal", 0, 0, 1, 0.01, this);
	public NumberSetting vertical = new NumberSetting("Vertical", 0, 0, 1, 0.01, this);
	
	public ModeSetting mode = new ModeSetting("Mode", "Cancel", "Cancel", "Edit", "Reduce");
	
	public int counter;
	public boolean receivedVelocity;
	
	private boolean stoppedBlinking;
	
	public Velocity() {
		super("Velocity", Category.COMBAT, ListeningType.ALWAYS);
		this.addSettings(mode, horizontal, vertical);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		Haptic.getPacketsProcessor().setBlinking(false);
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventRender) {
			horizontal.setShowed(mode.is("Edit") || mode.is("Reduce"));
			vertical.setShowed(mode.is("Edit"));
		}
	
		if(!stoppedBlinking && !mode.is("AAC4") && !mode.is("AAC4 full")) { // In case your velocity uses blink, like in the case of aac4
			Haptic.getPacketsProcessor().setBlinking(false);
			stoppedBlinking = true;
		}
		
		if(!isEnabled()) {
			return;
		}
		
		this.setDisplayName(this.getName() + " " + ChatFormatting.GRAY + mode.getMode());
		
		switch(mode.getMode()) {
		case "Cancel":
			Cancel(e);
			break;
		case "Edit":
			Edit(e);
			break;
		case "Reduce":
			Reduce(e);
			break;
		}
		
	}
	
	private void Cancel(Event event) {
		if(event instanceof EventReceivePacket) {
			EventReceivePacket e = (EventReceivePacket) event;
			if(e.getPacket() instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
				if(packet.getEntityID() == mc.thePlayer.getEntityId()) {
					e.setCancelled(true);
				}
			} else if(e.getPacket() instanceof S27PacketExplosion) {
				S27PacketExplosion packet = (S27PacketExplosion) e.getPacket();
				e.setCancelled(true);
			}
		}
	}
	
	private void Edit(Event event) {
		if(event instanceof EventReceivePacket) {
			EventReceivePacket e = (EventReceivePacket) event;
			if(e.getPacket() instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
				if(packet.getEntityID() == mc.thePlayer.getEntityId()) {
					packet.setMotionX((int) (packet.getMotionX() * horizontal.getValue()));
					packet.setMotionZ((int) (packet.getMotionX() * horizontal.getValue()));
					
					packet.setMotionY((int) (packet.getMotionY() * vertical.getValue()));
				}
			} else if(e.getPacket() instanceof S27PacketExplosion) {
				S27PacketExplosion packet = (S27PacketExplosion) e.getPacket();
				packet.setMotionX((float) (packet.getMotionX() * horizontal.getValue()));
				packet.setMotionZ((float) (packet.getMotionX() * horizontal.getValue()));
				
				packet.setMotionY((float) (packet.getMotionY() * vertical.getValue()));
			}
		}
	}
	
	private void Reduce(Event event) {
		if(event instanceof EventReceivePacket) {
			EventReceivePacket e = (EventReceivePacket) event;
			if(e.getPacket() instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
				if(packet.getEntityID() == mc.thePlayer.getEntityId()) {
					receivedVelocity = true;
				}
			} else if(e.getPacket() instanceof S27PacketExplosion) {
				receivedVelocity = true;
			}
		} else if(event instanceof EventUpdate) {
			if(mc.thePlayer.hurtTime == 9) {
				move.motionMult(horizontal.getValue());
			}
			
			if(mc.thePlayer.hurtTime == 0) {
				receivedVelocity = false;
			}
		}
	}

}
