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
 */
package vip.astroline.client.layout.dropdown.components.impl;

import vip.astroline.client.layout.dropdown.ClickGUI;
import vip.astroline.client.layout.dropdown.components.Component;
import vip.astroline.client.layout.dropdown.panel.Panel;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.service.module.impl.render.Hud;
import vip.astroline.client.storage.utils.render.ColorUtils;

public class ModeLabel
extends Component {
    public ModeLabel(Panel panel, int offX, int offY, String title) {
        super(panel, offX, offY, title);
        this.width = ClickGUI.settingsWidth;
        this.height = (int)(FontManager.tiny.getHeight(this.title) + 1.0f);
        this.type = "Mode Label";
    }

    public void render(int mouseX, int mouseY) {
        FontManager.tiny.drawString(this.title, (float)(this.x + 5), (float)this.y + ((float)(this.height / 2) - FontManager.tiny.getHeight(this.title) / 2.0f) + 2.0f, Hud.isLightMode.getValue() != false ? ColorUtils.GREY.c : 0xFFFFFF);
    }

    public void mouseUpdates(int var1, int var2, boolean var3) {
    }
}
