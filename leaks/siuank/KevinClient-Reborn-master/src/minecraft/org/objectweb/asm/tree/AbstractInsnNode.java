package org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.MethodVisitor;

public abstract class AbstractInsnNode {
  public static final int INSN = 0;
  
  public static final int INT_INSN = 1;
  
  public static final int VAR_INSN = 2;
  
  public static final int TYPE_INSN = 3;
  
  public static final int FIELD_INSN = 4;
  
  public static final int METHOD_INSN = 5;
  
  public static final int INVOKE_DYNAMIC_INSN = 6;
  
  public static final int JUMP_INSN = 7;
  
  public static final int LABEL = 8;
  
  public static final int LDC_INSN = 9;
  
  public static final int IINC_INSN = 10;
  
  public static final int TABLESWITCH_INSN = 11;
  
  public static final int LOOKUPSWITCH_INSN = 12;
  
  public static final int MULTIANEWARRAY_INSN = 13;
  
  public static final int FRAME = 14;
  
  public static final int LINE = 15;
  
  protected int opcode;
  
  public List<TypeAnnotationNode> visibleTypeAnnotations;
  
  public List<TypeAnnotationNode> invisibleTypeAnnotations;
  
  AbstractInsnNode previousInsn;
  
  AbstractInsnNode nextInsn;
  
  int index;
  
  protected AbstractInsnNode(int opcode) {
    this.opcode = opcode;
    this.index = -1;
  }
  
  public int getOpcode() { return this.opcode; }
  
  public abstract int getType();
  
  public AbstractInsnNode getPrevious() { return this.previousInsn; }
  
  public AbstractInsnNode getNext() { return this.nextInsn; }
  
  public abstract void accept(MethodVisitor paramMethodVisitor);
  
  protected final void acceptAnnotations(MethodVisitor methodVisitor) {
    if (this.visibleTypeAnnotations != null)
      for (int i = 0, n = this.visibleTypeAnnotations.size(); i < n; i++) {
        TypeAnnotationNode typeAnnotation = (TypeAnnotationNode)this.visibleTypeAnnotations.get(i);
        typeAnnotation.accept(methodVisitor
            .visitInsnAnnotation(typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, true));
      }  
    if (this.invisibleTypeAnnotations != null)
      for (int i = 0, n = this.invisibleTypeAnnotations.size(); i < n; i++) {
        TypeAnnotationNode typeAnnotation = (TypeAnnotationNode)this.invisibleTypeAnnotations.get(i);
        typeAnnotation.accept(methodVisitor
            .visitInsnAnnotation(typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, false));
      }  
  }
  
  public abstract AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap);
  
  static LabelNode clone(LabelNode label, Map<LabelNode, LabelNode> clonedLabels) { return (LabelNode)clonedLabels.get(label); }
  
  static LabelNode[] clone(List<LabelNode> labels, Map<LabelNode, LabelNode> clonedLabels) {
    LabelNode[] clones = new LabelNode[labels.size()];
    for (int i = 0, n = clones.length; i < n; i++)
      clones[i] = (LabelNode)clonedLabels.get(labels.get(i)); 
    return clones;
  }
  
  protected final AbstractInsnNode cloneAnnotations(AbstractInsnNode insnNode) {
    if (insnNode.visibleTypeAnnotations != null) {
      this.visibleTypeAnnotations = new ArrayList();
      for (int i = 0, n = insnNode.visibleTypeAnnotations.size(); i < n; i++) {
        TypeAnnotationNode sourceAnnotation = (TypeAnnotationNode)insnNode.visibleTypeAnnotations.get(i);
        TypeAnnotationNode cloneAnnotation = new TypeAnnotationNode(sourceAnnotation.typeRef, sourceAnnotation.typePath, sourceAnnotation.desc);
        sourceAnnotation.accept(cloneAnnotation);
        this.visibleTypeAnnotations.add(cloneAnnotation);
      } 
    } 
    if (insnNode.invisibleTypeAnnotations != null) {
      this.invisibleTypeAnnotations = new ArrayList();
      for (int i = 0, n = insnNode.invisibleTypeAnnotations.size(); i < n; i++) {
        TypeAnnotationNode sourceAnnotation = (TypeAnnotationNode)insnNode.invisibleTypeAnnotations.get(i);
        TypeAnnotationNode cloneAnnotation = new TypeAnnotationNode(sourceAnnotation.typeRef, sourceAnnotation.typePath, sourceAnnotation.desc);
        sourceAnnotation.accept(cloneAnnotation);
        this.invisibleTypeAnnotations.add(cloneAnnotation);
      } 
    } 
    return this;
  }
}
