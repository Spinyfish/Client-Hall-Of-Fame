package me.felix.shader.container;

import de.lirium.util.interfaces.IMinecraft;
import me.felix.shader.annotation.Info;
import me.felix.shader.data.ShaderRenderType;
import me.felix.shader.render.Type;

import java.util.List;

//Copied idea from rise,
//We don't make each shader separate, we handle all shader with a runnable

public abstract class ShaderContainer extends ShaderReload implements IMinecraft {

    public String vert, frag;

    public ShaderContainer() {
        final Info info = getClass().getAnnotation(Info.class);
        vert = info.vert();
        frag = info.frag();
    }


    protected abstract void reload();

    public abstract void doRender(final ShaderRenderType type, Type renderType, List<Runnable> runnables);

}
