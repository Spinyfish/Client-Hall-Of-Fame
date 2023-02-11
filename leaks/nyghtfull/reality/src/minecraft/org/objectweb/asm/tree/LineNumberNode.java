package org.objectweb.asm.tree;

import java.util.Map;
import org.objectweb.asm.MethodVisitor;

public class LineNumberNode extends AbstractInsnNode {
  public int line;
  
  public LabelNode start;
  
  public LineNumberNode(int line, LabelNode start) {
    super(-1);
    this.line = line;
    this.start = start;
  }
  
  public int getType() { return 15; }
  
  public void accept(MethodVisitor methodVisitor) { methodVisitor.visitLineNumber(this.line, this.start.getLabel()); }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> clonedLabels) { return new LineNumberNode(this.line, clone(this.start, clonedLabels)); }
}
