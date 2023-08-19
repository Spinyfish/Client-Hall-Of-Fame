package dev.thread.api.util.render.shader;

import dev.thread.api.util.render.shader.impl.BlurShader;
import dev.thread.api.util.render.shader.impl.RoundedShader;
import lombok.Getter;

@Getter
public class ShaderManager {
    private final RoundedShader roundedShader;
    private final BlurShader blurShader;

    public ShaderManager() {
        this.roundedShader = new RoundedShader();
        this.blurShader = new BlurShader();
    }
}
