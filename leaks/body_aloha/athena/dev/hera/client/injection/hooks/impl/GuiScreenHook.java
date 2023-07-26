package dev.hera.client.injection.hooks.impl;

import dev.hera.client.injection.hooks.Hook;
import dev.hera.client.injection.hooks.HookClass;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.*;

import java.util.List;

@HookClass(klass = "net/minecraft/client/gui/GuiIngame")
public class GuiScreenHook extends Hook {


    @Override
    public void inject(ClassReader classReader, ClassNode classNode) {
        for (MethodNode methodNode : (List<MethodNode>) classNode.methods) {

            if (methodNode.name.equals(getMethod("renderGameOverlay", "(F)V")) && methodNode.desc.equals("(F)V")) //thingy
            {
                InsnList list = new InsnList();
                list.add(new TypeInsnNode(NEW, "dev/hera/client/events/impl/EventRenderHUD"));
                list.add(new InsnNode(DUP));
                list.add(new MethodInsnNode(INVOKESPECIAL, "dev/hera/client/events/impl/EventRenderHUD", "<init>", "()V", false));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "dev/hera/client/events/impl/EventRenderHUD", "post", "()Ldev/hera/client/events/types/Event;", false));
                list.add(new InsnNode(POP));
                methodNode.instructions.insertBefore(methodNode.instructions.getLast().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious(), list);
            }
        }
    }
}