/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.click.comp;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.ui.click.Clickgui;
import lodomir.dev.ui.click.comp.Comp;
import lodomir.dev.utils.render.RenderUtils;

public class Slider
extends Comp {
    private boolean dragging = false;
    private double renderWidth;
    private double renderWidth2;
    private NumberSetting setting;

    public Slider(double x, double y, Clickgui parent, Module module, NumberSetting setting) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.module = module;
        this.setting = setting;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isInside(mouseX, mouseY, this.parent.posX + this.x + 30.0, this.parent.posY + this.y + 15.0, this.parent.posX + this.x + 30.0 + this.renderWidth2, this.parent.posY + this.y + 20.0) && mouseButton == 0) {
            this.dragging = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.dragging = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);
        double min = this.setting.getMin();
        double max = this.setting.getMax();
        double l = 90.0;
        this.renderWidth = l * ((double)this.setting.getValueFloat() - min) / (max - min);
        this.renderWidth2 = l * (this.setting.getMax() - min) / (max - min);
        double diff = Math.min(l, Math.max(0.0, (double)mouseX - (this.parent.posX + this.x + 30.0)));
        if (this.dragging) {
            if (diff == 0.0) {
                this.setting.setValue(this.setting.getMin());
            } else {
                double newValue = diff / l * (max - min) + min;
                this.setting.setValue(newValue);
            }
        }
        RenderUtils.drawRect((int)(this.parent.posX + this.x + 30.0), (int)(this.parent.posY + this.y + 15.0), (int)(this.parent.posX + this.x + 30.0 + this.renderWidth2), (int)(this.parent.posY + this.y + 20.0), new Color(21, 118, 182).getRGB());
        RenderUtils.drawRect((int)(this.parent.posX + this.x + 30.0), (int)(this.parent.posY + this.y + 15.0), (int)(this.parent.posX + this.x + 30.0 + this.renderWidth), (int)(this.parent.posY + this.y + 20.0), this.parent.color);
        this.parent.fr.drawStringWithShadow(this.setting.getName() + ": " + this.setting.getValueFloat(), (int)(this.parent.posX + this.x + 30.0), (int)(this.parent.posY + this.y), -1);
    }

    private double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}

