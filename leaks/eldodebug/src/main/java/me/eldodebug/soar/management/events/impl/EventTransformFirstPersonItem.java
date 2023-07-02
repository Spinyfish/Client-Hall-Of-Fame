package me.eldodebug.soar.management.events.impl;

import me.eldodebug.soar.management.events.Event;
import net.minecraft.item.ItemStack;

public class EventTransformFirstPersonItem extends Event{

	private ItemStack itemToRender;
	private float equipProgress;
	private float swingProgress;
	
	public EventTransformFirstPersonItem(ItemStack itemToRender, float equipProgress, float swingProgress) {
		this.itemToRender = itemToRender;
		this.equipProgress = equipProgress;
		this.swingProgress = swingProgress;
	}

	public ItemStack getItemToRender() {
		return itemToRender;
	}

	public void setItemToRender(ItemStack itemToRender) {
		this.itemToRender = itemToRender;
	}

	public float getEquipProgress() {
		return equipProgress;
	}

	public void setEquipProgress(float equipProgress) {
		this.equipProgress = equipProgress;
	}

	public float getSwingProgress() {
		return swingProgress;
	}

	public void setSwingProgress(float swingProgress) {
		this.swingProgress = swingProgress;
	}
}
