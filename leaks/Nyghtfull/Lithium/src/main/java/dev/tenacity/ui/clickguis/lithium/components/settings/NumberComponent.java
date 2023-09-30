package dev.tenacity.ui.clickguis.lithium.components.settings;

import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.ui.clickguis.lithium.components.SettingComponent;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.ContinualAnimation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.misc.HoveringUtil;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import dev.tenacity.utils.tuples.Pair;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class NumberComponent extends SettingComponent<NumberSetting> {

    private final Animation hoverAnimation = new DecelerateAnimation(250, 1, Direction.BACKWARDS);

    private final Pair<Animation, Animation> textAnimations = Pair.of(
            new DecelerateAnimation(250, 1), new DecelerateAnimation(250, 1));


    private boolean dragging;
    private final ContinualAnimation animationWidth = new ContinualAnimation();

    public float clickCountAdd = 0;
    private boolean selected;


    public NumberComponent(NumberSetting numberSetting) {
        super(numberSetting);
    }


    @Override
    public void initGui() {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (selected) {
            Keyboard.enableRepeatEvents(true);
            double increment = getSetting().getIncrement();
            switch (keyCode) {
                case Keyboard.KEY_LEFT:
                    getSetting().setValue(getSetting().getValue() - increment);
                    break;
                case Keyboard.KEY_RIGHT:
                    getSetting().setValue(getSetting().getValue() + increment);
                    break;
            }
        }

    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        NumberSetting numberSetting = getSetting();

        String value = String.valueOf(MathUtils.round(getSetting().getValue(), 2));

        value = value.contains(".") ? value.replaceAll("0*$", "").replaceAll("\\.$", "") : value;

        float sliderX = x + 8;
        float sliderY = y + 18;
        float sliderWidth = width - 16;
        float sliderHeight = 6;

        textAnimations.getFirst().setDirection(dragging ? Direction.BACKWARDS : Direction.FORWARDS);
        textAnimations.getSecond().setDirection(selected && !dragging ? Direction.FORWARDS : Direction.BACKWARDS);

        boolean hovering = HoveringUtil.isHovering(sliderX, sliderY - 2, sliderWidth, sliderHeight + 4, mouseX, mouseY);
        hoverAnimation.setDirection(hovering || dragging ? Direction.FORWARDS : Direction.BACKWARDS);

        double currentValue = numberSetting.getValue();

        if (dragging) {
            float percent = Math.min(1, Math.max(0, (mouseX - sliderX) / sliderWidth));
            double newValue = MathUtils.interpolate(numberSetting.getMinValue(), numberSetting.getMaxValue(), percent);
            numberSetting.setValue(newValue);
        }

        float widthPercentage = (float) (((currentValue) - numberSetting.getMinValue()) / (numberSetting.getMaxValue() - numberSetting.getMinValue()));

        animationWidth.animate(sliderWidth * widthPercentage, 20);
        float animatedWidth = animationWidth.getOutput();

        // Slider background
        RoundedUtil.drawRound(
                sliderX,
                sliderY,
                sliderWidth,
                sliderHeight,
                2.5F,
                ColorUtil.brighter(settingRectColor, .7f - (.2f * hoverAnimation.getOutput().floatValue()))
        );

        // The Slider itself
        RoundedUtil.drawRound(
                sliderX,
                sliderY,
                sliderWidth * widthPercentage,
                sliderHeight,
                2.5F,
                new Color(0xBB0066FF, true)
        );

        // Draw the balls
        RenderUtil.drawCircleNotSmooth(
                sliderX + animatedWidth - 3,
                sliderY,
                6,
                textColor.getRGB()
        );

        // Name & Value
        String string = numberSetting.name;
        lithiumFont20.drawString(string, sliderX, y + 8, textColor);

        lithiumBoldFont20.drawString(
                value,
                sliderX + sliderWidth - lithiumBoldFont20.getStringWidth(value),
                y + 8,
                ColorUtil.applyOpacity(textColor, 0.8F).getRGB()
        );

        countSize = (float) (1.5 + clickCountAdd);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        float sliderX = x + 8;
        float sliderY = y + 15;
        float sliderWidth = width - 16;
        float sliderHeight = 4;

        if (!HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY)) {
            selected = false;
        }

        if (isClickable(sliderY + sliderHeight) && HoveringUtil.isHovering(sliderX, sliderY - 2, sliderWidth, sliderHeight + 4, mouseX, mouseY) && button == 0) {
            selected = true;
            dragging = true;
        }

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (dragging) dragging = false;
    }
}
