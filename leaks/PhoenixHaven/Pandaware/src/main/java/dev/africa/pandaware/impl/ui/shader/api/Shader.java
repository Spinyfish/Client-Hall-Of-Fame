package dev.africa.pandaware.impl.ui.shader.api;

import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import dev.africa.pandaware.impl.ui.shader.utils.ShaderUtils;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class Shader implements IShader, MinecraftInstance {
    private final ShaderUtils.ShaderProgram shaderProgram;
    private final Map<String, Integer> uniformsMap;

    public Shader(String fragmentName) {
        this.shaderProgram = ShaderUtils.loadShader(
                "/assets/minecraft/pandaware/shader/vertex.vert",
                "/assets/minecraft/pandaware/shader/" + fragmentName
        );

        this.uniformsMap = new HashMap<>();
    }

    public void useProgram() {
        GL20.glUseProgram(this.shaderProgram.getProgramID());
    }

    public void disableProgram() {
        GL20.glUseProgram(0);
    }

    public void setUniform1i(String name, int value) {
        this.setupUniform(name);

        GL20.glUniform1i(this.uniformsMap.get(name), value);
    }

    public void setUniform1f(String name, float value) {
        this.setupUniform(name);

        GL20.glUniform1f(this.uniformsMap.get(name), value);
    }

    public void setUniform1(String name, FloatBuffer value) {
        this.setupUniform(name);

        GL20.glUniform1(this.uniformsMap.get(name), value);
    }

    public void setUniform2f(String name, float first, float second) {
        this.setupUniform(name);

        GL20.glUniform2f(this.uniformsMap.get(name), first, second);
    }

    public void setUniform3f(String name, float first, float second, float third) {
        this.setupUniform(name);

        GL20.glUniform3f(this.uniformsMap.get(name), first, second, third);
    }

    public void setUniform4f(String name, float first, float second, float third, float fourth) {
        this.setupUniform(name);

        GL20.glUniform4f(this.uniformsMap.get(name), first, second, third, fourth);
    }

    void setupUniform(String uniformName) {
        if (!this.uniformsMap.containsKey(uniformName)) {
            this.uniformsMap.put(uniformName, GL20.glGetUniformLocation(this.shaderProgram.getProgramID(), uniformName));
        }
    }
}