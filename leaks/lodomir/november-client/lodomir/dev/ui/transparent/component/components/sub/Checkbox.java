/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.transparent.component.components.sub;

import java.awt.Color;
import lodomir.dev.settings.BooleanSetting;
import lodomir.dev.ui.transparent.component.Component;
import lodomir.dev.ui.transparent.component.components.Button;
import lodomir.dev.utils.render.RenderUtils;

public class Checkbox
extends Component {
    private boolean hovered;
    private BooleanSetting op;
    private Button parent;
    private int offset;
    private int x;
    private int y;

    public Checkbox(BooleanSetting option, Button button, int offset) {
        this.op = option;
        this.parent = button;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.offset = offset;
    }

    @Override
    public void renderComponent() {
        if (this.op.isVisible()) {
            RenderUtils.drawRect(this.parent.parent.getX(), this.parent.parent.getY() + this.offset, this.parent.parent.getX() + this.parent.parent.getWidth(), this.parent.parent.getY() + this.offset + 12, this.hovered ? new Color(20, 20, 20, 191).getRGB() : new Color(0, 0, 0, 191).getRGB());
            RenderUtils.drawRect(this.parent.parent.getX(), this.parent.parent.getY() + this.offset, this.parent.parent.getX(), this.parent.parent.getY() + this.offset + 12, new Color(0, 0, 0, 191).getRGB());
            RenderUtils.drawRect(this.parent.parent.getX() + 5, this.parent.parent.getY() + this.offset + 3, this.parent.parent.getX() + 9 + 3, this.parent.parent.getY() + this.offset + 9, new Color(89, 89, 89, 191).getRGB());
            this.parent.fr.drawString(this.op.getName(), this.parent.parent.getX() + 15, this.parent.parent.getY() + this.offset + 2 + 1, this.parent.mod.isEnabled() ? -1 : new Color(190, 190, 190).getRGB());
            if (this.op.isEnabled()) {
                RenderUtils.drawRect(this.parent.parent.getX() + 5, this.parent.parent.getY() + this.offset + 3, this.parent.parent.getX() + 9 + 3, this.parent.parent.getY() + this.offset + 9, this.parent.mod.isEnabled() ? -1 : new Color(190, 190, 190, 191).getRGB());
            }
        }
    }

    @Override
    public void setOff(int newOff) {
        this.offset = newOff;
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
            this.op.toggle();
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12;
    }
}

