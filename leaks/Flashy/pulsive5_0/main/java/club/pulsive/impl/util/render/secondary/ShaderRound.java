package club.pulsive.impl.util.render.secondary;

import club.pulsive.impl.util.render.ColorUtil;
import club.pulsive.impl.util.render.RenderUtil;
import club.pulsive.impl.util.render.RoundedUtil;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@UtilityClass
public class ShaderRound {



    public void drawRound(float x, float y, float width, float height, float radius, Color color) {
        GlStateManager.pushMatrix();
        RoundedUtil.drawRoundedRect(x, y, x + width, y + height, 2, color.getRGB());
        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
    }




    public void drawRoundOutline(float x, float y, float width, float height, float radius, float outlineThickness, Color color, Color outlineColor) {
        RoundedUtil.drawRoundedOutline(x, y, x + width, y + height, 8, 3, outlineColor.getRGB());
        RenderUtil.color(-1);
    }




}