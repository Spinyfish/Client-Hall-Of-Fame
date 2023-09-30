package de.fanta.module.impl.movement;

import java.awt.Color;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.module.Module;

import de.fanta.setting.Setting;
import de.fanta.setting.settings.DropdownBox;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

public class AntiVoid extends Module {
	public AntiVoid() {
		super("AntiVoid", 0, Type.Movement, Color.yellow);
		this.settings
		.add(new Setting("Modes", new DropdownBox("Intave", new String[] { "Watchdog", "Intave"})));
	}

	
	public void onEnable() {
		mc.thePlayer.respawnPlayer();
		mc.thePlayer.isDead = false;
		super.onEnable();
	}
	
	public void onDisable() {
		mc.thePlayer.respawnPlayer();
		mc.thePlayer.isDead = false;
		super.onDisable();
	}

	@Override
	public void onEvent(Event event) {
		switch (((DropdownBox) this.getSetting("Modes").getSetting()).curOption) {

		case "Intave":
			if (!isBlockUnder()) {	
				if (mc.thePlayer.fallDistance > 25F) {
					mc.thePlayer.isDead = true;
				}else {
					mc.thePlayer.isDead = false;
				}
			}
			break;
			

		case "Watchdog":
			if (!isBlockUnder()) {	
				if (mc.thePlayer.fallDistance > 0.2F) {
				mc.thePlayer.motionY = -1F;
				Client.INSTANCE.moduleManager.getModule("Speed").setState(false);
				}

			}
		
			break;
			
		}
	}
		
	
	
	

	public boolean isBlockUnder() {
		for (int i = (int) mc.thePlayer.posY; i >= 0; --i) {
			BlockPos position = new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ);

			if (!(mc.theWorld.getBlockState(position).getBlock() instanceof BlockAir)) {
				return true;
			}
		}

		return false;
	}
}
