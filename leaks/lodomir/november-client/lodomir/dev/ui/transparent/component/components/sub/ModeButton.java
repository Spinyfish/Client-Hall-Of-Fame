/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.transparent.component.components.sub;

import java.awt.Color;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.ModeSetting;
import lodomir.dev.ui.transparent.component.Component;
import lodomir.dev.ui.transparent.component.components.Button;
import lodomir.dev.utils.render.RenderUtils;

public class ModeButton
extends Component {
    private boolean hovered;
    private Button parent;
    private ModeSetting set;
    private int offset;
    private int x;
    private int y;
    private Module mod;
    private int modeIndex;

    public ModeButton(ModeSetting set, Button button, Module mod, int offset) {
        this.set = set;
        this.parent = button;
        this.mod = mod;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.offset = offset;
        this.modeIndex = 0;
    }

    @Override
    public void setOff(int newOff) {
        this.offset = newOff;
    }

    @Override
    public void renderComponent() {
        if (this.set.isVisible()) {
            RenderUtils.drawRect(this.parent.parent.getX(), this.parent.parent.getY() + this.offset, this.parent.parent.getX() + this.parent.parent.getWidth(), this.parent.parent.getY() + this.offset + 12, this.hovered ? new Color(20, 20, 20, 191).getRGB() : new Color(0, 0, 0, 191).getRGB());
            RenderUtils.drawRect(this.parent.parent.getX(), this.parent.parent.getY() + this.offset, this.parent.parent.getX(), this.parent.parent.getY() + this.offset + 12, new Color(0, 0, 0, 191).getRGB());
            this.parent.fr.drawString(this.set.getName(), this.parent.parent.getX() + 3, this.parent.parent.getY() + this.offset + 3, this.parent.mod.isEnabled() ? -1 : new Color(190, 190, 190).getRGB());
            this.parent.fr.drawString(this.set.getMode(), this.parent.parent.getX() - this.parent.fr.getStringWidth(this.set.getMode()) + this.parent.parent.getWidth(), (this.parent.parent.getY() + this.offset) * 1 + 3, this.parent.mod.isEnabled() ? -1 : new Color(190, 190, 190).getRGB());
        }
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.hovered = this.isMouseOnButton(mouseX, mouseY);
        this.y = this.parent.parent.getY() + this.offset;
        this.x = this.parent.parent.getX();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (this.isMouseOnButton(mouseX, mouseY) && button == 0 && this.parent.open) {
            this.set.cycle(false);
        } else if (this.isMouseOnButton(mouseX, mouseY) && button == 1 && this.parent.open) {
            this.set.cycle(true);
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12;
    }
}

