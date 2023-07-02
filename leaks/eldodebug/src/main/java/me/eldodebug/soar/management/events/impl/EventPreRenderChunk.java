package me.eldodebug.soar.management.events.impl;

import me.eldodebug.soar.management.events.Event;
import net.minecraft.client.renderer.chunk.RenderChunk;

public class EventPreRenderChunk extends Event{

	private RenderChunk renderChunk;
	
	public EventPreRenderChunk(RenderChunk renderChunk) {
		this.renderChunk = renderChunk;
	}

	public RenderChunk getRenderChunk() {
		return renderChunk;
	}
}
