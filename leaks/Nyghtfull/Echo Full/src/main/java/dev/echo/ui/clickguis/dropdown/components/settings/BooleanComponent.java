package dev.echo.ui.clickguis.dropdown.components.settings;

import dev.echo.module.settings.impl.BooleanSetting;
import dev.echo.ui.clickguis.dropdown.components.SettingComponent;
import dev.echo.utils.animations.Animation;
import dev.echo.utils.animations.Direction;
import dev.echo.utils.animations.impl.DecelerateAnimation;
import dev.echo.utils.misc.HoveringUtil;
import dev.echo.utils.render.ColorUtil;
import dev.echo.utils.render.RenderUtil;
import dev.echo.utils.render.RoundedUtil;

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

        echoFont16.drawString(getSetting().name, x + 5, y + echoFont16.getMiddleOfBox(height),
                ColorUtil.applyOpacity(textColor, .5f + (.5f * toggleAnimation.getOutput().floatValue())));

        float switchWidth = 17;
        float switchHeight = 7;
        float booleanX = x + width - (switchWidth + 5.5f);
        float booleanY = y + height /2f - switchHeight /2f;

        boolean hovering = HoveringUtil.isHovering(booleanX - 2, booleanY - 2, switchWidth + 4, switchHeight + 4, mouseX, mouseY);

        hoverAnimation.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);

        Color accentCircle = ColorUtil.applyOpacity(clientColors.getSecond(), alpha);
        Color rectColor =  ColorUtil.interpolateColorC(settingRectColor.brighter().brighter(), accentCircle, toggleAnimation.getOutput().floatValue());
        rectColor = ColorUtil.interpolateColorC(rectColor, ColorUtil.brighter(rectColor, .8f), hoverAnimation.getOutput().floatValue());

        RenderUtil.resetColor();
        RoundedUtil.drawRound(booleanX, booleanY, switchWidth, switchHeight, 3, rectColor);

        RenderUtil.resetColor();

        RoundedUtil.drawRound(x + width - (switchWidth + 4) + ((switchWidth - 8) * toggleAnimation.getOutput().floatValue()),
                y + echoFont16.getMiddleOfBox(height) + .5f, 5, 5, 2,textColor);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        float switchWidth = 17;
        float switchHeight = 7;
        float booleanX = x + width - (switchWidth + 5.5f);
        float booleanY = y + height /2f - switchHeight /2f;

        boolean hovering = HoveringUtil.isHovering(booleanX - 2, booleanY - 2, switchWidth + 4, switchHeight + 4, mouseX, mouseY);

        if(isClickable(booleanY + switchHeight) && hovering && button == 0) {
            getSetting().toggle();
        }

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
