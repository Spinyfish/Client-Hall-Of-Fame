package me.wavelength.baseclient.module.modules.player;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;

import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;

public class Nofall extends Module {

    public Nofall() {
        super("Nofall", "No fall damage!", Keyboard.KEY_F, Category.PLAYER, AntiCheat.VANILLA, AntiCheat.HYPIXEL, AntiCheat.VERUS);
    }

    private boolean isFlying;
    private boolean allowFlying;

    @Override
    public void setup() {
        moduleSettings.addDefault("speed", 1.0D);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate(UpdateEvent event) {
    	if(this.antiCheat == AntiCheat.VANILLA) {
        	if (mc.thePlayer.fallDistance >= 2) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
        	}
    	}
    	
    	if(this.antiCheat == AntiCheat.HYPIXEL) {
    	if (mc.thePlayer.fallDistance >= 3) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
            mc.thePlayer.fallDistance = 0;
        	}
    	}
    	
    	if(this.antiCheat == AntiCheat.VERUS) {


    	
    	if(mc.thePlayer.fallDistance >= 2) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
            mc.thePlayer.fallDistance = 0;
    	}
    	}
    	
    	
    	
    }
}