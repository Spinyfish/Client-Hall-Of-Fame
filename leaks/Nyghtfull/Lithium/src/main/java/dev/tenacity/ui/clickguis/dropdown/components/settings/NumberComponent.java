package dev.tenacity.ui.clickguis.dropdown.components.settings;

import dev.tenacity.utils.tuples.Pair;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.ui.clickguis.dropdown.components.SettingComponent;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.ContinualAnimation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.misc.HoveringUtil;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RoundedUtil;
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

        float sliderX = x + 5;
        float sliderWidth = width - 10;
        float sliderY = y + 13;
        float sliderHeight = 3;


        textAnimations.getFirst().setDirection(dragging ? Direction.BACKWARDS : Direction.FORWARDS);
        textAnimations.getSecond().setDirection(selected && !dragging ? Direction.FORWARDS : Direction.BACKWARDS);

        boolean hovering = HoveringUtil.isHovering(sliderX, sliderY - 2, sliderWidth, sliderHeight + 4, mouseX, mouseY);

        hoverAnimation.setDirection(hovering || dragging ? Direction.FORWARDS : Direction.BACKWARDS);


        float firstTextAnim = textAnimations.getFirst().getOutput().floatValue();

        float funnyWidth = (lithiumFont16.getStringWidth(numberSetting.name) - (lithiumFont16.getStringWidth(": " + value)));
        lithiumFont16.drawString(": §l" + value, sliderX +
                        funnyWidth + (lithiumFont16.getStringWidth(": " + value) * firstTextAnim),
                y + 2, ColorUtil.applyOpacity(textColor, firstTextAnim));


        String text = "You can use arrow keys";
        lithiumFont14.drawCenteredString(text, x + width /2f, sliderY + sliderHeight + 4.5f,
                ColorUtil.applyOpacity(-1, textAnimations.getSecond().getOutput().floatValue() * .25f));

        lithiumFont16.drawString(numberSetting.name, sliderX, y + 2, textColor);
        RoundedUtil.drawRound(sliderX, sliderY, sliderWidth, sliderHeight, 1.5f,
                ColorUtil.brighter(settingRectColor, .7f - (.2f * hoverAnimation.getOutput().floatValue())));






        double currentValue = numberSetting.getValue();

        if (dragging) {
            float percent = Math.min(1, Math.max(0, (mouseX - sliderX) / sliderWidth));
            double newValue = MathUtils.interpolate(numberSetting.getMinValue(), numberSetting.getMaxValue(), percent);
            numberSetting.setValue(newValue);
        }

        float widthPercentage = (float) (((currentValue) - numberSetting.getMinValue()) / (numberSetting.getMaxValue() - numberSetting.getMinValue()));


        animationWidth.animate(sliderWidth * widthPercentage, 20);

        float animatedWidth = animationWidth.getOutput();

        RoundedUtil.drawRound(sliderX, sliderY, animatedWidth, sliderHeight, 1.5f, clientColors.getSecond());

        float size = 7f;
        RoundedUtil.drawRound(sliderX + animatedWidth - size / 2f, sliderY - (size / 4f + .5f), size, size, (size / 2f) - .5f, settingRectColor);
        size = 5;
        RoundedUtil.drawRound(sliderX + animatedWidth - size / 2f, sliderY - size / 4f, size, size, (size / 2f) - .5f, textColor);


        float secondTextAnim = 1 - textAnimations.getFirst().getOutput().floatValue();
        float rectWidth = lithiumFont14.getStringWidth("§l" + value) + 4;
        float rectX = Math.max(x, 2 + (sliderX + animatedWidth - size / 2f) - rectWidth / 2f);
        float rectY = sliderY + sliderHeight + 4;
        float rectHeight = lithiumFont14.getHeight() + 2;
        RoundedUtil.drawRound(rectX, rectY, rectWidth, rectHeight, 3, ColorUtil.applyOpacity(settingRectColor.brighter(), secondTextAnim));

        lithiumFont14.drawString("§l" + value, rectX + 2, rectY + lithiumFont14.getMiddleOfBox(rectHeight),
                ColorUtil.applyOpacity(textColor, secondTextAnim));

        clickCountAdd = (.3f * secondTextAnim) + (.3f * textAnimations.getSecond().getOutput().floatValue());

        countSize = (float) (1.5 + clickCountAdd);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        float sliderX = x + 5;
        float sliderWidth = width - 10;
        float sliderY = y + height / 2f + 2;
        float sliderHeight = 3;
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
