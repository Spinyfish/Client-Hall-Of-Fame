/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.dropclick.comps.impl;

import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lodomir.dev.modules.impl.render.HUD;
import lodomir.dev.settings.NumberSetting;
import lodomir.dev.settings.Setting;
import lodomir.dev.ui.dropclick.ModuleButton;
import lodomir.dev.ui.dropclick.comps.Component;
import lodomir.dev.utils.render.RenderUtils;

public class Slider
extends Component {
    private NumberSetting numset;
    private boolean sliding = false;

    public Slider(Setting setting, ModuleButton parent, int offset) {
        super(setting, parent, offset);
        this.numset = (NumberSetting)setting;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (this.setting.isVisible()) {
            RenderUtils.drawRect(this.parent.parent.x, this.parent.parent.y + this.parent.offset + this.offset, this.parent.parent.x + this.parent.parent.width, this.parent.parent.y + this.parent.offset + this.offset + this.parent.parent.height, new Color(28, 28, 28).getRGB());
            double diff = Math.min(this.parent.parent.width, Math.max(0, mouseX - this.parent.parent.x));
            int renderWidth = (int)((double)this.parent.parent.width * (this.numset.getValue() - this.numset.getMin()) / (this.numset.getMax() - this.numset.getMin()));
            RenderUtils.drawRect(this.parent.parent.x, this.parent.parent.y + this.parent.offset + this.offset, this.parent.parent.x + renderWidth, this.parent.parent.y + this.parent.offset + this.offset + this.parent.parent.height, HUD.getColor());
            if (this.sliding) {
                if (diff == 0.0) {
                    this.numset.setValue(this.numset.getMin());
                } else {
                    this.numset.setValue(this.roundToPlace(diff / (double)this.parent.parent.width * (this.numset.getMax() - this.numset.getMin()) + this.numset.getMin(), 2));
                }
            }
            int textOffset = (int)((float)(this.parent.parent.height / 2) - this.parent.parent.fr.getHeight(this.setting.getName() + ": " + this.roundToPlace(this.numset.getValue(), 2)) / 2.0f);
            this.parent.parent.fr.drawStringWithShadow(this.setting.getName() + ": " + this.roundToPlace(this.numset.getValue(), 2), this.parent.parent.x + textOffset, this.parent.parent.y + this.parent.offset + this.offset + textOffset, -1);
        }
        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) throws IOException {
        if (this.isHovered(mouseX, mouseY)) {
            this.sliding = true;
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        this.sliding = false;
        super.mouseReleased(mouseX, mouseY, button);
    }

    private double roundToPlace(double value, int place) {
        if (place < 0) {
            return value;
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(place, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}

