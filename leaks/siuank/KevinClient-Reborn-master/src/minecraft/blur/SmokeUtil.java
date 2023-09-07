package blur;

import kevin.persional.milk.utils.ShaderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

public class SmokeUtil {


    public static Minecraft mc=Minecraft.getMinecraft();
    public static ShaderUtil shader = new ShaderUtil("shaders/Shaders/background.frag");


    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        }
        return framebuffer;
    }

    private static float time;


    public static void bindTexture(int texture) {
        glBindTexture(GL_TEXTURE_2D, texture);
    }
    static FloatBuffer createFloatBuffer(float[] l){
        final FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(l.length);
        weightBuffer.put(l);
        return weightBuffer;
    }
    static IntBuffer createIntBuffer(int[] l){
        final IntBuffer weightBuffer = BufferUtils.createIntBuffer(l.length);
        weightBuffer.put(l);
        return weightBuffer;
    }
    public static void render() {
        shader.init();

        shader.setUniformf("iTime", time);
        shader.setUniformf("iResolution", (float) Display.getWidth(), (float) Display.getHeight());
        time += 0.005F * System.nanoTime();


        ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();
        final Tessellator instance = Tessellator.getInstance();
        final WorldRenderer worldRenderer = instance.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(0, height, 0.0D).endVertex();
        worldRenderer.pos(width, height, 0.0D).endVertex();
        worldRenderer.pos(width, 0, 0.0D).endVertex();
        worldRenderer.pos(0, 0, 0.0D).endVertex();
        instance.draw();

        shader.unload();



    }

}
