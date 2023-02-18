/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.Astroline
 *  vip.astroline.client.service.font.FontManager
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.service.module.impl.render.Hud
 *  vip.astroline.client.storage.utils.render.ColorUtils
 */
package vip.astroline.client.layout.clickgui;

import java.awt.Color;
import java.util.Iterator;
import vip.astroline.client.Astroline;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.service.module.impl.render.Hud;
import vip.astroline.client.storage.utils.render.ColorUtils;

public class ClickGUI.CategoryButton {
    public Category cat;
    public float x;
    public float y;

    public ClickGUI.CategoryButton(Category category) {
        this.cat = category;
    }

    public void draw(float x, float y) {
        FontManager.baloo16.drawString(this.cat.name(), x + 25.0f, y + 6.5f, ColorUtils.WHITE.c);
        FontManager.icon15.drawString(this.cat.icon, x + 10.0f, y + 8.0f, ColorUtils.WHITE.c);
    }

    public void drawBackground(float x, float y) {
        this.x = x;
        this.y = y;
        FontManager.baloo16.drawString(this.cat.name(), x + 25.0f, y + 6.5f, Hud.isLightMode.getValue() != false ? -16777216 : Color.WHITE.getRGB());
        FontManager.icon15.drawString(this.cat.icon, x + 10.0f, y + 8.0f, ColorUtils.GREY.c);
    }

    public void onClick(int mouseX, int mouseY) {
        if (!ClickGUI.this.isHovering((float)mouseX, (float)mouseY, this.x - 0.5f, this.y, this.x + 99.5f, this.y + 25.0f)) return;
        if (Astroline.INSTANCE.getMaterial().currentCatButton == this) return;
        Astroline.INSTANCE.getMaterial().currentCatButton = this;
        Iterator iterator = Astroline.INSTANCE.moduleManager.getModules().iterator();
        while (iterator.hasNext()) {
            Module module = (Module)iterator.next();
            module.toggleButtonAnimation = module.isToggled() ? 218.0f : 222.0f;
            module.ySmooth.setValue(0.0f);
        }
    }
}
