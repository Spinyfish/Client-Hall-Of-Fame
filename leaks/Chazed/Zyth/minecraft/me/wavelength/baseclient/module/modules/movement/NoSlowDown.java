package me.wavelength.baseclient.module.modules.movement;

import java.awt.Desktop.Action;

import me.wavelength.baseclient.event.EventHandler;
import me.wavelength.baseclient.event.EventManager;
import me.wavelength.baseclient.event.events.EventPreUpdate;
import me.wavelength.baseclient.event.events.PacketReceivedEvent;
import me.wavelength.baseclient.event.events.PacketSentEvent;
import me.wavelength.baseclient.event.events.PreMotionEvent;
import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoSlowDown extends Module {

	public NoSlowDown() {
		super("NoSlowDown", "keep you sprint no slow down", 0, Category.MOVEMENT, AntiCheat.VANILLA, AntiCheat.HYPIXEL);
	}

	public static int ticks = 0;

	
	@Override
	   public void onUpdate(UpdateEvent event) {
		ticks++;
		
		if(this.antiCheat == AntiCheat.HYPIXEL) {
		if(mc.thePlayer.isEating()) {
			if(ticks >= 10) {
			mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
			ticks = 0;
				}
			}
		
		
		
		

		}
		
		
		
	}
	
	@Override
	public void onPacketSent(PacketSentEvent event) {
		
	}

	public void PreMotionEvent(PreMotionEvent event) {
		
    }


	
}