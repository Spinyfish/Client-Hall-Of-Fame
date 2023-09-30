package com.alan.clients.script.api.wrapper.impl.event.impl;

import com.alan.clients.newevent.impl.render.Render3DEvent;
import com.alan.clients.script.api.wrapper.impl.event.ScriptEvent;

public class ScriptRender3DEvent extends ScriptEvent<Render3DEvent>
{
	public ScriptRender3DEvent(Render3DEvent wrappedEvent)
	{
		super(wrappedEvent);
	}

	public float getPartialTicks() {
		return this.wrapped.getPartialTicks();
	}

	@Override
	public String getHandlerName()
	{
		return "onRender3D";
	}
}
