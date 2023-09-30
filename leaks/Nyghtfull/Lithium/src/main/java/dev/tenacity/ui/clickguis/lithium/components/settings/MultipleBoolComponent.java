package dev.tenacity.ui.clickguis.lithium.components.settings;

import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.MultipleBoolSetting;
import dev.tenacity.ui.clickguis.lithium.components.SettingComponent;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.misc.HoveringUtil;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RoundedUtil;
import dev.tenacity.utils.tuples.Pair;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MultipleBoolComponent extends SettingComponent<MultipleBoolSetting> {

    public float realHeight;
    public float normalCount;

    private final HashMap<BooleanSetting, Pair<Animation, Animation>> booleanSettingAnimations = new HashMap<>();

    private boolean opened;


    private final List<BooleanSetting> sortedSettings;
    public MultipleBoolComponent(MultipleBoolSetting setting) {
        super(setting);

        sortedSettings = setting.getBoolSettings().stream().sorted(Comparator.comparingDouble(this::getEnabledWidth)).collect(Collectors.toList());

        for (BooleanSetting booleanSetting : sortedSettings) {
            booleanSettingAnimations.put(booleanSetting, Pair.of(new DecelerateAnimation(250, 1), new DecelerateAnimation(250, 1)));
        }

        normalCount = 2;
    }


    @Override
    public void initGui() {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    private float additionalHeight = 0;

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        float boxHeight = 15;
        float boxY = y + realHeight / 2f - (boxHeight / 2f) + 4;
        float boxX = x + 6;
        float boxWidth = width - 10;

        RoundedUtil.drawRound(
                x + 6,
                boxY + 1,
                width - 12,
                (boxHeight - 2) + additionalHeight,
                4.75F,
                settingRectColor
        );

        lithiumFont20.drawString(
                getSetting().name,
                x + width / 2.0F - lithiumFont20.getStringWidth(getSetting().name) / 2.0F,
                y + 2,
                textColor
        );

        float addHeight = 0;
        float xOffset = 2;
        float yOffset = 3;
        float spacing = 3;

        float avaliableWidth = boxWidth - 2;

        for (BooleanSetting setting : sortedSettings) {
            float enabledWidth = getEnabledWidth(setting);
            float enabledHeight = lithiumFont14.getHeight() + 4;

            if (xOffset + enabledWidth > avaliableWidth) {
                xOffset = 2;
                yOffset += enabledHeight + spacing;

                addHeight += (yOffset + enabledHeight + spacing) - ((boxHeight) + addHeight);
            }

            float enabledX = boxX + xOffset;
            float enabledY = boxY + yOffset;

            boolean hovering = HoveringUtil.isHovering(enabledX, enabledY, enabledWidth, enabledHeight, mouseX, mouseY);

            Animation hoverAnimation = booleanSettingAnimations.get(setting).getFirst();
            Animation toggleAnimation = booleanSettingAnimations.get(setting).getSecond();

            hoverAnimation.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);
            toggleAnimation.setDirection(setting.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);

            Color rectColorBool = ColorUtil.interpolateColorC(
                    new Color(0xBB0066FF, true).darker(),
                    new Color(0xBB0066FF, true),
                    hoverAnimation.getOutput().floatValue()
            );

            RoundedUtil.drawRound(
                    enabledX,
                    enabledY,
                    enabledWidth,
                    enabledHeight,
                    2.5F,
                    setting.isEnabled()
                            ? rectColorBool
                            : settingRectColor
            );

            lithiumBoldFont14.drawString(setting.name, enabledX + 2, enabledY + 3,
                    ColorUtil.applyOpacity(textColor, .5f + (.5f * toggleAnimation.getOutput().floatValue())));


            xOffset += enabledWidth + spacing;
        }

        additionalHeight = addHeight;

        float increment = (((boxY - y) + boxHeight + addHeight + 3) - realHeight) / (realHeight / normalCount);


        countSize = normalCount + increment;
    }

    private float getEnabledWidth(BooleanSetting setting) {
        return (lithiumFont14.getStringWidth(setting.name) + 4);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        float boxHeight = 15;
        float boxY = y + realHeight / 2f - (boxHeight / 2f) + 4;
        float boxX = x + 6;
        float boxWidth = width - 10;

        float xOffset = 2;
        float yOffset = 3;
        float spacing = 3;

        //The available space for the settings
        float avaliableWidth = boxWidth - 2;

        //Sort the boolean settings to have the smallest width ones at the top

        for (BooleanSetting setting : sortedSettings) {
            float enabledWidth = getEnabledWidth(setting);
            float enabledHeight = lithiumFont14.getHeight() + 4;

            //If the width exceeds the available space, we need to add a new line
            if (xOffset + enabledWidth > avaliableWidth) {
                xOffset = 2;
                yOffset += enabledHeight + spacing;
            }

            float enabledX = boxX + xOffset;
            float enabledY = boxY + yOffset;
            boolean hovered = HoveringUtil.isHovering(enabledX, enabledY, enabledWidth, enabledHeight, mouseX, mouseY);

            if(isClickable(enabledY + enabledHeight) && hovered && button == 0) {
                setting.toggle();
            }

            xOffset += enabledWidth + spacing;
        }

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
