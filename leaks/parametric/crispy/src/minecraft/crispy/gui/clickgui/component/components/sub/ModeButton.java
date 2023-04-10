package crispy.gui.clickgui.component.components.sub;


import crispy.Crispy;
import crispy.gui.clickgui.component.Component;
import crispy.gui.clickgui.component.components.Button;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.impl.render.ClickGui;
import crispy.fonts.greatfont.TTFFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.superblaubeere27.valuesystem.ModeValue;
import org.lwjgl.opengl.GL11;

import java.awt.*;

//Your Imports

public class ModeButton extends Component {

    private boolean hovered;
    private final Button parent;
    private final ModeValue set;
    private int offset;
    private int x;
    private int y;
    private final Hack mod;

    private final int modeIndex;

    public ModeButton(ModeValue set, Button button, Hack mod, int offset) {
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
        offset = newOff;
    }

    @Override
    public void renderComponent(Category category) {
        TTFFontRenderer clean = Crispy.INSTANCE.getFontManager().getFont("clean 28");

        if (ClickGui.modeValue.getObject() == 0) {
            Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + (parent.parent.getWidth() * 1), parent.parent.getY() + offset + 12, this.hovered ? 0xFF222222 : 0xFF111111);
            Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 12, 0xFF111111);
        } else if (ClickGui.modeValue.getObject() == 1) {
            Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + parent.parent.getWidth(), parent.parent.getY() + offset + 12, this.hovered ? new Color(0, 0, 0, 170).getRGB() : new Color(0, 0, 0, 140).getRGB());


        }

        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        if (ClickGui.modeValue.getObject() == 0) {
            Minecraft.fontRendererObj.drawStringWithShadow(set.getName() + ": " + set.getModes()[set.getObject()], (parent.parent.getX() + 7) * 2, (parent.parent.getY() + offset + 2) * 2 + 5, -1);
        } else if (ClickGui.modeValue.getObject() == 1) {
            clean.drawStringWithShadow(set.getName() + ": " + set.getModes()[set.getObject()], (parent.parent.getX() + 7) * 2, (parent.parent.getY() + offset + 2) * 2 + 5, -1);

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
            set.cycle();
        } else if (isMouseOnButton(mouseX, mouseY) && button == 1 && this.parent.open) {
            set.cycleReverse();
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12;
    }
}
