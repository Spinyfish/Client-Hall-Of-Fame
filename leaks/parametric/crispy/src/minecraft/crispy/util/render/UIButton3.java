package crispy.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

import java.awt.*;

public class UIButton3 extends GuiButton {
    private int fade;
    private int visibility;

    public UIButton3(final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.visibility = 0;
    }

    public void drawButton(final Minecraft mc, final int mouseX, final int mouseY) {
        if (this.visible) {

            if (!(this.hovered = (mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height))) {
                this.fade = 0;
                final Color a = new Color(10, 10, 10, this.visibility);
                if (this.visibility < 255) {
                    if (this.visibility < 235) {
                        this.visibility += 10;
                    } else {
                        this.visibility += 255 - this.visibility;
                    }
                }
                Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, a.getRGB());
            } else if (this.visibility >= 255) {
                if (this.fade < 255) {
                    if (this.fade < 235) {
                        this.fade += 20;
                    } else {
                        this.fade += 255 - this.fade;
                    }
                }
                final Color a2 = new Color(this.fade, this.fade, this.fade, 255);
                Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, a2.getRGB());
            }

            drawCenteredString(Minecraft.fontRendererObj, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, Color.white.getRGB());
        }
    }
}
