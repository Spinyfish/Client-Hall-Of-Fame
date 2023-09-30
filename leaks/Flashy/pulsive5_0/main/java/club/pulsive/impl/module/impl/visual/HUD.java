package club.pulsive.impl.module.impl.visual;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.api.font.FontRenderer;
import club.pulsive.api.font.Fonts;
import club.pulsive.api.main.Pulsive;
import club.pulsive.api.yoint.shader.impl.BoxBlur;
import club.pulsive.impl.event.client.ShaderEvent;
import club.pulsive.impl.event.render.Render2DEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.module.impl.misc.ClientSettings;
import club.pulsive.impl.property.Property;
import club.pulsive.impl.property.implementations.ColorProperty;
import club.pulsive.impl.property.implementations.DoubleProperty;
import club.pulsive.impl.property.implementations.EnumProperty;
import club.pulsive.impl.property.implementations.MultiSelectEnumProperty;
import club.pulsive.impl.util.client.Logger;
import club.pulsive.impl.util.math.MathUtil;
import club.pulsive.impl.util.math.apache.ApacheMath;
import club.pulsive.impl.util.player.MovementUtil;
import club.pulsive.impl.util.render.ColorUtil;
import club.pulsive.impl.util.render.Draw;
import club.pulsive.impl.util.render.RenderUtil;
import club.pulsive.impl.util.render.RoundedUtil;
import club.pulsive.impl.util.render.animations.Animation;
import club.pulsive.impl.util.render.secondary.ShaderUtil;
import com.google.common.collect.Lists;
import com.sun.security.ntlm.Client;
import lombok.AllArgsConstructor;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.nio.FloatBuffer;
import java.util.*;
import java.util.List;

@ModuleInfo(name = "HUD", renderName = "HUD", description = "Arraylist", aliases = "HUD", category = Category.VISUALS)

public class HUD extends Module {
    //Vars
    int count;
    FontRenderer mainFont = null;
    private EnumProperty<COLORS> selectedColor = new EnumProperty<COLORS>("Color", COLORS.MAIN_CLIENT);
    private EnumProperty<FONTS> selectedFont = new EnumProperty<FONTS>("Font", FONTS.PULSIVE);
    private MultiSelectEnumProperty<ELEMENTS> elements = new MultiSelectEnumProperty<ELEMENTS>("Elements", Lists.newArrayList(ELEMENTS.BOX), ELEMENTS.values());
    private MultiSelectEnumProperty<SHADERS> shaders = new MultiSelectEnumProperty<SHADERS>("Shaders", Lists.newArrayList(SHADERS.BLUR), SHADERS.values());
    private Property<Boolean> animatedMainColor = new Property<>("Animated Main Color", true);
    private DoubleProperty saturation = new DoubleProperty("Saturation", 0.5, 0.5, 1, 0.1);
    private ColorProperty bgColor = new ColorProperty("Background Color", Color.BLACK);
    private DoubleProperty bgAlpha = new DoubleProperty("Background Alpha", 1, 1, 255, 1);
    private final ShaderUtil outlineShader = new ShaderUtil("outline.frag");
    private final ShaderUtil glowShader = new ShaderUtil("glow.frag");

    public Framebuffer framebuffer;
    public Framebuffer outlineFrameBuffer;
    public Framebuffer glowFrameBuffer;
    int y;
    @Override
    public void onDisable() {
        super.onDisable();
    }
    public List<Module> modules;

    private final Comparator<Object> SORT_METHOD = Comparator.comparingDouble(m -> {
        Module module = (Module) m;
        String name = module.getRenderName();
        if (mainFont != null && selectedFont.getValue() != FONTS.MINECRAFT) {
            return mainFont.getStringWidth(name) - (module.isHasSuffix() ? mainFont.getStringWidth("" + EnumChatFormatting.GRAY) : 0);
        } else {
            return mc.fontRendererObj.getStringWidth(name) - (module.isHasSuffix() ? mc.fontRendererObj.getStringWidth("" + EnumChatFormatting.GRAY) : 0);
        }
    }).reversed();


    @Override
    public void onEnable() {
        super.onEnable();
    }

    public void kms(){
        framebuffer = RenderUtil.createFramebuffer(framebuffer, true);
        outlineFrameBuffer = RenderUtil.createFramebuffer(outlineFrameBuffer, true);
        glowFrameBuffer = RenderUtil.createFramebuffer(glowFrameBuffer, true);


        ScaledResolution sr = new ScaledResolution(mc);
        if (framebuffer != null && outlineFrameBuffer != null) {
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(516, 0.0f);
            GlStateManager.enableBlend();
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

            outlineFrameBuffer.framebufferClear();
            outlineFrameBuffer.bindFramebuffer(true);
            outlineShader.init();
            setupOutlineUniforms(0, 1);
            RenderUtil.bindTexture(framebuffer.framebufferTexture);
            ShaderUtil.drawQuads();
            outlineShader.init();
            setupOutlineUniforms(1, 0);
            RenderUtil.bindTexture(framebuffer.framebufferTexture);
            ShaderUtil.drawQuads();
            outlineShader.unload();
            outlineFrameBuffer.unbindFramebuffer();

            GlStateManager.color(1, 1, 1, 1);
            glowFrameBuffer.framebufferClear();
            glowFrameBuffer.bindFramebuffer(true);
            glowShader.init();
            setupGlowUniforms(1, 0);
            RenderUtil.bindTexture(outlineFrameBuffer.framebufferTexture);
            ShaderUtil.drawQuads();
            glowShader.unload();
            glowFrameBuffer.unbindFramebuffer();

            mc.getFramebuffer().bindFramebuffer(true);
            glowShader.init();
            setupGlowUniforms(0, 1);
            GL13.glActiveTexture(GL13.GL_TEXTURE16);
            RenderUtil.bindTexture(framebuffer.framebufferTexture);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            RenderUtil.bindTexture(glowFrameBuffer.framebufferTexture);
            ShaderUtil.drawQuads();
            glowShader.unload();

            framebuffer.framebufferClear();
            framebuffer.bindFramebuffer(true);
            RoundedUtil.drawRoundedRect(30, 30, 100, 100, 8, Color.WHITE.getRGB());
            framebuffer.unbindFramebuffer();
            mc.getFramebuffer().bindFramebuffer(true);
            GlStateManager.disableLighting();

        }
    }

    public void setupGlowUniforms(float dir1, float dir2) {
        Color color = new Color(getColor());
        glowShader.setUniformi("texture", 0);
        glowShader.setUniformi("textureToCheck", 16);
        glowShader.setUniformf("radius", 8);
        glowShader.setUniformf("texelSize", 1.0f / mc.displayWidth, 1.0f / mc.displayHeight);
        glowShader.setUniformf("direction", dir1, dir2);
        glowShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
        glowShader.setUniformf("exposure", 1.5f);
        glowShader.setUniformi("avoidTexture", 1);

        final FloatBuffer buffer = BufferUtils.createFloatBuffer(256);
        for (int i = 1; i <= 8; i++) {
            buffer.put(MathUtil.calculateGaussianValue(i, 4));
        }
        buffer.rewind();

        GL20.glUniform1(glowShader.getUniform("weights"), buffer);
    }


    public void setupOutlineUniforms(float dir1, float dir2) {
        Color color = new Color(getColor());
        outlineShader.setUniformi("texture", 0);
        outlineShader.setUniformf("radius", 8 / 1.5f);
        outlineShader.setUniformf("texelSize", 1.0f / mc.displayWidth, 1.0f / mc.displayHeight);
        outlineShader.setUniformf("direction", dir1, dir2);
        outlineShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
    }

    @EventHandler
    private final Listener<Render2DEvent> render2DEventListener = event -> {
        modules = Pulsive.INSTANCE.getModuleManager().getModules();

        EntityLivingBase entity = mc.thePlayer;


        final double diffX = entity.posX - entity.lastTickPosX;
        final double diffY = entity.posY - entity.lastTickPosY;
        final double diffZ = entity.posZ - entity.lastTickPosZ;
        final float partialTicks = event.getPartialTicks();
        final AxisAlignedBB interpolatedBB = new AxisAlignedBB(
                entity.lastTickPosX - entity.width / 2 + diffX * partialTicks,
                entity.lastTickPosY + diffY * partialTicks,
                entity.lastTickPosZ - entity.width / 2 + diffZ * partialTicks,
                entity.lastTickPosX + entity.width / 2 + diffX * partialTicks,
                entity.lastTickPosY + entity.height + diffY * partialTicks,
                entity.lastTickPosZ + entity.width / 2 + diffZ * partialTicks);
        final double[][] vectors = new double[8][2];
        final float[] coords = new float[4];
        convertTo2D(interpolatedBB, vectors, coords);
        float minX = coords[0], minY = coords[1], maxX = coords[2], maxY = coords[3];

       // kms();
      // Logger.print("" + (mc.thePlayer.posX - mc.getRenderManager().renderPosX));
      //  RenderUtil.drawRect(minX, minY, minX + 30, minY + 10, -1);
        //RenderUtil.drawRect((float) (mc.thePlayer.posX - mc.getRenderManager().renderPosX), (float) (mc.thePlayer.posY - mc.getRenderManager().renderPosY), 10, 30, -1);
        //GlStateManager.pushMatrix();
       //Iterator iterator = toggledCheats.iterator();
        count = 0;
        y = (int) 1;
        switch (selectedFont.getValue()) {
            case PULSIVE:
                mainFont = Fonts.sf;
                break;
            case MOON:
                mainFont = Fonts.moon;
                break;
            case NOVOLINE:
                mainFont = Fonts.jetbrains;
                break;
            case ASTOLFO:
                mainFont = Fonts.astolfo;
                break;
            case SIGMA:
                mainFont = Fonts.sigma;
                break;
            case EXHIBITION:
                mainFont = Fonts.tahoma;
                break;
        }

        long ping2 = 0;
        long ping = mc.isSingleplayer() ? 0 : mc.getCurrentServerData().pingToServer == -1 ? ping2 : mc.getCurrentServerData().pingToServer;
        if(ping != -1 && ping != ping2){
            ping2 = ping;
        }

        mc.fontRendererObj.drawStringWithShadow("Ping: " + ping,
                event.getScaledResolution().getScaledWidth() - mc.fontRendererObj.getStringWidth("Ping: " + ping) - 1,
                event.getScaledResolution().getScaledHeight() - mc.fontRendererObj.FONT_HEIGHT - 1, -1);

        
        modules.sort(SORT_METHOD);
        for (Module m : modules) {
            //GlStateManager.pushMatrix();
            String name = m.getRenderName() ;
           
            ScaledResolution sr = event.getScaledResolution();
            Animation moduleAnimation = m.getModuleAnimation();
            
            if(!m.isToggled() && moduleAnimation.isDone()) continue;
//            Translate translate = m.getTranslate();
//            translate.animate(-120, y);
            //GlStateManager.translate(x, translate.getY() , 0);
            if (mainFont != null && selectedFont.getValue() != FONTS.MINECRAFT) {
                double heightVal = (mainFont.getHeight() + 2 );
                float textWidth = mainFont.getStringWidth(name) - (m.isHasSuffix() ? mainFont.getStringWidth("" + EnumChatFormatting.GRAY) : 0);
                double x = sr.getScaledWidth() - (textWidth + 4);
                float alphaAnimation = 1;
                // RenderUtil.scaleStart((float) (x + mainFont.getStringWidth(name) / 2f), y, (float) moduleAnimation.getOutput());
                alphaAnimation = (float) moduleAnimation.getOutput();
                x += ApacheMath.abs((moduleAnimation.getOutput() - 1) * (4 + textWidth));
                if(elements.isSelected(ELEMENTS.BOX)) {
                    Draw.drawRectangle(x - 2, y - 1, sr.getScaledWidth(), y + 1 + mainFont.getHeight(), RenderUtil.applyOpacity(bgColor.getValue().getRGB(), bgAlpha.getValue().floatValue() / 255));
                }
                if(elements.isSelected(ELEMENTS.LINE)) {
                    Draw.drawRectangle(event.getScaledResolution().getScaledWidth() - 1, y - 1, event.getScaledResolution().getScaledWidth(), y + 1 + mainFont.getHeight(), HUD.getColor());
                }
                mainFont.drawStringWithShadow(name, x, y, RenderUtil.applyOpacity(getColor(), alphaAnimation));
                //RenderUtil.scaleEnd();
                y += heightVal * moduleAnimation.getOutput();
                count++;
            } else {
                double heightVal = (mc.fontRendererObj.FONT_HEIGHT );
                float textWidth = mc.fontRendererObj.getStringWidth(name) - (m.isHasSuffix() ? mc.fontRendererObj.getStringWidth("" + EnumChatFormatting.GRAY) : 0);
                double x = sr.getScaledWidth() - (textWidth + 4);
                float alphaAnimation = 1;
                // RenderUtil.scaleStart((float) (x + mainFont.getStringWidth(name) / 2f), y, (float) moduleAnimation.getOutput());
                alphaAnimation = (float) moduleAnimation.getOutput();
                x += ApacheMath.abs((moduleAnimation.getOutput() - 1) * (4 + textWidth));
                if(elements.isSelected(ELEMENTS.BOX)) {
                    Draw.drawRectangle(x - 2, y - 1, sr.getScaledWidth(), y + 1 + mc.fontRendererObj.FONT_HEIGHT - 2, RenderUtil.applyOpacity(bgColor.getValue().getRGB(), bgAlpha.getValue().floatValue() / 255));
                }
                if(elements.isSelected(ELEMENTS.LINE)) {
                    Draw.drawRectangle(event.getScaledResolution().getScaledWidth() - 1, y - 1, event.getScaledResolution().getScaledWidth(), y + 1 + mc.fontRendererObj.FONT_HEIGHT, HUD.getColor());
                }
                mc.fontRendererObj.drawStringWithShadow(name, (float) x, y, RenderUtil.applyOpacity(getColor(), alphaAnimation));
                //RenderUtil.scaleEnd();
                y += heightVal * moduleAnimation.getOutput();
                count++;
            }

            // GlStateManager.popMatrix();
        }
     
    };

    private void convertTo2D(AxisAlignedBB interpolatedBB, double[][] vectors, float[] coords) {
        if (coords == null || vectors == null || interpolatedBB == null) return;
        double x = mc.getRenderManager().viewerPosX;
        double y = mc.getRenderManager().viewerPosY;
        double z = mc.getRenderManager().viewerPosZ;

        vectors[0] = RenderUtil.project2D(interpolatedBB.minX - x, interpolatedBB.minY - y,
                interpolatedBB.minZ - z);
        vectors[1] = RenderUtil.project2D(interpolatedBB.minX - x, interpolatedBB.minY - y,
                interpolatedBB.maxZ - z);
        vectors[2] = RenderUtil.project2D(interpolatedBB.minX - x, interpolatedBB.maxY - y,
                interpolatedBB.minZ - z);
        vectors[3] = RenderUtil.project2D(interpolatedBB.maxX - x, interpolatedBB.minY - y,
                interpolatedBB.minZ - z);
        vectors[4] = RenderUtil.project2D(interpolatedBB.maxX - x, interpolatedBB.maxY - y,
                interpolatedBB.minZ - z);
        vectors[5] = RenderUtil.project2D(interpolatedBB.maxX - x, interpolatedBB.minY - y,
                interpolatedBB.maxZ - z);
        vectors[6] = RenderUtil.project2D(interpolatedBB.minX - x, interpolatedBB.maxY - y,
                interpolatedBB.maxZ - z);
        vectors[7] = RenderUtil.project2D(interpolatedBB.maxX - x, interpolatedBB.maxY - y,
                interpolatedBB.maxZ - z);

        float minW = (float) Arrays.stream(vectors).min(Comparator.comparingDouble(pos -> pos[2])).orElse(new double[]{0.5})[2];
        float maxW = (float) Arrays.stream(vectors).max(Comparator.comparingDouble(pos -> pos[2])).orElse(new double[]{0.5})[2];
        if (maxW > 1 || minW < 0) return;
        float minX = (float) Arrays.stream(vectors).min(Comparator.comparingDouble(pos -> pos[0])).orElse(new double[]{0})[0];
        float maxX = (float) Arrays.stream(vectors).max(Comparator.comparingDouble(pos -> pos[0])).orElse(new double[]{0})[0];
        final float top = (mc.displayHeight / (float) new ScaledResolution(mc).getScaleFactor());
        float minY = (float) (top - Arrays.stream(vectors).min(Comparator.comparingDouble(pos -> top - pos[1])).orElse(new double[]{0})[1]);
        float maxY = (float) (top - Arrays.stream(vectors).max(Comparator.comparingDouble(pos -> top - pos[1])).orElse(new double[]{0})[1]);
        coords[0] = minX;
        coords[1] = minY;
        coords[2] = maxX;
        coords[3] = maxY;
    }

    @EventHandler
    private final Listener<ShaderEvent> shaderEventListener = event -> {
        modules = Pulsive.INSTANCE.getModuleManager().getModules();

        //GlStateManager.pushMatrix();
        //Iterator iterator = toggledCheats.iterator();
        count = 0;
        y = (int) 1;
        switch (selectedFont.getValue()) {
            case PULSIVE:
                mainFont = Fonts.sf;
                break;
            case MOON:
                mainFont = Fonts.moon;
                break;
            case NOVOLINE:
                mainFont = Fonts.jetbrains;
                break;
            case ASTOLFO:
                mainFont = Fonts.astolfo;
                break;
            case SIGMA:
                mainFont = Fonts.sigma;
                break;
            case EXHIBITION:
                mainFont = Fonts.tahoma;
                break;
        }

        double heightVal = (selectedFont.getValue() == FONTS.EXHIBITION ? mainFont.getHeight() : mainFont.getHeight() + 2 );

        modules.sort(SORT_METHOD);
        
        for (Module m : modules) {
            //GlStateManager.pushMatrix();
            String name = m.getRenderName() ;
            float textWidth = mainFont.getStringWidth(name) - (m.isHasSuffix() ? mainFont.getStringWidth("" + EnumChatFormatting.GRAY) : 0);
            ScaledResolution sr = new ScaledResolution(mc);
            Animation moduleAnimation = m.getModuleAnimation();

            if(!m.isToggled() && moduleAnimation.isDone()) continue;
//            Translate translate = m.getTranslate();
//            translate.animate(-120, y);
            double x = sr.getScaledWidth() - (textWidth + 4);
            float alphaAnimation = 1;
            x += ApacheMath.abs((moduleAnimation.getOutput() - 1) * (4 + textWidth));
            if (mainFont != null && selectedFont.getValue() != FONTS.MINECRAFT) {
                if(shaders.isSelected(SHADERS.BLUR) && event.isBlur()) {
                    Draw.drawRectangle(x - 2, y - 1, sr.getScaledWidth(), y + 1 + mainFont.getHeight(), RenderUtil.applyOpacity(bgColor.getValue().getRGB(), bgAlpha.getValue().floatValue()));
                }
                if(shaders.isSelected(SHADERS.SHADOW) && event.isShadow()) {
                    Draw.drawRectangle(x - 2, y - 1, sr.getScaledWidth(), y + 1 + mainFont.getHeight(), 0xff121212);
                }
                if(shaders.isSelected(SHADERS.GLOW) && event.isGlow()){
                    Draw.drawRectangle(x - 2, y - 1, sr.getScaledWidth(), y + 1 + mainFont.getHeight(), 0xff121212);
                }
                //RenderUtil.scaleEnd();
                
                y += heightVal * moduleAnimation.getOutput();
                count++;
            } else {
                
                textWidth = mc.fontRendererObj.getStringWidth(name) - (m.isHasSuffix() ? mc.fontRendererObj.getStringWidth("" + EnumChatFormatting.GRAY) : 0);
                x = sr.getScaledWidth() - (textWidth + 4);
                if(shaders.isSelected(SHADERS.BLUR) && event.isBlur()) {
                    Draw.drawRectangle(x - 2, y - 1, sr.getScaledWidth(), y + 1 + mc.fontRendererObj.FONT_HEIGHT, RenderUtil.applyOpacity(bgColor.getValue().getRGB(), bgAlpha.getValue().floatValue()));
                }
                if(shaders.isSelected(SHADERS.SHADOW) && event.isShadow()) {
                    Draw.drawRectangle(x - 2, y - 1, sr.getScaledWidth(), y + 1 + mc.fontRendererObj.FONT_HEIGHT, 0xff121212);
                }

                if(shaders.isSelected(SHADERS.GLOW) && event.isGlow()){
                    Draw.drawRectangle(x - 2, y - 1, sr.getScaledWidth(), y + 1 + mc.fontRendererObj.FONT_HEIGHT, getColor());
                }
                //RenderUtil.scaleEnd();
                y += heightVal * moduleAnimation.getOutput();
                count++;
            }

            // GlStateManager.popMatrix();
        }
    };
    
    public static  int getColor() {
        final int modH = mc.fontRendererObj.FONT_HEIGHT + 3;
        long ms = (long) (1.3 * 1000L);
        long currentMillis = -1;
        currentMillis = System.currentTimeMillis();
        final float offset = (currentMillis + (Pulsive.INSTANCE.getModuleManager().getModule(HUD.class).y * 2 / modH * 50)) % ms / (ms / 2.0F);
       switch(Pulsive.INSTANCE.getModuleManager().getModule(HUD.class).selectedColor.getValue()) {
           case RAINBOW:
            return rainbow(Pulsive.INSTANCE.getModuleManager().getModule(HUD.class).y * 2 / modH * 100);
           case ASTOLFO:
            return astolfoColors2(Pulsive.INSTANCE.getModuleManager().getModule(HUD.class).count * 17, 5000);
           case MAIN_CLIENT:
            return Pulsive.INSTANCE.getModuleManager().getModule(HUD.class).animatedMainColor.getValue() ? fade(ClientSettings.mainColor.getValue(), 1100, Pulsive.INSTANCE.getModuleManager().getModule(HUD.class).y / 5 * 3 + 35).getRGB() : ClientSettings.mainColor.getValue().getRGB();
           case DOUBLE_MAIN_CLIENT:
            return fadeBetween(ClientSettings.mainColor.getValue().getRGB(), ClientSettings.secondColor.getValue().getRGB(),offset);
        }

        return -1;
    }
    private String getSeperator() {
        return " ";
    }
   
        public java.util.ArrayList<Module> getToggledModulesSortedLTS() {
            java.util.ArrayList<Module> toggledCheats = getNonHiddenToggledCheats();
            if(mainFont != null && selectedFont.getValue() != FONTS.MINECRAFT) {
                toggledCheats.sort((cheat1, cheat2) -> (mainFont.getStringWidth(cheat2.getRenderName())) - (cheat1.isHasSuffix() ? mainFont.getStringWidth(cheat1.getRenderName() ) -30 : mainFont.getStringWidth(cheat1.getRenderName())));

            } else {
                toggledCheats.sort((cheat1, cheat2) -> (mc.fontRendererObj.getStringWidth(cheat2.getRenderName())) - (cheat1.isHasSuffix() ? mc.fontRendererObj.getStringWidth(cheat1.getRenderName() ) -30 : mc.fontRendererObj.getStringWidth(cheat1.getRenderName())));
            }
            return toggledCheats;
        }

    private static int astolfoColors2(int yOffset, int yTotal) {
        float speed = 3000f;
        float hue = (float) (System.currentTimeMillis() % (int) speed) + ((yTotal - yOffset) * 6);
        while (hue > speed) {
            hue -= speed;
        }
        hue /= speed;
        if (hue > 0.5) {
            hue = 0.5F - (hue - 0.5f);
        }
        hue += 0.5F;
        return Color.HSBtoRGB(hue, Pulsive.INSTANCE.getModuleManager().getModule(HUD.class).saturation.getValue().floatValue(), 1F);
    }
    private static int fadeBetween(int color1, int color2, float offset) {
        if (offset > 1)
            offset = 1 - offset % 1;

        double invert = 1 - offset;
        int r = (int) ((color1 >> 16 & 0xFF) * invert + (color2 >> 16 & 0xFF) * offset);
        int g = (int) ((color1 >> 8 & 0xFF) * invert + (color2 >> 8 & 0xFF) * offset);
        int b = (int) ((color1 & 0xFF) * invert + (color2 & 0xFF) * offset);
        int a = (int) ((color1 >> 24 & 0xFF) * invert + (color2 >> 24 & 0xFF) * offset);
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }
    private static int rainbow(int delay) {
        double rainbowState = ApacheMath.ceil((System.currentTimeMillis() + delay / 2) / 10.0);
        rainbowState %= 360;
        return Color.getHSBColor((float) (rainbowState / 360.0f), Pulsive.INSTANCE.getModuleManager().getModule(HUD.class).saturation.getValue().floatValue(), 1f).getRGB();
    }
    public static Color fade(Color color, int index, int count) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        float speed = (float) (2000);
        float value = (float) ((System.nanoTime() / 1000000 - (count) * 12) % speed) / speed;
        value *= 2;
        value -= 1;
        float function = -1 * (value * value) + 1;
        if (function < 0.4)
            function = 0.4F;
        int c = Color.HSBtoRGB(hsb[0], hsb[1], function);
        color = new Color(c);
        return new Color(c);
    }
    private ArrayList<Module> getNonHiddenToggledCheats() {
        ArrayList<Module> toggledCheats = new ArrayList<>();
        for (Module cheat : Pulsive.INSTANCE.getModuleManager().getModules()) {
            if (cheat.isToggled() && !cheat.isHidden()) {
                toggledCheats.add(cheat);
            }

        }

        return toggledCheats;
    }

    @AllArgsConstructor
    private enum COLORS {
        RAINBOW("Rainbow"),
        ASTOLFO("Astolfo"),
        MAIN_CLIENT("Main Client"),
        DOUBLE_MAIN_CLIENT("Double Main Client");

        private final String color;

        @Override
        public String toString() {return color;}
    }

    @AllArgsConstructor
    private enum FONTS {
        PULSIVE("Pulsive"),
        MINECRAFT("Minecraft"),
        NOVOLINE("Novoline"),
        MOON("Moon"),
        ASTOLFO("Astolfo"),
        SIGMA("Sigma"),
        EXHIBITION("Exhibition");

        private final String chosenFont;

        @Override
        public String toString() {return chosenFont;}
    }


    @AllArgsConstructor
    private enum ELEMENTS {
        BOX("Box"),
        LINE("Line"),
        BORDER("Border");

        private final String addonName;

        @Override
        public String toString() {return addonName;}
    }

    @AllArgsConstructor
    private enum SHADERS {
        BLUR("Blur"),
        SHADOW("Shadow"),
        GLOW("Glow");

        private final String addonName;

        @Override
        public String toString() {return addonName;}
    }
        
}
