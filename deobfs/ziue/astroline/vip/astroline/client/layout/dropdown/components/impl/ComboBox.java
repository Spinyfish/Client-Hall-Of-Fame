/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.layout.dropdown.ClickGUI
 *  vip.astroline.client.layout.dropdown.components.Component
 *  vip.astroline.client.layout.dropdown.panel.Panel
 *  vip.astroline.client.service.font.FontManager
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.service.module.impl.render.Hud
 *  vip.astroline.client.service.module.value.ModeValue
 *  vip.astroline.client.storage.utils.render.ColorUtils
 *  vip.astroline.client.storage.utils.render.render.GuiRenderUtils
 */
package vip.astroline.client.layout.dropdown.components.impl;

import java.awt.Color;
import vip.astroline.client.layout.dropdown.ClickGUI;
import vip.astroline.client.layout.dropdown.components.Component;
import vip.astroline.client.layout.dropdown.panel.Panel;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.service.module.impl.render.Hud;
import vip.astroline.client.service.module.value.ModeValue;
import vip.astroline.client.storage.utils.render.ColorUtils;
import vip.astroline.client.storage.utils.render.render.GuiRenderUtils;

public class ComboBox
extends Component {
    public String current;
    public ModeValue modeValue;
    public int HoveredIndex;
    public Module module;
    public int yMod;
    private static final int space = 11;

    public ComboBox(ModeValue modeValue, Panel panel, int offX, int offY, Module mod) {
        super(panel, offX, offY, "");
        this.width = ClickGUI.settingsWidth;
        this.modeValue = modeValue;
        this.height = 11 * modeValue.getModes().length;
        this.type = "ComboBox";
        this.module = mod;
    }

    public ComboBox(ModeValue modeValue, Panel panel, int offX, int offY, int yMod) {
        this(modeValue, panel, offX, offY, null);
        this.yMod = yMod;
    }

    public ComboBox(ModeValue modeValue, Panel panel, int offX, int offY) {
        this(modeValue, panel, offX, offY, null);
    }

    public ComboBox(Module module, Panel panel, int offX, int offY) {
        this(module.getMode(), panel, offX, offY, module);
    }

    public void update(int mousex, int mousey) {
        super.update(mousex, mousex);
    }

    public void render(int mouseX, int mouseY) {
        int i = 0;
        while (i < this.modeValue.getModes().length) {
            float y = this.y + this.yMod + 11 * i;
            int draw = this.HoveredIndex == i ? (Hud.isLightMode.getValue().booleanValue() ? new Color(-1710875).darker().darker().getRGB() : GuiRenderUtils.darker((int)ClickGUI.mainColor, (int)-20)) : (Hud.isLightMode.getValue() != false ? new Color(-1710875).darker().getRGB() : -14342875);
            FontManager.tiny.drawString(this.modeValue.getModes()[i], (float)(this.x + 17 + 2), y + 4.5f, Hud.isLightMode.getValue() != false ? ColorUtils.GREY.c : 0xFFFFFF);
            GuiRenderUtils.drawRoundedRect((float)(this.x + 10), (float)(y + 5.0f), (float)6.5f, (float)6.5f, (float)5.0f, (int)draw, (float)1.0f, (int)draw);
            if (this.modeValue.getValue().equals(this.modeValue.getModes()[i])) {
                GuiRenderUtils.drawRoundedRect((float)((float)this.x + 11.0f), (float)(y + 6.0f), (float)4.5f, (float)4.5f, (float)5.0f, (int)Hud.hudColor1.getColorInt(), (float)0.1f, (int)Hud.hudColor1.getColorInt());
            }
            ++i;
        }
    }

    public void mouseUpdates(int mouseX, int mouseY, boolean isPressed) {
        boolean bl = this.isHovered = this.contains(mouseX, mouseY) && this.parent.mouseOver(mouseX, mouseY);
        if (!this.isHovered) {
            this.HoveredIndex = -1;
            return;
        }
        this.HoveredIndex = this.containsIndex(mouseX, mouseY);
        if (isPressed && !this.wasMousePressed && this.isHovered && this.HoveredIndex != -1) {
            this.modeValue.setValue(this.modeValue.getModes()[this.HoveredIndex]);
            if (this.module != null && this.module.isToggled()) {
                this.module.setEnabled(false);
                this.module.setEnabled(true);
            }
        }
        this.wasMousePressed = isPressed;
    }

    private int containsIndex(int mouseX, int mouseY) {
        int diff = mouseY - this.y;
        int result = 0;
        int i = diff;
        while (i > 11) {
            ++result;
            i -= 11;
        }
        return result;
    }
}
