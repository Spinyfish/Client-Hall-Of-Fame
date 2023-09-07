package org.objectweb.asm.tree;

import java.util.Map;
import org.objectweb.asm.MethodVisitor;

public class IntInsnNode extends AbstractInsnNode {
  public int operand;
  
  public IntInsnNode(int opcode, int operand) {
    super(opcode);
    this.operand = operand;
  }
  
  public void setOpcode(int opcode) { this.opcode = opcode; }
  
  public int getType() { return 1; }
  
  public void accept(MethodVisitor methodVisitor) {
    methodVisitor.visitIntInsn(this.opcode, this.operand);
    acceptAnnotations(methodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> clonedLabels) { return (new IntInsnNode(this.opcode, this.operand)).cloneAnnotations(this); }
}
