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

public class Chat extends Module {
    TimeUtils timeUtils;
    public static Setting setting, targetset;
    public Chat() {
        super("Chat", "Chat", Keyboard.KEY_NONE, Category.Render);
    }

    public void onEnable() {
        super.onEnable();
    }

    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void setup() {
        super.setup();
        final ArrayList<SettingsItem> items = new ArrayList<>();
        ArrayList<String> modes = new ArrayList<>();
        modes.add("Broke");
        modes.add("Broke2");
        items.add(new SettingsItem("ChatMode", modes, "Broke2", "", ""));
        Management.instance.settingsmgr.addSetting(new Setting(this, items));
        setting = Management.instance.settingsmgr.getSettingByName(getName());
    }


    public void onUpdate() {
        if (this.isEnabled()) {
            super.onUpdate();
            setExtraTag("Broke");

        }
    }
    }
