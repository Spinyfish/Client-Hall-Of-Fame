package crispy.gui.clickgui.component.components.sub;


import crispy.Crispy;
import crispy.gui.clickgui.component.Component;
import crispy.gui.clickgui.component.components.Button;
import crispy.features.hacks.Category;
import crispy.features.hacks.impl.render.ClickGui;
import crispy.fonts.greatfont.TTFFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;


public class Keybind extends Component {

    private boolean hovered;
    private boolean binding;
    private final Button parent;
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
        offset = newOff;
    }

    @Override
    public void renderComponent(Category category) {
        TTFFontRenderer clean = Crispy.INSTANCE.getFontManager().getFont("clean 28");
        if (ClickGui.modeValue.getObject() == 0) {
            Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + (parent.parent.getWidth() * 1), parent.parent.getY() + offset + 12, this.hovered ? 0xFF222222 : 0xFF111111);
            Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 12, new Color(0xFF111111).getRGB());
        } else if (ClickGui.modeValue.getObject() == 1) {
            Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + parent.parent.getWidth(), parent.parent.getY() + offset + 12, this.hovered ? new Color(0, 0, 0, 170).getRGB() : new Color(0, 0, 0, 140).getRGB());

        }
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        if (ClickGui.modeValue.getObject() == 0) {
            Minecraft.fontRendererObj.drawStringWithShadow(binding ? "Press a key..." : ("Key: " + Keyboard.getKeyName(this.parent.mod.getKey())), (parent.parent.getX() + 7) * 2, (parent.parent.getY() + offset + 2) * 2 + 5, -1);
        } else if (ClickGui.modeValue.getObject() == 1) {
            clean.drawStringWithShadow(binding ? "Press a key..." : ("Key: " + Keyboard.getKeyName(this.parent.mod.getKey())), (parent.parent.getX() + 7) * 2, (parent.parent.getY() + offset + 2) * 2 + 5, -1);
        }
        GL11.glPopMatrix();
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.hovered = isMouseOnButton(mouseX, mouseY);
        this.y = parent.parent.getY() + offset;
        this.x = parent.parent.getX();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButton(mouseX, mouseY) && button == 0 && this.parent.open) {
            this.binding = !this.binding;
        }
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if (this.binding) {
            this.parent.mod.setKey(key);
            this.binding = false;
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12;
    }
}
