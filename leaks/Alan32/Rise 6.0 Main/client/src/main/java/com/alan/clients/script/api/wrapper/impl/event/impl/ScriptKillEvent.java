package com.alan.clients.script.api.wrapper.impl.event.impl;

import com.alan.clients.newevent.impl.other.KillEvent;
import com.alan.clients.script.api.wrapper.impl.ScriptEntity;
import com.alan.clients.script.api.wrapper.impl.event.ScriptEvent;

public class ScriptKillEvent extends ScriptEvent<KillEvent>
{
	public ScriptEntity getEntity() {
		return new ScriptEntity(this.wrapped.getEntity());
	}
	public ScriptKillEvent(KillEvent wrappedEvent)
	{
		super(wrappedEvent);
	}

	@Override
	public String getHandlerName()
	{
		return "onKill";
	}
}
