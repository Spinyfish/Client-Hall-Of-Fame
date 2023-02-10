/**
 * @project Myth
 * @author CodeMan
 * @at 05.08.22, 14:11
 */
package dev.myth.features.display;

import com.sun.javafx.scene.control.SizeLimitedList;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.event.EventState;
import dev.myth.api.utils.MovementUtil;
import dev.myth.api.utils.StringUtil;
import dev.myth.api.utils.font.CFontRenderer;
import dev.myth.api.utils.font.FontLoaders;
import dev.myth.api.utils.math.MathUtil;
import dev.myth.api.utils.render.ColorUtil;
import dev.myth.api.utils.render.Rect;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.api.utils.render.StencilUtil;
import dev.myth.api.utils.render.shader.list.BlurShader;
import dev.myth.api.utils.render.shader.list.CoolShader;
import dev.myth.api.utils.render.shader.list.DropShadowUtil;
import dev.myth.api.utils.render.shader.list.KawaseBlurShader;
import dev.myth.events.Render2DEvent;
import dev.myth.api.feature.Feature;
import dev.myth.events.UpdateEvent;
import dev.myth.features.visual.BlurFeature;
import dev.myth.features.visual.GlowFeature;
import dev.myth.features.visual.ShaderFeature;
import dev.myth.features.visual.ShadowFeature;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import dev.myth.managers.ShaderManager;
import dev.myth.settings.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Feature.Info(
        name = "HUD",
        description = "Displays the HUD",
        category = Feature.Category.DISPLAY
)
public class HUDFeature extends Feature {

    public final EnumSetting<WatermarkMode> watermarkMode = new EnumSetting<>("Watermark", WatermarkMode.OLD_MOON);
    public final EnumSetting<ArrayListMode> arrayListMode = new EnumSetting<>("ArrayList", ArrayListMode.BASIC);
    public final ListSetting<OutlineAddons> outline = new ListSetting<>("Outline", OutlineAddons.TOP).addDependency(() -> arrayListMode.is(ArrayListMode.ROFL));
    public final EnumSetting<FontMode> fontMode = new EnumSetting<>("Font", FontMode.BOLD);
    public final ListSetting<HudAddons> hudSettings = new ListSetting<>("Hud Addons", HudAddons.BPS);
    public final BooleanSetting arrayListAnimation = new BooleanSetting("Animation", true).addDependency(() -> !arrayListMode.is(ArrayListMode.NONE));
    public final BooleanSetting background = new BooleanSetting("Background", true).addDependency(() -> arrayListMode.is(ArrayListMode.ROFL) || arrayListMode.is(ArrayListMode.BASIC));
    public final EnumSetting<Side> arraylistSide = new EnumSetting<>("Arraylist Side", Side.RIGHT).addDependency(() -> arrayListMode.is(ArrayListMode.BASIC));
    public final BooleanSetting line = new BooleanSetting("Line", true).addDependency(() -> arrayListMode.is(ArrayListMode.BASIC));
    public final BooleanSetting skipRender = new BooleanSetting("Skip Render", true);
    public final BooleanSetting watermarkBloom = new BooleanSetting("Watermark Bloom", true).addDependency(() -> watermarkMode.is(WatermarkMode.TEXT) || watermarkMode.is(WatermarkMode.JAPAN));
    public final ColorSetting simpleColor1 = new ColorSetting("Color 1", Color.CYAN).addDependency(() -> watermarkMode.is(WatermarkMode.SIMPLE) || watermarkMode.is(WatermarkMode.TEXT) || arrayListMode.is(ArrayListMode.BASIC));
    public final ColorSetting simpleColor2 = new ColorSetting("Color 2", Color.CYAN.darker()).addDependency(() -> watermarkMode.is(WatermarkMode.SIMPLE) || arrayListMode.is(ArrayListMode.BASIC));
    public final ColorSetting arrayListColor = new ColorSetting("Client Color", Color.CYAN.darker());
    public final TextBoxSetting clientName = new TextBoxSetting("Client Name", "Myth");

    private SizeLimitedList<Double> bpsList = new SizeLimitedList<>(20);
    private double bps;
    private BlurShader blurShader;

    @Handler
    public final Listener<Render2DEvent> render2DEventListener = event -> {


        final ScaledResolution sr = event.getScaledResolution();
        final FontRenderer fr = MC.fontRendererObj;

        if (blurShader == null) {
            blurShader = ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER;
        }

        this.drawWatermark();

        this.drawArrayList(fr, sr);

        if (this.hudSettings.isEnabled("BPS"))
            this.drawBPS(sr);

        this.drawInfo(sr);
    };

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        if(event.getState() != EventState.PRE) return;
        double currentSpeed = MovementUtil.getDist(getPlayer().posX, getPlayer().posZ, getPlayer().lastTickPosX, getPlayer().lastTickPosZ);
        bpsList.add(currentSpeed * MC.timer.timerSpeed);
        double total = 0;
        for(int i = 0; i < bpsList.size(); i++) {
            total += bpsList.get(i);
        }
        bps = MathUtil.round(currentSpeed * MC.timer.timerSpeed * MC.timer.ticksPerSecond, 0.01);
    };

    private void drawWatermark() {
        switch (watermarkMode.getValue()) {
            case OLD_MOON:
                drawOldMoonWatermark();
                break;
            case TEXT:
                drawTextWatermark();
                break;
            case SIMPLE:
                drawSimpleWatermark();
                break;
            case JAPAN:
                drawJapanWatermark();
                break;
            case MYTH:
                drawCooleMythWatermark();
                break;
        }
    }

    private void drawOldMoonWatermark() {
        final SimpleDateFormat format = new SimpleDateFormat("hh:mm aa");
        final String timeString = format.format(new Date());

        long currentPing = 0;
        if (MC.getCurrentServerData() != null && !MC.isSingleplayer()) {
            currentPing = MC.getCurrentServerData().pingToServer;
        }

        String name = clientName.getValue();
        int color = -1;
        if(Objects.equals(clientName.getValue(), "Exhibition")) {
            name = "§lE§r§fxhibition";
            color = ColorUtil.rainbow(0).getRGB();
        }

        drawString(name + " §7(" + timeString + ")", 2, 2, color);
        drawString("§7FPS: " + Minecraft.getDebugFPS() + " - MS: " + currentPing, 2, 2 + getFontHeight(), -1);
    }

    private void drawSimpleWatermark() {
        GlStateManager.disableBlend(); // Hi
        final String name = clientName.getValue() + ClientMain.INSTANCE.getVersion() + " - " + getPlayer().getName() + " - " + (!MC.isSingleplayer() ? MC.getCurrentServerData().serverIP : "SinglePlayer");
        Gui.drawGradientRectSideways(5, 5.5, 6 + getStringWidth(name), 7, simpleColor1.getColor(), simpleColor2.getColor());
        Gui.drawRect(5, 7, 6 + getStringWidth(name), 17, new Color(0, 0, 0, 130).getRGB());
        drawString(name, 5F, 8.5F, -1);
    }

    private void drawTextWatermark() {
        GlStateManager.disableBlend();
        if (watermarkBloom.getValue()) {
            DropShadowUtil.start();
            drawString(clientName.getValue(), 6, 6, arrayListColor.getColor());
            DropShadowUtil.stop(13, arrayListColor.getValue());
        }
        drawString(clientName.getValue(), 6, 6, arrayListColor.getColor());
    }

    private void drawJapanWatermark() {
        GlStateManager.disableBlend();
        if (watermarkBloom.getValue()) {
            DropShadowUtil.start();
            FontLoaders.SFUI_12.drawString(" ", 1, 0, -1); // Glow Bug Fix
            FontLoaders.JAPAN.drawString("M", 5, 12, arrayListColor.getColor());
            FontLoaders.JAPAN.drawString("yth", 23, 12, arrayListColor.getColor());
            DropShadowUtil.stop(13, arrayListColor.getValue());
        }
        FontLoaders.JAPAN.drawString("M", 5, 12, arrayListColor.getColor());
        FontLoaders.JAPAN.drawString("yth", 23, 12, arrayListColor.getColor());
    }

    private void drawArrayList(final FontRenderer fr, final ScaledResolution sr) {
        final BlurFeature blurFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(BlurFeature.class);
        final GlowFeature glowFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(GlowFeature.class);
        final ShadowFeature shadowFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(ShadowFeature.class);


        switch (arrayListMode.getValue()) {
            case ROFL:
                if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("ArrayList") || glowFeature.isEnabled() && glowFeature.modules.isEnabled("ArrayList") || shadowFeature.isEnabled() && shadowFeature.modules.isEnabled("ArrayList")) {
                    if (glowFeature.isEnabled() && glowFeature.modules.isEnabled("ArrayList") || shadowFeature.isEnabled() && shadowFeature.modules.isEnabled("ArrayList")) {
                        DropShadowUtil.start();
                        drawRoflArrayList(sr, true);
                        int radius = shadowFeature.isEnabled() ? 13 : glowFeature.glowRadius.getValue().intValue();
                        Color glowColor = shadowFeature.isEnabled() ? Color.BLACK : glowFeature.glowColor.getValue();
                        DropShadowUtil.stop(radius, glowColor);
                    }

                    if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("ArrayList")) {
                        ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.startBlur();
                        drawRoflArrayList(sr, true);
                        ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.stopBlur(blurFeature.blurSigma.getValue().intValue(), blurFeature.blurRadius.getValue().intValue(), 1);
                    }
                    drawRoflArrayList(sr, false);
                } else {
                    drawRoflArrayList(sr, true);
                }
                break;
            case MYTH:
                if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("ArrayList")) {
                    ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.startBlur();
                    drawCooleMythArrayList(sr, true);
                    ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.stopBlur(10, 10, 1);
                }

                drawCooleMythArrayList(sr, false);
                break;
            case BASIC:
                drawBasicArrayList(sr);
                break;
        }
    }

    private void drawCooleMythWatermark() {
        if (watermarkBloom.getValue()) {
            DropShadowUtil.start();
            FontLoaders.NOVO_FONT_35.drawString(clientName.getValue(), 6, 6, arrayListColor.getColor());
            DropShadowUtil.stop(5, arrayListColor.getValue());
        }

        FontLoaders.NOVO_FONT_35.drawStringWithShadow(clientName.getValue(), 4, 4, arrayListColor.getColor());
    }

    private void drawCooleMythArrayList(final ScaledResolution sr, final boolean rect) {
        final List<Feature> features = this.getManager(FeatureManager.class).getFeatures().values().stream()
                .filter(module -> (module.isEnabled() || module.getArrayListAnimation().isAlive()) && (!skipRender.getValue() || (module.getCategory() != Category.VISUAL && module.getCategory() != Category.DISPLAY)))
                .sorted(Comparator.comparing(feature -> -getStringWidth(feature.getDisplayName()))).collect(Collectors.toList());

        if (features.isEmpty()) return;

        final AtomicLong time = new AtomicLong(Minecraft.getSystemTime());
        final AtomicInteger index = new AtomicInteger(0);
        features.forEach(feature -> {
            final double height = FontLoaders.ARIMO.getHeight() + 3.5;

            final double extraRight = 2;
            final double extraWidth = 5;
            final double extraHeight = 5;

            feature.getArrayListAnimation().updateAnimation();
            if (rect)
                Gui.drawRect((float) ((sr.getScaledWidth() - getStringWidth(feature.getDisplayName()) - 2) + getStringWidth(feature.getDisplayName()) * feature.getArrayListAnimation().getValue()) - 1 - extraWidth - extraRight,
                        (float) (2 + (index.get() * height) - (feature.getArrayListAnimation().getValue() * height)) - 0.5 + extraHeight,
                        (float) (sr.getScaledWidth() - 2) + 1 - extraWidth,
                        (float) (2 + (index.get() * height) + (height + 1) - (feature.getArrayListAnimation().getValue() * height)) - 1.5 + extraHeight,
                        new Color(0, 0, 0, 130).getRGB());
            RenderUtil.drawRoundedRectWithXY((float) (sr.getScaledWidth() - 2) + 1 - (extraWidth / 2.0D) - 3.5F,
                    (float) (2 + (index.get() * height) - (feature.getArrayListAnimation().getValue() * height)) - 0.5 + extraHeight + 0.5D,
                    (float) (sr.getScaledWidth() - 2) + 1 - extraWidth + 1,
                    (float) (2 + (index.get() * height) + (height + 1) - (feature.getArrayListAnimation().getValue() * height)) - 1.5 + extraHeight - 0.5D,
                    10, getColor(time.get()), 10, 10);
            drawString(feature.getDisplayName(), (float) ((float) ((sr.getScaledWidth() - getStringWidth(feature.getDisplayName()) - 2) + getStringWidth(feature.getDisplayName()) * feature.getArrayListAnimation().getValue()) - extraWidth - extraRight), (float) ((float) (2 + (index.get() * height) - (feature.getArrayListAnimation().getValue() * height)) + 1.5F + extraHeight), ColorUtil.getGradientByTime(simpleColor1.getValue(), simpleColor2.getValue(), -index.get() / 10F).getRGB());
            index.getAndIncrement();
            time.set(time.get() - 300);
        });
    }

    private void drawBasicArrayList(final ScaledResolution sr) {
        final List<Feature> features = this.getManager(FeatureManager.class).getFeatures().values().stream()
                .filter(module -> (module.isEnabled() || module.getArrayListAnimation().isAlive()) && (!skipRender.getValue() || (module.getCategory() != Category.VISUAL && module.getCategory() != Category.DISPLAY)))
                .sorted(Comparator.comparing(feature -> -getStringWidth(feature.getDisplayName()))).collect(Collectors.toList());

        if (features.isEmpty()) return;
        float x;
        double stringWidth;
        double fontHeight = getFontHeight();
        boolean background = this.background.getValue(), line = this.line.getValue();
        boolean right = arraylistSide.getValue() == Side.RIGHT;
        int defaultYOffset = 0;
        if(!right) {
            if(!watermarkMode.is(WatermarkMode.NONE)) defaultYOffset += 20;
        }
        int yOffset = defaultYOffset;

        if (background) {
//            blurShader.startBlur();
            for (Feature feature : features) {
                feature.getArrayListAnimation().updateAnimation();
                stringWidth = getStringWidth(feature.getDisplayName());
                if (right) {
                    x = (float) (((sr.getScaledWidth() - stringWidth) - 2) + stringWidth * feature.getArrayListAnimation().getValue());
                } else {
                    x = (float) -(feature.getArrayListAnimation().getValue() * stringWidth);
                }

                Rect rect = new Rect((int) (x - 2), 2 + yOffset, getStringWidth(feature.getDisplayName()) + 6, (int) fontHeight);

                if(ShaderFeature.INSTANCE.isEnabled() && ShaderFeature.INSTANCE.blur.getValue())
                    ShaderFeature.INSTANCE.blurArea(rect);
                else
                    RenderUtil.drawRect(x - 2, 2 + yOffset, getStringWidth(feature.getDisplayName()) + 6, fontHeight, 0x50000000);

                if(ShaderFeature.INSTANCE.isEnabled() && ShaderFeature.INSTANCE.glow.getValue()) {
                    ShaderFeature.INSTANCE.glowArea(rect);
                }

                yOffset += (int) (fontHeight - (feature.getArrayListAnimation().getValue() * fontHeight));
            }
//            blurShader.stopBlur(15, 30, 1);
        }
//        final AtomicLong time = new AtomicLong(Minecraft.getSystemTime());
        int counter = 0;
        yOffset = defaultYOffset;
        int color;
        for (Feature feature : features) {
            if (!background) feature.getArrayListAnimation().updateAnimation();
            stringWidth = getStringWidth(feature.getDisplayName());
            if (right) {
                x = (float) (((sr.getScaledWidth() - stringWidth) - 2) + stringWidth * feature.getArrayListAnimation().getValue());
            } else {
                x = (float) -(feature.getArrayListAnimation().getValue() * stringWidth) + 2;
            }
//            color = getColor(time.get());
            color = ColorUtil.getGradientByTime(simpleColor1.getValue(), simpleColor2.getValue(), -counter / 10F).getRGB();
            drawString(feature.getDisplayName(), x, (float) (2 + yOffset), color);
            if(line) RenderUtil.drawRect(x + (right ? stringWidth + 1 : -2), (float) (2 + (yOffset)), 1, fontHeight, color);
            yOffset += (int) (fontHeight - (feature.getArrayListAnimation().getValue() * fontHeight));
//            time.set(time.get() - 300);
            counter++;
        }
    }

    private void drawRoflArrayList(final ScaledResolution sr, final Boolean rect) {
        final CFontRenderer fr = getFont();
        final List<Feature> features = this.getManager(FeatureManager.class).getFeatures().values().stream()
                .filter(module -> (module.isEnabled() || module.getArrayListAnimation().isAlive())
                        && (!skipRender.getValue() || (module.getCategory() != Category.VISUAL && module.getCategory() != Category.DISPLAY)))
                .sorted(Comparator.comparing(feature -> -getStringWidth(feature.getDisplayName()))).collect(Collectors.toList());

        /* Return if there are no features to render */
        if (features.isEmpty()) return;

        final AtomicLong time = new AtomicLong(Minecraft.getSystemTime());
        final AtomicReference<Float> yPos = new AtomicReference<>(3.0F);
        final AtomicInteger count = new AtomicInteger(1);

        features.forEach(feature -> {
            final int color = ColorUtil.getGradientByTime(simpleColor1.getValue(), simpleColor2.getValue(), -count.get() / 10F).getRGB();

            feature.getArrayListAnimation().updateAnimation();

            /* Background Rect */
            if (background.getValue() && rect)
                Gui.drawRect(sr.getScaledWidth() - getStringWidth(feature.getDisplayName()) - 4.0F, yPos.get() - 1.0F,
                        sr.getScaledWidth() - 1.5F, yPos.get() + fr.getStringHeight(feature.getDisplayName()) + 0.5F, new Color(0, 0, 0, 100).getRGB());

            /* Left Rect */
            if (outline.isEnabled("Left"))
                Gui.drawRect(sr.getScaledWidth() - getStringWidth(feature.getDisplayName()) - 5.0F, yPos.get() - 1.0F,
                        sr.getScaledWidth() - getStringWidth(feature.getDisplayName()) - 4.0F, yPos.get() + fr.getStringHeight(feature.getDisplayName()) + 0.5F, color);


            /* Top Rect */
            if (outline.isEnabled("Top"))
                if (features.indexOf(feature) == 0)
                    Gui.drawRect(sr.getScaledWidth() - getStringWidth(feature.getDisplayName()) - 5.0F, yPos.get() - 2.0F,
                            sr.getScaledWidth() - 0.5F, yPos.get() - 1.0F, color);

            /* Bottom Rect */
            if (outline.isEnabled("Bottom"))
                if (features.indexOf(feature) == (features.size() - 1))
                    Gui.drawRect(sr.getScaledWidth() - getStringWidth(feature.getDisplayName()) - 5.0F, yPos.get() + fr.getStringHeight(feature.getDisplayName()) + 0.5F,
                            sr.getScaledWidth() - 0.5F, yPos.get() + fr.getStringHeight(feature.getDisplayName()) + 1.5F, color);

            /* Right Line */
            if (outline.isEnabled("Right"))
                Gui.drawRect(sr.getScaledWidth() - 1.5F, yPos.get() - 1.0F,
                        sr.getScaledWidth() - 0.5F, yPos.get() + fr.getStringHeight(feature.getDisplayName()) + 0.5F, color);

            /* Bottom Line */
            if (outline.isEnabled("Bottom"))
                try {
                    final Feature nextfeature = features.get(features.indexOf(feature) + 1);

                    Gui.drawRect(sr.getScaledWidth() - getStringWidth(feature.getDisplayName()) - 5.0F, yPos.get() + fr.getStringHeight(nextfeature.getDisplayName()) + 0.5F,
                            sr.getScaledWidth() - getStringWidth(nextfeature.getDisplayName()) - 5.0F, yPos.get() + fr.getStringHeight(nextfeature.getDisplayName()) + 1.5F, color);
                } catch (Exception ignored) {
                }


            drawString(feature.getDisplayName(), (float) (sr.getScaledWidth() - getStringWidth(feature.getDisplayName()) - 3.0F + getStringWidth(feature.getDisplayName()) * feature.getArrayListAnimation().getValue()), (float) (yPos.get() - (feature.getArrayListAnimation().getValue() * fr.getStringHeight(feature.getDisplayName()))), color);
            count.set(count.get() + 1);
            yPos.set(yPos.get() + fr.getStringHeight(feature.getDisplayName()) + 1.5F);
            time.set(time.get() - 300);
        });
    }

    private void drawBPS(final ScaledResolution sr) {
        final float offset = MC.currentScreen instanceof GuiChat ? 15 : 0;

        drawString("BPS: " + bps, 2, sr.getScaledHeight() - getFontHeight() - offset, -1);
    }

    private void drawInfo(final ScaledResolution sr) {
        final String user = ClientMain.INSTANCE.getUsername();
        final int uid = ClientMain.INSTANCE.getUid();

        final String text = "§7" + user + " - " + uid;
        float offset = MC.currentScreen instanceof GuiChat ? 15 : 0;

        drawString(text, sr.getScaledWidth() - getStringWidth(text) - 2, sr.getScaledHeight() - getFontHeight() - offset, -1);

        offset += getFontHeight();

        if (MC.thePlayer.getActivePotionEffects().isEmpty()) return;

        ArrayList<String> effects = new ArrayList<>();

        for (PotionEffect potioneffect : MC.thePlayer.getActivePotionEffects()) {
            Potion potion = Potion.potionTypes[potioneffect.getPotionID()];

            String s1 = I18n.format(potion.getName());

            if (potioneffect.getAmplifier() == 1) {
                s1 = s1 + " " + I18n.format("enchantment.level.2");
            } else if (potioneffect.getAmplifier() == 2) {
                s1 = s1 + " " + I18n.format("enchantment.level.3");
            } else if (potioneffect.getAmplifier() == 3) {
                s1 = s1 + " " + I18n.format("enchantment.level.4");
            }
            String s = "§7" + s1 + " " + (potioneffect.getDuration() < 15 * 20 ? "§c" : potioneffect.getDuration() < 30 * 20 ? "§e" : "§7") + Potion.getDurationString(potioneffect);
            effects.add(s);
        }

        effects.sort(Comparator.comparing(s -> -getStringWidth(s)));

        for (String s : effects) {
            float x = sr.getScaledWidth() - getStringWidth(s) - 2;
            float y = sr.getScaledHeight() - getFontHeight() - offset;

            drawString(s, x, y, -1);
            offset += getFontHeight();
        }
    }

    public int getColor(final long delay) {
        return ColorUtil.getClientColor(delay);
    }

    public void drawString(final String text, final float x, final float y, final int color) {
        if (fontMode.is(FontMode.MINECRAFT)) {
            if (hudSettings.isEnabled("Text Shadow"))
                MC.fontRendererObj.drawStringWithShadow(hudSettings.isEnabled("Lowercase") ? text.toLowerCase(Locale.ROOT) : text, x, y, color);
            else
                MC.fontRendererObj.drawString(hudSettings.isEnabled("Lowercase") ? text.toLowerCase(Locale.ROOT) : text, (int) x, (int) y, color);
        } else {
            if (hudSettings.isEnabled("Text Shadow"))
                getFont().drawStringWithShadow(hudSettings.isEnabled("Lowercase") ? text.toLowerCase(Locale.ROOT) : text, x, y, color);
            else
                getFont().drawString(hudSettings.isEnabled("Lowercase") ? text.toLowerCase(Locale.ROOT) : text, x, y, color);
        }
    }

    public int getStringWidth(String text) {
        return fontMode.is(FontMode.MINECRAFT) ? MC.fontRendererObj.getStringWidth(text) : getFont().getStringWidth(text);
    }

    public int getFontHeight() {
        return fontMode.is(FontMode.MINECRAFT) ? MC.fontRendererObj.FONT_HEIGHT : getFont().getHeight();
    }

    public CFontRenderer getFont() {
        return fontMode.is(FontMode.BOLD) ? FontLoaders.BOLD : fontMode.is(FontMode.SAND) ? FontLoaders.QUICK_SAND : fontMode.is(FontMode.CHILL) ? FontLoaders.CHILL : fontMode.is(FontMode.FLUX) ? FontLoaders.FLUX : fontMode.is(FontMode.ARIMO) ? FontLoaders.ARIMO : FontLoaders.SFUI_19;
    }

    public enum WatermarkMode {
        NONE("None"),
        OLD_MOON("Old Moon"),
        TEXT("Text"),
        SIMPLE("Simple"),
        JAPAN("Japan"),
        MYTH("Myth");

        private final String name;

        WatermarkMode(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum ArrayListMode {
        NONE("None"),
        BASIC("Basic"),
        ROFL("Rofl"),
        MYTH("Myth");

        private final String name;

        ArrayListMode(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum OutlineAddons {
        LEFT("Left"),
        TOP("Top"),
        BOTTOM("Bottom"),
        RIGHT("Right");


        private final String name;

        OutlineAddons(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum HudAddons {
        BPS("BPS"),
        TEXT_SHADOW("Text Shadow"),
        LOWERCASE("Lowercase");

        private final String name;

        HudAddons(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum FontMode {
        MINECRAFT("Minecraft"),
        SAND("Sand"),
        BOLD("Bold"),
        CHILL("Chill"),
        NORMAL("Normal"),
        FLUX("Flux"),
        ARIMO("Arimo");

        private final String name;

        FontMode(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum Side {
        LEFT("Left"),
        RIGHT("Right");

        private final String name;

        Side(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

}
