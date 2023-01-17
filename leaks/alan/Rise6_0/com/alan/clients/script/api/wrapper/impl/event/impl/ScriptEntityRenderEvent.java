package com.alan.clients.script.api.wrapper.impl.event.impl;

import com.alan.clients.newevent.impl.render.EntityRenderEvent;
import com.alan.clients.script.api.wrapper.impl.ScriptEntity;
import com.alan.clients.script.api.wrapper.impl.event.ScriptEvent;

public class ScriptEntityRenderEvent extends ScriptEvent<EntityRenderEvent>
{
	public ScriptEntityRenderEvent(EntityRenderEvent wrappedEvent)
	{
		super(wrappedEvent);
	}

	public boolean isPre() {
		return this.wrapped.isPre();
	}

	public ScriptEntity getEntity() {
		return new ScriptEntity(this.wrapped.getEntity());
	}

	@Override
	public String getHandlerName()
	{
		return "onEntityRender";
	}
}
