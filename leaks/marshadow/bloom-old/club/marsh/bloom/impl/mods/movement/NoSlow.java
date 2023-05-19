package club.marsh.bloom.impl.mods.movement;


import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.SlowdownEvent;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.api.value.BooleanValue;
import club.marsh.bloom.api.value.ModeValue;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C09PacketHeldItemChange;


public class NoSlow extends Module {
	public NoSlow() {
		super("No Slow",Keyboard.KEY_NONE,Category.MOVEMENT);
	}
	
	BooleanValue stopsprint = new BooleanValue("Stop Sprint",true,() -> true);
	ModeValue mode = new ModeValue("Mode","Watchdog",new String[] {"Watchdog","Vanilla"});
	@Subscribe
	public void onSlow(SlowdownEvent e) {
		if (mode.getMode() == "Watchdog") {
			if (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
				e.strafe = 0.9f;
				e.forward = 0.9f;
			}
		} else {
			e.strafe = 1.0f;
			e.forward = 1.0f;
		}
		e.stopSprint = stopsprint.isOn();
		if (stopsprint.isOn())
			mc.thePlayer.setSprinting(false);
	}
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		switch (mode.getMode()) {
			case "Watchdog": 
				if (mc.thePlayer.ticksExisted % 2 == 0 || mc.thePlayer.getItemInUseCount() < 3) {
					if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
						mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem - 1 < 0 ? 8 : mc.thePlayer.inventory.currentItem - 1));
						mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
					}
				}
				break;
		}
	}
	@Override
	public void addModesToModule() {
		autoSetName(mode);
	}
}
