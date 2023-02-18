/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.layout.dropdown.ClickGUI
 *  vip.astroline.client.layout.dropdown.components.Component
 *  vip.astroline.client.layout.dropdown.panel.Panel
 *  vip.astroline.client.layout.dropdown.utils.AnimationTimer
 *  vip.astroline.client.storage.utils.render.render.GuiRenderUtils
 */
package vip.astroline.client.layout.dropdown.components.impl;

import java.util.ArrayList;
import java.util.List;
import vip.astroline.client.layout.dropdown.ClickGUI;
import vip.astroline.client.layout.dropdown.components.Component;
import vip.astroline.client.layout.dropdown.panel.Panel;
import vip.astroline.client.layout.dropdown.utils.AnimationTimer;
import vip.astroline.client.storage.utils.render.render.GuiRenderUtils;

public class Box
extends Component {
    private List<Component> children = new ArrayList<Component>();
    private int maxHeight;
    private boolean virtual_visible;
    private AnimationTimer openAnimation = new AnimationTimer(15);
    private boolean lastBox;

    public Box(Panel panel, int offX, int offY, String title) {
        super(panel, offX, offY, title);
        this.width = ClickGUI.settingsWidth;
        this.height = 0;
        this.type = "Box";
    }

    public void addChild(Component component) {
        this.reposition();
        component.box = this;
        component.x = this.x + 10;
        this.children.add(component);
    }

    public void render(int mouseX, int mouseY) {
        if (!this.virtual_visible && this.openAnimation.getValue() == 0.0) {
            return;
        }
        float cropX = this.x + 5;
        float cropY = Math.min((float)this.parent.y + (float)((double)(this.parent.height - ClickGUI.defaultHeight)) + (float)ClickGUI.defaultHeight, (float)(this.height + this.y));
        float cropWidth = this.width - 10;
        float cropHeight = Math.min(this.y + this.height - (this.parent.y + 12), this.height);
        if (cropHeight <= 0.0f) return;
        float f = this.height;
        float f2 = this.lastBox ? 4.5f : 1.0f;
        if (f <= f2) {
            return;
        }
        GuiRenderUtils.endCrop();
        GuiRenderUtils.beginCrop((float)cropX, (float)cropY, (float)cropWidth, (float)cropHeight, (float)2.0f);
        this.children.forEach(c -> c.render(mouseX, mouseY));
        GuiRenderUtils.endCrop();
        GuiRenderUtils.beginCrop((float)this.parent.crX, (float)this.parent.crY, (float)this.parent.crXX, (float)this.parent.crYY, (float)2.0f);
    }

    public void mouseUpdates(int var1, int var2, boolean var3) {
        if (!this.virtual_visible && this.openAnimation.getValue() == 0.0) {
            return;
        }
        this.children.forEach(c -> c.mouseUpdates(var1, var2, var3));
    }

    public void update(int mouseX, int mouseY) {
        super.update(mouseX, mouseY);
        this.children.forEach(c -> c.update(mouseX, mouseY));
        int y = this.y;
        int x = this.x;
        for (Component child : this.children) {
            child.y = y;
            child.x = x;
            y += child.height;
        }
        this.openAnimation.update(this.virtual_visible);
        if (this.openAnimation.getValue() == 1.0) {
            return;
        }
        this.height = (int)(this.openAnimation.getValue() * (double)this.maxHeight);
        this.parent.repositionComponents();
    }

    public void recalcHeight() {
        this.maxHeight = this.children.stream().mapToInt(c -> c.height).sum() + (this.lastBox ? 10 : 5);
    }

    public void setVirtual_visible(boolean virtual_visible) {
        this.virtual_visible = virtual_visible;
    }

    public boolean isVirtual_visible() {
        return this.virtual_visible;
    }

    public boolean isLastBox() {
        return this.lastBox;
    }

    public void setLastBox(boolean lastBox) {
        this.lastBox = lastBox;
    }
}
