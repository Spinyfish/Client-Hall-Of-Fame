/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  vip.astroline.client.Astroline
 *  vip.astroline.client.layout.clickgui.ClickGUI
 *  vip.astroline.client.layout.dropdown.ClickGUI
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.service.module.value.ModeValue
 */
package vip.astroline.client.service.module.impl.render;

import net.minecraft.client.gui.GuiScreen;
import vip.astroline.client.Astroline;
import vip.astroline.client.layout.dropdown.ClickGUI;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.service.module.value.ModeValue;

public class ClickGui
extends Module {
    public ModeValue modeValue = new ModeValue("ClickGUI", "ClickGUI Mode", "Dropdown", new String[]{"Material"});

    public ClickGui() {
        super("ClickGUI", Category.Render, 54, false);
    }

    public void onEnable() {
        if (this.modeValue.isCurrentMode("Dropdown")) {
            Astroline.INSTANCE.setDropdown(new ClickGUI());
            mc.displayGuiScreen((GuiScreen)Astroline.INSTANCE.getDropdown());
        } else if (this.modeValue.isCurrentMode("Material")) {
            Astroline.INSTANCE.setMaterial(new vip.astroline.client.layout.clickgui.ClickGUI());
            mc.displayGuiScreen((GuiScreen)Astroline.INSTANCE.getMaterial());
        }
        this.enableModule();
        super.onEnable();
    }

    public void onDisable() {
        super.onDisable();
    }
}
