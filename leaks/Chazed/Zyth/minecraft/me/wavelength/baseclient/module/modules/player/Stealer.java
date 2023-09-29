package me.wavelength.baseclient.module.modules.player;

import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.module.Module;
import me.wavelength.baseclient.utils.Timer;
import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.network.play.client.C00PacketKeepAlive;

public class Stealer extends Module{

    public Stealer() {
        super("Stealer", "Steals loot for you.", Keyboard.KEY_B, Category.PLAYER,AntiCheat.TICKS);
        moduleSettings.addDefault("delay", 1.0D);
    }

    public Timer time = new Timer();

    int delay;
    
    public void onEnable() {
		time.reset();
    }


    public void onUpdate(UpdateEvent event) {
    	if(this.isToggled()) {
			if((mc.thePlayer.openContainer != null) && ((mc.thePlayer.openContainer instanceof ContainerChest))) {
				ContainerChest chest = (ContainerChest) mc.thePlayer.openContainer;
				for(int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
					if((chest.getLowerChestInventory().getStackInSlot(i) != null) && (time.delay(this.moduleSettings.getDouble("delay")*50))) {
						mc.playerController.windowClick(chest.windowId, i, 0, 1, mc.thePlayer);
						time.reset();

					}	

				}
				
			}
		}
    }
}