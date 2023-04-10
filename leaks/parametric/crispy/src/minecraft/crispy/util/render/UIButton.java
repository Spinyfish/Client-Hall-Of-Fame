package crispy.util.render;


import arithmo.gui.altmanager.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.awt.*;

public class UIButton extends GuiButton {
    private int fade;

    public UIButton(final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);

    }

    public void drawButton(final Minecraft mc, final int mouseX, final int mouseY) {
        if (this.visible) {
            if (!(this.hovered = (mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height))) {
                this.fade = 0;
                final Color a = new Color(0, 0, 0, 155);
            } else {
                if (this.fade < 255) {
                    if (this.fade < 235) {
                        this.fade += 20;
                    } else {
                        this.fade += 255 - this.fade;
                    }
                }
                final Color a2 = new Color(this.fade, this.fade, this.fade, 100);
            }

        }
    }
}
