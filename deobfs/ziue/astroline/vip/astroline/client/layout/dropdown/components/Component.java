/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Mouse
 *  vip.astroline.client.layout.dropdown.components.impl.Box
 *  vip.astroline.client.layout.dropdown.panel.Panel
 *  vip.astroline.client.storage.utils.other.MathUtils
 */
package vip.astroline.client.layout.dropdown.components;

import org.lwjgl.input.Mouse;
import vip.astroline.client.layout.dropdown.components.impl.Box;
import vip.astroline.client.layout.dropdown.panel.Panel;
import vip.astroline.client.storage.utils.other.MathUtils;

public abstract class Component {
    public int x;
    public int y;
    public int width;
    public int height;
    public int offX;
    public int offY;
    public String title;
    protected boolean isHovered;
    private boolean isEnabled = true;
    protected boolean wasMousePressed;
    public Panel parent;
    public String type = "Component";
    public boolean editable = true;
    public String group = "default";
    private boolean visible = true;
    public Box box = null;

    public Component(Panel parent, int offX, int offY, String title) {
        this.parent = parent;
        this.offX = offX;
        this.offY = offY;
        this.title = title;
    }

    protected void reposition() {
        if (this.box != null) {
            this.y -= this.parent.scrollY;
        } else {
            this.x = this.parent.x + this.offX;
            this.y = this.parent.y + this.offY - this.parent.scrollY;
        }
    }

    public boolean contains(int mouseX, int mouseY) {
        float mod = this.group.contains("_setting") ? 5.0f : 0.0f;
        return MathUtils.contains((float)mouseX, (float)mouseY, (float)((float)this.x + mod), (float)this.y, (float)((float)(this.x + this.width) - mod), (float)(this.y + this.height));
    }

    public void noMouseUpdates() {
        this.isHovered = false;
        this.wasMousePressed = Mouse.isButtonDown((int)0);
    }

    public void setVisible(boolean value) {
        this.visible = value;
        if (value) return;
        if (!(this instanceof Box)) return;
        this.height = 0;
    }

    public void update(int mouseX, int mouseY) {
        this.reposition();
    }

    public void doRender(int var1, int var2) {
        if (!this.visible) {
            return;
        }
        this.render(var1, var2);
    }

    public abstract void render(int var1, int var2);

    public abstract void mouseUpdates(int var1, int var2, boolean var3);

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isVisible() {
        return this.visible;
    }
}
