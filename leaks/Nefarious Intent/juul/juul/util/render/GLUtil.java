package juul.util.render;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class GLUtil {


    public static void render(int mode, Runnable render) {
        glBegin(mode);
        render.run();
        glEnd();
    }

    public static void matrix(Runnable f) {
        GlStateManager.pushMatrix();
        f.run();
        GlStateManager.popMatrix();
    }

    public static void disableDepthTest(Runnable f) {
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        f.run();
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
    }

    public static void enable(Runnable f, int... caps) {
        for (int cap : caps) glEnable(cap);
        f.run();
        for (int cap : caps) glDisable(cap);
    }

    public static void setup2DRendering(Runnable f) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_TEXTURE_2D);
        f.run();
        glEnable(GL_TEXTURE_2D);
        GlStateManager.disableBlend();
    }

    public static void rotate(float x, float y, float rotate, Runnable f) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.rotate(rotate, 0, 0, -1);
        GlStateManager.translate(-x, -y, 0);
        f.run();
        GlStateManager.popMatrix();
    }


}
