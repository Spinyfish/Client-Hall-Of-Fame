package me.wavelength.baseclient.module.modules.combat;

import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import me.wavelength.baseclient.module.modules.exploit.HypixelHelper;
import me.wavelength.baseclient.utils.MovementUtils;
import me.wavelength.baseclient.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import org.lwjgl.input.Keyboard;

public class Criticals extends Module {
    public Criticals() {
        super("Criticals", "Criticals with none Jumping.", Keyboard.KEY_M, Category.COMBAT, AntiCheat.HYPIXEL, AntiCheat.MMC);
    }
    
    public void setup() {
        setToggled(false);
    }

public static boolean toggled;
public static int mode;

    public void onDisable(){
    	toggled = false;
    }

    public void onUpdate(UpdateEvent event) {
    	

    	
    	if(this.antiCheat == AntiCheat.HYPIXEL) {
    		mode = 1;
        	if(mc.getCurrentServerData().serverIP.equalsIgnoreCase("hypixel.net") || mc.getCurrentServerData().serverIP.equalsIgnoreCase("hypixel.io") && mc.getCurrentServerData().serverIP.equalsIgnoreCase("mc.hypixel.net") || mc.getCurrentServerData().serverIP.equalsIgnoreCase("mc.hypixel.io")) {
        		Utils.message = "This module has been disabled due to unstable hypixel bypasses.";
        		//Utils.print();
        		//toggle();
            	toggled = false;
        	}
    	}
    	
    	if(this.antiCheat == AntiCheat.MMC) {
    		mode = 2;
    	}
    	
    	toggled = true;
    }
}
