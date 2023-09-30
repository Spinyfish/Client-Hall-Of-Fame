package dev.tenacity.ui.clickguis.zeroday.components.settings;

import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.ui.clickguis.zeroday.components.SettingComponent;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.ContinualAnimation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.misc.HoveringUtil;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.GradientUtil;
import dev.tenacity.utils.render.RoundedUtil;
import dev.tenacity.utils.tuples.Pair;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

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
        float sliderY = y + 15;
        float sliderWidth = width - 16;
        float sliderHeight = 4;

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

        // Background
        Gui.drawRect(
                x + 5, y + 2,
                x + width - 5, y + height - 16,
                settingRectColor.getRGB()
        );

        // Slider background
        Gui.drawRect(
                sliderX,
                sliderY,
                sliderX + sliderWidth,
                sliderY + sliderHeight,
                ColorUtil.brighter(settingRectColor, .7f - (.2f * hoverAnimation.getOutput().floatValue())).getRGB()
        );

        // The Slider itself
        Gui.drawGradientRectSideways(
                sliderX,
                sliderY,
                sliderX + animatedWidth,
                sliderY + sliderHeight,
                ColorUtil.rainbow(10, (int) (x) / 10, 0.35F, 0.8F, 1.0F).getRGB(),
                ColorUtil.rainbow(10, (int) (x + width) / 10, 0.35F, 0.8F, 1.0F).getRGB()
        );

        // Draw the balls
        Gui.drawRect(
                sliderX + animatedWidth - 2,
                sliderY - 2,
                sliderX + animatedWidth + 2,
                sliderY + 6,
                textColor.getRGB()
        );

        // Name & Value
        String string = numberSetting.name;
        lithiumFont20.drawString(string, sliderX, y + 5, textColor);

        String val = "§l" + value;

        Gui.drawRect(
                sliderX + sliderWidth / 2 - lithiumBoldFont20.getStringWidth(val) / 2.0F - 1.0F,
                sliderY - 1.0F,
                sliderX + sliderWidth / 2 + lithiumBoldFont20.getStringWidth(val) / 2.0F + 2.0F,
                sliderY + lithiumBoldFont20.getHeight() - 2,
                settingRectColor.getRGB()
        );

        lithiumBoldFont20.drawString(
                val,
                sliderX + sliderWidth / 2 - lithiumBoldFont20.getStringWidth(val) / 2.0F,
                sliderY - lithiumBoldFont20.getHeight() / 4.0F,
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
