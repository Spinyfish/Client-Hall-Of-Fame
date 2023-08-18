package me.jinthium.straight.impl.ui.components;

import me.jinthium.straight.api.util.Util;
import me.jinthium.straight.impl.shaders.KawaseBloom;
import me.jinthium.straight.impl.utils.animation.Direction;
import me.jinthium.straight.impl.utils.animation.impl.SmoothStepAnimation;
import me.jinthium.straight.impl.utils.render.RenderUtil;
import me.jinthium.straight.impl.utils.render.RoundedUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.shader.Framebuffer;

import java.awt.*;

public class Button extends GuiButton implements Util {

    private Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);
    
    public Button(int id, int x, int y, int width, int height, String text){
        super(id, x, y, width, height, text);
        hoverAnimation = new SmoothStepAnimation(175, 0.2f);
        animation = new SmoothStepAnimation(175, 1, Direction.FORWARDS);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        boolean hovered = RenderUtil.isHovered(mouseX, mouseY, xPosition, yPosition, width, height);
        hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);


        RenderUtil.scaleStart(xPosition + (float) width / 2, yPosition + (float) height / 2, animation.getOutput().floatValue());
        stencilFramebuffer = RenderUtil.createFrameBuffer(stencilFramebuffer);
        stencilFramebuffer.framebufferClear();
        stencilFramebuffer.bindFramebuffer(false);
        RenderUtil.scaleStart(xPosition + (float) width / 2, yPosition + (float) height / 2, 1 + hoverAnimation.getOutput().floatValue());
        RoundedUtil.drawRound(xPosition, yPosition, width, height, 6, Color.black);
        RenderUtil.scaleEnd();
//        Gui.drawRect2(xPosition, yPosition, width, height, Color.white.getRGB());
        stencilFramebuffer.unbindFramebuffer();
        KawaseBloom.renderBlur(stencilFramebuffer.framebufferTexture, 2, 2);

        RenderUtil.scaleStart(xPosition + (float) width / 2, yPosition + (float) height / 2, 1 + hoverAnimation.getOutput().floatValue());
        RoundedUtil.drawRound(xPosition, yPosition, width, height, 6, new Color(0, 0, 0, 190));
        RenderUtil.scaleEnd();

//        RenderUtil.scaleStart(xPosition + width / 2, yPosition + height / 2, 1 + hoverAnimation.getOutput().floatValue());
        normalFont22.drawStringWithShadow(this.displayString, (xPosition + (float) width / 2) - normalFont22.getStringWidth(this.displayString) / 2f, (yPosition + (float) height / 2) - (float) normalFont22.getHeight() / 2 + 2, -1);
        RenderUtil.scaleEnd();
    }
}
