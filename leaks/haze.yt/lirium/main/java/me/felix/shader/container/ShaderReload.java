package me.felix.shader.container;

public abstract class ShaderReload {

    //Status for shader running, -1 = not running, 1 = running

    public int status = -1;

    abstract void reload();

}
