package me.jinthium.straight.impl.ui;

import me.jinthium.straight.api.shader.ShaderUtil;
import me.jinthium.straight.impl.utils.render.RenderUtil;
import me.jinthium.straight.impl.utils.render.RoundedUtil;
import me.jinthium.straight.impl.utils.render.StencilUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class ThemeSelector extends GuiScreen {
    private final ShaderUtil shaderUtil = new ShaderUtil("backgroundShader");
    private long initTime;
    private Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);
    private final ResourceLocation classicHudImage = new ResourceLocation("straight/images/classicHud.png");
//                overhaulHudImage = new ResourceLocation("straight/images/overhaulHud.png");




    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        shaderUtil.init();
        setupUniforms(initTime);
        ShaderUtil.drawQuads();
        shaderUtil.unload();
        ScaledResolution sc = ScaledResolution.fetchResolution(mc);

//        System.out.printl;
        StencilUtil.initStencilToWrite();
        RoundedUtil.drawRound(5, 5, 950, 455, 8, Color.white);
        StencilUtil.readStencilBuffer(1);
        RenderUtil.drawImage(classicHudImage, 5, 5, 950, 455);
        StencilUtil.uninitStencilBuffer();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        initTime = System.currentTimeMillis();
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        initTime = System.currentTimeMillis() - 10;
        super.onGuiClosed();
    }

    public void setupUniforms(long initTime){
        shaderUtil.setUniformf("time", (System.currentTimeMillis() - initTime) / 1000f);
        shaderUtil.setUniformf("resolution", mc.displayWidth, mc.displayHeight);
//        shaderUtil.setUniformf("mouse", Mouse.getX(), Mouse.getY());
    }
}
