package dev.myth.managers;

import dev.myth.api.manager.Manager;
import dev.myth.api.utils.render.shader.ShaderLoader;
import dev.myth.api.utils.render.shader.ShaderProgram;
import dev.myth.api.utils.render.shader.list.CoolShader;
import dev.myth.api.utils.render.shader.list.KawaseBlurShader;
import dev.myth.api.utils.render.shader.list.RoundedRectShader;
import dev.myth.api.utils.render.shader.list.backgrounds.BackgroundShader1;
import dev.myth.api.utils.render.shader.list.BlurShader;
import dev.myth.api.utils.render.shader.list.backgrounds.BackgroundShader2;
import dev.myth.api.utils.render.shader.list.backgrounds.BackgroundShader3;
import dev.myth.api.utils.render.shader.list.backgrounds.BackgroundShader4;
import dev.myth.main.ClientMain;
import lombok.Getter;

public class ShaderManager implements Manager {

    /** Shader Instances */
    public final BlurShader BLUR_SHADER = new BlurShader();
    public final RoundedRectShader ROUNDED_RECT_SHADER = new RoundedRectShader();
    public final BackgroundShader1 BACKGROUND_SHADER1 = new BackgroundShader1();
    public final BackgroundShader2 BACKGROUND_SHADER2 = new BackgroundShader2();
    public final BackgroundShader3 BACKGROUND_SHADER3 = new BackgroundShader3();
    public final BackgroundShader4 BACKGROUND_SHADER4 = new BackgroundShader4();
    public final KawaseBlurShader KAWASE_BLUR_SHADER = new KawaseBlurShader();



    @Getter
    ShaderLoader shaderLoader;

    @Override
    public void run() {

    }

    @Override
    public void shutdown() {
    }

    @SuppressWarnings("unchecked")
    public <T extends ShaderProgram> T getShader(Class<T> tClass) {
        return (T) shaderLoader.shaders.stream().filter(mod -> mod.getClass().equals(tClass)).findFirst().orElse(null);
    }
}
