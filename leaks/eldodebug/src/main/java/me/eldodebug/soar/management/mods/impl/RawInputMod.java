package me.eldodebug.soar.management.mods.impl;

import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;
import me.eldodebug.soar.utils.mouse.RawMouseHelper;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MouseHelper;

public class RawInputMod extends Mod{

    public static Controller[] controllers;
    public static Mouse mouse;
    public static int dx = 0;
    public static int dy = 0;
    
	public RawInputMod() {
		super("Raw Input", "Use raw input your mouse", ModCategory.OTHER);
	}

	@Override
	public void onEnable() {
        Minecraft.getMinecraft().mouseHelper = new RawMouseHelper();
        controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

        Thread inputThread = new Thread(() -> {
            while (true) {
                for (int i = 0; i < controllers.length && mouse == null; i++) {
                    if (controllers[i].getType() == Controller.Type.MOUSE) {
                        controllers[i].poll();
                        if (((Mouse) controllers[i]).getX().getPollData() != 0.0 || ((Mouse) controllers[i]).getY().getPollData() != 0.0)
                            mouse = (Mouse) controllers[i];
                    }
                }
                if (mouse != null) {
                    mouse.poll();

                    dx += (int) mouse.getX().getPollData();
                    dy += (int) mouse.getY().getPollData();
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        inputThread.setName("inputThread");
        inputThread.start();
        
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
        mc.mouseHelper = new MouseHelper();
	}
}
