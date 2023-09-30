package org.objectweb.asm.tree;

import java.util.Map;
import org.objectweb.asm.MethodVisitor;

public class MethodInsnNode extends AbstractInsnNode {
  public String owner;
  
  public String name;
  
  public String desc;
  
  public boolean itf;
  
  @Deprecated
  public MethodInsnNode(int opcode, String owner, String name, String descriptor) { this(opcode, owner, name, descriptor, (opcode == 185)); }
  
  public MethodInsnNode(int opcode, String owner, String name, String descriptor, boolean isInterface) {
    super(opcode);
    this.owner = owner;
    this.name = name;
    this.desc = descriptor;
    this.itf = isInterface;
  }
  
  public void setOpcode(int opcode) { this.opcode = opcode; }
  
  public int getType() { return 5; }
  
  public void accept(MethodVisitor methodVisitor) {
    methodVisitor.visitMethodInsn(this.opcode, this.owner, this.name, this.desc, this.itf);
    acceptAnnotations(methodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> clonedLabels) { return (new MethodInsnNode(this.opcode, this.owner, this.name, this.desc, this.itf)).cloneAnnotations(this); }
}
