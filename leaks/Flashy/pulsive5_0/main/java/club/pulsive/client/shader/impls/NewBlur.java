package club.pulsive.client.shader.impls;

import club.pulsive.api.minecraft.MinecraftUtil;
import club.pulsive.impl.module.impl.visual.Shaders;
import club.pulsive.impl.util.render.RenderUtil;
import club.pulsive.impl.util.render.Shader;
import club.pulsive.impl.util.render.secondary.ShaderUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

@Getter
@Setter
public class NewBlur implements MinecraftUtil {
    private final ShaderUtil shaderUtil = new ShaderUtil("newBlur.frag");
    private Framebuffer blurFramebuffer = new Framebuffer(1, 1, false);
    public float radius;
    
    public NewBlur(){
        radius = Shaders.blurRadius.getValue().floatValue();
    }
    
    public void renderBlur(){
        renderBlur(0, 0, mc.displayWidth, mc.displayHeight);
    }
    
    public void renderBlur(float x, float y, float width, float height){
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        blurFramebuffer = RenderUtil.createFramebuffer(blurFramebuffer, false);

        // horizontal blur
        shaderUtil.init();
        setupUniforms(1, 0, width, height);
        blurFramebuffer.framebufferClear();
        blurFramebuffer.bindFramebuffer(true);
        glBindTexture(GL_TEXTURE_2D, mc.getFramebuffer().framebufferTexture);
        ShaderUtil.drawQuads();
        blurFramebuffer.unbindFramebuffer();

        // vertical blur
        shaderUtil.init();
        setupUniforms(0, 1, width, height);
        mc.getFramebuffer().bindFramebuffer(true);
        glBindTexture(GL_TEXTURE_2D, blurFramebuffer.framebufferTexture);
        ShaderUtil.drawQuads();
        shaderUtil.unload();
    }
    
    public void setupUniforms(int x, int y, float width, float height){
        shaderUtil.setUniformi("originalTexture", 0);
        shaderUtil.setUniformf("texelSize", 1 / width, 1 / height);
        shaderUtil.setUniformf("direction", x, y);
        shaderUtil.setUniformf("radius", radius);
    }
}
