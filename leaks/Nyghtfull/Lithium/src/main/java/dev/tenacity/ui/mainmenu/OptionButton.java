package dev.tenacity.ui.mainmenu;

import dev.tenacity.ui.Screen;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.misc.HoveringUtil;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.GLUtil;
import dev.tenacity.utils.render.RoundedUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class OptionButton implements Screen {

    private Animation hoverAnimation;
    private final String icon;
    public final String name;
    public float x, y, iconAdjustY;
    public Color color;
    public float width, height;

    public Runnable clickAction;

    public OptionButton(String icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public OptionButton(String name) {
        icon = null;
        this.name = name;
    }

    @Override
    public void initGui() {
        hoverAnimation = new DecelerateAnimation(250, 1);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        hoverAnimation.setDirection(HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY) ? Direction.FORWARDS : Direction.BACKWARDS);
        //Black opaque background
        RoundedUtil.drawRound(x - 69, y, width + 69, height, 6, ColorUtil.applyOpacity(Color.BLACK, .2f));
        //Have the actual color fade in as the button is hovered
        RoundedUtil.drawRound(x - 10, y, (float) ((width + 10) * hoverAnimation.getOutput().floatValue()), height, 6, ColorUtil.applyOpacity(color, .5f));

        Gui.drawRect2(x, y, 3, height, ColorUtil.applyOpacity(color, (float) hoverAnimation.getOutput().floatValue()).getRGB());

        float middle = y + lithiumBoldFont40.getMiddleOfBox(height);
        lithiumBoldFont40.drawString(name, x + 60, y + lithiumBoldFont40.getMiddleOfBox(height), -1);

        if (icon != null) {
            FontUtil.iconFont40.drawString(icon, x + 20, middle + iconAdjustY, -1);
        } else {
            float iconWidth = 38 / 2f;
            float iconHeight = 27 / 2f;
            GLUtil.startBlend();
            mc.getTextureManager().bindTexture(new ResourceLocation("Tenacity/MainMenu/discord.png"));
            Gui.drawModalRectWithCustomSizedTexture(x + 20, middle + iconAdjustY, 0, 0, iconWidth, iconHeight, iconWidth, iconHeight);
            GLUtil.endBlend();
        }

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 0 && HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY)) {
            clickAction.run();
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
