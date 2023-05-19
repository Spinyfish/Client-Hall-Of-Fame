package marshscript;

import club.marsh.bloom.Bloom;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

public class ScriptManager {
    public ConcurrentSet<Script> scripts = new ConcurrentSet<>();
    ScriptEngine ee = new ScriptEngineManager().getEngineByName("Nashorn");
    String requiredforscript =
            "var Minecraft = Java.type(\"" + Minecraft.class.getCanonicalName() + "\");\n"
                    +
                    "var mc = Minecraft.getMinecraft();"
                    +
                    "\nvar MovementUtils = Java.type(\"" + MovementUtils.class.getCanonicalName() + "\");\n" +
                    "var ModuleManager = Java.type(\"" + Bloom.class.getCanonicalName() + "\").INSTANCE.moduleManager;";
    String scriptend = "function onDisable() {script[\"onDisable\"]()};\n" +
            "function onEnable() {script[\"onEnable\"]()};\n" +
            "function onUpdate(e) {script[\"onUpdate\"](e)};";
    public void load(File marshscriptdir) {
        if (!marshscriptdir.exists()) {
            marshscriptdir.mkdir();
        }

        for (File script : Objects.requireNonNull(marshscriptdir.listFiles())) {
            System.out.println("loading: " + script.getName());
            if (script.exists()) {
                StringBuilder scriptdata = new StringBuilder();
                scriptdata.append("var script=[];");
                scriptdata.append(requiredforscript);
                try {
                    Scanner myReader = new Scanner(script);
                    while (myReader.hasNextLine()) {
                        scriptdata.append("\n"+myReader.nextLine());
                    }
                    myReader.close();
                } catch (FileNotFoundException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
                scriptdata.append(scriptend);
                String thescriptsdata = scriptdata.toString();
                //	System.out.println(thescriptsdata); we already know it detects the script, however it's still broken for some reason
                try {
                    scripts.add(new Script(thescriptsdata, ee.eval(thescriptsdata + "script[\"name\"]").toString(), ee.eval(thescriptsdata + "script[\"key\"]").toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (Script scriptg : scripts) {
                Bloom.INSTANCE.moduleManager.addModules(scriptg);
            }
        }
    }
    public void reload(File marshscriptdir) {
        for (Script script : scripts) {
            Bloom.INSTANCE.moduleManager.getModules().remove(script);
            scripts.remove(script);
        }
        this.load(marshscriptdir);
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("\247dBloom \2479>>\247a Reloaded scripts."));
    }
}