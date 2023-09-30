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

public class CapeManager extends Module {
    TimeUtils timeUtils;
    public CapeManager() {
        super("CapeManager", "CapeManager", Keyboard.KEY_NONE, Category.Render);
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



        }
    }
    @Override

    public void setup() {
        super.setup();
        final ArrayList<SettingsItem> items = new ArrayList<>();
        final ArrayList<String> teest = new ArrayList<>();
        teest.add("Dad");
        teest.add("Hentai");
        teest.add("Main");
        teest.add("Fire");
       // teest.add("HAZE");
        items.add(new SettingsItem("Cape", teest, "Dad", "", ""));
        Management.instance.settingsmgr.addSetting(new Setting(this, items));
    }
}
