package dev.elysium.client.scripting.api;

import dev.elysium.client.scripting.Script;

public class ScriptAPI{

	public Script script;
	public ScriptAPI(Script script) {
		this.script = script;
	}
	
	public void Toggle() {
		script.toggle();
	}
	
}
