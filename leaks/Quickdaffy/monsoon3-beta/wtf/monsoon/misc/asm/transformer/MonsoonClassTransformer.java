/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.launchwrapper.IClassTransformer
 *  org.objectweb.asm.ClassReader
 *  org.objectweb.asm.ClassVisitor
 *  org.objectweb.asm.ClassWriter
 *  org.objectweb.asm.tree.AbstractInsnNode
 *  org.objectweb.asm.tree.ClassNode
 *  org.objectweb.asm.tree.InsnList
 *  org.objectweb.asm.tree.InsnNode
 *  org.objectweb.asm.tree.MethodInsnNode
 *  org.objectweb.asm.tree.MethodNode
 *  org.objectweb.asm.tree.TypeInsnNode
 *  org.objectweb.asm.tree.VarInsnNode
 */
package wtf.monsoon.misc.asm.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class MonsoonClassTransformer
implements IClassTransformer {
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (name.equals("org.lwjgl.nanovg.NanoVGGLConfig")) {
            ClassReader reader = new ClassReader(basicClass);
            ClassNode node = new ClassNode();
            reader.accept((ClassVisitor)node, 8);
            for (MethodNode method : node.methods) {
                if (!method.name.equals("configGL")) continue;
                InsnList list = new InsnList();
                list.add((AbstractInsnNode)new VarInsnNode(22, 0));
                list.add((AbstractInsnNode)new TypeInsnNode(187, "me/djtheredstoner/lwjgl/Lwjgl2FunctionProvider"));
                list.add((AbstractInsnNode)new InsnNode(89));
                list.add((AbstractInsnNode)new MethodInsnNode(183, "me/djtheredstoner/lwjgl/Lwjgl2FunctionProvider", "<init>", "()V", false));
                list.add((AbstractInsnNode)new MethodInsnNode(184, "org/lwjgl/nanovg/NanoVGGLConfig", "config", "(JLorg/lwjgl/system/FunctionProvider;)V", false));
                list.add((AbstractInsnNode)new InsnNode(177));
                method.instructions.clear();
                method.instructions.insert(list);
            }
            ClassWriter cw = new ClassWriter(2);
            node.accept((ClassVisitor)cw);
            return cw.toByteArray();
        }
        return basicClass;
    }
}

