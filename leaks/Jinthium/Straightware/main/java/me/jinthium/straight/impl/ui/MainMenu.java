package me.jinthium.straight.impl.ui;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import me.jinthium.straight.api.shader.ShaderUtil;
import me.jinthium.straight.api.util.Util;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.ui.components.Button;
import me.jinthium.straight.impl.shaders.KawaseBloom;
import me.jinthium.straight.impl.utils.animation.Animation;
import me.jinthium.straight.impl.utils.animation.impl.DecelerateAnimation;
import me.jinthium.straight.impl.utils.render.RenderUtil;
import net.minecraft.client.gui.*;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.StringUtils;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class MainMenu extends GuiScreen implements Util {

    private long initTime;
    private final Animation imageAnimation = new DecelerateAnimation(400, 1);
    private final ShaderUtil shaderUtil = new ShaderUtil("backgroundShader");
    private Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);

    public void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch (button.id) {
            case 0 -> this.mc.displayGuiScreen(new GuiSelectWorld(this));
            case 1 -> mc.displayGuiScreen(new GuiMultiplayer(this));
            case 2 -> mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
            case 3 -> mc.shutdownMinecraftApplet();
            case 4 -> mc.displayGuiScreen(Client.INSTANCE.getAltManager());
            case 5 -> {
                try {
                    URI uri = new URI("https://onlyfans.com/bretthax");

                    Desktop.getDesktop().browse(uri);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//        this.drawDefaultBackground();

        shaderUtil.init();
        setupUniforms(initTime);
        ShaderUtil.drawQuads();
        shaderUtil.unload();

        ScaledResolution scaledResolution = ScaledResolution.fetchResolution(mc);
        float x = (float) scaledResolution.getScaledWidth() / 2;
        float y = 175;
        //mc.normalFont18.drawString("Straightware", x, y, Color.white.getRGB());
        stencilFramebuffer = RenderUtil.createFrameBuffer(stencilFramebuffer);
        stencilFramebuffer.framebufferClear();
        stencilFramebuffer.bindFramebuffer(false);
        RenderUtil.scaleStart((x - normalFont40.getStringWidth(StringUtils.colorCodeString("S&ftraight&rW&fare")) / 2) + normalFont40.getStringWidth(StringUtils.colorCodeString("S&ftraight&rW&fare")) / 2, y + (float) normalFont40.getHeight() / 2, imageAnimation.getOutput().floatValue());
        normalFont40.drawStringWithShadow(StringUtils.colorCodeString("S&ftraight&rW&fare"),
                x - normalFont40.getStringWidth(StringUtils.colorCodeString("S&ftraight&rW&fare")) / 2, y, Client.INSTANCE.getClientColor().getRGB());
        RenderUtil.scaleEnd();
        stencilFramebuffer.unbindFramebuffer();
        KawaseBloom.renderBlur(stencilFramebuffer.framebufferTexture, 2, 2);

        RenderUtil.scaleStart((x - normalFont40.getStringWidth(StringUtils.colorCodeString("S&ftraight&rW&fare")) / 2) + normalFont40.getStringWidth(StringUtils.colorCodeString("S&ftraight&rW&fare")) / 2, y + (float) normalFont40.getHeight() / 2, imageAnimation.getOutput().floatValue());
        normalFont40.drawStringWithShadow(StringUtils.colorCodeString("S&ftraight&rW&fare"),
                x - normalFont40.getStringWidth(StringUtils.colorCodeString("S&ftraight&rW&fare")) / 2, y, Client.INSTANCE.getClientColor().getRGB());
        RenderUtil.scaleEnd();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void initGui() {
        ScaledResolution scaledResolution = ScaledResolution.fetchResolution(mc);
        this.buttonList.add(new Button(0, scaledResolution.getScaledWidth() / 2 - 100, 200, 90, 25, "Singleplayer"));
        this.buttonList.add(new Button(1, scaledResolution.getScaledWidth() / 2 + 5, 200, 90, 25, "Multiplayer"));
        this.buttonList.add(new Button(2, scaledResolution.getScaledWidth() / 2 - 100, 235, 90, 25, "Options"));
        this.buttonList.add(new Button(3, scaledResolution.getScaledWidth() / 2 + 5, 235, 90, 25, "Exit"));
        this.buttonList.add(new Button(4, scaledResolution.getScaledWidth() / 2 - 63, 270, 120, 25, "Alt Manager"));
        this.buttonList.add(new Button(5, 5, scaledResolution.getScaledHeight() - 30, 140, 25, "Goto Bretthax's Onlyfans"));
        initTime = System.currentTimeMillis();
//        this.buttonList.add(new Button(2, ))
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        initTime = System.currentTimeMillis() - 10;
        buttonList.forEach(button -> button.animation.reset());
        imageAnimation.reset();
    }

    public void setupUniforms(long initTime){
        shaderUtil.setUniformf("time", (System.currentTimeMillis() - initTime) / 1000f);
        shaderUtil.setUniformf("resolution", mc.displayWidth, mc.displayHeight);
//        shaderUtil.setUniformf("mouse", Mouse.getX(), Mouse.getY());
    }
}
