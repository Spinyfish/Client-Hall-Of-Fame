/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.hud;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.awt.Color;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.impl.event.EventRender2D;

public class HUD
extends Module {
    public static Setting<Watermark> watermark = new Setting<Watermark>("Watermark", Watermark.LOGO).describedBy("How to draw the watermark");
    public static Setting<BasicTextElement> coordinates = new Setting<BasicTextElement>("Coordinates", BasicTextElement.SIMPLE).describedBy("How to draw your coordinates");
    public static Setting<BasicTextElement> speed = new Setting<BasicTextElement>("Speed", BasicTextElement.SIMPLE).describedBy("How to draw your speed");
    public static Setting<BasicTextElement> build = new Setting<BasicTextElement>("Build", BasicTextElement.SIMPLE).describedBy("How to draw the client's build");
    public static Setting<BasicTextElement> uid = new Setting<BasicTextElement>("UID", BasicTextElement.SIMPLE).describedBy("How to draw your UID");
    public static Setting<BasicTextElement> time = new Setting<BasicTextElement>("Time", BasicTextElement.SIMPLE).describedBy("How to draw the time");
    public Setting<Object> hudModuleOptions = new Setting<Object>("Draggables", new Object()).describedBy("Settings for the draggable HUD modules");
    public Setting<Boolean> hudModuleOutline = new Setting<Boolean>("Outline", true).describedBy("Outline the HUD modules").childOf(this.hudModuleOptions);
    public Setting<Boolean> hudModuleShadow = new Setting<Boolean>("Shadow", false).describedBy("Draw a shadow around the HUD modules").childOf(this.hudModuleOptions);
    public Setting<Boolean> hudModuleBackground = new Setting<Boolean>("Background", true).describedBy("Draw a background on the HUD modules").childOf(this.hudModuleOptions);
    public Setting<Boolean> blur = new Setting<Boolean>("Blur", false).describedBy("Blurs various interfaces");
    public Setting<Integer> blurStrength = new Setting<Integer>("Intensity", 6).minimum(1).maximum(10).incrementation(1).describedBy("How intense the blur is").childOf(this.blur);
    @EventLink
    private final Listener<EventRender2D> render2DListener = event -> {
        if (!this.mc.gameSettings.showDebugInfo) {
            String text;
            float topLeftOffset = 2.0f;
            float bottomLeftOffset = (float)(event.getSr().getScaledHeight() - Wrapper.getFontUtil().productSans.getHeight()) - 2.0f;
            float bottomRightOffset = (float)(event.getSr().getScaledHeight() - Wrapper.getFontUtil().productSans.getHeight()) - 2.0f;
            switch (watermark.getValue()) {
                case CSGO: {
                    text = ((Object)((Object)EnumChatFormatting.WHITE) + "Monsoon" + (Object)((Object)EnumChatFormatting.RESET) + "sense " + (Object)((Object)EnumChatFormatting.WHITE) + Wrapper.getMonsoon().getVersion() + " - " + Wrapper.getMonsoonAccount().getUsername() + " - " + Minecraft.getDebugFPS() + " fps").toLowerCase();
                    float width = Wrapper.getFontUtil().productSansSmall.getStringWidth(text);
                    RenderUtil.rect(2.0f, topLeftOffset, width + 13.0f, 16.5f, new Color(40, 40, 40, 255));
                    RenderUtil.rect(4.0f, topLeftOffset += 2.0f, width + 9.0f, 12.5f, new Color(15, 15, 15, 255));
                    Wrapper.getFontUtil().productSansSmallBold.drawString(text, 6.0f, topLeftOffset += 1.0f, new Color(this.getColor()), false);
                    topLeftOffset += 10.5f;
                    int i = 0;
                    while ((float)i < width + 9.0f) {
                        RenderUtil.rect(4 + i, topLeftOffset, 1.0f, 1.0f, new Color(this.getColor()));
                        ++i;
                    }
                    break;
                }
                case SIMPLE: {
                    Wrapper.getFontUtil().productSans.drawStringWithShadow("M" + (Object)((Object)EnumChatFormatting.WHITE) + "onsoon " + Wrapper.getMonsoon().getVersion() + (Object)((Object)EnumChatFormatting.DARK_GRAY) + " (" + (Object)((Object)EnumChatFormatting.WHITE) + Minecraft.getDebugFPS() + " FPS" + (Object)((Object)EnumChatFormatting.DARK_GRAY) + ")", 2.0f, topLeftOffset, new Color(this.getColor()));
                    topLeftOffset += (float)Wrapper.getFontUtil().productSans.getHeight();
                    break;
                }
                case LOGO: {
                    this.mc.getTextureManager().bindTexture(new ResourceLocation("monsoon/utterly insane logo.png"));
                    RenderUtil.renderTexture(2.0f, 2.0f, 129.0f, 71.0f);
                    topLeftOffset += 71.5f;
                    break;
                }
                case LOGO2: {
                    this.mc.getTextureManager().bindTexture(new ResourceLocation("monsoon/utterly insane logo2.png"));
                    RenderUtil.renderTexture(2.0f, 2.0f, 151.0f, 108.0f);
                    topLeftOffset += 71.5f;
                }
            }
            switch (coordinates.getValue()) {
                case SIMPLE: {
                    text = "X: " + (Object)((Object)EnumChatFormatting.WHITE) + (int)this.mc.thePlayer.posX + (Object)((Object)EnumChatFormatting.RESET) + " Y: " + (Object)((Object)EnumChatFormatting.WHITE) + (int)this.mc.thePlayer.posY + (Object)((Object)EnumChatFormatting.RESET) + " Z: " + (Object)((Object)EnumChatFormatting.WHITE) + (int)this.mc.thePlayer.posZ + (Object)((Object)EnumChatFormatting.RESET) + " ";
                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text, 2.0f, bottomLeftOffset, new Color(this.getColor()));
                    bottomLeftOffset -= (float)Wrapper.getFontUtil().productSans.getHeight();
                    break;
                }
                case SQUARE: {
                    text = "XYZ " + (Object)((Object)EnumChatFormatting.GRAY) + "[" + (Object)((Object)EnumChatFormatting.WHITE) + (int)this.mc.thePlayer.posX + ", " + (int)this.mc.thePlayer.posY + ", " + (int)this.mc.thePlayer.posZ + (Object)((Object)EnumChatFormatting.GRAY) + "]";
                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text, 2.0f, bottomLeftOffset, new Color(this.getColor()));
                    bottomLeftOffset -= (float)Wrapper.getFontUtil().productSans.getHeight();
                    break;
                }
                case DASH: {
                    text = "XYZ " + (Object)((Object)EnumChatFormatting.GRAY) + "- " + (int)this.mc.thePlayer.posX + ", " + (int)this.mc.thePlayer.posY + ", " + (int)this.mc.thePlayer.posZ;
                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text, 2.0f, bottomLeftOffset, new Color(this.getColor()));
                    bottomLeftOffset -= (float)Wrapper.getFontUtil().productSans.getHeight();
                    break;
                }
            }
            switch (speed.getValue()) {
                case SIMPLE: {
                    text = "BPS " + (Object)((Object)EnumChatFormatting.WHITE) + new DecimalFormat("#.##").format(PlayerUtil.getPlayerSpeed());
                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text, 2.0f, bottomLeftOffset, new Color(this.getColor()));
                    bottomLeftOffset -= (float)Wrapper.getFontUtil().productSans.getHeight();
                    break;
                }
                case SQUARE: {
                    text = "BPS " + (Object)((Object)EnumChatFormatting.GRAY) + "[" + (Object)((Object)EnumChatFormatting.WHITE) + new DecimalFormat("#.##").format(PlayerUtil.getPlayerSpeed()) + (Object)((Object)EnumChatFormatting.GRAY) + "]";
                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text, 2.0f, bottomLeftOffset, new Color(this.getColor()));
                    bottomLeftOffset -= (float)Wrapper.getFontUtil().productSans.getHeight();
                    break;
                }
                case DASH: {
                    text = "BPS " + (Object)((Object)EnumChatFormatting.GRAY) + "- " + new DecimalFormat("#.##").format(PlayerUtil.getPlayerSpeed());
                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text, 2.0f, bottomLeftOffset, new Color(this.getColor()));
                    bottomLeftOffset -= (float)Wrapper.getFontUtil().productSans.getHeight();
                    break;
                }
            }
            String timeString = DateTimeFormatter.ofPattern("h:mm").format(LocalDateTime.now()) + " " + DateTimeFormatter.ofPattern("a").format(LocalDateTime.now());
            switch (time.getValue()) {
                case SIMPLE: {
                    StringBuilder text2 = new StringBuilder().append("Time ").append((Object)EnumChatFormatting.WHITE).append(timeString);
                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text2.toString(), (float)(event.getSr().getScaledWidth() - Wrapper.getFontUtil().productSans.getStringWidth(text2.toString())) - 2.0f, bottomRightOffset, new Color(this.getColor()));
                    bottomRightOffset -= (float)Wrapper.getFontUtil().productSans.getHeight();
                    break;
                }
                case SQUARE: {
                    StringBuilder text3 = new StringBuilder().append("Time ").append((Object)EnumChatFormatting.GRAY).append("[").append((Object)EnumChatFormatting.WHITE).append(timeString).append((Object)EnumChatFormatting.GRAY).append("]");
                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text3.toString(), (float)(event.getSr().getScaledWidth() - Wrapper.getFontUtil().productSans.getStringWidth(text3.toString())) - 2.0f, bottomRightOffset, new Color(this.getColor()));
                    bottomRightOffset -= (float)Wrapper.getFontUtil().productSans.getHeight();
                    break;
                }
                case DASH: {
                    StringBuilder text4 = new StringBuilder().append("Time ").append((Object)EnumChatFormatting.GRAY).append("- ").append(timeString);
                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text4.toString(), (float)(event.getSr().getScaledWidth() - Wrapper.getFontUtil().productSans.getStringWidth(text4.toString())) - 2.0f, bottomRightOffset, new Color(this.getColor()));
                    bottomRightOffset -= (float)Wrapper.getFontUtil().productSans.getHeight();
                    break;
                }
            }
            switch (build.getValue()) {
                case SIMPLE: {
                    StringBuilder text5 = new StringBuilder().append("Build ").append((Object)EnumChatFormatting.WHITE).append(Wrapper.getMonsoon().getVersion());
                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text5.toString(), (float)(event.getSr().getScaledWidth() - Wrapper.getFontUtil().productSans.getStringWidth(text5.toString())) - 2.0f, bottomRightOffset, new Color(this.getColor()));
                    bottomRightOffset -= (float)Wrapper.getFontUtil().productSans.getHeight();
                    break;
                }
                case SQUARE: {
                    StringBuilder text6 = new StringBuilder().append("Build ").append((Object)EnumChatFormatting.GRAY).append("[").append((Object)EnumChatFormatting.WHITE).append(Wrapper.getMonsoon().getVersion()).append((Object)EnumChatFormatting.GRAY).append("]");
                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text6.toString(), (float)(event.getSr().getScaledWidth() - Wrapper.getFontUtil().productSans.getStringWidth(text6.toString())) - 2.0f, bottomRightOffset, new Color(this.getColor()));
                    bottomRightOffset -= (float)Wrapper.getFontUtil().productSans.getHeight();
                    break;
                }
                case DASH: {
                    StringBuilder text7 = new StringBuilder().append("Build ").append((Object)EnumChatFormatting.GRAY).append("- ").append(Wrapper.getMonsoon().getVersion());
                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text7.toString(), (float)(event.getSr().getScaledWidth() - Wrapper.getFontUtil().productSans.getStringWidth(text7.toString())) - 2.0f, bottomRightOffset, new Color(this.getColor()));
                    bottomRightOffset -= (float)Wrapper.getFontUtil().productSans.getHeight();
                    break;
                }
            }
            switch (uid.getValue()) {
                case SIMPLE: {
                    StringBuilder text8 = new StringBuilder().append("UID ").append((Object)EnumChatFormatting.WHITE).append(Wrapper.getMonsoonAccount().getUid());
                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text8.toString(), (float)(event.getSr().getScaledWidth() - Wrapper.getFontUtil().productSans.getStringWidth(text8.toString())) - 2.0f, bottomRightOffset, new Color(this.getColor()));
                    bottomRightOffset -= (float)Wrapper.getFontUtil().productSans.getHeight();
                    break;
                }
                case SQUARE: {
                    StringBuilder text9 = new StringBuilder().append("UID ").append((Object)EnumChatFormatting.GRAY).append("[").append((Object)EnumChatFormatting.WHITE).append(Wrapper.getMonsoonAccount().getUid()).append((Object)EnumChatFormatting.GRAY).append("]");
                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text9.toString(), (float)(event.getSr().getScaledWidth() - Wrapper.getFontUtil().productSans.getStringWidth(text9.toString())) - 2.0f, bottomRightOffset, new Color(this.getColor()));
                    bottomRightOffset -= (float)Wrapper.getFontUtil().productSans.getHeight();
                    break;
                }
                case DASH: {
                    StringBuilder text10 = new StringBuilder().append("UID ").append((Object)EnumChatFormatting.GRAY).append("- ").append(Wrapper.getMonsoonAccount().getUid());
                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text10.toString(), (float)(event.getSr().getScaledWidth() - Wrapper.getFontUtil().productSans.getStringWidth(text10.toString())) - 2.0f, bottomRightOffset, new Color(this.getColor()));
                    bottomRightOffset -= (float)Wrapper.getFontUtil().productSans.getHeight();
                    break;
                }
            }
        }
    };

    public HUD() {
        super("HUD", "uhhhh hud you know what it does", Category.HUD);
    }

    public int getColor() {
        return ColorUtil.fadeBetween(ColorUtil.getClientAccentTheme()[0].getRGB(), ColorUtil.getClientAccentTheme()[1].getRGB(), (float)(System.currentTimeMillis() % 1500L) / 750.0f);
    }

    public int getColorForArray(int yOffset, int yTotal) {
        return ColorUtil.fadeBetween(ColorUtil.getClientAccentTheme(yOffset, yTotal)[0].getRGB(), ColorUtil.getClientAccentTheme(yOffset, yTotal)[1].getRGB(), (float)((System.currentTimeMillis() + (long)(yTotal * 3)) % 1500L) / 750.0f);
    }

    public static enum BasicTextElement {
        SIMPLE,
        SQUARE,
        DASH,
        NONE;

    }

    public static enum Watermark {
        CSGO,
        SIMPLE,
        LOGO,
        LOGO2,
        NONE;

    }
}

