package club.pulsive.impl.mainmenu.impl;

import club.pulsive.api.font.Fonts;
import club.pulsive.impl.util.render.RenderUtil;
import club.pulsive.impl.util.render.RoundedUtil;
import club.pulsive.impl.util.render.secondary.RenderUtils;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.Gui;

import java.awt.*;

@Getter @Setter
public class MainMenuButton {

    private final String displayName;
    private double x,y,ogWidth,ogHeight,width,height;
    @Getter
    private int id;
    private boolean colored;
    private double scale;

    public MainMenuButton(int id,String displayName, double width, double height, boolean colored) {
        this.id = id;
        this.displayName = displayName;
        this.ogWidth = width;
        this.ogHeight = height;
        this.colored = colored;
    }

    public void drawButton(double x, double y, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;
        boolean hovered = RenderUtil.isHovered((float) x, (float) y, (float) width, (float) height, (int) mouseX, (int) mouseY);
        if(width >= 225) {
            this.width = scale != 1 ? 229 * scale : 230 * scale;
        }

        if(!colored) {
            RenderUtils.drawRoundedRect(x, y,  width,  height, 5, hovered ? new Color(25, 25, 25).getRGB() : new Color(31, 31, 31).getRGB());
            RoundedUtil.drawRoundedOutline((float) x, (float) y, (float) ((float) x +  width), (float) ((float) y + height), 8, 3, -1);
        } else {
            Gui.drawGradientRect((float) x, (float) y, (float) (x + width), (float) (y + height), 0xffff7575, 0xffca75ff);
            if(hovered)
                Gui.drawRect((float) x, (float) y, (float) ((float) x + width), (float) ((float) y + height), 0x20000000);
        }
        if(colored)
            Fonts.popBold20.drawString(displayName, x + width / 2f - Fonts.popBold20.getStringWidth(displayName) /2f,y + height/2f - Fonts.popBold20.getHeight() /2f, -1);
        else
            Fonts.popMed20.drawString(displayName, x + width / 2f - Fonts.popMed20.getStringWidth(displayName) /2f,y + height/2f - Fonts.popMed20.getHeight() /2f, -1);
    }

    public void setSizeBigger(boolean state) {
        this.scale = (state ? 1.25 : 1);
        this.width = this.ogWidth * scale;
        this.height = this.ogHeight * scale;
    }
}
