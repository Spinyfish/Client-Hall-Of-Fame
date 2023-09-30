package intentions.modules.player;

import java.awt.KeyboardFocusManager;

import org.lwjgl.input.Keyboard;

import intentions.Client;
import intentions.events.Event;
import intentions.modules.Module;
import intentions.settings.BooleanSetting;
import intentions.settings.ModeSetting;
import intentions.settings.Setting;
import intentions.util.PlayerUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

public class InvMove extends Module {
  
  
  public InvMove() {
    super("InvMove", 38, Module.Category.PLAYER, "Allows you to move in your inventory", true);
  }
  
  public void onEvent(Event e) {
    if (e instanceof intentions.events.listeners.EventUpdate && 
      e.isPre()) {
    	if(mc.currentScreen != null) {
    		if(mc.thePlayer != null) {
    			
    			if(Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
    				mc.gameSettings.keyBindForward.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
    			}else {
    				mc.gameSettings.keyBindForward.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
    			}
    			if(Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode())) {
    				mc.gameSettings.keyBindRight.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), true);
    			}else {
    				mc.gameSettings.keyBindRight.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);
    			}
    			if(Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode())) {
    				mc.gameSettings.keyBindLeft.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), true);
    			}else {
    				mc.gameSettings.keyBindLeft.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
    			}
    			if(Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
    				mc.gameSettings.keyBindBack.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), true);
    			}else {
    				mc.gameSettings.keyBindBack.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
    			}
    			if(Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
    				mc.gameSettings.keyBindJump.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), true);
    			}else {
    				mc.gameSettings.keyBindJump.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
    			}
    			if(Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode())) {
    				mc.gameSettings.keyBindSprint.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
    			}else {
    				mc.gameSettings.keyBindSprint.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
    			}
    			
    		}
    	}
    }
    
  }
}
