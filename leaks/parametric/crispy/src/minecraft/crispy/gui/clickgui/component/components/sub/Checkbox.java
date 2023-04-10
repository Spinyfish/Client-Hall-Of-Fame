package crispy.gui.clickgui.component.components.sub;


import crispy.Crispy;
import crispy.gui.clickgui.component.Component;
import crispy.gui.clickgui.component.components.Button;
import crispy.features.hacks.Category;
import crispy.features.hacks.impl.render.ClickGui;
import crispy.fonts.greatfont.TTFFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.superblaubeere27.valuesystem.BooleanValue;
import org.lwjgl.opengl.GL11;

import java.awt.*;

//Your Imports
public class Checkbox extends Component {

    private boolean hovered;
    private final BooleanValue op;
    private final Button parent;
    private int offset;
    private int x;
    private int y;

    public Checkbox(BooleanValue option, Button button, int offset) {
        this.op = option;
        this.parent = button;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.offset = offset;
    }

    @Override
    public void renderComponent(Category category) {
        TTFFontRenderer clean = Crispy.INSTANCE.getFontManager().getFont("clean 28");
        if (ClickGui.modeValue.getObject() == 0) {
            Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + (parent.parent.getWidth() * 1), parent.parent.getY() + offset + 12, this.hovered ? 0xFF222222 : 0xFF111111);
            Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 12, 0xFF111111);
        } else if (ClickGui.modeValue.getObject() == 1) {
            Color color = new Color(255, 255, 255);
            Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + parent.parent.getWidth(), parent.parent.getY() + offset + 12, this.hovered ? new Color(0, 0, 0, 170).getRGB() : new Color(0, 0, 0, 140).getRGB());

            GlStateManager.color(1, 1, 1, 1);
            Gui.drawRect(0, 0, 0, 0, color.getRGB());


        }
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        if (ClickGui.modeValue.getObject() == 0) {
            Minecraft.fontRendererObj.drawStringWithShadow(this.op.getName(), (parent.parent.getX() + 10 + 4) * 2 + 5, (parent.parent.getY() + offset + 2) * 2 + 4, -1);
        } else if (ClickGui.modeValue.getObject() == 1) {
            clean.drawStringWithShadow(this.op.getName(), (parent.parent.getX() + 10 + 4) * 2 + 5, (parent.parent.getY() + offset + 2) * 2 + 4, -1);

        }
        GL11.glPopMatrix();
        if (ClickGui.modeValue.getObject() == 0) {
            Gui.drawRect(parent.parent.getX() + 3 + 4, parent.parent.getY() + offset + 3, parent.parent.getX() + 9 + 4, parent.parent.getY() + offset + 9, 0xFF999999);
        } else if (ClickGui.modeValue.getObject() == 1) {
            Color color = new Color(255, 255, 255);
            Gui.drawRect(parent.parent.getX() + 3 + 4, parent.parent.getY() + offset + 3, parent.parent.getX() + 9 + 4, parent.parent.getY() + offset + 9, new Color(255, 255, 255, 100).getRGB());
        }
        if (this.op.getObject()) {
            if (ClickGui.modeValue.getObject() == 1) {
                Gui.drawRect(parent.parent.getX() + 3 + 4, parent.parent.getY() + offset + 3, parent.parent.getX() + 9 + 4, parent.parent.getY() + offset + 9, new Color(255, 255, 255, 255).getRGB());
                ResourceLocation resourceLocation = new ResourceLocation("Client/gui/check.png");
                Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
                Gui.drawModalRectWithCustomSizedTexture(parent.parent.getX() + 4 + 4, parent.parent.getY() + offset + 4, 5, 5, 5, 5, 5, 5);
            } else if (ClickGui.modeValue.getObject() == 0) {
                Gui.drawRect(parent.parent.getX() + 4 + 4, parent.parent.getY() + offset + 4, parent.parent.getX() + 8 + 4, parent.parent.getY() + offset + 8, 0xFF666666);
            }
        }
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
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
            this.op.setObject(!op.getObject());
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12;
    }
}
