/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  org.lwjgl.input.Keyboard
 */
package lodomir.dev.ui.transclick.component.components.sub;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import lodomir.dev.ui.transclick.component.Component;
import lodomir.dev.ui.transclick.component.components.Button;
import lodomir.dev.utils.render.RenderUtils;
import org.lwjgl.input.Keyboard;

public class Keybind
extends Component {
    private boolean hovered;
    private boolean binding;
    private Button parent;
    private int offset;
    private int x;
    private int y;

    public Keybind(Button button, int offset) {
        this.parent = button;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.offset = offset;
    }

    @Override
    public void setOff(int newOff) {
        this.offset = newOff;
    }

    @Override
    public void renderComponent() {
        RenderUtils.drawRect(this.parent.parent.getX(), this.parent.parent.getY() + this.offset, this.parent.parent.getX() + this.parent.parent.getWidth(), this.parent.parent.getY() + this.offset + 12, this.hovered ? new Color(20, 20, 20, 191).getRGB() : new Color(0, 0, 0, 191).getRGB());
        RenderUtils.drawRect(this.parent.parent.getX(), this.parent.parent.getY() + this.offset, this.parent.parent.getX(), this.parent.parent.getY() + this.offset + 12, new Color(0, 0, 0, 191).getRGB());
        this.parent.fr.drawString(this.binding ? "Press a key..." : "Bind", this.parent.parent.getX() + 3, (this.parent.parent.getY() + this.offset + 0) * 1 + 3, this.parent.mod.isEnabled() ? -1 : new Color(190, 190, 190).getRGB());
        this.parent.fr.drawString(this.binding ? "" : ChatFormatting.GRAY + "[" + Keyboard.getKeyName((int)this.parent.mod.getKey()) + "]", this.parent.parent.getX() - this.parent.fr.getStringWidth(this.binding ? "" : ChatFormatting.GRAY + "[" + Keyboard.getKeyName((int)this.parent.mod.getKey()) + "]") + this.parent.parent.getWidth(), (this.parent.parent.getY() + this.offset + 0) * 1 + 3, new Color(255, 255, 255, 255).getRGB());
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
            this.binding = !this.binding;
        }
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if (this.binding && this.parent.mod.key == 14) {
            this.parent.mod.setKey(0);
            this.binding = false;
        }
        if (this.binding) {
            this.parent.mod.setKey(key);
            this.binding = false;
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12;
    }
}

