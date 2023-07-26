package com.craftworks.pearclient.hud.mods.impl;

import java.util.ArrayList;

import com.craftworks.pearclient.PearClient;
import com.craftworks.pearclient.event.EventTarget;
import com.craftworks.pearclient.event.impl.EventReceivePacket;
import com.craftworks.pearclient.event.impl.EventUpdate;
import com.craftworks.pearclient.hud.mods.HudMod;

import net.minecraft.network.play.server.S2BPacketChangeGameState;

public class WeatherChanger extends HudMod {
	
	public WeatherChanger() {
		super("Weather Changer", "", 0, 0);
	}
	
	@EventTarget
    public void onUpdate(EventUpdate event) {
    	
    	/*String mode = PearClient.instance.settingsManager.getSettingByName(this, "Weather").getValString();
    	
    	switch(mode) {
	    	default:
	            mc.theWorld.setRainStrength(0);
	            mc.theWorld.setThunderStrength(0);
	    		break;
	    	case "Clear":
	            mc.theWorld.setRainStrength(0);
	            mc.theWorld.setThunderStrength(0);
	    		break;
	    	case "Rain":
	            mc.theWorld.setRainStrength(1);
	            mc.theWorld.setThunderStrength(0);
	    		break;
	    	case "Thunder":
	            mc.theWorld.setRainStrength(1);
	            mc.theWorld.setThunderStrength(1);
	    		break;
    	}
    	*/
    }

    @EventTarget
    public void onReceivePacket(EventReceivePacket event) {
        if(event.getPacket() instanceof S2BPacketChangeGameState){
            S2BPacketChangeGameState S2BPacket=(S2BPacketChangeGameState) event.getPacket();
            if(S2BPacket.getGameState()==7||S2BPacket.getGameState()==8)
                event.setCancelled(true);
        }
    }

}
