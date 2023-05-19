package club.marsh.bloom.impl.utils.render.shaders;


import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;

import java.util.concurrent.Callable;

import static org.lwjgl.opengl.GL11.*;

public class BloomShader{

    private String bloomShaderSource = "#version 120\n" +
            "\n" +
            "uniform sampler2D originalTexture;\n" +
            "uniform float shadowAlpha;\n" +
            "uniform vec2 texelSize, direction;\n" +
            "uniform const float radius = 10;\n" +
            "\n" +
            "float gauss(float x, float sigma) {\n" +
            "    return .4 * exp(-.5 * x * x / (sigma * sigma)) / sigma;\n" +
            "}\n" +
            "\n" +
            "void main() {\n" +
            "    //    if (direction.y == 1)\n" +
            "    //        if(texture2D(checkedTexture, gl_TexCoord[0].st).a != 0.0) return;\n" +
            "\n" +
            "    float alpha = 0.0;\n" +
            "    for(float i = -radius; i <= radius; i++) {\n" +
            "        alpha += texture2D(originalTexture, gl_TexCoord[0].st + i * texelSize * direction).a * gauss(i, (radius * shadowAlpha) / 2);\n" +
            "    }\n" +
            "    gl_FragColor = vec4(0.0,0.0,0.0, alpha);\n" +
            "}";
    private ShaderProgram bloomShader = new ShaderProgram(bloomShaderSource);

    private static Framebuffer bloomBuffer = new Framebuffer(1, 1, false);

    public float radius;

    public BloomShader(int radius){
        this.radius = radius;
    }

    public void bloom(Callable<Void> callable) {

        bloomBuffer = Shader.createFramebuffer(bloomBuffer);
        // horizontal bloom
        bloomShader.init();
        setupUniforms(1, 0, 0);
        bloomBuffer.framebufferClear();
        bloomBuffer.bindFramebuffer(true);
        glBindTexture(GL_TEXTURE_2D, Minecraft.getMinecraft().getFramebuffer().framebufferTexture);
        bloomShader.renderCanvas();
        bloomBuffer.unbindFramebuffer();

        // vertical bloom
        bloomShader.init();
        setupUniforms(0, 1, 0);
        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
        glBindTexture(GL_TEXTURE_2D, bloomBuffer.framebufferTexture);
        bloomShader.renderCanvas();
        bloomShader.uninit();


    }

    public void setupUniforms(float x, float y, int textureID) {
        bloomShader.setUniformi("originalTexture", 0);
        bloomShader.setUniformi("checkedTexture", textureID);
        bloomShader.setUniformf("texelSize", (float) (1.0 / Minecraft.getMinecraft().displayWidth), (float) (1.0 / Minecraft.getMinecraft().displayHeight));
        bloomShader.setUniformf("direction", x, y);
        bloomShader.setUniformf("shadowAlpha", 120);
        bloomShader.setUniformf("radius", radius);
    }
}