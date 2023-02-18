/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  vip.astroline.client.layout.dropdown.ClickGUI
 *  vip.astroline.client.layout.dropdown.components.Component
 *  vip.astroline.client.layout.dropdown.panel.Panel
 *  vip.astroline.client.service.font.FontManager
 *  vip.astroline.client.storage.utils.render.render.GuiRenderUtils
 */
package vip.astroline.client.layout.dropdown.components.impl;

import net.minecraft.client.Minecraft;
import vip.astroline.client.layout.dropdown.ClickGUI;
import vip.astroline.client.layout.dropdown.components.Component;
import vip.astroline.client.layout.dropdown.panel.Panel;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.storage.utils.render.render.GuiRenderUtils;

public class Info
extends Component {
    boolean heightInitialized = false;

    public Info(Panel panel, int offX, int offY, String title) {
        super(panel, offX, offY, title);
        this.width = Math.max(ClickGUI.defaultWidth, panel.width);
        this.height = ClickGUI.defaultWidth;
        this.type = "Info";
        this.editable = false;
    }

    public void render(int mouseX, int mouseY) {
        float fontHeight = FontManager.normal.getHeight(this.title);
        int y = this.y;
        String coords = "X:" + (int)Minecraft.getMinecraft().thePlayer.posX + " Y:" + (int)Minecraft.getMinecraft().thePlayer.posY + " Z:" + (int)Minecraft.getMinecraft().thePlayer.posZ;
        GuiRenderUtils.drawRect((float)this.x, (float)y, (float)this.width, (float)14.0f, (int)ClickGUI.backgroundColor);
        FontManager.normal.drawString(coords, (float)(this.x + 2), (float)(y + 7) - fontHeight / 2.0f, 0xFFFFFF);
        y += 14;
        this.height = (y += 14) - this.y;
        if (this.heightInitialized) return;
        this.heightInitialized = true;
        this.parent.repositionComponents();
    }

    public void mouseUpdates(int mouseX, int mouseY, boolean isPressed) {
    }
}
