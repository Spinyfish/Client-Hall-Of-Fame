package me.jinthium.straight.api.clickgui.dropdown.components.settings;


import me.jinthium.straight.api.clickgui.dropdown.components.SettingComponent;
import me.jinthium.straight.impl.settings.BooleanSetting;
import me.jinthium.straight.impl.utils.animation.Animation;
import me.jinthium.straight.impl.utils.animation.Direction;
import me.jinthium.straight.impl.utils.animation.impl.DecelerateAnimation;
import me.jinthium.straight.impl.utils.misc.HoveringUtil;
import me.jinthium.straight.impl.utils.render.ColorUtil;
import me.jinthium.straight.impl.utils.render.RenderUtil;
import me.jinthium.straight.impl.utils.render.RoundedUtil;

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

        normalFont16.drawString(getSetting().name, x + 5, y + normalFont16.getMiddleOfBox(height) + 2,
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
                y + normalFont16.getMiddleOfBox(height) + .5f, 5, 5, 2,textColor);
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
