package dev.hera.client.injection.hooks;

import dev.hera.client.injection.InjectionHandler;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

public abstract class Hook implements Opcodes {

    public String klass;

    public Hook(){
        klass = this.getClass().getAnnotation(HookClass.class).klass();
    }
    public abstract void inject(ClassReader classReader, ClassNode classNode);

    public String getMethod(String s, String desc){
        return InjectionHandler.remapper.getMethodMap(this.klass + "." + s, desc);
    }


}