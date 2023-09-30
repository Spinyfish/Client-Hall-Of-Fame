package club.pulsive.impl.mainmenu;

import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.mainmenu.impl.MainMenuButton;
import club.pulsive.impl.module.impl.visual.HUD;
import club.pulsive.impl.util.render.RenderUtil;
import club.pulsive.impl.util.render.RoundedUtil;
import club.pulsive.impl.util.render.StencilUtil;
import club.pulsive.impl.util.render.animations.Animation;
import club.pulsive.impl.util.render.animations.Direction;
import club.pulsive.impl.util.render.animations.SmoothStep;
import club.pulsive.impl.util.render.shaders.Blur;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class LoadingScreen extends GuiScreen {
    int alpha;
    public static Animation ease = new SmoothStep(1000, 1);
    public LoadingScreen() {
        ease.setDirection(Direction.FORWARDS);
        alpha = 255;
        ease.reset();
    }

    private long initTime;

    @Override
    public void initGui() {
        initTime = System.currentTimeMillis();
        ease.reset();
        alpha = 255;
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //this.scale = (height >= 1080 /1.5f ? (this.height) : this.height * 1.5f) / 1920f;
        boolean shouldCenter = this.width < 900;
        club.pulsive.impl.util.render.shaders.MainMenu.setTime((System.currentTimeMillis() - initTime) / 1000);
        club.pulsive.impl.util.render.shaders.MainMenu.nigger2();
        Gui.drawRect(0,0,this.width,this.height, new Color(31,31,31, 200).getRGB());
        Blur.renderBlur(30);
        int count = 0;
        int width = 0;
        double center = (shouldCenter ? this.width /2f : 70 + (230 * 1.25)/2f);
        if(ease.finished(Direction.FORWARDS) && alpha >= 3) {
            alpha--;
            alpha--;
            alpha--;
        }
        if(alpha == 0) {
            this.mc.displayGuiScreen(new MainMenuNew());
            MainMenuNew.ease.reset();
            return;
        }
        RenderUtil.scaleStart((0 + this.width / 2), (0 + this.height / 2), (float) ease.getOutput());

        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        RenderUtil.color(new Color(255,255,255,alpha).getRGB());
        RenderUtil.drawImage(false, new ResourceLocation("pulsabo/images/wihte png.png"), this.width - this.width /2 - 100 , this.height - this.height /2 - 100, 200, 200);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        RenderUtil.scaleEnd();
    }
    }
