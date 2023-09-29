package me.jinthium.straight.api.clickgui.dropdown.components.settings;

import me.jinthium.straight.api.clickgui.dropdown.components.SettingComponent;
import me.jinthium.straight.impl.settings.ModeSetting;
import me.jinthium.straight.impl.utils.animation.Animation;
import me.jinthium.straight.impl.utils.animation.ContinualAnimation;
import me.jinthium.straight.impl.utils.animation.Direction;
import me.jinthium.straight.impl.utils.animation.impl.DecelerateAnimation;
import me.jinthium.straight.impl.utils.font.FontUtil;
import me.jinthium.straight.impl.utils.misc.HoveringUtil;
import me.jinthium.straight.impl.utils.render.ColorUtil;
import me.jinthium.straight.impl.utils.render.RenderUtil;
import me.jinthium.straight.impl.utils.render.RoundedUtil;
import me.jinthium.straight.impl.utils.tuples.muttable.MutablePair;

import java.awt.*;

public class ModeComponent extends SettingComponent<ModeSetting> {

    private final Animation hoverAnimation = new DecelerateAnimation(250, 1, Direction.BACKWARDS);
    private final Animation openAnimation = new DecelerateAnimation(250, 1, Direction.BACKWARDS);
    private final Animation selectionBox = new DecelerateAnimation(250, 1, Direction.BACKWARDS);

    private boolean opened;

    public float realHeight;
    public float normalCount;

    private final ContinualAnimation selectionBoxY = new ContinualAnimation();
    private String hoveringMode = "";

    public ModeComponent(ModeSetting modeSetting) {
        super(modeSetting);
        normalCount = 2;
    }


    @Override
    public void initGui() {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        ModeSetting modeSetting = getSetting();

        float boxHeight = 18;
        float boxY = y + realHeight / 2f - (boxHeight / 2f) + 4;
        float boxX = x + 5;
        float boxWidth = width - 10;

        boolean hoveringBox = HoveringUtil.isHovering(boxX, boxY, boxWidth, boxHeight, mouseX, mouseY);

        hoverAnimation.setDirection(hoveringBox ? Direction.FORWARDS : Direction.BACKWARDS);
        openAnimation.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);

        Color outlineColor = ColorUtil.interpolateColorC(settingRectColor.brighter().brighter(), clientColors.getSecond(), .3f * hoverAnimation.getOutput().floatValue());

        outlineColor = ColorUtil.interpolateColorC(outlineColor, clientColors.getSecond(), openAnimation.getOutput().floatValue());


        Color rectColor = ColorUtil.interpolateColorC(settingRectColor.brighter(), settingRectColor.brighter().brighter(),
                (.5f * hoverAnimation.getOutput().floatValue()) + (openAnimation.getOutput().floatValue()));


        // Gui.drawRect2(x,y,width,height, -1);
        RoundedUtil.drawRound(boxX, boxY, boxWidth, boxHeight, 4, outlineColor);


        RoundedUtil.drawRound(boxX + 1, boxY + 1, boxWidth - 2, boxHeight - 2, 3, rectColor);

        normalFont14.drawString(modeSetting.name, boxX + boxWidth / 2 - normalFont14.getStringWidth(modeSetting.getName()) / 2 - 2, y + 5, textColor);

        normalFont16.drawString(modeSetting.getMode(), boxX + boxWidth / 2 - normalFont16.getStringWidth(modeSetting.getMode()) / 2 - 2, boxY + normalFont16.getMiddleOfBox(boxHeight) + 2, textColor);

        RenderUtil.resetColor();

        RenderUtil.resetColor();
        float arrowX = boxX + boxWidth - 11;
        float arrowY = boxY + iconFont20.getMiddleOfBox(boxHeight) + 1;
        float openAnim = openAnimation.getOutput().floatValue();

        RenderUtil.rotateStart(arrowX, arrowY, iconFont20.getStringWidth(FontUtil.DROPDOWN_ARROW), iconFont20.getHeight(), 180 * openAnim);
        iconFont20.drawString(FontUtil.DROPDOWN_ARROW, boxX + boxWidth - 11, boxY + iconFont20.getMiddleOfBox(boxHeight) + 3, textColor);
        RenderUtil.rotateEnd();

        if (opened || !openAnimation.isDone()) {
            float rectHeight = 15;
            float rectCount = 0;


            float modeHeight = (modeSetting.modes.size() - 1) * rectHeight;
            float modeY = boxY + boxHeight + 4;
            float modeX = boxX - .25f;
            RoundedUtil.drawRound(modeX, modeY, boxWidth, Math.max(4, modeHeight * openAnim), 4,
                    ColorUtil.applyOpacity(settingRectColor.brighter(), openAnim));


            boolean mouseOutsideRect = (mouseY < modeY || mouseY > modeY + modeHeight) || (mouseX < modeX || mouseX > modeX + boxWidth);

            selectionBox.setDirection(mouseOutsideRect ? Direction.BACKWARDS : Direction.FORWARDS);

            RoundedUtil.drawRound(modeX + 1.5f, (float) (modeY + 1.5f + selectionBoxY.getOutput()), boxWidth - 3, rectHeight - 3, 2.5f,
                    ColorUtil.applyOpacity(settingRectColor.brighter().brighter(), openAnim * selectionBox.getOutput().floatValue()));

            for (String mode : modeSetting.modes) {
                if(mode.equals(modeSetting.getMode())) continue;
                boolean hoveringMode = HoveringUtil.isHovering(modeX, modeY + rectCount * rectHeight, boxWidth, rectHeight, mouseX, mouseY);
                if(hoveringMode) {
                    this.hoveringMode = mode;
                }


                if(mode.equals(this.hoveringMode)) {
                    selectionBoxY.animate(rectCount * rectHeight, 17);
                }


                RenderUtil.resetColor();
                normalFont16.drawString(mode, modeX + boxWidth / 2 - normalFont16.getStringWidth(mode) / 2,
                        modeY + ((normalFont16.getMiddleOfBox(rectHeight) + 2 + (rectHeight * rectCount)) * openAnimation.getOutput().floatValue()),
                        ColorUtil.applyOpacity(textColor, openAnim));

                rectCount++;
            }

            //2 plus the increment of the mode rect height divided by the normal setting rect height
            countSize = 2 + ((.25f + (rectCount * (rectHeight / (realHeight / normalCount)))) * openAnimation.getOutput().floatValue());
        }else {
            countSize = 2;
        }

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        ModeSetting modeSetting = getSetting();
        float boxHeight = 18;
        float boxY = y + realHeight / 2f - (boxHeight / 2f) + 3;
        float boxX = x + 6;

        float boxWidth = width - 10;

        if (isClickable(boxY + boxHeight) && HoveringUtil.isHovering(boxX, boxY, boxWidth, boxHeight, mouseX, mouseY) && button == 1) {
            opened = !opened;
        }


        if(opened) {
            float rectHeight = 15;
            float rectCount = 0;
            float modeY = boxY + boxHeight + 4;
            float modeX = boxX - 1;

            for (String mode : modeSetting.modes) {
                if(mode.equals(modeSetting.getMode())) continue;
                boolean hoveringMode = HoveringUtil.isHovering(modeX, modeY + rectCount * rectHeight, boxWidth, rectHeight, mouseX, mouseY);
                if(isClickable((modeY + rectCount * rectHeight) + rectHeight) && hoveringMode && button == 0) {
                    modeSetting.setCurrentMode(mode);
                    opened = false;
                    return;
                }
                rectCount++;
            }
        }


    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
