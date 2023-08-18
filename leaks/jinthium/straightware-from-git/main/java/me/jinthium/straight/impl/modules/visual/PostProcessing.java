package me.jinthium.straight.impl.modules.visual;

import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.api.setting.ParentAttribute;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.render.ShaderEvent;
import me.jinthium.straight.impl.settings.BooleanSetting;
import me.jinthium.straight.impl.settings.ModeSetting;
import me.jinthium.straight.impl.settings.MultiBoolSetting;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.shaders.GaussianBlur;
import me.jinthium.straight.impl.shaders.KawaseBloom;
import me.jinthium.straight.impl.shaders.KawaseBlur;
import me.jinthium.straight.impl.utils.render.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;

import java.awt.*;

public class PostProcessing extends Module {
    private Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);
    private final ModeSetting blurMode = new ModeSetting("Blur Mode", "Kawase", "Kawase", "Gaussian");
    private final BooleanSetting blur = new BooleanSetting("Blur", false);
    private final NumberSetting gaussianBlurRadius = new NumberSetting("Blur Radius", 8, 1, 50, 1);

    private final NumberSetting gaussianBlurCompression = new NumberSetting("Blur Compression", 2, 1, 50, 1);
    private final NumberSetting blurIterations = new NumberSetting("Blur Iterations", 2, 1, 8, 1);
    private final NumberSetting blurOffset = new NumberSetting("Blur Offset", 3, 1, 10, 1);
    private final BooleanSetting bloom = new BooleanSetting("Bloom", false);
    private final NumberSetting bloomIterations = new NumberSetting("Bloom Iterations", 2, 1, 8, 1);
    private final NumberSetting bloomOffset = new NumberSetting("Bloom Offset", 3, 1, 10, 1);

    private final MultiBoolSetting stuffToBlur = new MultiBoolSetting("Blur", new BooleanSetting("Chat", true));



    public PostProcessing(){
        super("Post Processing", 0, Category.VISUALS);
        blurMode.addParent(blur, ParentAttribute.BOOLEAN_CONDITION);
        gaussianBlurRadius.addParent(blurMode, r -> blurMode.is("Gaussian"));
        gaussianBlurCompression.addParent(blurMode, r -> blurMode.is("Gaussian"));
        blurIterations.addParent(blur, r -> blur.isEnabled() && blurMode.is("Kawase"));
        blurOffset.addParent(blur, r -> blur.isEnabled() && blurMode.is("Kawase"));
        bloomIterations.addParent(bloom, ParentAttribute.BOOLEAN_CONDITION);
        bloomOffset.addParent(bloom, ParentAttribute.BOOLEAN_CONDITION);

        addSettings(blurMode, blur, gaussianBlurRadius, gaussianBlurCompression, blurIterations, blurOffset, bloom, bloomIterations, bloomOffset, stuffToBlur);
    }


    public void funny() {
        if(!this.isEnabled())
            return;

        if(blur.isEnabled()){
            switch(blurMode.getMode()) {
                case "Kawase" -> {
                    stencilFramebuffer = RenderUtil.createFrameBuffer(stencilFramebuffer);

                    stencilFramebuffer.framebufferClear();
                    stencilFramebuffer.bindFramebuffer(false);
                    Client.INSTANCE.getPubSubEventBus().publish(new ShaderEvent(ShaderEvent.SHADER_TYPE.BLUR, ShaderEvent.BLUR_TYPE.KAWASE));
                    blurStuff();
                    stencilFramebuffer.unbindFramebuffer();


                    KawaseBlur.renderBlur(stencilFramebuffer.framebufferTexture, blurIterations.getValue().intValue(), blurOffset.getValue().intValue());
                }
                case "Gaussian" -> {
                    GaussianBlur.startBlur();
                    Client.INSTANCE.getPubSubEventBus().publish(new ShaderEvent(ShaderEvent.SHADER_TYPE.BLUR, ShaderEvent.BLUR_TYPE.GAUSSIAN));
                    GaussianBlur.endBlur(gaussianBlurRadius.getValue().floatValue(), gaussianBlurCompression.getValue().floatValue());
                }
            }
        }

        if(bloom.isEnabled()){
            stencilFramebuffer = RenderUtil.createFrameBuffer(stencilFramebuffer);
            stencilFramebuffer.framebufferClear();
            stencilFramebuffer.bindFramebuffer(false);
            Client.INSTANCE.getPubSubEventBus().publish(new ShaderEvent(ShaderEvent.SHADER_TYPE.BLOOM, ShaderEvent.BLUR_TYPE.KAWASE));
            blurStuff();
            stencilFramebuffer.unbindFramebuffer();
            KawaseBloom.renderBlur(stencilFramebuffer.framebufferTexture, bloomIterations.getValue().intValue(), bloomOffset.getValue().intValue());
        }
    }

    public void blurStuff(){
        ScaledResolution sr = ScaledResolution.fetchResolution(mc);
        if(stuffToBlur.isEnabled("Chat")){
            if (mc.currentScreen instanceof GuiChat) {
                Gui.drawRect2(2, sr.getScaledHeight() - 14, sr.getScaledWidth() - 4, 12, Color.BLACK.getRGB());
            }
            RenderUtil.resetColor();
            mc.ingameGUI.getChatGUI().renderChatBox();
            RenderUtil.resetColor();
        }
    }
}
