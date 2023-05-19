package club.marsh.bloom.impl.utils.render.shaders;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;

import static org.lwjgl.opengl.GL11.*;

public abstract class Shader  {

    protected int pass;
    Minecraft mc = Minecraft.getMinecraft();

    protected ShaderProgram shaderProgram;
    protected Framebuffer frameBuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, false);

    protected float width, height;

    public Shader(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }

    public abstract void setUniforms();

    public void render(float x, float y, float width, float height) {
        this.width = width;
        this.height = height;
        if (frameBuffer.framebufferWidth != mc.displayWidth || frameBuffer.framebufferHeight != mc.displayHeight) {
            frameBuffer.deleteFramebuffer();
            frameBuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, false);
        }
        frameBuffer.framebufferClear();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        mc.getFramebuffer().bindFramebuffer(false);
        doShaderPass(x, y, mc.getFramebuffer());
        pass++;
    }

    public static Framebuffer createFramebuffer(Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != Minecraft.getMinecraft().displayWidth || framebuffer.framebufferHeight != Minecraft.getMinecraft().displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, true);
        }
        return framebuffer;
    }


    private void doShaderPass(float x, float y, Framebuffer framebufferIn) {
        shaderProgram.init();
        setUniforms();
        glBindTexture(GL_TEXTURE_2D, framebufferIn.framebufferTexture);
        shaderProgram.renderCanvas();
        shaderProgram.uninit();
    }

    public int pass() {
        return pass;
    }

    public void pass(int pass) {
        this.pass = pass;
    }
}