package igbt.astolfy.ui.ClickGUI.dropDownLight.impl.set;

import igbt.astolfy.font.CFontRenderer;
import igbt.astolfy.settings.settings.NumberSetting;
import igbt.astolfy.ui.ClickGUI.dropDownLight.ClickGui;
import igbt.astolfy.ui.ClickGUI.dropDownLight.impl.Button;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.text.DecimalFormat;

public class Slider extends SetComp {

    private boolean dragging = false;
    private double x;
    private double y;
    private static int height = 12;
    private boolean hovered;
    private NumberSetting set;

    public Slider(NumberSetting s, Button b) {
        super(s, b, height);
        this.set = s;
    }

    @Override
    public int drawScreen(int mouseX, int mouseY, double x, double y) {
        this.hovered = this.isHovered(mouseX, mouseY);
        this.x = x;
        this.y = y;
        //Gui.drawRect(x, y, x + this.parent.getWidth(), y + height, ClickGui.getSecondaryColor().getRGB());
        if (this.dragging) {
            float toSet = (float) ((float) mouseX - (float) (this.x - 2)) / (float) (this.parent.getWidth() - 1);
            if (toSet > 1) {
                toSet = 1;
            }
            if (toSet < 0) {
                toSet = 0;
            }
            this.set.setValue(((this.set.getMax() - this.set.getMin()) * toSet) + this.set.getMin());
        }
        CFontRenderer font = Minecraft.getMinecraft().customFont;
        float distance = (float) ((this.set.getValue() - this.set.getMin()) / (this.set.getMax() - this.set.getMin()));
        //Gui.drawRect(this.x, this.y, this.x + this.parent.getWidth(), this.y + this.height, new Color(21, 21, 21).getRGB());
        String name = this.set.getName() + " " + new DecimalFormat("#.##").format(this.set.getValue());
        Gui.drawRect(this.x + 1, this.y + font.getHeight() - 9, (int) (this.x -1 + (this.parent.getWidth() * 1)), this.y + this.height - 0, ClickGui.getSecondaryColor().darker().getRGB());
        Gui.drawRect(this.x + 1, this.y + font.getHeight() - 9, (int) (this.x - 1+ (this.parent.getWidth() * distance)), this.y + this.height - 0, this.hovered ? parent.panel.category.color.darker().darker().darker().getRGB():parent.panel.category.color.darker().darker().getRGB());
        GlStateManager.pushMatrix();
        float scale = 1;
        GlStateManager.scale(scale, scale, scale);
        font.drawString(name, (this.x + 2) / scale + 2, (y + 1.5) / scale, 0);
        GlStateManager.popMatrix();
        return height;
    }

    @Override
    public void mouseClicked(int x, int y, int button) {
        if (button == 0 && this.hovered) {
            this.dragging = !this.dragging;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            this.dragging = false;
        }
    }

    private boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= x + 1 && mouseX <= x + 10+ this.parent.getWidth() && mouseY >= y && mouseY <= y + height;
    }
}
