package org.objectweb.asm.tree;

import org.objectweb.asm.MethodVisitor;

public class LocalVariableNode {
  public String name;
  
  public String desc;
  
  public String signature;
  
  public LabelNode start;
  
  public LabelNode end;
  
  public int index;
  
  public LocalVariableNode(String name, String descriptor, String signature, LabelNode start, LabelNode end, int index) {
    this.name = name;
    this.desc = descriptor;
    this.signature = signature;
    this.start = start;
    this.end = end;
    this.index = index;
  }
  
  public void accept(MethodVisitor methodVisitor) {
    methodVisitor.visitLocalVariable(this.name, this.desc, this.signature, this.start
        .getLabel(), this.end.getLabel(), this.index);
  }
}
