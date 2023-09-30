package haptic.module.impl.render;

import org.lwjgl.input.Keyboard;

import haptic.Haptic;
import haptic.event.Event;
import haptic.event.impl.EventRender;
import haptic.event.impl.EventUpdate;
import haptic.module.Category;
import haptic.module.ListeningType;
import haptic.module.Module;
import haptic.processor.impl.PacketsProcessor;
import haptic.setting.impl.ModeSetting;
import haptic.ui.click.vestige.HapticClickGUI;
import haptic.util.misc.TimerUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class ClickGuiModule extends Module {
	
	private ModeSetting mode = new ModeSetting("Mode", "Haptic", "Haptic");
	
	private TimerUtil timer = new TimerUtil();
	
	public ClickGuiModule() {
		super("ClickGUI", Category.RENDER, Keyboard.KEY_RSHIFT);
		this.addSettings(mode);
	}
	
	public void onEnable() {
		switch(mode.getMode()) {
		case "Haptic":
			mc.displayGuiScreen(new HapticClickGUI());
			break;
		}
	}
	
	public void onEvent(Event event) {
		keyset(mc.gameSettings.keyBindForward);
		keyset(mc.gameSettings.keyBindLeft);
		keyset(mc.gameSettings.keyBindRight);
		keyset(mc.gameSettings.keyBindBack);
		keyset(mc.gameSettings.keyBindJump);
		
		if(event instanceof EventRender) {
			handleRotations();
		}
	}
	
	private void keyset(KeyBinding key){
        key.pressed = GameSettings.isKeyDown(key);
    }
	
	private void handleRotations() {
		final double speed = 0.15F;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) mc.thePlayer.rotationYaw += speed * timer.getTimeElapsed();
		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) mc.thePlayer.rotationYaw -= speed * timer.getTimeElapsed();
		if(Keyboard.isKeyDown(Keyboard.KEY_UP)) mc.thePlayer.rotationPitch -= speed * timer.getTimeElapsed();
		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) mc.thePlayer.rotationPitch += speed * timer.getTimeElapsed();
		
		if(mc.thePlayer.rotationPitch > 90) {
			mc.thePlayer.rotationPitch = 90;
		} else if(mc.thePlayer.rotationPitch < -90) {
			mc.thePlayer.rotationPitch = -90;
		}
		
		timer.reset();
	}

}
