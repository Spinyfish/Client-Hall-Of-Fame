package marshscript;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.UpdateEvent;
import com.google.common.eventbus.Subscribe;
import marshscript.events.ScriptUpdateEvent;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class Script extends Module {
    ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("Nashorn");
    Invocable invocable = ((Invocable) scriptEngine);
    String scriptdata;
    public Script(String data,String name,String key) {
        super(name, Integer.parseInt(key), Category.SCRIPT);
        try {
            scriptEngine.eval(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        /**/
        try {
            invocable.invokeFunction("onDisable");
        } catch (Exception ignored) {

        }
    }
    @Subscribe
    public void onUpdate (UpdateEvent e) {
        // if (e instanceof EventUpdate) {
        try {
            ScriptUpdateEvent scriptUpdateEvent = new ScriptUpdateEvent(e);
            invocable.invokeFunction("onUpdate", scriptUpdateEvent);
        } catch (Exception g) {
            g.printStackTrace();
        }
        // }
    }
    @Override
    public void onEnable() {
        super.onEnable();
        try {
            invocable.invokeFunction("onEnable");
        } catch (Exception ignored) {

        }
    }
}
