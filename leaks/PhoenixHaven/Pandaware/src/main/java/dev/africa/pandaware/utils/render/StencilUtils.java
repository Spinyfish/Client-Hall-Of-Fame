package dev.africa.pandaware.utils.render;

import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import lombok.experimental.UtilityClass;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@UtilityClass
public class StencilUtils implements MinecraftInstance {
    public void stencilStage(StencilStage stencilStage) {
        if (stencilStage == StencilStage.ENABLE_MASK) {
            GL11.glEnable(GL11.GL_STENCIL_TEST);
            GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);

            GL11.glStencilMask(0xFF);
            GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
            GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        } else if (stencilStage == StencilStage.ENABLE_DRAW) {
            GL11.glStencilMask(0x00);
            GL11.glStencilFunc(GL11.GL_NOTEQUAL, 0, 0xFF);
            ColorUtils.glColor(Color.WHITE);
        } else if (stencilStage == StencilStage.DISABLE) {
            GL11.glStencilMask(0xFF);
            GL11.glDisable(GL11.GL_STENCIL_TEST);
        }
    }

    public enum StencilStage {
        ENABLE_MASK, ENABLE_DRAW, DISABLE
    }
}