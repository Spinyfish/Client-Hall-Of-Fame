/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  vip.astroline.client.layout.dropdown.ClickGUI
 *  vip.astroline.client.layout.dropdown.components.Component
 *  vip.astroline.client.layout.dropdown.panel.Panel
 *  vip.astroline.client.service.font.FontManager
 *  vip.astroline.client.service.module.impl.render.Hud
 *  vip.astroline.client.service.module.value.BooleanValue
 *  vip.astroline.client.storage.utils.render.ColorUtils
 *  vip.astroline.client.storage.utils.render.render.GuiRenderUtils
 *  vip.astroline.client.storage.utils.render.render.RenderUtil
 */
package vip.astroline.client.layout.dropdown.components.impl;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import vip.astroline.client.layout.dropdown.ClickGUI;
import vip.astroline.client.layout.dropdown.components.Component;
import vip.astroline.client.layout.dropdown.panel.Panel;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.service.module.impl.render.Hud;
import vip.astroline.client.service.module.value.BooleanValue;
import vip.astroline.client.storage.utils.render.ColorUtils;
import vip.astroline.client.storage.utils.render.render.GuiRenderUtils;
import vip.astroline.client.storage.utils.render.render.RenderUtil;

public class Checkbox
extends Component {
    public boolean value;
    public BooleanValue storage;
    private int yMod;

    public Checkbox(BooleanValue storage, Panel parent, int offX, int offY) {
        super(parent, offX, offY, storage.getGroup());
        this.width = ClickGUI.settingsWidth;
        this.height = 11;
        this.storage = storage;
        this.type = "Checkbox";
    }

    public Checkbox(BooleanValue storage, Panel parent, int offX, int offY, int yMod) {
        this(storage, parent, offX, offY);
        this.yMod = yMod;
    }

    public void update(int mouseX, int mouseY) {
        super.update(mouseX, mouseY);
        this.value = this.storage != null && this.storage.getValue() != false;
    }

    public void render(int mouseX, int mouseY) {
        FontManager.tiny.drawString(this.storage.getKey(), (float)(this.x + 5), (float)(this.y + this.yMod) + 4.5f, Hud.isLightMode.getValue() != false ? ColorUtils.GREY.c : 0xFFFFFF);
        int col1 = Hud.isLightMode.getValue() != false ? new Color(ColorUtils.GREY.c).brighter().getRGB() : -14342875;
        int col2 = Hud.isLightMode.getValue() != false ? ClickGUI.lightMainColor : ClickGUI.mainColor;
        int col3 = Hud.isLightMode.getValue() != false ? RenderUtil.reAlpha((int)ColorUtils.BLACK.c, (float)0.25f) : RenderUtil.reAlpha((int)ColorUtils.WHITE.c, (float)0.15f);
        GuiRenderUtils.drawRoundedRect((float)(this.x + 80), (float)(this.y + this.yMod + 5), (float)6.5f, (float)6.5f, (float)1.0f, (int)col1, (float)0.5f, (int)col1);
        if (this.value) {
            GuiRenderUtils.drawRoundedRect((float)(this.x + 80), (float)((float)(this.y + this.yMod) + 6.0f), (float)4.5f, (float)4.5f, (float)1.0f, (int)Hud.hudColor1.getColorInt(), (float)0.5f, (int)Hud.hudColor1.getColorInt());
        }
        if (!this.isHovered) return;
        GuiRenderUtils.drawRoundedRect((float)(this.x + 80), (float)(this.y + this.yMod + 5), (float)6.5f, (float)6.5f, (float)1.0f, (int)col3, (float)0.5f, (int)col3);
    }

    public void mouseUpdates(int mouseX, int mouseY, boolean isPressed) {
        boolean bl = this.isHovered = this.contains(mouseX, mouseY) && this.parent.mouseOver(mouseX, mouseY);
        if (isPressed && !this.wasMousePressed && this.isHovered && this.storage != null) {
            this.value = !this.value;
            this.storage.setValue((Object)this.value);
            if (this.storage.getGroup().equals("XRay")) {
                Minecraft.getMinecraft().renderGlobal.loadRenderers();
            }
        }
        this.wasMousePressed = isPressed;
    }
}
