package org.objectweb.asm.tree;

import java.util.Map;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;

public class InvokeDynamicInsnNode extends AbstractInsnNode {
  public String name;
  
  public String desc;
  
  public Handle bsm;
  
  public Object[] bsmArgs;
  
  public InvokeDynamicInsnNode(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
    super(186);
    this.name = name;
    this.desc = descriptor;
    this.bsm = bootstrapMethodHandle;
    this.bsmArgs = bootstrapMethodArguments;
  }
  
  public int getType() { return 6; }
  
  public void accept(MethodVisitor methodVisitor) {
    methodVisitor.visitInvokeDynamicInsn(this.name, this.desc, this.bsm, this.bsmArgs);
    acceptAnnotations(methodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> clonedLabels) { return (new InvokeDynamicInsnNode(this.name, this.desc, this.bsm, this.bsmArgs)).cloneAnnotations(this); }
}
