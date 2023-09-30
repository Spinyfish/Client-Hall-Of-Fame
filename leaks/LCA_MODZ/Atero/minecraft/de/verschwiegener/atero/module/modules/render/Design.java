package de.verschwiegener.atero.module.modules.render;


import de.verschwiegener.atero.settings.Setting;
import de.verschwiegener.atero.settings.SettingsItem;
import net.minecraft.client.Minecraft;

import org.lwjgl.input.Keyboard;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.module.Category;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.util.TimeUtils;
import god.buddy.aot.BCompiler;

import java.awt.*;
import java.util.ArrayList;

public class Design extends Module {
    TimeUtils timeUtils;
    public static Setting setting;
    public Design() {
        super("Design", "Design", Keyboard.KEY_NONE, Category.Render);
    }

    public void onEnable() {
        super.onEnable();
    }

    public void onDisable() {
        super.onDisable();
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public void onUpdate() {
        if (this.isEnabled()) {
            super.onUpdate();
            setExtraTag("Normal");
        }
    }
    public void setup() {
        final ArrayList<SettingsItem> items = new ArrayList<>();
        ArrayList<String> modes = new ArrayList<>();
        items.add(new SettingsItem("Alpha", 0F, 255F, 100F, ""));
        items.add(new SettingsItem("Design", modes, "Dark", "", ""));
        Management.instance.settingsmgr.addSetting(new Setting(this, items));
    }

}
