package dev.tenacity.module.impl.render;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.render.Render2DEvent;
import dev.tenacity.event.impl.render.ShaderEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.ModuleCollection;
import dev.tenacity.module.settings.ParentAttribute;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.MultipleBoolSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.font.AbstractFontRenderer;
import dev.tenacity.utils.objects.Dragging;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.tuples.Pair;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.StringUtils;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class ArrayListMod extends Module {

    public final BooleanSetting importantModules = new BooleanSetting("Important", false);
    private final ModeSetting textShadow = new ModeSetting("Text Shadow", "Black", "Colored", "Black", "None");
    private final ModeSetting rectangle = new ModeSetting("Rectangle", "Top", "None", "Top", "Side", "Outline");
    private final BooleanSetting partialGlow = new BooleanSetting("Partial Glow", true);
    private final BooleanSetting minecraftFont = new BooleanSetting("Minecraft Font", false);
    private final MultipleBoolSetting fontSettings = new MultipleBoolSetting("Font Settings",
            new BooleanSetting("Bold", false),
            new BooleanSetting("Small Font", false), minecraftFont);
    public final NumberSetting height = new NumberSetting("Height", 11, 20, 9, .5f);
    private final ModeSetting animation = new ModeSetting("Animation", "Scale in", "Move in", "Scale in");
    private final NumberSetting colorIndex = new NumberSetting("Color Seperation", 20, 100, 5, 1);
    private final NumberSetting colorSpeed = new NumberSetting("Color Speed", 15, 30, 2, 1);
    private final BooleanSetting background = new BooleanSetting("Background", true);
    private final BooleanSetting backgroundColor = new BooleanSetting("Background Color", false);
    private final NumberSetting backgroundAlpha = new NumberSetting("Background Alpha", .35, 1, 0, .01);

    public AbstractFontRenderer font = lithiumFont.size(20);
    public List<Module> modules;

    public ArrayListMod() {
        super("ArrayList", Category.RENDER, "Displays your active modules");
        addSettings(importantModules, rectangle, partialGlow, textShadow, fontSettings, height, animation,
                colorIndex, colorSpeed, background, backgroundColor, backgroundAlpha);
        backgroundAlpha.addParent(background, ParentAttribute.BOOLEAN_CONDITION);
        backgroundColor.addParent(background, ParentAttribute.BOOLEAN_CONDITION);
        partialGlow.addParent(rectangle, modeSetting -> !modeSetting.is("None"));
        if (!enabled) this.toggleSilent();
    }

    public void getModulesAndSort() {
        if (modules == null || ModuleCollection.reloadModules) {
            List<Class<? extends Module>> hiddenModules = Tenacity.INSTANCE.getModuleCollection().getHiddenModules();
            List<Module> moduleList = Tenacity.INSTANCE.getModuleCollection().getModules();
            moduleList.removeIf(module -> hiddenModules.stream().anyMatch(moduleClass -> moduleClass == module.getClass()));
            modules = moduleList;
        }
        modules.sort(Comparator.<Module>comparingDouble(m -> {
            String name = HUDMod.get(m.getName() + (m.hasMode() ? " " + m.getSuffix() : ""));
            return font.getStringWidth(applyText(name));
        }).reversed());
    }

    public Dragging arraylistDrag = Tenacity.INSTANCE.createDrag(this, "arraylist", 2, 1);

    public String longest = "";

    @Override
    public void onShaderEvent(ShaderEvent e) {
        if (modules == null) return;
        float yOffset = 0;
        ScaledResolution sr = new ScaledResolution(mc);
        int count = 0;
        for (Module module : modules) {
            if (importantModules.isEnabled() && module.getCategory() == Category.RENDER) continue;
            final Animation moduleAnimation = module.getAnimation();
            if (!module.isEnabled() && moduleAnimation.finished(Direction.BACKWARDS)) continue;

            String displayText = HUDMod.get(module.getName() + (module.hasMode() ? " §7" + module.getSuffix() : ""));
            displayText = applyText(displayText);
            float textWidth = font.getStringWidth(displayText);

            float xValue = sr.getScaledWidth() - (arraylistDrag.getX());

            boolean flip = xValue <= sr.getScaledWidth() / 2f;
            float x = flip ? xValue : sr.getScaledWidth() - (textWidth + arraylistDrag.getX());

            float y = yOffset + arraylistDrag.getY();

            float heightVal = height.getValue().floatValue() + 1;
            boolean scaleIn = false;
            switch (animation.getMode()) {
                case "Move in":
                    if (flip) {
                        x -= Math.abs((moduleAnimation.getOutput().floatValue() - 1) * (sr.getScaledWidth() - (arraylistDrag.getX() + textWidth)));
                    } else {
                        x += Math.abs((moduleAnimation.getOutput().floatValue() - 1) * (arraylistDrag.getX() + textWidth));
                    }
                    break;
                case "Scale in":
                    if (!moduleAnimation.isDone()) {
                        RenderUtil.scaleStart((float) (x + font.getStringWidth(displayText) / 2f), (float) (y + heightVal / 2 - font.getHeight() / 2f), (float) moduleAnimation.getOutput().floatValue());
                    }
                    scaleIn = true;
                    break;
            }


            int index = (int) (count * colorIndex.getValue());
            Pair<Color, Color> colors = HUDMod.getClientColors();

            Color textcolor = ColorUtil.interpolateColorsBackAndForth(colorSpeed.getValue().intValue(), index, colors.getFirst(), colors.getSecond(), false);

            if (HUDMod.isRainbowTheme()) {
                textcolor = ColorUtil.rainbow(colorSpeed.getValue().intValue(), index, HUDMod.color1.getRainbow().getSaturation(), 1, 1);
            }

            if (background.isEnabled()) {
                float offset = minecraftFont.isEnabled() ? 4 : 5;
                int rectColor = e.isBloom() ? Color.BLACK.getRGB() : (rectangle.getMode().equals("Outline") && partialGlow.isEnabled() ? textcolor.getRGB() : Color.BLACK.getRGB());


                Gui.drawRect2(x - 2, y, font.getStringWidth(displayText) + offset, heightVal,
                        scaleIn ? ColorUtil.applyOpacity(rectColor, moduleAnimation.getOutput().floatValue()) : rectColor);

                float offset2 = minecraftFont.isEnabled() ? 1 : 0;

                int rectangleColor = partialGlow.isEnabled() ? textcolor.getRGB() : Color.BLACK.getRGB();

                if (scaleIn) {
                    rectangleColor = ColorUtil.applyOpacity(rectangleColor, moduleAnimation.getOutput().floatValue());
                }

                switch (rectangle.getMode()) {
                    default:
                        break;
                    case "Top":
                        if (count == 0) {
                            Gui.drawRect2(x - 2, y - 1, textWidth + 5 - (offset2), 9, rectangleColor);
                        }
                        break;
                    case "Side":
                        if (flip) {
                            Gui.drawRect2(x - 3, y, 9, heightVal, textcolor.getRGB());
                        } else {
                            Gui.drawRect2(x + textWidth - 7, y, 9, heightVal, rectangleColor);
                        }
                        break;
                    case "Outline":
                        break;
                }
            }


            if (animation.is("Scale in") && !moduleAnimation.isDone()) {
                RenderUtil.scaleEnd();
            }

            yOffset += moduleAnimation.getOutput().floatValue() * heightVal;
            count++;
        }
    }

    Module lastModule;
    int lastCount;

    @Override
    public void onRender2DEvent(Render2DEvent e) {
        font = getFont();
        getModulesAndSort();

        String longestModule = "";
        float longestWidth = 0;
        double yOffset = 0;
        ScaledResolution sr = new ScaledResolution(mc);
        int count = 0;
        for (Module module : modules) {
            if (importantModules.isEnabled() && module.getCategory() == Category.RENDER) continue;
            final Animation moduleAnimation = module.getAnimation();

            moduleAnimation.setDirection(module.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);

            if (!module.isEnabled() && moduleAnimation.finished(Direction.BACKWARDS)) continue;


            String displayText = HUDMod.get(module.getName() + (module.hasMode() ? (module.getCategory().equals(Category.SCRIPTS) ? " §c" : " §7") + module.getSuffix() : ""));
            displayText = applyText(displayText);
            float textWidth = font.getStringWidth(displayText);

            if (textWidth > longestWidth) {
                longestModule = displayText;
                longestWidth = textWidth;
            }

            double xValue = sr.getScaledWidth() - (arraylistDrag.getX());


            boolean flip = xValue <= sr.getScaledWidth() / 2f;
            float x = (float) (flip ? xValue : sr.getScaledWidth() - (textWidth + arraylistDrag.getX()));


            float alphaAnimation = 1;

            float y = (float) (yOffset + arraylistDrag.getY());

            float heightVal = (float) (height.getValue() + 1);

            switch (animation.getMode()) {
                case "Move in":
                    if (flip) {
                        x -= Math.abs((moduleAnimation.getOutput().floatValue() - 1) * (sr.getScaledWidth() - (arraylistDrag.getX() - textWidth)));
                    } else {
                        x += Math.abs((moduleAnimation.getOutput().floatValue() - 1) * (arraylistDrag.getX() + textWidth));
                    }
                    break;
                case "Scale in":
                    if (!moduleAnimation.isDone()) {
                        RenderUtil.scaleStart(x + font.getStringWidth(displayText) / 2f, y + heightVal / 2 - font.getHeight() / 2f, (float) moduleAnimation.getOutput().floatValue());
                    }
                    alphaAnimation = (float) moduleAnimation.getOutput().floatValue();
                    break;
            }


            int index = (int) (count * colorIndex.getValue());
            Pair<Color, Color> colors = HUDMod.getClientColors();

            Color textcolor = ColorUtil.interpolateColorsBackAndForth(colorSpeed.getValue().intValue(), index, colors.getFirst(), colors.getSecond(), false);

            if (HUDMod.isRainbowTheme()) {
                textcolor = ColorUtil.rainbow(colorSpeed.getValue().intValue(), index, HUDMod.color1.getRainbow().getSaturation(), 1, 1);
            }


            if (background.isEnabled()) {
                float offset = minecraftFont.isEnabled() ? 4 : 5;
                Color color = backgroundColor.isEnabled() ? textcolor : new Color(10, 10, 10);
                Gui.drawRect2(x - 2, y, font.getStringWidth(displayText) + offset, heightVal,
                        ColorUtil.applyOpacity(color, backgroundAlpha.getValue().floatValue() * alphaAnimation).getRGB());
            }

            float offset = minecraftFont.isEnabled() ? 1 : 0;
            switch (rectangle.getMode()) {
                default:
                    break;
                case "Top":
                    if (count == 0) {
                        Gui.drawRect2(x - 2, y - 1, textWidth + 5 - offset, 1, textcolor.getRGB());
                    }
                    break;
                case "Side":
                    if (flip) {
                        Gui.drawRect2(x - 3, y, 1, heightVal, textcolor.getRGB());
                    } else {
                        Gui.drawRect2(x + textWidth + 2, y, 1, heightVal, textcolor.getRGB());
                    }
                    break;
                case "Outline":
                    if (count != 0) {
                        String modText = applyText(HUDMod.get(lastModule.getName() + (lastModule.hasMode() ? " " + lastModule.getSuffix() : "")));
                        float texWidth = font.getStringWidth(modText) - textWidth;
                        //Draws the difference of width rect and also the rect on the side of the text
                        if (flip) {
                            Gui.drawRect2(x + textWidth + 3, y, 1, heightVal, textcolor.getRGB());
                            Gui.drawRect2(x + textWidth + 3, y, texWidth + 1, 1, textcolor.getRGB());
                        } else {
                            Gui.drawRect2(x - (3 + texWidth), y, texWidth + 1, 1, textcolor.getRGB());
                            Gui.drawRect2(x - 3, y, 1, heightVal, textcolor.getRGB());
                        }
                        if (count == (lastCount - 1)) {
                            Gui.drawRect2(x - 3, y + heightVal, textWidth + 6, 1, textcolor.getRGB());
                        }
                    } else {
                        //Draws the rects for the first module in the count
                        if (flip) {
                            Gui.drawRect2(x + textWidth + 3, y, 1, heightVal, textcolor.getRGB());
                        } else {
                            Gui.drawRect2(x - 3, y, 1, heightVal, textcolor.getRGB());
                        }

                        //Top Bar rect
                        Gui.drawRect2(x - 3, y - 1, textWidth + 6, 1, textcolor.getRGB());
                    }
                    //sidebar
                    if (flip) {
                        Gui.drawRect2(x - 3, y, 1, heightVal, textcolor.getRGB());
                    } else {
                        Gui.drawRect2(x + textWidth + 2, y, 1, heightVal, textcolor.getRGB());
                    }


                    break;
            }


            float textYOffset = minecraftFont.isEnabled() ? .5f : 0;
            y += textYOffset;
            Color color = ColorUtil.applyOpacity(textcolor, alphaAnimation);
            switch (textShadow.getMode()) {
                case "None":
                    font.drawString(displayText, x, y + font.getMiddleOfBox(heightVal), color.getRGB());
                    break;
                case "Colored":
                    RenderUtil.resetColor();
                    font.drawString(StringUtils.stripColorCodes(displayText), x + 1, y + font.getMiddleOfBox(heightVal) + 1, ColorUtil.darker(color, .5f).getRGB());
                    RenderUtil.resetColor();
                    font.drawString(displayText, x, y + font.getMiddleOfBox(heightVal), color.getRGB());
                    break;
                case "Black":
                    RenderUtil.resetColor();
                    float f = minecraftFont.isEnabled() ? 1 : .5f;
                    font.drawString(StringUtils.stripColorCodes(displayText), x + f, y + font.getMiddleOfBox(heightVal) + f,
                            ColorUtil.applyOpacity(Color.BLACK, alphaAnimation));
                    RenderUtil.resetColor();
                    font.drawString(displayText, x, y + font.getMiddleOfBox(heightVal), color.getRGB());
                    break;
            }


            //  font.drawString(displayText, x, (y - 3) + font.getMiddleOfBox(heightVal), color.getRGB());

            if (animation.is("Scale in") && !moduleAnimation.isDone()) {
                RenderUtil.scaleEnd();
            }

            lastModule = module;

            yOffset += moduleAnimation.getOutput().floatValue() * heightVal;
            count++;
        }
        lastCount = count;
        longest = longestModule;
    }

    private String applyText(String text) {
        if (minecraftFont.isEnabled() && fontSettings.getSetting("Bold").isEnabled()) {
            return "§l" + text.replace("§7", "§7§l");
        }
        return text;
    }


    private AbstractFontRenderer getFont() {
        boolean smallFont = fontSettings.getSetting("Small Font").isEnabled();
        if (minecraftFont.isEnabled()) {
            return mc.fontRendererObj;
        }

        if (fontSettings.getSetting("Bold").isEnabled()) {
            if (smallFont) {
                return lithiumBoldFont18;
            }
            return lithiumBoldFont20;
        }

        return smallFont ? lithiumFont18 : lithiumFont20;
    }

}
