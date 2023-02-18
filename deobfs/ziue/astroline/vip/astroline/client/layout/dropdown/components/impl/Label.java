/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.layout.dropdown.ClickGUI
 *  vip.astroline.client.layout.dropdown.components.Component
 *  vip.astroline.client.layout.dropdown.panel.Panel
 *  vip.astroline.client.service.font.FontManager
 *  vip.astroline.client.service.module.impl.render.Hud
 *  vip.astroline.client.storage.utils.render.ColorUtils
 *  vip.astroline.client.storage.utils.render.render.GuiRenderUtils
 */
package vip.astroline.client.layout.dropdown.components.impl;

import java.awt.Color;
import vip.astroline.client.layout.dropdown.ClickGUI;
import vip.astroline.client.layout.dropdown.components.Component;
import vip.astroline.client.layout.dropdown.panel.Panel;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.service.module.impl.render.Hud;
import vip.astroline.client.storage.utils.render.ColorUtils;
import vip.astroline.client.storage.utils.render.render.GuiRenderUtils;

public class Label
extends Component {
    public Label(Panel panel, int offX, int offY, String title) {
        super(panel, offX, offY, title);
        this.width = ClickGUI.settingsWidth;
        this.height = (int)(FontManager.tiny.getHeight(this.title) + 3.0f);
        this.type = "Label";
    }

    public void render(int mouseX, int mouseY) {
        FontManager.tiny.drawString(this.title, (float)((int)((float)this.x + ((float)(this.width / 2) - FontManager.tiny.getStringWidth(this.title) / 2.0f))), (float)this.y + ((float)(this.height / 2) - FontManager.tiny.getHeight(this.title) / 2.0f) + 2.0f, Hud.isLightMode.getValue() != false ? ColorUtils.GREY.c : 0xFFFFFF);
        float space = (float)this.width - FontManager.tiny.getStringWidth(this.title) - 8.0f;
        if (!(space > 2.0f)) return;
        float width = 0.5f;
        GuiRenderUtils.drawRect((float)(this.x + 2), (float)(this.y + this.height / 2 + 1), (float)(space / 2.0f), (float)width, (Color)new Color(195, 195, 195));
        GuiRenderUtils.drawRect((float)((float)(this.x + this.width - 2) - space / 2.0f), (float)(this.y + this.height / 2 + 1), (float)(space / 2.0f), (float)width, (Color)new Color(195, 195, 195));
    }

    public void mouseUpdates(int var1, int var2, boolean var3) {
    }
}
