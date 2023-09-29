package me.wavelength.baseclient.module.modules.render;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import me.wavelength.baseclient.utils.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;	

public class SessionInfo extends Module {

    public SessionInfo() {
        super("SessionInfo", "Shows your stats", 0, Category.RENDER, AntiCheat.GLOW);
    }
    
    public static boolean penis;
    
	@Override
	public void onUpdate(UpdateEvent event) {
		penis = true;
	}
	
	public void onDisable()
	{
		penis = false;
	}
	
	public void onEnable()
	{
		penis = true;
	}
}