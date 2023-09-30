package me.felix.shader.access;

import me.felix.shader.shaders.BloomShader;
import me.felix.shader.shaders.BlurShader;

import java.util.ArrayList;
import java.util.List;

public interface ShaderAccess {

    BlurShader blurShader = new BlurShader();

    List<Runnable> blurShaderRunnables = new ArrayList<>();


    BloomShader bloomShader = new BloomShader();

    List<Runnable> bloomRunnables = new ArrayList<>();


}
