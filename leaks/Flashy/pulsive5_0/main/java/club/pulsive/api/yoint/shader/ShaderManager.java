package club.pulsive.api.yoint.shader;

import club.pulsive.api.yoint.shader.impl.BoxBlur;

import java.util.ArrayList;
import java.util.List;

public class ShaderManager {
    private final List<Shader> shaderList;

    public ShaderManager() {
        shaderList = new ArrayList<>();
    }

    public void init() {
        this.shaderList.add(new BoxBlur());
    }

    public List<Shader> getShaderList() {
        return shaderList;
    }

    public <T extends Shader> T getShader(final Class<T> shaderClass) {
        return (T) shaderList.stream().filter(shader -> shader.getClass() == shaderClass).findFirst().orElse(null);
    }
}