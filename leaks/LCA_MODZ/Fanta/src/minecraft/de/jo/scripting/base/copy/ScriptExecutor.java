package de.jo.scripting.base.copy;

import java.io.File;
import java.io.FileReader;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import de.fanta.Client;
import net.minecraft.client.Minecraft;

public class ScriptExecutor {

	private ScriptEngine js;
	private Bindings bindings;

	public ScriptExecutor() {
		this.js = new ScriptEngineManager().getEngineByName("nashorn");
		this.bindings = js.getBindings(ScriptContext.ENGINE_SCOPE);
		registerVariable("sysout", System.out, true);
		registerVariable("syserr", System.err, true);
		registerVariable("pi", Math.PI, true);
		registerVariable("mc", Minecraft.getMinecraft(), true);
		registerVariable("client", Client.INSTANCE, true);
		registerVariable("modulemanager", Client.INSTANCE.moduleManager, true);
		registerLine("var Module = Java.extend(Java.type(\"de.fanta.module.Module\"))");
		registerLine("var Type = Java.type(\"de.fanta.module.Module.Type\")");
		registerLine("var Color = Java.type(\"java.awt.Color\")");
	}

	public Object execute(String cmd) {
		try {
			return js.eval(cmd);
		} catch (Exception e) {
			System.err.println("An Error occurred!");
			e.printStackTrace();
			return null;
		}
	}

	public Object execute(File file) {
		try {
			return js.eval(new FileReader(file));
		} catch (Exception e) {
			System.err.println("An Error occurred!");
			e.printStackTrace();
			return null;
		}
	}
	
	public Object execute(Script script) {
		try {
			return js.eval(new FileReader(script.getFile()));
		} catch (Exception e) {
			System.err.println("An Error occurred!");
			System.err.println("At the Script: "+script.getName());
			e.printStackTrace();
			return null;
		}
	}

	public boolean registerVariable(String var, Object obj, boolean overwrite) {
		try {
			if (bindings.containsKey(var) && !overwrite) {
				System.out.println("This variable already exists!");
				return false;
			} else {
				if (bindings.containsKey(var)) {
					bindings.remove(var);
					bindings.put(var, obj);
					return true;
				} else {
					bindings.put(var, obj);
					return true;
				}
			}
		} catch (Exception e) {
			System.err.println("An Error occurred!");
			return true;
		}
	}
	
	public boolean registerLine(String line) {
		try {
			this.js.eval(line);
			return true;
		} catch (Exception e) {
			System.err.println("An Error occurred!");
			return true;
		}
	}

	public boolean removeVariable(String var) {
		try {
			if (!bindings.containsKey(var))
				return false;
			else {
				bindings.remove(var);
				return true;
			}
		} catch (Exception e) {
			System.err.println("An Error occurred!");
			return false;
		}
	}

}
