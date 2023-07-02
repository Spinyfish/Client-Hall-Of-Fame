package me.eldodebug.soar.utils.shader;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonSyntaxException;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.mods.impl.MotionBlurMod;
import me.eldodebug.soar.utils.interfaces.IMixinShaderGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.util.ResourceLocation;

public class MotionBlurUtils {

    public static final MotionBlurUtils instance = new MotionBlurUtils();

    private static final ResourceLocation location = new ResourceLocation("minecraft:shaders/post/motion_blur.json");
    private static final Logger logger = LogManager.getLogger();

    private Minecraft mc = Minecraft.getMinecraft();
    private ShaderGroup shader;
    private float shaderBlur;
    
    public float getBlurFactor() {
        return (float) Soar.instance.settingsManager.getSettingByClass(MotionBlurMod.class, "Amount").getValDouble();
    }

    public ShaderGroup getShader() {

        if (shader == null) {
            shaderBlur = Float.NaN;

            try {
                shader = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), location);
                shader.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
            } catch (JsonSyntaxException | IOException error) {
            	logger.error("Could not load motion blur shader", error);
                return null;
            }
        }
        
        if (shaderBlur != getBlurFactor()) {
            ((IMixinShaderGroup)shader).getListShaders().forEach((shader) -> {
                ShaderUniform blendFactorUniform = shader.getShaderManager().getShaderUniform("BlurFactor");

                if (blendFactorUniform != null) {
                    blendFactorUniform.set(getBlurFactor());
                }
            });

            shaderBlur = getBlurFactor();
        }

        return shader;
    }

}
