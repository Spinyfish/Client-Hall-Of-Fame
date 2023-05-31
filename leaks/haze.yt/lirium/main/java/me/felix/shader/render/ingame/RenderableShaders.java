package me.felix.shader.render.ingame;

import me.felix.shader.access.ShaderAccess;
import me.felix.shader.data.ShaderRenderType;
import me.felix.shader.render.Type;

public interface RenderableShaders {

    static void render() {
        ShaderAccess.bloomShader.doRender(ShaderRenderType.OVERLAY, Type.QUADS, ShaderAccess.bloomRunnables);
        ShaderAccess.blurShader.doRender(ShaderRenderType.OVERLAY, Type.QUADS, ShaderAccess.blurShaderRunnables);
    }

}
