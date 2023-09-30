package dev.africa.pandaware.impl.script;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.impl.script.functions.ScriptPlayerFunctions;
import dev.africa.pandaware.impl.script.functions.ScriptPrinterFunctions;
import dev.africa.pandaware.impl.script.functions.ScriptRenderFunctions;
import dev.africa.pandaware.manager.script.event.ScriptEventManager;
import dev.africa.pandaware.manager.script.module.ScriptModuleManager;
import dev.africa.pandaware.manager.script.module.setting.ScriptSettingManager;
import dev.africa.pandaware.utils.java.FileUtils;
import lombok.Getter;
import lombok.Setter;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;

@Getter
public class Script implements MinecraftInstance {
    private final ScriptEngine scriptEngine = new ScriptEngineManager()
            .getEngineByName("nashorn");

    private final ScriptEventManager eventManager = new ScriptEventManager();

    @Setter
    private Module module;

    private File file;

    public void init(File file) throws ScriptException {
        this.file = file;

        this.registerFunctions();

        String input = FileUtils.readFromFile(file);

        if (input.contains("java.io.File") ||
                input.contains("java.net.URL") ||
                input.contains("java.net.Socket") ||
                !input.contains("moduleManager.register")) {
            return;
        }

        this.scriptEngine.eval(input);
    }

    void registerFunctions() {
        this.scriptEngine.put("moduleManager", new ScriptModuleManager(this));
        this.scriptEngine.put("settingManager", new ScriptSettingManager(this));

        this.scriptEngine.put("Printer", new ScriptPrinterFunctions());
        this.scriptEngine.put("PlayerUtils", new ScriptPlayerFunctions());
        this.scriptEngine.put("RenderUtils", new ScriptRenderFunctions());

        this.scriptEngine.put("events", this.eventManager);

        this.scriptEngine.put("mc", mc);
    }

    public void destroy() {
        this.module.toggle(false);

        Client.getInstance().getEventDispatcher()
                .unsubscribe(this.module);

        Client.getInstance().getModuleManager()
                .getMap()
                .remove(this.module.getClass());
    }
}