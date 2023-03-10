/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.dropclick.comps.impl;

import java.awt.Color;
import java.io.IOException;
import lodomir.dev.settings.ModeSetting;
import lodomir.dev.settings.Setting;
import lodomir.dev.ui.dropclick.ModuleButton;
import lodomir.dev.ui.dropclick.comps.Component;
import lodomir.dev.utils.render.RenderUtils;

public class ModeButton
extends Component {
    private ModeSetting modeset;

    public ModeButton(Setting setting, ModuleButton parent, int offset) {
        super(setting, parent, offset);
        this.modeset = (ModeSetting)setting;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (this.setting.isVisible()) {
            RenderUtils.drawRect(this.parent.parent.x, this.parent.parent.y + this.parent.offset + this.offset, this.parent.parent.x + this.parent.parent.width, this.parent.parent.y + this.parent.offset + this.offset + this.parent.parent.height, new Color(28, 28, 28).getRGB());
            int textOffset = (int)((float)(this.parent.parent.height / 2) - this.parent.parent.fr.getHeight(this.setting.getName() + ": " + this.modeset.getMode()) / 2.0f);
            this.parent.parent.fr.drawStringWithShadow(this.setting.getName() + ": " + this.modeset.getMode(), this.parent.parent.x + textOffset, this.parent.parent.y + this.parent.offset + this.offset + textOffset, -1);
        }
        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) throws IOException {
        if (this.isHovered(mouseX, mouseY) && button == 0) {
            this.modeset.cycle();
        }
        super.mouseClicked(mouseX, mouseY, button);
    }
}

