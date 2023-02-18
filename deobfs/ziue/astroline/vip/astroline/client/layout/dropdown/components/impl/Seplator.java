/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.layout.dropdown.ClickGUI
 *  vip.astroline.client.layout.dropdown.components.Component
 *  vip.astroline.client.layout.dropdown.panel.Panel
 *  vip.astroline.client.storage.utils.render.render.GuiRenderUtils
 */
package vip.astroline.client.layout.dropdown.components.impl;

import vip.astroline.client.layout.dropdown.ClickGUI;
import vip.astroline.client.layout.dropdown.components.Component;
import vip.astroline.client.layout.dropdown.panel.Panel;
import vip.astroline.client.storage.utils.render.render.GuiRenderUtils;

public class Seplator
extends Component {
    public Seplator(Panel window, int offX, int offY) {
        super(window, offX, offY, "");
        this.width = ClickGUI.settingsWidth;
        this.height = 1;
        this.type = "Seplator";
    }

    public void render(int mouseX, int mouseY) {
        GuiRenderUtils.drawRect((float)this.x, (float)this.y, (float)(this.width - (this.parent.scrollbarEnabled ? ClickGUI.scrollbarWidth : 0)), (float)1.0f, (int)ClickGUI.backgroundColor);
        GuiRenderUtils.drawRect((float)(this.x + 2), (float)this.y, (float)(this.width - (this.parent.scrollbarEnabled ? ClickGUI.scrollbarWidth : 0) - 4), (float)1.0f, (int)ClickGUI.mainColor);
    }

    public void mouseUpdates(int var1, int var2, boolean var3) {
    }
}
