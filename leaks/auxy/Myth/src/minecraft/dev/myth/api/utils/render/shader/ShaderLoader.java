package dev.myth.api.utils.render.shader;

import dev.myth.api.utils.render.shader.list.backgrounds.*;
import dev.myth.api.utils.render.shader.list.BlurShader;
import dev.myth.api.utils.render.shader.list.DropShadow;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ShaderLoader {

    public List<ShaderProgram> shaders;

    public ShaderLoader() {
        shaders = new ArrayList<>();
        shaders.add(new DropShadow());
        shaders.add(new BlurShader());
        shaders.add(new BackgroundShader1());
        shaders.add(new BackgroundShader2());
        shaders.add(new BackgroundShader3());
        shaders.add(new BackgroundShader4());
    }


}