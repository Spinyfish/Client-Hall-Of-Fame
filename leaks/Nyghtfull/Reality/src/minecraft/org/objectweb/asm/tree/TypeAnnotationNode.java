package org.objectweb.asm.tree;

import org.objectweb.asm.TypePath;

public class TypeAnnotationNode extends AnnotationNode {
  public int typeRef;
  
  public TypePath typePath;
  
  public TypeAnnotationNode(int typeRef, TypePath typePath, String descriptor) {
    this(458752, typeRef, typePath, descriptor);
    if (getClass() != TypeAnnotationNode.class)
      throw new IllegalStateException(); 
  }
  
  public TypeAnnotationNode(int api, int typeRef, TypePath typePath, String descriptor) {
    super(api, descriptor);
    this.typeRef = typeRef;
    this.typePath = typePath;
  }
}
