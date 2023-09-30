/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders.config;

import net.optifine.shaders.config.ShaderOption;

public class ScreenShaderOptions {
    private String name;
    private ShaderOption[] shaderOptions;
    private int columns;

    public ScreenShaderOptions(String name, ShaderOption[] shaderOptions, int columns) {
        this.name = name;
        this.shaderOptions = shaderOptions;
        this.columns = columns;
    }

    public String getName() {
        return this.name;
    }

    public ShaderOption[] getShaderOptions() {
        return this.shaderOptions;
    }

    public int getColumns() {
        return this.columns;
    }
}

