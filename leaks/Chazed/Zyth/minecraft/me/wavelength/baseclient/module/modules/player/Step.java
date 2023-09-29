package me.wavelength.baseclient.module.modules.player;

import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;

public class Step extends Module {

    public Step() {
        super("Step", "IDKKKK", 0 , Category.PLAYER, AntiCheat.VANILLA);
    }


    @Override
    public void setup() {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
    	mc.thePlayer.stepHeight = 0.5f;

    }

    @Override
    public void onUpdate(UpdateEvent event) {
    	if(mc.thePlayer.isCollidedHorizontally) {
    		mc.thePlayer.jump();
    	}

			
    }
    
    
}