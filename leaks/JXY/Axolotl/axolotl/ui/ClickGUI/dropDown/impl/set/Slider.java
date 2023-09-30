package axolotl.ui.ClickGUI.dropDown.impl.set;

import java.text.DecimalFormat;

import java.awt.*;
import axolotl.cheats.settings.NumberSetting;
import font.CFontRenderer;
import axolotl.ui.ClickGUI.dropDown.ClickGui;
import axolotl.ui.ClickGUI.dropDown.impl.Button;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

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
        if (this.dragging) {
            float toSet = ((float) mouseX - (float) (this.x - 2)) / (float) (this.parent.getWidth() - 1);
            if (toSet > 1) {
                toSet = 1;
            }
            if (toSet < 0) {
                toSet = 0;
            }
            double val = ((this.set.getMax() + 0.01 - this.set.getMin()) * toSet) + this.set.getMin();
            this.set.setValue((val - (val % set.increment)));
        }
        CFontRenderer font = Minecraft.getMinecraft().customFont;
        float distance = (float) ((this.set.getNumberValue() - this.set.getMin()) / (this.set.getMax() - this.set.getMin()));
        Gui.drawRect(this.x, this.y, this.x + this.parent.getWidth(), this.y + this.height, new Color(21, 21, 21).getRGB());
        String name = this.set.name + " " + new DecimalFormat("#.##").format(this.set.getNumberValue());
        Gui.drawRect(this.x + 1, this.y + font.getHeight() - 9, (int) (this.x -1 + (this.parent.getWidth() * 1)), this.y + this.height - 0, ClickGui.getSecondaryColor().brighter().getRGB());
        Gui.drawRect(this.x + 1, this.y + font.getHeight() - 9, (int) (this.x - 1+ (this.parent.getWidth() * distance)), this.y + this.height - 0, this.hovered ? parent.panel.category.color.darker().darker().darker().getRGB():parent.panel.category.color.darker().darker().getRGB());
        GlStateManager.pushMatrix();
        float scale = 1;
        GlStateManager.scale(scale, scale, scale);
        font.drawString(name, (this.x + 2) / scale + 2, (y + 1.5) / scale, -1);
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
