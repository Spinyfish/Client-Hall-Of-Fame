/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.viaversion.viaversion.util.Pair
 *  org.lwjgl.opengl.GL11
 */
package wtf.monsoon.impl.module.hud;

import com.viaversion.viaversion.util.Pair;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.font.IFontRenderer;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.impl.event.EventRender2D;
import wtf.monsoon.impl.event.EventRenderScoreboard;
import wtf.monsoon.impl.module.hud.HUD;
import wtf.monsoon.impl.module.visual.Accent;

public class HUDArrayList
extends Module {
    public static Setting<Background> arrayListBackground = new Setting<Background>("Background", Background.NONE).describedBy("How to draw the ArrayList's background");
    public static Setting<HUD.BasicTextElement> arrayListMetaData = new Setting<HUD.BasicTextElement>("Metadata", HUD.BasicTextElement.DASH).describedBy("How to draw the module metadata");
    public static Setting<AnimationEnum> arrayListAnimation = new Setting<AnimationEnum>("Animation", AnimationEnum.SLIDE).describedBy("How to draw the module animations");
    public static Setting<Float> elementHeight = new Setting<Float>("Height", Float.valueOf(12.0f)).minimum(Float.valueOf(11.0f)).maximum(Float.valueOf(15.0f)).incrementation(Float.valueOf(1.0f)).describedBy("The height of each element");
    public static Setting<Boolean> arrayListTextShadow = new Setting<Boolean>("Text Shadow", true).describedBy("Whether to draw the text shadow");
    public static Setting<Boolean> arrayListIgnoreVisuals = new Setting<Boolean>("Ignore Visuals", true).describedBy("Whether to ignore visual modules");
    public static Setting<Outline> arrayListOutline = new Setting<Outline>("Outline", Outline.NONE).describedBy("How to outline the arraylist");
    public static Setting<Float> arrayListOpacity = new Setting<Float>("Opacity", Float.valueOf(100.0f)).minimum(Float.valueOf(1.0f)).maximum(Float.valueOf(255.0f)).incrementation(Float.valueOf(1.0f)).describedBy("The opacity of the background").visibleWhen(() -> arrayListBackground.getValue() == Background.CHILL || arrayListBackground.getValue() == Background.AMBIENT);
    public static Setting<EnumFont> font = new Setting<EnumFont>("Font", EnumFont.PRODUCT_SANS).describedBy("What font to use");
    public static Setting<Boolean> lowercase = new Setting<Boolean>("Lowercase", false).describedBy("Make the text forced lowercase");
    private float arrayListHeight = 0.0f;
    @EventLink
    private final Listener<EventRender2D> render2DListener = event -> {
        if (!this.mc.gameSettings.showDebugInfo) {
            float topRightOffset = 4.0f;
            List sortedModules = Wrapper.getMonsoon().getModuleManager().getModules().stream().filter(module -> arrayListAnimation.getValue() == AnimationEnum.NONE ? module.isEnabled() : module.getAnimation().getAnimationFactor() > 0.0).filter(module -> arrayListIgnoreVisuals.getValue() == false || module.getCategory() != Category.VISUAL && module.getCategory() != Category.HUD).filter(module -> !module.isDuplicate()).filter(module -> module.isVisible()).sorted(Comparator.comparingDouble(module -> ((Float)this.generateModuleDataAndWidth((Module)module).key()).floatValue()).reversed()).collect(Collectors.toList());
            int index = 0;
            for (Module module2 : sortedModules) {
                GL11.glPushMatrix();
                Pair<Float, String> data = this.generateModuleDataAndWidth(module2);
                float x = (float)event.getSr().getScaledWidth() - ((Float)data.key()).floatValue() - 8.0f;
                float height = elementHeight.getValue().floatValue();
                float animationFactor = (float)(arrayListAnimation.getValue() == AnimationEnum.NONE ? 1.0 : module2.getAnimation().getAnimationFactor());
                Color colour = this.generateColour(index);
                Color nextColour = this.generateColour(index + 1);
                boolean scissored = false;
                switch (arrayListBackground.getValue()) {
                    case CHILL: {
                        RenderUtil.rect(x, topRightOffset, ((Float)data.key()).floatValue() + 4.0f, height * animationFactor, new Color(0, 0, 0, arrayListOpacity.getValue().intValue()));
                        break;
                    }
                    case AMBIENT: {
                        RenderUtil.verticalGradient(x, topRightOffset, ((Float)data.key()).floatValue() + 4.0f, height * animationFactor, ColorUtil.integrateAlpha(colour, arrayListOpacity.getValue().intValue()), ColorUtil.integrateAlpha(nextColour, arrayListOpacity.getValue().intValue()));
                    }
                }
                switch (arrayListAnimation.getValue()) {
                    case SLIDE: {
                        if (module2.getAnimation().getAnimationFactor() != 1.0) {
                            RenderUtil.pushScissor(x, topRightOffset, ((Float)data.key()).floatValue() + 4.0f, height);
                            scissored = true;
                        }
                        GL11.glTranslated((double)((((Float)data.key()).floatValue() + 4.0f) * (1.0f - animationFactor)), (double)0.0, (double)0.0);
                        break;
                    }
                    case SCALE: {
                        RenderUtil.scale(x, topRightOffset, new float[]{1.0f, animationFactor});
                        break;
                    }
                    case SCISSOR: {
                        if (module2.getAnimation().getAnimationFactor() == 1.0) break;
                        RenderUtil.pushScissor(x, topRightOffset, ((Float)data.key()).floatValue() + 4.0f, height * animationFactor);
                        scissored = true;
                    }
                }
                HUDArrayList.font.getValue().font.drawString((String)data.value(), x + 2.0f, topRightOffset + (height / 2.0f - (float)HUDArrayList.font.getValue().font.getHeight() / 2.0f) - (font.getValue() == EnumFont.MINECRAFT ? 0.0f : 1.0f), arrayListBackground.getValue() == Background.AMBIENT ? Color.WHITE : colour, (boolean)arrayListTextShadow.getValue());
                if (scissored) {
                    RenderUtil.popScissor();
                }
                GL11.glPopMatrix();
                switch (arrayListOutline.getValue()) {
                    case TOP: {
                        if (index != 0) break;
                        RenderUtil.rect(x, topRightOffset, ((Float)data.key()).floatValue() + 4.0f, 1.0f, colour);
                        break;
                    }
                    case RIGHT: {
                        RenderUtil.verticalGradient(x + ((Float)data.key()).floatValue() + 4.0f, topRightOffset, 1.0f, height, colour, nextColour);
                        break;
                    }
                    case TOP_RIGHT: {
                        RenderUtil.verticalGradient(x + ((Float)data.key()).floatValue() + 4.0f, topRightOffset, 1.0f, height, colour, nextColour);
                        if (index != 0) break;
                        RenderUtil.rect(x, topRightOffset - 1.0f, ((Float)data.key()).floatValue() + 5.0f, 1.0f, colour);
                        break;
                    }
                    case LEFT: {
                        RenderUtil.verticalGradient(x - 1.0f, topRightOffset, 1.0f, height * animationFactor, colour, nextColour);
                        break;
                    }
                    case FULL: {
                        RenderUtil.verticalGradient(x - 1.0f, topRightOffset, 1.0f, height * animationFactor, colour, nextColour);
                        RenderUtil.verticalGradient(x + ((Float)data.key()).floatValue() + 4.0f, topRightOffset, 1.0f, height, colour, nextColour);
                        if (index == 0) {
                            RenderUtil.rect(x - 1.0f, topRightOffset - 1.0f, ((Float)data.key()).floatValue() + 6.0f, 1.0f, colour);
                        } else if (index == sortedModules.size() - 1) {
                            RenderUtil.rect(x - 1.0f, topRightOffset + height, ((Float)data.key()).floatValue() + 6.0f, 1.0f, nextColour);
                        }
                        if (index == sortedModules.size() - 1) break;
                        RenderUtil.verticalGradient(x - 1.0f, (float)((double)topRightOffset + (double)height * module2.getAnimation().getAnimationFactor()), ((Float)data.key()).floatValue() - ((Float)this.generateModuleDataAndWidth((Module)sortedModules.get(index + 1)).key()).floatValue(), 1.0f, colour, nextColour);
                    }
                }
                topRightOffset += elementHeight.getValue().floatValue() * animationFactor;
                if (index >= sortedModules.size()) {
                    // empty if block
                }
                ++index;
            }
            this.arrayListHeight = 4.0f + elementHeight.getValue().floatValue() * (float)(index + 2);
        }
    };
    @EventLink
    private final Listener<EventRenderScoreboard> eventRenderScoreboardListener = e -> {
        if ((float)e.getY() <= this.arrayListHeight) {
            return;
        }
    };

    public HUDArrayList() {
        super("Array List", "yea", Category.HUD);
    }

    private Pair<Float, String> generateModuleDataAndWidth(Module module) {
        StringBuilder text = new StringBuilder().append(module.getName());
        if (!module.getMetaData().equals("")) {
            switch (arrayListMetaData.getValue()) {
                case SIMPLE: {
                    text.append(" ").append((Object)EnumChatFormatting.GRAY).append(module.getMetaData());
                    break;
                }
                case SQUARE: {
                    text.append(" ").append((Object)EnumChatFormatting.GRAY).append("[").append((Object)EnumChatFormatting.WHITE).append(module.getMetaData()).append((Object)EnumChatFormatting.GRAY).append("]");
                    break;
                }
                case DASH: {
                    text.append(" ").append((Object)EnumChatFormatting.GRAY).append("- ").append(module.getMetaData());
                }
            }
        }
        String finalText = text.toString();
        if (lowercase.getValue().booleanValue()) {
            finalText = finalText.toLowerCase(Locale.getDefault());
        }
        return new Pair((Object)Float.valueOf(HUDArrayList.font.getValue().font.getStringWidthF(finalText)), (Object)finalText);
    }

    private Color generateColour(int index) {
        Color combined = ColorUtil.fadeBetween(5, index * 15, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]);
        Accent.EnumAccents enumeration = Wrapper.getModule(Accent.class).accents.getValue();
        if (enumeration.equals((Object)Accent.EnumAccents.ASTOLFO)) {
            combined = ColorUtil.astolfoColorsC(index * 5, index * 20);
        } else if (enumeration.equals((Object)Accent.EnumAccents.RAINBOW)) {
            combined = ColorUtil.rainbow((long)index * 300L);
        } else if (enumeration.equals((Object)Accent.EnumAccents.EXHIBITION)) {
            combined = ColorUtil.exhibition((long)index * 300L);
        }
        return combined;
    }

    public static enum AnimationEnum {
        SCISSOR,
        SLIDE,
        SCALE,
        NONE;

    }

    public static enum Background {
        CHILL,
        AMBIENT,
        NONE;

    }

    static enum Outline {
        TOP,
        RIGHT,
        TOP_RIGHT,
        LEFT,
        FULL,
        NONE;

    }

    static enum EnumFont {
        PRODUCT_SANS("Product Sans", Wrapper.getFontUtil().productSans, 2, 3),
        PRODUCT_SANS_MEDIUM("Product Sans Medium", Wrapper.getFontUtil().productSansMedium, 2, 3),
        PRODUCT_SANS_BOLD("Product Sans Bold", Wrapper.getFontUtil().productSansBold, 8, 2),
        PRODUCT_SANS_SMALL("Product Sans Small", Wrapper.getFontUtil().productSansSmall, 2, 1),
        PRODUCT_SANS_SMALL_BOLD("Product Sans Small Bold", Wrapper.getFontUtil().productSansSmallBold, 2, 1),
        COMIC_SANS("Comic Sans", Wrapper.getFontUtil().comicSans, 2, 3),
        COMIC_SANS_MEDIUM("Comic Sans Medium", Wrapper.getFontUtil().comicSansMedium, 2, 3),
        COMIC_SANS_SMALL("Comic Sans Small", Wrapper.getFontUtil().comicSansSmall, 2, 1),
        COMIC_SANS_BOLD("Comic Sans Bold", Wrapper.getFontUtil().comicSansBold, 8, 2),
        COMIC_SANS_MEDIUM_BOLD("Comic Sans Mediun Bold", Wrapper.getFontUtil().comicSansMediumBold, 8, 2),
        COMIC_SANS_SMALL_BOLD("Comic Sans Small Bold", Wrapper.getFontUtil().comicSansSmallBold, 2, 1),
        GREYCLIFF("Greycliff", Wrapper.getFontUtil().greycliff19, 2, 1),
        MINECRAFT("Minecraftia", Minecraft.getMinecraft().fontRendererObj, 4, 2),
        UW_U("uwu", Wrapper.getFontUtil().ubuntuwu, 2, 3),
        UW_U_MEDIUM("uwu medium", Wrapper.getFontUtil().ubuntuwuMedium, 2, 3),
        UW_U_SMALL("uwu smawl", Wrapper.getFontUtil().ubuntuwuSmall, 2, 1);

        String fontName;
        IFontRenderer font;
        int xOffset;
        int yOffset;

        public String toString() {
            return this.fontName;
        }

        private EnumFont(String fontName, IFontRenderer font, int xOffset, int yOffset) {
            this.fontName = fontName;
            this.font = font;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }
}

