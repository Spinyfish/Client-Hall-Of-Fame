package dev.tenacity.ui.clickguis.zeroday.components.settings;

import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.ui.clickguis.zeroday.components.SettingComponent;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.misc.HoveringUtil;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public class BooleanComponent extends SettingComponent<BooleanSetting> {

    public BooleanComponent(BooleanSetting booleanSetting) {
        super(booleanSetting);
    }


    private final Animation toggleAnimation = new DecelerateAnimation(250, 1, Direction.BACKWARDS);

    private final Animation hoverAnimation = new DecelerateAnimation(250, 1, Direction.BACKWARDS);



    @Override
    public void initGui() {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        toggleAnimation.setDirection(getSetting().isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
        RenderUtil.resetColor();

        lithiumFont20.drawString(
                getSetting().name,
                x + lithiumFont20.getMiddleOfBox(height) / 1.8F,
                y + lithiumFont16.getMiddleOfBox(height) / 2.8F,
                ColorUtil.applyOpacity(textColor, .5f + (.5f * toggleAnimation.getOutput().floatValue()))
        );

        float switchWidth = 10;
        float switchHeight = 10;
        float booleanX = x + width - (switchWidth) * 1.65F;
        float booleanY = y + height - switchHeight * 2.75F;

        boolean hovering = HoveringUtil.isHovering(booleanX - 2, booleanY - 2, switchWidth + 4, switchHeight + 4, mouseX, mouseY);

        hoverAnimation.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);

        Color rectColor = ColorUtil.interpolateColorC(
                settingRectColor,
                ColorUtil.rainbow(10, (int) (x) / 10, 0.35F, 0.8F, 1.0F),
                toggleAnimation.getOutput().floatValue()
        );

        RenderUtil.resetColor();

        Gui.drawRect(
                booleanX,
                booleanY,
                booleanX + switchWidth,
                booleanY + switchHeight,
                getSetting().isEnabled()
                        ? rectColor.getRGB()
                        : settingRectColor.getRGB()
        );
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        float switchWidth = 10;
        float switchHeight = 10;
        float booleanX = x + width - (switchWidth) * 1.65F;
        float booleanY = y + height - switchHeight * 2.85F;

        boolean hovering = HoveringUtil.isHovering(booleanX - 2, booleanY - 2, switchWidth + 4, switchHeight + 4, mouseX, mouseY);

        if(isClickable(booleanY + switchHeight) && hovering && button == 0) {
            getSetting().toggle();
        }

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
