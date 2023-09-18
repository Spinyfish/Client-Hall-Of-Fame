package org.objectweb.asm.tree;

import java.util.List;
import org.objectweb.asm.MethodVisitor;

public class TryCatchBlockNode {
  public LabelNode start;
  
  public LabelNode end;
  
  public LabelNode handler;
  
  public String type;
  
  public List<TypeAnnotationNode> visibleTypeAnnotations;
  
  public List<TypeAnnotationNode> invisibleTypeAnnotations;
  
  public TryCatchBlockNode(LabelNode start, LabelNode end, LabelNode handler, String type) {
    this.start = start;
    this.end = end;
    this.handler = handler;
    this.type = type;
  }
  
  public void updateIndex(int index) {
    int newTypeRef = 0x42000000 | index << 8;
    if (this.visibleTypeAnnotations != null)
      for (int i = 0, n = this.visibleTypeAnnotations.size(); i < n; i++)
        ((TypeAnnotationNode)this.visibleTypeAnnotations.get(i)).typeRef = newTypeRef;  
    if (this.invisibleTypeAnnotations != null)
      for (int i = 0, n = this.invisibleTypeAnnotations.size(); i < n; i++)
        ((TypeAnnotationNode)this.invisibleTypeAnnotations.get(i)).typeRef = newTypeRef;  
  }
  
  public void accept(MethodVisitor methodVisitor) {
    methodVisitor.visitTryCatchBlock(this.start
        .getLabel(), this.end.getLabel(), (this.handler == null) ? null : this.handler.getLabel(), this.type);
    if (this.visibleTypeAnnotations != null)
      for (int i = 0, n = this.visibleTypeAnnotations.size(); i < n; i++) {
        TypeAnnotationNode typeAnnotation = (TypeAnnotationNode)this.visibleTypeAnnotations.get(i);
        typeAnnotation.accept(methodVisitor
            .visitTryCatchAnnotation(typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, true));
      }  
    if (this.invisibleTypeAnnotations != null)
      for (int i = 0, n = this.invisibleTypeAnnotations.size(); i < n; i++) {
        TypeAnnotationNode typeAnnotation = (TypeAnnotationNode)this.invisibleTypeAnnotations.get(i);
        typeAnnotation.accept(methodVisitor
            .visitTryCatchAnnotation(typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, false));
      }  
  }
}
