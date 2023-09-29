package haptic.module.impl.movement;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import haptic.Haptic;
import haptic.event.Event;
import haptic.event.impl.EventMotionUpdate;
import haptic.event.impl.EventPostMotionUpdate;
import haptic.event.impl.EventReceivePacket;
import haptic.event.impl.EventRender;
import haptic.event.impl.EventUpdate;
import haptic.event.impl.MovementEvent;
import haptic.module.Category;
import haptic.module.ListeningType;
import haptic.module.Module;
import haptic.setting.impl.BooleanSetting;
import haptic.setting.impl.ModeSetting;
import haptic.setting.impl.NumberSetting;
import haptic.util.misc.TimerUtil;
import haptic.util.movement.MovementUtils;
import haptic.util.network.ServerUtils;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

public class Speed extends Module {
	
	private ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "NCP");
	
	private BooleanSetting autoJump = new BooleanSetting("AutoJump", true, this);
	private NumberSetting vanillaSpeed = new NumberSetting("Vanilla Speed", 1, 0.1, 5, 0.1, this);
	
	private TimerUtil timerUtil = new TimerUtil();
	
	private double speed;
	private boolean prevOnGround;
	
	public Speed() {
		super("Speed", Category.MOVEMENT, ListeningType.ALWAYS, Keyboard.KEY_B);
		this.addSettings(mode, autoJump, vanillaSpeed);
	}
	
	public void onEnable() {
		if(mode.is("NCP")) {
			speed = move.getBaseMoveSpeed() + 0.05;
		}
	}
	
	public void onDisable() {
		mc.timer.timerSpeed = 1F;
		mc.thePlayer.speedInAir = 0.02F;
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventRender) {
			autoJump.setShowed(mode.is("Vanilla"));
			vanillaSpeed.setShowed(mode.is("Vanilla"));
		}
		
		if(!isEnabled()) {
			return;
		}
		
		if(e instanceof EventUpdate) {
			this.setDisplayName(this.getName() + " " + ChatFormatting.GRAY + mode.getMode());
		}
		
		switch(mode.getMode()) {
		case "Vanilla":
			Vanilla(e);
			break;
		case "NCP":
			NCP(e);
			break;
		}
	}
	
	private void Vanilla(Event e) {
		if(e instanceof MovementEvent) {
			MovementEvent event = (MovementEvent) e;
			if(autoJump.isEnabled()) {
				if(mc.thePlayer.onGround && move.isWalking()) {
					event.setMotionY(0.42);
				}
			}
			mc.thePlayer.motionY = event.getMotionY();
			move.setSpeed(event, vanillaSpeed.getValue());
		}
	}
	
	private void NCP(Event event) {
		if(event instanceof MovementEvent) {
			MovementEvent e = (MovementEvent) event;
			if(move.isWalking()) {
				if(mc.thePlayer.onGround) {
					prevOnGround = true;
					move.jump();
					e.setMotionY(mc.thePlayer.motionY);
					speed += 0.2035;
				} else {
					if(prevOnGround) {
						speed *= 0.665;
						prevOnGround = false;
					} else {
						speed = Math.max(move.getCurrentMotion(), speed - speed / 159);
					}
				}
				if(speed < move.getBaseMoveSpeed() * 0.6) {
					speed = move.getBaseMoveSpeed() * 0.6;
				}
			}
			move.setSpeed(e, speed);
			
			speed = Math.min(speed, move.getBaseMoveSpeed() + 0.4);
		} else if(event instanceof EventUpdate) {
			mc.timer.timerSpeed = 1F;
			if(move.isWalking()) {
				if(mc.thePlayer.onGround) {
					mc.timer.timerSpeed = 1.12F;
				} else {
					mc.timer.timerSpeed = 1.08F;
				}
			}
		}
	}
	
}