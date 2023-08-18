package me.jinthium.straight.api.clickgui.dropdown.components.config;

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

public class ConfigButton extends GuiButton implements Util {

    public ConfigButton(int id, int x, int y, int width, int height, String text){
        super(id, x, y, width, height, text);
        hoverAnimation = new SmoothStepAnimation(175, 0.2f);
        animation = new SmoothStepAnimation(175, 1, Direction.FORWARDS);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        RoundedUtil.drawRoundOutline(this.xPosition, this.yPosition, width, height, 2, 1.5f, new Color(30, 30, 30), new Color(40, 40,40));
        normalFont17.drawCenteredString(this.displayString, this.xPosition + (float) this.width / 2, this.yPosition + normalFont17.getMiddleOfBox(height) + 2, -1);
    }
}
