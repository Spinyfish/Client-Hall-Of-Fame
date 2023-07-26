package dev.hera.client.injection.hooks.impl;

import dev.hera.client.injection.hooks.Hook;
import dev.hera.client.injection.hooks.HookClass;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

@HookClass(
    klass = "net/minecraft/client/Minecraft"
)
public class MinecraftHook extends Hook {
    @Override
    public void inject(ClassReader classReader, ClassNode classNode) {
        for(MethodNode methodNode : classNode.methods){
            if(methodNode.name.equals(getMethod("runTick", "()V")) && methodNode.desc.equals("()V")){
                InsnList list = new InsnList();
                list.add(new TypeInsnNode(NEW, "dev/hera/client/events/impl/EventTick"));
                list.add(new InsnNode(DUP));
                list.add(new MethodInsnNode(INVOKESPECIAL, "dev/hera/client/events/impl/EventTick", "<init>", "()V", false));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "dev/hera/client/events/impl/EventTick", "post", "()Ldev/hera/client/events/types/Event;", false));
                list.add(new InsnNode(POP));
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), list);
            }
            if (methodNode.name.equals(getMethod("runTick", "()V")) && methodNode.desc.equals("()V"))
            {
                AbstractInsnNode target = null;
                int count = 0;
                for (final AbstractInsnNode abs : methodNode.instructions.toArray()) {
                    if (abs instanceof MethodInsnNode && ((MethodInsnNode)abs).name.equals("getEventKeyState") && ++count == 3) {
                        target = abs.getNext().getNext().getNext().getNext().getNext();
                    }
                }
                final InsnList list = new InsnList();
                list.add(new TypeInsnNode(NEW, "dev/hera/client/events/impl/EventKeyType"));
                list.add(new InsnNode(DUP));
                list.add(new VarInsnNode(Opcodes.ILOAD, 1));
                list.add(new MethodInsnNode(INVOKESPECIAL, "dev/hera/client/events/impl/EventKeyType", "<init>", "(I)V", false));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "dev/hera/client/events/impl/EventKeyType", "post", "()Ldev/hera/client/events/types/Event;", false));
                list.add(new InsnNode(POP));
                methodNode.instructions.insert(target, list);
            }
        }
    }
}