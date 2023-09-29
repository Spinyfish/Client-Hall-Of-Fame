package me.jinthium.scripting.api.objects;

import me.jinthium.straight.api.shader.ShaderUtil;

public class ScriptShaderUtil {
    private final ShaderUtil shaderUtil;

    public ScriptShaderUtil(String fragSource) {
        this.shaderUtil = new ShaderUtil(fragSource, false);
    }

    public void init() {
        shaderUtil.init();
    }

    public void unload() {
        shaderUtil.unload();
    }

    public void setUniformf(String name, float... args) {
        shaderUtil.setUniformf(name, args);
    }

    public void setUniformi(String name, int... args) {
        shaderUtil.setUniformi(name, args);
    }

    public void drawQuads() {
        ShaderUtil.drawQuads();
    }

    public void drawQuads(float x, float y, float width, float height) {
        ShaderUtil.drawQuads(x, y, width, height);
    }
}
