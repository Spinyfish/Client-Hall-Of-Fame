/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.dropclick.comps;

import java.io.IOException;
import lodomir.dev.settings.Setting;
import lodomir.dev.ui.dropclick.ModuleButton;

public class Component {
    public Setting setting;
    public ModuleButton parent;
    public int offset;

    public Component(Setting setting, ModuleButton parent, int offset) {
        this.setting = setting;
        this.parent = parent;
        this.offset = offset;
    }

    public void render(int mouseX, int mouseY, float partialTicks) {
    }

    public void mouseClicked(double mouseX, double mouseY, int button) throws IOException {
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX > (double)this.parent.parent.x && mouseX < (double)(this.parent.parent.x + this.parent.parent.width) && mouseY > (double)(this.parent.parent.y + this.parent.offset + this.offset) && mouseY < (double)(this.parent.parent.y + this.parent.offset + this.offset + this.parent.parent.height);
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
    }
}

