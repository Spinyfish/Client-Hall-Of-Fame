package dev.tenacity.ui.clickguis.modern;

import dev.tenacity.utils.tuples.Pair;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.module.settings.Setting;
import dev.tenacity.module.settings.impl.*;
import dev.tenacity.ui.GuiEvents;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.misc.HoveringUtil;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.misc.StringUtils;
import dev.tenacity.utils.objects.TextField;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;


public class SettingsPanel extends Panel {

    private final Module module;
    private final HashMap<Setting, Animation> booleanSettingHashMap;

    private final HashMap<Setting, Animation> modeSettingHashMap;
    private final HashMap<Setting, Boolean> modeSettingClick;
    private final HashMap<Setting, HashMap<String, Animation>> modesHoverAnimation;
    private final HashMap<Setting, Animation> multiBoolSettingMap;
    private final HashMap<Setting, Boolean> multiBoolSettingClickMap;
    private final HashMap<NumberSetting, Float> sliderMap;
    private final HashMap<Setting, HashMap<BooleanSetting, Animation>> boolHoverAnimation;
    private final HashMap<StringSetting, TextField> textFieldMap;
    public Setting draggingNumber;
    public float maxScroll = 0;
    private boolean hueFlag;
    private boolean saturationFlag;
    public boolean typing;

    public SettingsPanel(Module module) {
        this.module = module;
        booleanSettingHashMap = new HashMap<>();

        multiBoolSettingMap = new HashMap<>();
        multiBoolSettingClickMap = new HashMap<>();
        boolHoverAnimation = new HashMap<>();
        sliderMap = new HashMap<>();

        modeSettingHashMap = new HashMap<>();
        modeSettingClick = new HashMap<>();
        modesHoverAnimation = new HashMap<>();
        textFieldMap = new HashMap<>();

        for (Setting setting : module.getSettingsList()) {
            if (setting instanceof BooleanSetting) {
                booleanSettingHashMap.put(setting, new DecelerateAnimation(250, 1));
            }
            if (setting instanceof MultipleBoolSetting) {
                multiBoolSettingMap.put(setting, new DecelerateAnimation(250, 1));
                multiBoolSettingClickMap.put(setting, false);
                HashMap<BooleanSetting, Animation> boolMap = new HashMap<>();
                for (BooleanSetting booleanSetting : ((MultipleBoolSetting) setting).getBoolSettings()) {
                    boolMap.put(booleanSetting, new DecelerateAnimation(250, 1));
                }
                boolHoverAnimation.put(setting, boolMap);
            }

            if (setting instanceof NumberSetting) {
                sliderMap.put((NumberSetting) setting, 0f);
            }

            if (setting instanceof ModeSetting) {
                HashMap<String, Animation> modesHashmap = new HashMap<>();
                modeSettingHashMap.put(setting, new DecelerateAnimation(250, 1));
                modeSettingClick.put(setting, false);
                ModeSetting modeSetting = (ModeSetting) setting;

                for (String mode : modeSetting.modes) {
                    modesHashmap.put(mode, new DecelerateAnimation(250, 1));

                }

                modesHoverAnimation.put(setting, modesHashmap);
            }

            if (setting instanceof StringSetting) {
                StringSetting stringSetting = (StringSetting) setting;
                TextField textField = new TextField(lithiumFont18);
                textField.setText(stringSetting.getString());
                textField.setCursorPositionZero();
                textFieldMap.put(stringSetting, textField);
            }

        }


    }

    @Override
    public void initGui() {
        if (textFieldMap != null) {
            for (Map.Entry<StringSetting, TextField> entry : textFieldMap.entrySet()) {
                entry.getValue().setText(entry.getKey().getString());
                entry.getValue().setCursorPositionZero();
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (textFieldMap != null) {
            for (Map.Entry<StringSetting, TextField> entry : textFieldMap.entrySet()) {
                entry.getValue().keyTyped(typedChar, keyCode);
            }
        }

    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        handle(mouseX, mouseY, -1, GuiEvents.DRAW);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        handle(mouseX, mouseY, button, GuiEvents.CLICK);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        handle(mouseX, mouseY, state, GuiEvents.RELEASE);
    }


    public void handle(int mouseX, int mouseY, int button, GuiEvents type) {
        typing = false;
        if (type == GuiEvents.RELEASE && button == 0) {
            draggingNumber = null;
        }

        Pair<Color, Color> colors = HUDMod.getClientColors();


        float count = 0;
        float settingHeight = 22;
        for (Setting setting : module.getSettingsList()) {
            if (setting.cannotBeShown()) continue;
            if (setting instanceof KeybindSetting) continue;
            float settingY = y + 30 + count * settingHeight;
            boolean isHoveringSetting = HoveringUtil.isHovering(x + 5, settingY, 130, settingHeight - 2, mouseX, mouseY);
            if (setting instanceof BooleanSetting) {
                BooleanSetting booleanSetting = (BooleanSetting) setting;
                Animation animation = booleanSettingHashMap.get(setting);
                animation.setDirection(booleanSetting.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);

                if (type == GuiEvents.CLICK && isHoveringSetting && button == 0) {
                    booleanSetting.toggle();
                }

                int color = ColorUtil.interpolateColor(new Color(30, 31, 35), colors.getFirst(), (float) animation.getOutput().floatValue());
                lithiumFont16.drawString(setting.name, x + 13, settingY + lithiumFont16.getMiddleOfBox(8), -1);
                //  RenderUtil.renderRoundedRect(x + 95, settingY, 20, 10, 4,
                //color);

                GL11.glEnable(GL11.GL_BLEND);
                RoundedUtil.drawRound(x + 109, settingY, 18, 8, 4, ColorUtil.interpolateColorC(new Color(30, 31, 35), colors.getFirst(), (float) animation.getOutput().floatValue()));

                GlStateManager.color(1, 1, 1);
                RenderUtil.drawGoodCircle(x + 113 + (10 * animation.getOutput().floatValue()), settingY + 4, 4f, -1);
                count -= .2f;
            }

            if (setting instanceof ModeSetting) {
                ModeSetting modeSetting = (ModeSetting) setting;
                Animation animation = modeSettingHashMap.get(setting);
                if (type == GuiEvents.CLICK && isHoveringSetting && button == 1) {
                    modeSettingClick.put(setting, !modeSettingClick.get(setting));
                }

                float stringWidth = (float) lithiumFont16.getStringWidth(StringUtils.getLongestModeName(modeSetting.modes));

                animation.setDirection(modeSettingClick.get(setting) ? Direction.FORWARDS : Direction.BACKWARDS);

                float modeWidth = 15 + stringWidth;

                float math = (modeSetting.modes.size() - 1) * 15;


                float boxX = (x + 114 - stringWidth) - 1;
                float nameWidth = lithiumFont16.getStringWidth(modeSetting.name);
                float stringX = x + 13;
                if (nameWidth > (boxX - stringX)) {
                    lithiumFont16.drawWrappedText(modeSetting.name, stringX, settingY - (lithiumFont16.getMiddleOfBox(12) / 2), -1, boxX - stringX, 2);
                    //    lithiumFont16.drawString(modeSetting.name, stringX + (boxX - stringX) / 2f, settingY + lithiumFont16.getMiddleOfBox(8), -1);
                } else {
                    lithiumFont16.drawString(modeSetting.name, x + 13, settingY + 2, -1);
                }


                RoundedUtil.drawRound(boxX, settingY - 1, modeWidth + 2, 12 + (float) (math * animation.getOutput().floatValue()), 4, new Color(68, 71, 78));
                //  RenderUtil.renderRoundedRect((float) (x + 113.5 - stringWidth), settingY - 1.5f, modeWidth + 3, 14 + (float) (math * animation.getOutput().floatValue()),
                //      4, new Color(68, 71, 78).getRGB());

                RoundedUtil.drawRound(x + 114 - stringWidth, settingY, modeWidth, 10 + (float) (math * animation.getOutput().floatValue()), 3, new Color(30, 31, 35));


                float modeX = x + 114 - stringWidth + modeWidth / 2f;
                RenderUtil.resetColor();
                lithiumFont16.drawCenteredString(modeSetting.getMode(), modeX, settingY + lithiumFont16.getMiddleOfBox(10), -1);

                int modeCount = 1;
                for (String mode : modeSetting.modes) {
                    if (!mode.equalsIgnoreCase(modeSetting.getMode())) {
                        Animation modeAnimation = modesHoverAnimation.get(modeSetting).get(mode);
                        boolean isHoveringMode = animation.getDirection().equals(Direction.FORWARDS) &&
                                HoveringUtil.isHovering(x + 115 - stringWidth, settingY + (modeCount * 15), modeWidth, 11, mouseX, mouseY);


                        if (type == GuiEvents.CLICK && button == 0 && isHoveringMode) {
                            modeSettingClick.put(setting, !modeSettingClick.get(setting));
                            modeSetting.setCurrentMode(mode);
                        }

                        modeAnimation.setDirection(isHoveringMode ? Direction.FORWARDS : Direction.BACKWARDS);
                        modeAnimation.setDuration(isHoveringMode ? 200 : 350);

                        int colorInterpolate = ColorUtil.interpolateColor(new Color(128, 134, 141), colors.getSecond(), (float) modeAnimation.getOutput().floatValue());

                        GlStateManager.color(1, 1, 1, 1);
                        lithiumFont16.drawCenteredString(mode, modeX, (float) (settingY + 2 + ((modeCount * 15) * animation.getOutput().floatValue())),
                                ColorUtil.interpolateColor(new Color(40, 40, 40, 10), new Color(colorInterpolate), (float) animation.getOutput().floatValue()));
                        modeCount++;
                    }
                }

                count += ((math / settingHeight) * animation.getOutput().floatValue());
            }


            if (setting instanceof ColorSetting) {
                ColorSetting colorSetting = (ColorSetting) setting;
                float height = (colorSetting.isRainbow() ? 100 : 90);
                RoundedUtil.drawRound(x + 12, settingY - 1, 117, height, 4, new Color(68, 71, 78));
                RoundedUtil.drawRound(x + 13, settingY, 115, height - 2, 3, new Color(30, 31, 35));
                //   RenderUtil.renderRoundedRect(x + 8.5f, settingY - 4.5f, 118, 85, 4, new Color(68, 71, 78).getRGB());
                //  RenderUtil.renderRoundedRect(x + 10, settingY - 3, 115, 82, 3, new Color(30, 31, 35).getRGB());
                lithiumFont16.drawCenteredString(colorSetting.name, (x + 13) + 115 / 2f, settingY + 3, -1);

                if (colorSetting.isRainbow()) {
                    Color color = colorSetting.getColor();
                    int red = color.getRed(), green = color.getGreen(), blue = color.getBlue();
                    float[] hsb = Color.RGBtoHSB(red, green, blue, null);
                    colorSetting.setHue(hsb[0]);
                    colorSetting.setSaturation(hsb[1]);
                    colorSetting.setBrightness(hsb[2]);
                }


                float[] hsb = {(float) colorSetting.getHue(), (float) colorSetting.getSaturation(), (float) colorSetting.getBrightness()};

                /*Draw.colorRGBA(colorSetting.getColor().getRGB());
                GL11.glEnable(GL11.GL_BLEND);
                mc.getTextureManager().bindTexture(new ResourceLocation("Tenacity/booleanslider1.png"));
                Gui.drawModalRectWithCustomSizedTexture(x + 98, settingY, 0, 0, 20, 10, 20, 10);*/
                //RenderUtil.renderRoundedRect(x + 95, settingY, 20, 10, 3, colorSetting.getColor().getRGB());

                float gradientX = x + 17;
                float gradientY = settingY + 15;
                float gradientWidth = 97;
                float gradientHeight = 50;


                if (button == 0 && type == GuiEvents.CLICK) {
                    if (HoveringUtil.isHovering(gradientX, gradientY + gradientHeight + 3, (gradientWidth + 10), 6, mouseX, mouseY)) {
                        draggingNumber = setting;
                        hueFlag = true;
                    }
                    if (HoveringUtil.isHovering(gradientX, gradientY, gradientWidth, gradientHeight, mouseX, mouseY)) {
                        draggingNumber = setting;
                        hueFlag = false;
                    }
                }

                if (draggingNumber != null && draggingNumber.equals(setting)) {
                    if (hueFlag) {
                        colorSetting.setHue(Math.min(1, Math.max(0, (mouseX - gradientX) / (gradientWidth + 10))));
                    } else {
                        colorSetting.setBrightness(Math.min(1, Math.max(0, 1 - ((mouseY - gradientY) / gradientHeight))));
                        colorSetting.setSaturation(Math.min(1, Math.max(0, (mouseX - gradientX) / gradientWidth)));
                    }
                }


                //Picker gradients
                Gui.drawRect2(gradientX, gradientY, gradientWidth, gradientHeight, Color.getHSBColor(hsb[0], 1, 1).getRGB());
                Gui.drawGradientRectSideways2(gradientX, gradientY, gradientWidth, gradientHeight, Color.getHSBColor(hsb[0], 0, 1).getRGB(),
                        ColorUtil.applyOpacity(Color.getHSBColor(hsb[0], 0, 1).getRGB(), 0));

                Gui.drawGradientRect2(gradientX, gradientY, gradientWidth, gradientHeight,
                        ColorUtil.applyOpacity(Color.getHSBColor(hsb[0], 1, 0).getRGB(), 0), Color.getHSBColor(hsb[0], 1, 0).getRGB());

                float pickerY = gradientY + (gradientHeight * (1 - hsb[2]));
                float pickerX = gradientX + (gradientWidth * hsb[1] - 1);
                pickerY = Math.max(Math.min(gradientY + gradientHeight - 2, pickerY), gradientY);
                pickerX = Math.max(Math.min(gradientX + gradientWidth - 2, pickerX), gradientX);

                GL11.glEnable(GL11.GL_BLEND);
                RenderUtil.color(-1);
                mc.getTextureManager().bindTexture(new ResourceLocation("Tenacity/colorpicker2.png"));
                Gui.drawModalRectWithCustomSizedTexture(pickerX, pickerY, 0, 0, 4, 4, 4, 4);


                GlStateManager.color(1, 1, 1, 1);
                Gui.drawRect2(gradientX + gradientWidth + 5, gradientY, 5, gradientHeight, colorSetting.getColor().getRGB());


                // Hue bar
                RenderUtil.color(-1);
                mc.getTextureManager().bindTexture(new ResourceLocation("Tenacity/hue.png"));
                Gui.drawModalRectWithCustomSizedTexture(gradientX, gradientY + gradientHeight + 4.5f, 0, 0, gradientWidth + 10, 4, gradientWidth + 10, 4);
                GlStateManager.color(1, 1, 1, 1);

                //Hue slider
                Gui.drawRect2(gradientX + ((gradientWidth + 10) * hsb[0]), gradientY + gradientHeight + 3.5, 1, 6, -1);


                lithiumFont14.drawString("Rainbow: ", gradientX, gradientY + gradientHeight + 14, -1);

                String text = colorSetting.isRainbow() ? "Enabled" : "Disabled";
                float textX = gradientX + lithiumFont14.getStringWidth("Rainbow: ") + 4;
                float textY = gradientY + gradientHeight + 13;

                boolean hoveringRound = HoveringUtil.isHovering(textX, textY, lithiumFont14.getStringWidth(text) + 4, lithiumFont14.getHeight() + 2, mouseX, mouseY);

                if (type == GuiEvents.CLICK && hoveringRound && button == 0) {
                    colorSetting.setRainbow(!colorSetting.isRainbow());
                }
                Color roundColor = colorSetting.isRainbow() ? new Color(0, 203, 33) : new Color(203, 0, 33);
                RoundedUtil.drawRound(textX, textY, lithiumFont14.getStringWidth(text) + 4, lithiumFont14.getHeight() + 2, 3,
                        hoveringRound ? roundColor.brighter() : roundColor);

                lithiumFont14.drawCenteredString(text, textX + (lithiumFont14.getStringWidth(text) + 4) / 2f,
                        textY + lithiumFont14.getMiddleOfBox(lithiumFont14.getHeight() + 2), -1);


                if (colorSetting.isRainbow()) {

                    Gui.drawGradientRectSideways2(gradientX, textY + 12, gradientWidth + 10, 4, Color.WHITE.getRGB(), Color.RED.getRGB());

                    Gui.drawRect2(gradientX + ((gradientWidth + 10) * hsb[1]), textY + 11, 1, 6, Color.BLACK.getRGB());

                    boolean hoveringSat = HoveringUtil.isHovering(gradientX, textY + 10, gradientWidth + 10, 6, mouseX, mouseY);

                    if (type == GuiEvents.CLICK && hoveringSat && button == 0) {
                        saturationFlag = true;
                        draggingNumber = setting;
                    }

                    if (type == GuiEvents.RELEASE && saturationFlag) {
                        saturationFlag = false;
                    }

                    if (saturationFlag) {
                        colorSetting.getRainbow().setSaturation(Math.min(1, Math.max(0, (mouseX - gradientX) / (gradientWidth + 10))));
                    }

                }


                count += 3.5 + (colorSetting.isRainbow() ? .5f : 0);
            }
            if (setting instanceof NumberSetting) {
                NumberSetting numberSetting = (NumberSetting) setting;
                lithiumFont16.drawString(setting.name, x + 13, settingY + 2, -1);

                if (type == GuiEvents.CLICK && isHoveringSetting && button == 0) {
                    draggingNumber = numberSetting;
                }

                double currentValue = numberSetting.getValue();


                if (draggingNumber != null && draggingNumber == setting) {
                    float percent = Math.min(1, Math.max(0, (mouseX - (x + 14)) / 108));
                    double newValue = (percent * (numberSetting.getMaxValue() - numberSetting.getMinValue())) + numberSetting.getMinValue();
                    numberSetting.setValue(newValue);
                }

                String value = Double.toString(MathUtils.round(currentValue, 2));
                lithiumFont16.drawString(value, x + 120 - lithiumFont16.getStringWidth(value), settingY + 2, -1);

                float sliderMath = (float) (((currentValue) - numberSetting.getMinValue())
                        / (numberSetting.getMaxValue() - numberSetting.getMinValue()));


                RoundedUtil.drawRound(x + 12, settingY + 13, 110, 5, 2, new Color(30, 31, 35));


                Color sliderColor = colors.getFirst();
                float[] hsb = Color.RGBtoHSB(sliderColor.getRed(), sliderColor.getGreen(), sliderColor.getBlue(), null);
                float saturation = hsb[1] * sliderMath;

                float totalSliderWidth = 108;

                sliderMap.put(numberSetting, (float) RenderUtil.animate(totalSliderWidth * sliderMath, sliderMap.get(numberSetting), .1));


                RoundedUtil.drawRound(x + 13, settingY + 14, Math.max(4, sliderMap.get(numberSetting)), 3, 1.5f, new Color(Color.HSBtoRGB(hsb[0], saturation, hsb[2])));

                count += .5;
            }
            if (setting instanceof StringSetting) {
                StringSetting stringSetting = (StringSetting) setting;

                // RenderUtil.renderRoundedRect(x + 13, settingY - 3, 115, 40, 3, new Color(30, 31, 35).getRGB());
                lithiumFont16.drawString(setting.name, x + 16, settingY + 1, -1);

                TextField textField = textFieldMap.get(stringSetting);

                textField.setBackgroundText("Type here...");
                textField.setXPosition(x + 17);
                textField.setYPosition(settingY + 12);
                textField.setWidth(100);
                textField.setHeight(15);
                textField.setOutline(new Color(68, 71, 78));
                textField.setFill(new Color(30, 31, 35));

                stringSetting.setString(textField.getText());

                if (type == GuiEvents.CLICK) textField.mouseClicked(mouseX, mouseY, button);

                if (!typing) {
                    typing = textField.isFocused();
                }


                textField.drawTextBox();

                count += .5f;
            }
            if (setting instanceof MultipleBoolSetting) {
                Animation animation = multiBoolSettingMap.get(setting);
                MultipleBoolSetting multiBoolSetting = (MultipleBoolSetting) setting;

                if (type == GuiEvents.CLICK && isHoveringSetting && button == 1) {
                    multiBoolSettingClickMap.put(setting, !multiBoolSettingClickMap.get(setting));
                }

                animation.setDirection(multiBoolSettingClickMap.get(setting) ? Direction.FORWARDS : Direction.BACKWARDS);


                float math = (multiBoolSetting.getBoolSettings().size()) * 15;

                float adjustment = (float) (math * animation.getOutput().floatValue());
                RenderUtil.renderRoundedRect(x + 11.5f, settingY - 4.5f, 118, 19 + adjustment, 4, new Color(68, 71, 78).getRGB());
                RenderUtil.renderRoundedRect(x + 13, settingY - 3, 115, 16 + adjustment, 3, new Color(30, 31, 35).getRGB());
                lithiumFont16.drawCenteredString(multiBoolSetting.name, x + 70.5f, settingY - 3 + 16 / 2f - lithiumFont16.getHeight() / 2f, -1);

                int boolCount = 1;
                for (BooleanSetting booleanSetting : multiBoolSetting.getBoolSettings()) {
                    Animation boolAnimation = boolHoverAnimation.get(multiBoolSetting).get(booleanSetting);
                    boolean isHoveringBool = animation.getDirection().equals(Direction.FORWARDS) &&
                            HoveringUtil.isHovering(x + 17, settingY + (boolCount * 15),
                                    (float) lithiumFont16.getStringWidth(booleanSetting.name) + 13, 11, mouseX, mouseY);

                    if (type == GuiEvents.CLICK && button == 0 && isHoveringBool) {
                        multiBoolSetting.getSetting(booleanSetting.name).toggle();
                    }

                    boolAnimation.setDirection(isHoveringBool ? Direction.FORWARDS : Direction.BACKWARDS);

                    Color boolColor = booleanSetting.isEnabled() ? colors.getSecond() : new Color(128, 134, 141);
                    int colorInterpolate = ColorUtil.interpolateColor(boolColor,
                            boolColor.brighter(), boolAnimation.getOutput().floatValue());

                    GlStateManager.color(1, 1, 1, 1);

                    RenderUtil.drawGoodCircle(x + 20, (float) (settingY + 5 + ((boolCount * 15) * animation.getOutput().floatValue())), 2,
                            ColorUtil.interpolateColor(new Color(40, 40, 40, 10), new Color(colorInterpolate), (float) animation.getOutput().floatValue()));

                    GlStateManager.color(1, 1, 1, 1);
                    lithiumFont16.drawString(booleanSetting.name, x + 28, (float) (settingY + 2 + ((boolCount * 15) * animation.getOutput().floatValue())),
                            ColorUtil.interpolateColor(new Color(40, 40, 40, 10), new Color(colorInterpolate), (float) animation.getOutput().floatValue()));
                    boolCount++;

                }


                count += ((math / settingHeight) * animation.getOutput().floatValue());
            }
            count++;

        }
        maxScroll = count * settingHeight;
    }

}
