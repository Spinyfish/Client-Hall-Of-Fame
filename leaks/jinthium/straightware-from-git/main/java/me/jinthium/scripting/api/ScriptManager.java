package me.jinthium.scripting.api;

import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.api.notification.NotificationType;
import me.jinthium.straight.api.util.MinecraftInstance;
import me.jinthium.straight.api.util.Util;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.manager.ModuleManager;
import net.minecraft.util.ChatComponentText;

import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;


public class ScriptManager implements MinecraftInstance, Util {

    private final File scriptDirectory = new File(mc.mcDataDir, "/Straightware/Scripts");
    private final List<Script> scripts = new ArrayList<>();

    public ScriptManager() {
        if (!scriptDirectory.exists()) scriptDirectory.mkdirs();
    }

    public void reloadScripts() {
        HashMap<Object, Module> moduleList = Client.INSTANCE.getModuleManager().getModuleMap();

        scripts.removeIf(Script::isReloadable);

        File[] scriptFiles = scriptDirectory.listFiles(((dir, name) -> name.endsWith(".js")));

        if (scriptFiles == null) return;

        for (Module moduleValue : moduleList.values()) {
            if (moduleValue.getCategory().equals(Module.Category.SCRIPTS) && moduleValue.isEnabled()) {
                if (((ScriptModule) moduleValue).isReloadable()) {
                    moduleValue.toggle();
                }
            }
        }

        moduleList.values().removeIf(moduleClass ->
                moduleClass.getCategory().equals(Module.Category.SCRIPTS) && ((ScriptModule) moduleClass).isReloadable());

        for (File scriptFile : scriptFiles) {
            if (scripts.stream().anyMatch(script -> script.getFile().equals(scriptFile))) continue;
            try {
                scripts.add(new Script(scriptFile));
            } catch (Exception e) {
                mc.thePlayer.addChatComponentMessage(new ChatComponentText(e.getMessage().replace("\r", "").replace("<eval>", "§l" + scriptFile.getName() + "§r") + "\n"));
                //NotificationManager.post(NotificationType.WARNING, "Failed to load script §l" + scriptFile.getName(), "Check chat for more information.");
            }
        }

        scripts.forEach(script -> moduleList.put(script.getName() + script.getDescription(), script.getScriptModule()));

        scripts.stream().filter(script -> !script.isReloadable()).forEach(script -> {
            script.setReloadable(true);
            script.getScriptModule().setReloadable(true);
        });


        ModuleManager.reloadModules = true;
        //ScriptPanel.reInit = true;
        Client.INSTANCE.getNotificationManager().post("Script Manager", "Local scripts loaded", NotificationType.INFO);
    }


    public String[] getScriptFileNameList() {
        List<File> scriptFiles = Arrays.asList(Objects.requireNonNull(scriptDirectory.listFiles(((dir, name) -> name.endsWith(".js")))));
        HashMap<File, BasicFileAttributes> attributesHashMap = new HashMap<>();
        try {
            for (File scriptFile : scriptFiles) {
                attributesHashMap.put(scriptFile, Files.readAttributes(scriptFile.toPath(), BasicFileAttributes.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        scriptFiles.sort(Comparator.<File>comparingLong(file -> attributesHashMap.get(file).lastModifiedTime().toMillis()).reversed());

        return scriptFiles.stream().map(File::getName).toArray(String[]::new);
    }


    public boolean processScriptData(File scriptFile) {
        Script script;
        try {
            script = new Script(scriptFile);
        } catch (ScriptException e) {
            Client.INSTANCE.getNotificationManager().post("Error", e.getMessage(), NotificationType.WARNING, 10);
            return false;
        }

        if (script.getEventHashMap().isEmpty()) {
            Client.INSTANCE.getNotificationManager().post("Error", "You cannot upload empty scripts", NotificationType.WARNING, 10);
            return false;
        }

        return true;
    }

}
