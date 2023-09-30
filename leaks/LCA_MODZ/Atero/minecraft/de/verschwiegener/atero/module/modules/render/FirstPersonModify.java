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

public class FirstPersonModify extends Module {
    TimeUtils timeUtils;
    public static Setting setting;
    private double sslow = 0;
    public FirstPersonModify() {
        super("FirstPersonModify", "FirstPersonModify", Keyboard.KEY_NONE, Category.Render);
    }

    public void onEnable() {
        super.onEnable();
    }

    public void onDisable() {
        super.onDisable();
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    @Override
    public void onUpdate() {
        if (this.isEnabled()) {
            super.onUpdate();

            sslow = Management.instance.settingsmgr.getSettingByName("FirstPersonModify").getItemByName("Slow").getCurrentValue();
           setExtraTag("Delay "  + Math.round(sslow));
        }
    }
    public void setup() {
        final ArrayList<SettingsItem> items = new ArrayList<>();
        ArrayList<String> modes = new ArrayList<>();
        items.add(new SettingsItem("Slow", 6F, 50, 6, ""));
        items.add(new SettingsItem("Scale", 0.1F, 0.9F, 0.4F, ""));
        items.add(new SettingsItem("BlockHitX", 0.01F, 80F, 38F, ""));
        items.add(new SettingsItem("BlockHitZ", 0.1F, 80F, 23F, ""));
        items.add(new SettingsItem("BlockHitY", 0.1F, 80F, 32F, ""));
        items.add(new SettingsItem("Rotate", 0.1F, 100F, 40F, ""));
        Management.instance.settingsmgr.addSetting(new Setting(this, items));
    }

}
