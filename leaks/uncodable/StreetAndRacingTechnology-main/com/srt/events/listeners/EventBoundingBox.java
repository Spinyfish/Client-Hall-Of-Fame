package com.srt.events.listeners;

import com.srt.events.Event;

import net.minecraft.util.AxisAlignedBB;

public class EventBoundingBox extends Event<EventBoundingBox> {
	public AxisAlignedBB bb;

	public EventBoundingBox(AxisAlignedBB bb){
		this.bb = bb;
	}

	public AxisAlignedBB getBB() {
		return bb;
	}

	public void setBB(AxisAlignedBB bb) {
		this.bb = bb;
	}
	
}
