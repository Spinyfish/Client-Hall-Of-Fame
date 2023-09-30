package dev.tenacity.ui.clickguis.lithium.components.settings;

import dev.tenacity.module.settings.impl.ColorSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.ui.clickguis.lithium.components.SettingComponent;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.misc.HoveringUtil;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.objects.TextField;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.GLUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import dev.tenacity.utils.tuples.Pair;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ColorComponent extends SettingComponent<ColorSetting> {

    private final Animation hoverAnimation = new DecelerateAnimation(250, 1);

    private final Animation openAnimation = new DecelerateAnimation(250, 1);
    private final Animation rainbowAnimation = new DecelerateAnimation(250, 1);
    private final Pair<Animation, Animation> errorAnimations = Pair.of(
            new DecelerateAnimation(1000, 1),
            new DecelerateAnimation(250, 1));

    public float realHeight;
    public float openedHeight;
    private boolean opened;

    private final TextField hexField = new TextField(FontUtil.lithiumFont16);

    private boolean draggingPicker;
    private boolean draggingHue;

    private final NumberSetting speedSetting = new NumberSetting("Speed", 15, 40, 1, 1);
    private final NumberSetting saturationSetting = new NumberSetting("Saturation", 1, 1, 0, .05);


    private List<NumberComponent> rainbowSettings;


    public ColorComponent(ColorSetting setting) {
        super(setting);
        if(setting.isRainbow()) {
            rainbowSettings = new ArrayList<>();
            rainbowSettings.add(new NumberComponent(speedSetting));
            rainbowSettings.add(new NumberComponent(saturationSetting));
            speedSetting.setValue(setting.getRainbow().getSpeed());
            saturationSetting.setValue(setting.getRainbow().getSaturation());
        }
    }


    @Override
    public void initGui() {

    }

    String hexLetters = "abcdef0123456789";

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        char c = typedChar;
        if (!hexLetters.contains(Character.toString(typedChar))) {
            //Idk what this is but its not allowed in ChatAllowedCharacters which we use to filter out all non-allowed characters
            c = 167;
        }

        hexField.keyTyped(c, keyCode);

    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        ColorSetting colorSetting = getSetting();

        openAnimation.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);

        lithiumFont16.drawString(colorSetting.name, x + 8, y + lithiumFont16.getMiddleOfBox(realHeight), textColor);

        float spacing = 4;
        float colorHeight = 6.5f;
        float colorWidth = 20;
        float colorX = x + width - (colorWidth + spacing) - 2;
        float colorY = y + (realHeight / 2) - (colorHeight / 2);
        float colorRadius = 3;


        float openAnim = openAnimation.getOutput().floatValue();


        float newColorY = y + realHeight - 1;
        float newColorHeight = 5 + ((openAnimation.finished(Direction.FORWARDS) || !openAnimation.isDone()) ? (5 * hoverAnimation.getOutput().floatValue()) : 0);
        colorX = MathUtils.interpolateFloat(colorX, x + 9, openAnim);
        colorY = MathUtils.interpolateFloat(colorY, newColorY, openAnim);
        colorWidth = MathUtils.interpolateFloat(colorWidth, width - 18, openAnim);
        colorHeight = MathUtils.interpolateFloat(colorHeight, newColorHeight, openAnim);
        colorRadius = MathUtils.interpolateFloat(colorRadius, 2, openAnim);

        boolean hovered = HoveringUtil.isHovering(colorX - 4, colorY - 4, colorWidth + 8, colorHeight + 8, mouseX, mouseY);

        hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
        Color actualColor = ColorUtil.applyOpacity(colorSetting.getColor(), alpha);

        RoundedUtil.drawRound(
                colorX,
                colorY,
                colorWidth,
                colorHeight,
                colorRadius,
                ColorUtil.interpolateColorC(actualColor, actualColor.darker(), hoverAnimation.getOutput().floatValue())
        );

        String text = colorSetting.isRainbow() ? "Shift + Click for picker" : "Shift + Click for rainbow";
        lithiumFont14.drawCenteredStringWithShadow(text, colorX + colorWidth / 2f,
                colorY + lithiumFont14.getMiddleOfBox(colorHeight),
                ColorUtil.applyOpacity(-1, hoverAnimation.getOutput().floatValue() * (openAnim * openAnim)));

        rainbowAnimation.setDirection(colorSetting.isRainbow() ? Direction.FORWARDS : Direction.BACKWARDS);
        float rainbowAnim = rainbowAnimation.getOutput().floatValue();

        float rainbowCount = 0;
        if (opened || !openAnimation.isDone()) {
            if (colorSetting.isRainbow()) {
                Color color = colorSetting.getColor();
                int red = color.getRed(), green = color.getGreen(), blue = color.getBlue();
                float[] hsb = Color.RGBtoHSB(red, green, blue, null);
                colorSetting.setHue(hsb[0]);
                colorSetting.setSaturation(hsb[1]);
                colorSetting.setBrightness(hsb[2]);
            }


            float[] hsb = {(float) colorSetting.getHue(), (float) colorSetting.getSaturation(), (float) colorSetting.getBrightness()};

            float gradientX = x + 9;
            float gradientY = newColorY + colorHeight + 4;
            float gradientWidth = width - 18;
            float gradientHeight = 10 + (55 * openAnim) - (20 * rainbowAnim);

            float colorAlpha = alpha * openAnim;

            if (draggingHue) {
                colorSetting.setHue(Math.min(1, Math.max(0, (mouseX - gradientX) / gradientWidth)));
            }

            if (draggingPicker) {
                colorSetting.setBrightness(Math.min(1, Math.max(0, 1 - ((mouseY - gradientY) / gradientHeight))));
                colorSetting.setSaturation(Math.min(1, Math.max(0, (mouseX - gradientX) / gradientWidth)));
            }


            Color firstColor = ColorUtil.applyOpacity(Color.getHSBColor(hsb[0], 1, 1), colorAlpha);
            RoundedUtil.drawRound(
                    gradientX, gradientY,
                    gradientWidth, gradientHeight,
                    colorRadius,
                    ColorUtil.applyOpacity(firstColor, colorAlpha)
            );


            Color secondColor = Color.getHSBColor(hsb[0], 0, 1);

            RoundedUtil.drawGradientHorizontal(
                    gradientX, gradientY,
                    gradientWidth,
                    gradientHeight,
                    colorRadius,
                    ColorUtil.applyOpacity(secondColor, colorAlpha),
                    ColorUtil.applyOpacity(secondColor, 0)
            );

            Color thirdColor = Color.getHSBColor(hsb[0], 1, 0);

            RoundedUtil.drawGradientVertical(
                    gradientX, gradientY,
                    gradientWidth,
                    gradientHeight,
                    colorRadius,
                    ColorUtil.applyOpacity(thirdColor, 0),
                    ColorUtil.applyOpacity(thirdColor, colorAlpha)
            );

            float pickerY = (gradientY - 2) + (gradientHeight * (1 - hsb[2]));
            float pickerX = (gradientX) + (gradientWidth * hsb[1] - 1);
            pickerY = Math.max(Math.min(gradientY + gradientHeight - 2, pickerY), gradientY - 2);
            pickerX = Math.max(Math.min(gradientX + gradientWidth - 2, pickerX), gradientX - 2);


            Color whiteColor = ColorUtil.applyOpacity(Color.WHITE, colorAlpha);
            RenderUtil.color(whiteColor.getRGB());
            GLUtil.startBlend();
            RenderUtil.drawImage(new ResourceLocation("Tenacity/colorpicker2.png"), pickerX, pickerY, 4, 4);
            GLUtil.endBlend();

            float hueY = gradientY + gradientHeight + 5;
            float hueHeight = 4;
            RenderUtil.resetColor();
            mc.getTextureManager().bindTexture(new ResourceLocation("Tenacity/hue.png"));
            RoundedUtil.drawRoundTextured(gradientX, hueY, gradientWidth, hueHeight, 1.5f, colorAlpha);


            float sliderSize = 6.5f;
            float sliderX = gradientX + (gradientWidth * hsb[0]) - (sliderSize / 2);
            RoundedUtil.drawRound(sliderX, hueY +
                            ((hueHeight / 2f) - sliderSize / 2f),
                    sliderSize, sliderSize, (sliderSize / 2f) - .5f, whiteColor);
            float miniSize = 4.25f;
            float movement = (sliderSize / 2f - miniSize / 2f);
            RoundedUtil.drawRound(sliderX + movement, hueY + (((hueHeight / 2f) - miniSize / 2f)),
                    miniSize, miniSize, (miniSize / 2f) - .5f, firstColor);

            Animation error2Anim = errorAnimations.getSecond();
            error2Anim.setDirection(colorSetting.isRainbow() ? Direction.BACKWARDS : error2Anim.getDirection());

            float newYVal = hueY + hueHeight + 4 + (5 * error2Anim.getOutput().floatValue());

            float heightLeft = height - (newYVal - y);

            if (!rainbowAnimation.finished(Direction.FORWARDS)) {
                float rainbowInverse = 1 - rainbowAnim;


                whiteColor = ColorUtil.applyOpacity(whiteColor, rainbowInverse);

                lithiumFont20.drawString("Hex", gradientX, newYVal + lithiumFont20.getMiddleOfBox(heightLeft) - 10, whiteColor);


                hexField.setWidth(50);
                hexField.setHeight(12);
                hexField.setXPosition(gradientX + ((gradientWidth - hexField.getWidth()) - 5));
                hexField.setYPosition(newYVal + heightLeft / 2f - hexField.getHeight() / 2f - 10);
                Color settingColor = ColorUtil.applyOpacity(settingRectColor.brighter(), openAnim * rainbowInverse);
                hexField.setOutline(settingColor.brighter().brighter());
                hexField.setFill(settingColor);
                hexField.setTextAlpha(colorAlpha * rainbowInverse);
                hexField.setMaxStringLength(6);

                if (!hexField.isFocused()) {
                    hexField.setText(colorSetting.getHexCode());
                    error2Anim.setDirection(Direction.BACKWARDS);
                } else {
                    try {
                        Color textFieldColor = Color.decode("#" + hexField.getText());
                        colorSetting.setColor(textFieldColor);
                        error2Anim.setDirection(Direction.BACKWARDS);
                    } catch (Exception e) {
                        Animation blinkAnimation = errorAnimations.getFirst();
                        error2Anim.setDirection(Direction.FORWARDS);
                        if (blinkAnimation.isDone()) {
                            blinkAnimation.changeDirection();
                        }

                        lithiumFont14.drawString("Invalid Hex Code", hexField.getXPosition() - .5f, newYVal - (lithiumFont14.getHeight() - .5f),
                                ColorUtil.applyOpacity(Color.RED, blinkAnimation.getOutput().floatValue()));
                    }
                }


                hexField.drawTextBox();
            }

            if (!rainbowAnimation.isDone() || rainbowAnimation.finished(Direction.FORWARDS)) {
                Color textColor = ColorUtil.applyOpacity(Color.WHITE, colorAlpha * rainbowAnim);
                if (rainbowSettings != null) {
                    float realHeightLeft = openedHeight - (newYVal - y);
                    float componentHeight = realHeightLeft / 2f;
                    rainbowCount = 0;
                    int count = 0;
                    for (NumberComponent numberComponent : rainbowSettings) {
                        numberComponent.x = gradientX - 9;
                        numberComponent.y = (newYVal) + (count * componentHeight) + (componentHeight * rainbowCount);
                        numberComponent.width = gradientWidth + 18;
                        numberComponent.height = componentHeight;
                        numberComponent.settingRectColor = ColorUtil.applyOpacity(settingRectColor, colorAlpha * rainbowAnim);
                        numberComponent.textColor = textColor;
                        numberComponent.panelLimitY = panelLimitY;
                        numberComponent.clientColors = clientColors.apply((c1, c2) ->
                                Pair.of(ColorUtil.applyOpacity(c1, colorAlpha * rainbowAnim), ColorUtil.applyOpacity(c2, colorAlpha * rainbowAnim)));

                        numberComponent.alpha = openAnim * alpha * rainbowAnim;

                        numberComponent.drawScreen(mouseX, mouseY);

                        count++;
                        rainbowCount += numberComponent.clickCountAdd;
                    }

                    if(colorSetting.isRainbow()) {
                        colorSetting.getRainbow().setSpeed(speedSetting.getValue().intValue());
                        colorSetting.getRainbow().setSaturation(saturationSetting.getValue().floatValue());
                    }
                }

            }

        }

        if (!typing) {
            typing = hexField.isFocused();
        }

        Animation errorAnimation = errorAnimations.getSecond();

        openedHeight = realHeight * (1 + (6.75f * openAnim));

        countSize = 1 + (6.75f * openAnim) + (errorAnimation.getOutput().floatValue() * .25f) + (Math.max(0, rainbowCount * 2));
    }

    private String getHexCode(ColorSetting colorSetting) {
        Color color = colorSetting.getColor();
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        float spacing = 4;
        float colorHeight = 6.5f;
        float colorWidth = 30;
        float colorX = x + width - (colorWidth + spacing);
        float colorY = y + (realHeight / 2) - (colorHeight / 2);

        float openAnim = openAnimation.getOutput().floatValue();

        float newColorY = y + realHeight - 1;
        float newColorHeight = 5 + ((openAnimation.finished(Direction.FORWARDS) || !openAnimation.isDone()) ? (5 * hoverAnimation.getOutput().floatValue()) : 0);
        colorX = MathUtils.interpolateFloat(colorX, x + 6, openAnim);
        colorY = MathUtils.interpolateFloat(colorY, newColorY, openAnim);
        colorWidth = MathUtils.interpolateFloat(colorWidth, width - 12, openAnim);
        colorHeight = MathUtils.interpolateFloat(colorHeight, newColorHeight, openAnim);

        boolean hovered = isClickable(colorY + colorHeight) &&
                HoveringUtil.isHovering(colorX - 4, colorY - 4, colorWidth + 8, colorHeight + 8, mouseX, mouseY);

        if (hovered && button == 1) {
            opened = !opened;
            hexField.mouseClicked(mouseX, mouseY, button);
        }

        if (opened) {
            if (hovered && button == 0 && (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))) {
                getSetting().setRainbow(!getSetting().isRainbow());
                if(getSetting().isRainbow()) {
                    rainbowSettings = new ArrayList<>();
                    rainbowSettings.add(new NumberComponent(speedSetting));
                    rainbowSettings.add(new NumberComponent(saturationSetting));
                    getSetting().getRainbow().setSpeed(speedSetting.getValue().intValue());
                    getSetting().getRainbow().setSaturation(saturationSetting.getValue().floatValue());
                }
            }

            if (getSetting().isRainbow() && rainbowSettings != null) {
                for (NumberComponent numberComponent : rainbowSettings) {
                    numberComponent.mouseClicked(mouseX, mouseY, button);
                }
                return;
            }


            float gradientX = x + 6;
            float gradientY = newColorY + colorHeight + 4;
            float gradientWidth = width - 12;
            float gradientHeight = 10 + (55 * openAnim);
            float radius = 2;

            if (button == 0) {
                float hueY = gradientY + gradientHeight + 5;
                if (isClickable(hueY + 4) && HoveringUtil.isHovering(gradientX, hueY, gradientWidth, 4, mouseX, mouseY)) {
                    draggingHue = true;
                }
                if (isClickable(gradientY + gradientHeight) && HoveringUtil.isHovering(gradientX, gradientY, gradientWidth, gradientHeight, mouseX, mouseY)) {
                    draggingPicker = true;
                }
            }


            hexField.mouseClicked(mouseX, mouseY, button);
        }

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        draggingHue = false;
        draggingPicker = false;
        if (getSetting().isRainbow() && rainbowSettings != null) {
            for (NumberComponent numberComponent : rainbowSettings) {
                numberComponent.mouseReleased(mouseX, mouseY, state);
            }
        }
    }


}
