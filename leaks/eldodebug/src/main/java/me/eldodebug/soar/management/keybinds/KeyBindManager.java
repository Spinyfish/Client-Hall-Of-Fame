package me.eldodebug.soar.management.keybinds;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;

import me.eldodebug.soar.mixin.SoarTweaker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class KeyBindManager {

	public KeyBinding STOPWATCH = new KeyBinding("Stopwatch", Keyboard.KEY_P, "Soar");
	public KeyBinding QUICKPLAY = new KeyBinding("Quick Play", Keyboard.KEY_V, "Soar");
	public KeyBinding ZOOM = new KeyBinding("Zoom", Keyboard.KEY_C, "Soar");
	public KeyBinding FREELOOK = new KeyBinding("Freelook", Keyboard.KEY_Z, "Soar");
	public KeyBinding SCREENSHOT_VIEWER = new KeyBinding("Screenshot Viewer", Keyboard.KEY_M, "Soar");
	public KeyBinding CLICKGUI = new KeyBinding("Click GUI", Keyboard.KEY_RSHIFT, "Soar");
	public KeyBinding EDITHUD = new KeyBinding("Edit HUD", Keyboard.KEY_H, "Soar");
	public KeyBinding TAPLOOK = new KeyBinding("Taplook", Keyboard.KEY_L, "Soar");
	
	public KeyBindManager() {
		if(SoarTweaker.hasOptifine) {
			try {
				this.unregisterKeybind((KeyBinding) GameSettings.class.getField("ofKeyBindZoom").get(Minecraft.getMinecraft().gameSettings));
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
		}
		
		this.registerKeyBind(CLICKGUI);
		this.registerKeyBind(EDITHUD);
		this.registerKeyBind(SCREENSHOT_VIEWER);
		this.registerKeyBind(STOPWATCH);
		this.registerKeyBind(QUICKPLAY);
		this.registerKeyBind(FREELOOK);
		this.registerKeyBind(TAPLOOK);
		this.registerKeyBind(ZOOM);
	}
	
    public void registerKeyBind(KeyBinding key) {
        Minecraft.getMinecraft().gameSettings.keyBindings = ArrayUtils.add(Minecraft.getMinecraft().gameSettings.keyBindings, key);
    }

    public void unregisterKeybind(KeyBinding key) {
        if (Arrays.asList(Minecraft.getMinecraft().gameSettings.keyBindings).contains(key)) {
            Minecraft.getMinecraft().gameSettings.keyBindings = ArrayUtils.remove(Minecraft.getMinecraft().gameSettings.keyBindings, Arrays.asList(Minecraft.getMinecraft().gameSettings.keyBindings).indexOf(key));
            key.setKeyCode(0);
        }
    }
}
