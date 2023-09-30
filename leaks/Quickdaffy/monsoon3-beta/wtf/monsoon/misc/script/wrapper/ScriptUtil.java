/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Mouse
 */
package wtf.monsoon.misc.script.wrapper;

import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.setting.Setting;

public class ScriptUtil {
    public static HashMap<String, Setting<?>> settings = new HashMap();
    static Minecraft mc = Minecraft.getMinecraft();

    public static String currentScreen() {
        return ScriptUtil.mc.currentScreen != null ? ScriptUtil.mc.currentScreen.getClass().getSimpleName().toLowerCase() : "null";
    }

    public static int mouseX() {
        return Mouse.getX() / new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
    }

    public static int mouseY() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight() - Mouse.getY() / new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
    }

    public static boolean mouseDown(int button) {
        return Mouse.isButtonDown((int)button);
    }

    public static void addSetting(String moduleName, String name, String description, double value, double min, double max, double incrementation) {
        Setting<Double> setting = new Setting<Double>(name, value).minimum(min).maximum(max).incrementation(incrementation).describedBy(description);
        settings.put(name + " - " + moduleName, setting);
    }

    public static void addSetting(String moduleName, String name, String description, boolean value) {
        Setting<Boolean> setting = new Setting<Boolean>(name, value).describedBy(description);
        settings.put(name + " - " + moduleName, setting);
    }

    public static Setting<?> getSetting(String moduleName, String name) {
        Setting[] set = new Setting[1];
        Wrapper.getMonsoon().getModuleManager().getModules().forEach(module -> {
            if (module.getName().equalsIgnoreCase(moduleName)) {
                module.getSettings().forEach(setting -> {
                    if (setting.getName().equalsIgnoreCase(name)) {
                        set[0] = setting;
                    }
                });
            }
        });
        return set[0];
    }
}

