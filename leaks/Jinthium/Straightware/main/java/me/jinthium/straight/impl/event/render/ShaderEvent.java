package me.jinthium.straight.impl.event.render;

import me.jinthium.straight.api.event.Event;

public class ShaderEvent extends Event {
    private final BLUR_TYPE blurType;
    private final SHADER_TYPE shaderType;

    public ShaderEvent(SHADER_TYPE shaderType, BLUR_TYPE blurType) {
        this.blurType = blurType;
        this.shaderType = shaderType;
    }

    public boolean isBloom() {
        return shaderType == SHADER_TYPE.BLOOM;
    }

    public boolean isBlur(){
        return shaderType == SHADER_TYPE.BLUR;
    }

    public BLUR_TYPE getBlurType() {
        return blurType;
    }

    public enum BLUR_TYPE {
        GAUSSIAN,
        KAWASE
    }

    public enum SHADER_TYPE {
        BLUR,
        BLOOM
    }
}
