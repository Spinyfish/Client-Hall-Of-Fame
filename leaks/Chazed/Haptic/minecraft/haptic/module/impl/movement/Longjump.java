package haptic.module.impl.movement;

import org.lwjgl.input.Keyboard;

import haptic.Haptic;
import haptic.event.Event;
import haptic.event.impl.EventReceivePacket;
import haptic.event.impl.EventRender;
import haptic.event.impl.EventSendPacket;
import haptic.event.impl.EventUpdate;
import haptic.event.impl.MovementEvent;
import haptic.module.Category;
import haptic.module.ListeningType;
import haptic.module.Module;
import haptic.module.impl.combat.Killaura;
import haptic.setting.impl.ModeSetting;
import haptic.setting.impl.NumberSetting;
import haptic.util.network.PacketUtils;
import haptic.util.player.DamageUtil;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class Longjump extends Module {
	
	private ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla");
	private NumberSetting vanillaSpeed = new NumberSetting("Vanilla Speed", 2, 0.2, 9, 0.2, this);
	private NumberSetting vanillaJumpHeight = new NumberSetting("Vanilla Jump Height", 0.4, 0.2, 9, 0.2, this);
	
	private int counter, ticks;
	private boolean doneBoosting;
	private double speed;
	
	public Longjump() {
		super("Longjump", Category.MOVEMENT, ListeningType.ALWAYS, Keyboard.KEY_C);
		this.addSettings(mode, vanillaSpeed, vanillaJumpHeight);
	}
	
	public void onDisable() {
		move.setSpeed(0.1);
		mc.thePlayer.motionY = Math.min(mc.thePlayer.motionY, 0);
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventRender) {
			vanillaSpeed.setShowed(mode.is("Vanilla"));
			vanillaJumpHeight.setShowed(mode.is("Vanilla"));
		}
		
		if(!isEnabled()) {
			return;
		}
		
		switch(mode.getMode()) {
		case "Vanilla":
			Vanilla(e);
			break;
		}
	}
	
	private void Vanilla(Event event) {
		if(event instanceof MovementEvent) {
			MovementEvent e = (MovementEvent) event;
			if(mc.thePlayer.onGround) {
				if(vanillaJumpHeight.getValue() >= 0.4 && vanillaJumpHeight.getValue() <= 0.42) {
					//Legit vertical motion
					e.setMotionY(0.41999998688698);
					mc.thePlayer.motionY = 0.42;
				} else {
					e.setMotionY(mc.thePlayer.motionY = vanillaJumpHeight.getValue());
				}
			}
			move.setSpeed(e, vanillaSpeed.getValue());
		}
	}

}
