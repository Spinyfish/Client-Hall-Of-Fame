/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import java.awt.Color;
import java.util.ArrayList;
import lodomir.dev.November;
import lodomir.dev.event.impl.game.EventKey;
import lodomir.dev.event.impl.render.EventRender2D;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.modules.impl.render.Interface;
import lodomir.dev.ui.font.TTFFontRenderer;
import lodomir.dev.utils.render.RenderUtils;

public class TabGUI
extends Module {
    public int currentTab;
    public boolean expanded;
    int code = 0;

    public TabGUI() {
        super("TabGUI", 0, Category.RENDER);
    }

    @Subscribe
    public void onEvent(EventRender2D e) {
        TTFFontRenderer cfr = November.INSTANCE.fm.getFont("PRODUCT SANS 20");
        int dr = Math.round(Interface.red.getValueFloat());
        int dg = Math.round(Interface.green.getValueFloat());
        int db = Math.round(Interface.blue.getValueFloat());
        float height = 15.0f;
        RenderUtils.drawRect(2.0, height, 65.0, 13 + Category.values().length * 12, -1879048192);
        RenderUtils.drawRect(2.0, height + (float)(this.currentTab * 12) + 10.0f, 65.0, height + (float)(this.currentTab * 12), Interface.getColorInt());
        int count = 0;
        for (Category c : Category.values()) {
            if (Interface.font.isMode("Client")) {
                cfr.drawStringWithShadow(c.name, 3.0f, height + (float)(count * 12), -1);
            } else {
                TabGUI.mc.fontRendererObj.drawStringWithShadow(c.name, 3.0f, height + (float)(count * 12) + 1.0f, -1);
            }
            ++count;
        }
        if (this.expanded) {
            Category category = Category.values()[this.currentTab];
            ArrayList<Module> modules = November.INSTANCE.moduleManager.getModulesByCategory(category);
            if (modules.size() == 0) {
                return;
            }
            RenderUtils.drawRect(66.0, height, 135.0, 13 + modules.size() * 12, -1879048192);
            RenderUtils.drawRect(66.0, height + (float)(category.moduleIndex * 12) + 10.0f, 135.0, height + (float)(category.moduleIndex * 12), Interface.getColorInt());
            count = 0;
            for (Module m : modules) {
                if (Interface.font.isMode("Client")) {
                    cfr.drawStringWithShadow(m.name, 66.0f, height + (float)(count * 12), m.isEnabled() ? new Color(255, 255, 255).getRGB() : new Color(190, 190, 190).getRGB());
                } else {
                    TabGUI.mc.fontRendererObj.drawStringWithShadow(m.name, 66.0f, height + (float)(count * 12) + 1.0f, m.isEnabled() ? new Color(255, 255, 255).getRGB() : new Color(190, 190, 190).getRGB());
                }
                if (count == category.moduleIndex && m.expanded) {
                    RenderUtils.drawRect(136.0, height + (float)(m.index * 12) + 10.0f, 135.0, height + (float)(m.index * 12), Interface.getColorInt());
                }
                ++count;
            }
        }
    }

    @Subscribe
    public void onKey(EventKey event) {
        Module module;
        this.code = event.key;
        Category category = Category.values()[this.currentTab];
        ArrayList<Module> modules = November.INSTANCE.moduleManager.getModulesByCategory(category);
        if (this.code == 200) {
            if (this.expanded) {
                if (this.expanded && !modules.isEmpty() && ((Module)modules.get((int)category.moduleIndex)).expanded) {
                    module = (Module)modules.get(category.moduleIndex);
                    module.index = module.index <= 0 ? module.settings.size() - 1 : --module.index;
                } else {
                    category.moduleIndex = category.moduleIndex <= 0 ? modules.size() - 1 : --category.moduleIndex;
                }
            } else {
                this.currentTab = this.currentTab <= 0 ? Category.values().length - 1 : --this.currentTab;
            }
        }
        if (this.code == 208) {
            if (this.expanded) {
                if (this.expanded && !modules.isEmpty() && ((Module)modules.get((int)category.moduleIndex)).expanded) {
                    module = (Module)modules.get(category.moduleIndex);
                    module.index = module.index >= module.settings.size() - 1 ? 0 : ++module.index;
                } else {
                    category.moduleIndex = category.moduleIndex >= modules.size() - 1 ? 0 : ++category.moduleIndex;
                }
            } else {
                this.currentTab = this.currentTab >= Category.values().length - 1 ? 0 : ++this.currentTab;
            }
        }
        if (this.code == 205) {
            if (this.expanded && modules.size() != 0) {
                module = (Module)modules.get(category.moduleIndex);
                module.toggle();
            } else {
                this.expanded = true;
            }
        }
        if (this.code == 203) {
            if (this.expanded && !modules.isEmpty() && ((Module)modules.get((int)category.moduleIndex)).expanded) {
                ((Module)modules.get((int)category.moduleIndex)).expanded = false;
            } else {
                this.expanded = false;
            }
        }
    }
}

