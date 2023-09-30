package org.objectweb.asm.tree;

import java.util.Map;
import org.objectweb.asm.MethodVisitor;

public class VarInsnNode extends AbstractInsnNode {
  public int var;
  
  public VarInsnNode(int opcode, int var) {
    super(opcode);
    this.var = var;
  }
  
  public void setOpcode(int opcode) { this.opcode = opcode; }
  
  public int getType() { return 2; }
  
  public void accept(MethodVisitor methodVisitor) {
    methodVisitor.visitVarInsn(this.opcode, this.var);
    acceptAnnotations(methodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> clonedLabels) { return (new VarInsnNode(this.opcode, this.var)).cloneAnnotations(this); }
}
