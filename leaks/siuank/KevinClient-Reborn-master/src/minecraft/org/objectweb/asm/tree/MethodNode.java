package org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

public class MethodNode extends MethodVisitor {
  public int access;
  
  public String name;
  
  public String desc;
  
  public String signature;
  
  public List<String> exceptions;
  
  public List<ParameterNode> parameters;
  
  public List<AnnotationNode> visibleAnnotations;
  
  public List<AnnotationNode> invisibleAnnotations;
  
  public List<TypeAnnotationNode> visibleTypeAnnotations;
  
  public List<TypeAnnotationNode> invisibleTypeAnnotations;
  
  public List<Attribute> attrs;
  
  public Object annotationDefault;
  
  public int visibleAnnotableParameterCount;
  
  public List<AnnotationNode>[] visibleParameterAnnotations;
  
  public int invisibleAnnotableParameterCount;
  
  public List<AnnotationNode>[] invisibleParameterAnnotations;
  
  public InsnList instructions;
  
  public List<TryCatchBlockNode> tryCatchBlocks;
  
  public int maxStack;
  
  public int maxLocals;
  
  public List<LocalVariableNode> localVariables;
  
  public List<LocalVariableAnnotationNode> visibleLocalVariableAnnotations;
  
  public List<LocalVariableAnnotationNode> invisibleLocalVariableAnnotations;
  
  private boolean visited;
  
  public MethodNode() {
    this(458752);
    if (getClass() != MethodNode.class)
      throw new IllegalStateException(); 
  }
  
  public MethodNode(int api) {
    super(api);
    this.instructions = new InsnList();
  }
  
  public MethodNode(int access, String name, String descriptor, String signature, String[] exceptions) {
    this(458752, access, name, descriptor, signature, exceptions);
    if (getClass() != MethodNode.class)
      throw new IllegalStateException(); 
  }
  
  public MethodNode(int api, int access, String name, String descriptor, String signature, String[] exceptions) {
    super(api);
    this.access = access;
    this.name = name;
    this.desc = descriptor;
    this.signature = signature;
    this.exceptions = Util.asArrayList(exceptions);
    if ((access & 0x400) == 0)
      this.localVariables = new ArrayList(5); 
    this.tryCatchBlocks = new ArrayList();
    this.instructions = new InsnList();
  }
  
  public void visitParameter(String name, int access) {
    if (this.parameters == null)
      this.parameters = new ArrayList(5); 
    this.parameters.add(new ParameterNode(name, access));
  }
  
  public AnnotationVisitor visitAnnotationDefault() {
    return new AnnotationNode(new ArrayList<Object>( 0) {
          public boolean add(Object o) {
            MethodNode.this.annotationDefault = o;
            return super.add(o);
          }
        });
  }
  
  public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
    AnnotationNode annotation = new AnnotationNode(descriptor);
    if (visible) {
      if (this.visibleAnnotations == null)
        this.visibleAnnotations = new ArrayList(1); 
      this.visibleAnnotations.add(annotation);
    } else {
      if (this.invisibleAnnotations == null)
        this.invisibleAnnotations = new ArrayList(1); 
      this.invisibleAnnotations.add(annotation);
    } 
    return annotation;
  }
  
  public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
    TypeAnnotationNode typeAnnotation = new TypeAnnotationNode(typeRef, typePath, descriptor);
    if (visible) {
      if (this.visibleTypeAnnotations == null)
        this.visibleTypeAnnotations = new ArrayList(1); 
      this.visibleTypeAnnotations.add(typeAnnotation);
    } else {
      if (this.invisibleTypeAnnotations == null)
        this.invisibleTypeAnnotations = new ArrayList(1); 
      this.invisibleTypeAnnotations.add(typeAnnotation);
    } 
    return typeAnnotation;
  }
  
  public void visitAnnotableParameterCount(int parameterCount, boolean visible) {
    if (visible) {
      this.visibleAnnotableParameterCount = parameterCount;
    } else {
      this.invisibleAnnotableParameterCount = parameterCount;
    } 
  }
  
  public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
    AnnotationNode annotation = new AnnotationNode(descriptor);
    if (visible) {
      if (this.visibleParameterAnnotations == null) {
        int params = Type.getArgumentTypes(this.desc).length;
        this.visibleParameterAnnotations = (List[])new List[params];
      } 
      if (this.visibleParameterAnnotations[parameter] == null)
        this.visibleParameterAnnotations[parameter] = new ArrayList(1); 
      this.visibleParameterAnnotations[parameter].add(annotation);
    } else {
      if (this.invisibleParameterAnnotations == null) {
        int params = Type.getArgumentTypes(this.desc).length;
        this.invisibleParameterAnnotations = (List[])new List[params];
      } 
      if (this.invisibleParameterAnnotations[parameter] == null)
        this.invisibleParameterAnnotations[parameter] = new ArrayList(1); 
      this.invisibleParameterAnnotations[parameter].add(annotation);
    } 
    return annotation;
  }
  
  public void visitAttribute(Attribute attribute) {
    if (this.attrs == null)
      this.attrs = new ArrayList(1); 
    this.attrs.add(attribute);
  }
  
  public void visitCode() {}
  
  public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
    this.instructions.add(new FrameNode(type, numLocal, (local == null) ? null : 
          
          getLabelNodes(local), numStack, (stack == null) ? null : 
          
          getLabelNodes(stack)));
  }
  
  public void visitInsn(int opcode) { this.instructions.add(new InsnNode(opcode)); }
  
  public void visitIntInsn(int opcode, int operand) { this.instructions.add(new IntInsnNode(opcode, operand)); }
  
  public void visitVarInsn(int opcode, int var) { this.instructions.add(new VarInsnNode(opcode, var)); }
  
  public void visitTypeInsn(int opcode, String type) { this.instructions.add(new TypeInsnNode(opcode, type)); }
  
  public void visitFieldInsn(int opcode, String owner, String name, String descriptor) { this.instructions.add(new FieldInsnNode(opcode, owner, name, descriptor)); }
  
  @Deprecated
  public void visitMethodInsn(int opcode, String owner, String name, String descriptor) {
    if (this.api >= 327680) {
      super.visitMethodInsn(opcode, owner, name, descriptor);
      return;
    } 
    this.instructions.add(new MethodInsnNode(opcode, owner, name, descriptor));
  }
  
  public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
    if (this.api < 327680) {
      super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
      return;
    } 
    this.instructions.add(new MethodInsnNode(opcode, owner, name, descriptor, isInterface));
  }
  
  public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) { this.instructions.add(new InvokeDynamicInsnNode(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments)); }
  
  public void visitJumpInsn(int opcode, Label label) { this.instructions.add(new JumpInsnNode(opcode, getLabelNode(label))); }
  
  public void visitLabel(Label label) { this.instructions.add(getLabelNode(label)); }
  
  public void visitLdcInsn(Object value) { this.instructions.add(new LdcInsnNode(value)); }
  
  public void visitIincInsn(int var, int increment) { this.instructions.add(new IincInsnNode(var, increment)); }
  
  public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) { this.instructions.add(new TableSwitchInsnNode(min, max, getLabelNode(dflt), getLabelNodes(labels))); }
  
  public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) { this.instructions.add(new LookupSwitchInsnNode(getLabelNode(dflt), keys, getLabelNodes(labels))); }
  
  public void visitMultiANewArrayInsn(String descriptor, int numDimensions) { this.instructions.add(new MultiANewArrayInsnNode(descriptor, numDimensions)); }
  
  public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
    AbstractInsnNode currentInsn = this.instructions.getLast();
    while (currentInsn.getOpcode() == -1)
      currentInsn = currentInsn.getPrevious(); 
    TypeAnnotationNode typeAnnotation = new TypeAnnotationNode(typeRef, typePath, descriptor);
    if (visible) {
      if (currentInsn.visibleTypeAnnotations == null)
        currentInsn.visibleTypeAnnotations = new ArrayList(1); 
      currentInsn.visibleTypeAnnotations.add(typeAnnotation);
    } else {
      if (currentInsn.invisibleTypeAnnotations == null)
        currentInsn.invisibleTypeAnnotations = new ArrayList(1); 
      currentInsn.invisibleTypeAnnotations.add(typeAnnotation);
    } 
    return typeAnnotation;
  }
  
  public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
    this.tryCatchBlocks.add(new TryCatchBlockNode(
          getLabelNode(start), getLabelNode(end), getLabelNode(handler), type));
  }
  
  public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
    TryCatchBlockNode tryCatchBlock = (TryCatchBlockNode)this.tryCatchBlocks.get((typeRef & 0xFFFF00) >> 8);
    TypeAnnotationNode typeAnnotation = new TypeAnnotationNode(typeRef, typePath, descriptor);
    if (visible) {
      if (tryCatchBlock.visibleTypeAnnotations == null)
        tryCatchBlock.visibleTypeAnnotations = new ArrayList(1); 
      tryCatchBlock.visibleTypeAnnotations.add(typeAnnotation);
    } else {
      if (tryCatchBlock.invisibleTypeAnnotations == null)
        tryCatchBlock.invisibleTypeAnnotations = new ArrayList(1); 
      tryCatchBlock.invisibleTypeAnnotations.add(typeAnnotation);
    } 
    return typeAnnotation;
  }
  
  public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
    this.localVariables.add(new LocalVariableNode(name, descriptor, signature, 
          
          getLabelNode(start), getLabelNode(end), index));
  }
  
  public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible) {
    LocalVariableAnnotationNode localVariableAnnotation = new LocalVariableAnnotationNode(typeRef, typePath, getLabelNodes(start), getLabelNodes(end), index, descriptor);
    if (visible) {
      if (this.visibleLocalVariableAnnotations == null)
        this.visibleLocalVariableAnnotations = new ArrayList(1); 
      this.visibleLocalVariableAnnotations.add(localVariableAnnotation);
    } else {
      if (this.invisibleLocalVariableAnnotations == null)
        this.invisibleLocalVariableAnnotations = new ArrayList(1); 
      this.invisibleLocalVariableAnnotations.add(localVariableAnnotation);
    } 
    return localVariableAnnotation;
  }
  
  public void visitLineNumber(int line, Label start) { this.instructions.add(new LineNumberNode(line, getLabelNode(start))); }
  
  public void visitMaxs(int maxStack, int maxLocals) {
    this.maxStack = maxStack;
    this.maxLocals = maxLocals;
  }
  
  public void visitEnd() {}
  
  protected LabelNode getLabelNode(Label label) {
    if (!(label.info instanceof LabelNode))
      label.info = new LabelNode(); 
    return (LabelNode)label.info;
  }
  
  private LabelNode[] getLabelNodes(Label[] labels) {
    LabelNode[] labelNodes = new LabelNode[labels.length];
    for (int i = 0, n = labels.length; i < n; i++)
      labelNodes[i] = getLabelNode(labels[i]); 
    return labelNodes;
  }
  
  private Object[] getLabelNodes(Object[] objects) {
    Object[] labelNodes = new Object[objects.length];
    for (int i = 0, n = objects.length; i < n; i++) {
      Object o = objects[i];
      if (o instanceof Label)
        o = getLabelNode((Label)o); 
      labelNodes[i] = o;
    } 
    return labelNodes;
  }
  
  public void check(int api) {
    if (api == 262144) {
      if (this.parameters != null && !this.parameters.isEmpty())
        throw new UnsupportedClassVersionException(); 
      if (this.visibleTypeAnnotations != null && !this.visibleTypeAnnotations.isEmpty())
        throw new UnsupportedClassVersionException(); 
      if (this.invisibleTypeAnnotations != null && !this.invisibleTypeAnnotations.isEmpty())
        throw new UnsupportedClassVersionException(); 
      if (this.tryCatchBlocks != null)
        for (int i = this.tryCatchBlocks.size() - 1; i >= 0; i--) {
          TryCatchBlockNode tryCatchBlock = (TryCatchBlockNode)this.tryCatchBlocks.get(i);
          if (tryCatchBlock.visibleTypeAnnotations != null && 
            !tryCatchBlock.visibleTypeAnnotations.isEmpty())
            throw new UnsupportedClassVersionException(); 
          if (tryCatchBlock.invisibleTypeAnnotations != null && 
            !tryCatchBlock.invisibleTypeAnnotations.isEmpty())
            throw new UnsupportedClassVersionException(); 
        }  
      for (int i = this.instructions.size() - 1; i >= 0; i--) {
        AbstractInsnNode insn = this.instructions.get(i);
        if (insn.visibleTypeAnnotations != null && !insn.visibleTypeAnnotations.isEmpty())
          throw new UnsupportedClassVersionException(); 
        if (insn.invisibleTypeAnnotations != null && !insn.invisibleTypeAnnotations.isEmpty())
          throw new UnsupportedClassVersionException(); 
        if (insn instanceof MethodInsnNode) {
          boolean isInterface = ((MethodInsnNode)insn).itf;
          if (isInterface != ((insn.opcode == 185)))
            throw new UnsupportedClassVersionException(); 
        } else if (insn instanceof LdcInsnNode) {
          Object value = ((LdcInsnNode)insn).cst;
          if (value instanceof Handle || (value instanceof Type && ((Type)value)
            .getSort() == 11))
            throw new UnsupportedClassVersionException(); 
        } 
      } 
      if (this.visibleLocalVariableAnnotations != null && !this.visibleLocalVariableAnnotations.isEmpty())
        throw new UnsupportedClassVersionException(); 
      if (this.invisibleLocalVariableAnnotations != null && 
        !this.invisibleLocalVariableAnnotations.isEmpty())
        throw new UnsupportedClassVersionException(); 
    } 
    if (api != 458752)
      for (int i = this.instructions.size() - 1; i >= 0; i--) {
        AbstractInsnNode insn = this.instructions.get(i);
        if (insn instanceof LdcInsnNode) {
          Object value = ((LdcInsnNode)insn).cst;
          if (value instanceof org.objectweb.asm.ConstantDynamic)
            throw new UnsupportedClassVersionException(); 
        } 
      }  
  }
  
  public void accept(ClassVisitor classVisitor) {
    String[] exceptionsArray = new String[this.exceptions.size()];
    this.exceptions.toArray(exceptionsArray);
    MethodVisitor methodVisitor = classVisitor.visitMethod(this.access, this.name, this.desc, this.signature, exceptionsArray);
    if (methodVisitor != null)
      accept(methodVisitor); 
  }
  
  public void accept(MethodVisitor methodVisitor) {
    if (this.parameters != null)
      for (int i = 0, n = this.parameters.size(); i < n; i++)
        ((ParameterNode)this.parameters.get(i)).accept(methodVisitor);  
    if (this.annotationDefault != null) {
      AnnotationVisitor annotationVisitor = methodVisitor.visitAnnotationDefault();
      AnnotationNode.accept(annotationVisitor, null, this.annotationDefault);
      if (annotationVisitor != null)
        annotationVisitor.visitEnd(); 
    } 
    if (this.visibleAnnotations != null)
      for (int i = 0, n = this.visibleAnnotations.size(); i < n; i++) {
        AnnotationNode annotation = (AnnotationNode)this.visibleAnnotations.get(i);
        annotation.accept(methodVisitor.visitAnnotation(annotation.desc, true));
      }  
    if (this.invisibleAnnotations != null)
      for (int i = 0, n = this.invisibleAnnotations.size(); i < n; i++) {
        AnnotationNode annotation = (AnnotationNode)this.invisibleAnnotations.get(i);
        annotation.accept(methodVisitor.visitAnnotation(annotation.desc, false));
      }  
    if (this.visibleTypeAnnotations != null)
      for (int i = 0, n = this.visibleTypeAnnotations.size(); i < n; i++) {
        TypeAnnotationNode typeAnnotation = (TypeAnnotationNode)this.visibleTypeAnnotations.get(i);
        typeAnnotation.accept(methodVisitor
            .visitTypeAnnotation(typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, true));
      }  
    if (this.invisibleTypeAnnotations != null)
      for (int i = 0, n = this.invisibleTypeAnnotations.size(); i < n; i++) {
        TypeAnnotationNode typeAnnotation = (TypeAnnotationNode)this.invisibleTypeAnnotations.get(i);
        typeAnnotation.accept(methodVisitor
            .visitTypeAnnotation(typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, false));
      }  
    if (this.visibleAnnotableParameterCount > 0)
      methodVisitor.visitAnnotableParameterCount(this.visibleAnnotableParameterCount, true); 
    if (this.visibleParameterAnnotations != null)
      for (int i = 0, n = this.visibleParameterAnnotations.length; i < n; i++) {
        List<AnnotationNode> parameterAnnotations = this.visibleParameterAnnotations[i];
        if (parameterAnnotations != null)
          for (int j = 0, m = parameterAnnotations.size(); j < m; j++) {
            AnnotationNode annotation = (AnnotationNode)parameterAnnotations.get(j);
            annotation.accept(methodVisitor.visitParameterAnnotation(i, annotation.desc, true));
          }  
      }  
    if (this.invisibleAnnotableParameterCount > 0)
      methodVisitor.visitAnnotableParameterCount(this.invisibleAnnotableParameterCount, false); 
    if (this.invisibleParameterAnnotations != null)
      for (int i = 0, n = this.invisibleParameterAnnotations.length; i < n; i++) {
        List<AnnotationNode> parameterAnnotations = this.invisibleParameterAnnotations[i];
        if (parameterAnnotations != null)
          for (int j = 0, m = parameterAnnotations.size(); j < m; j++) {
            AnnotationNode annotation = (AnnotationNode)parameterAnnotations.get(j);
            annotation.accept(methodVisitor.visitParameterAnnotation(i, annotation.desc, false));
          }  
      }  
    if (this.visited)
      this.instructions.resetLabels(); 
    if (this.attrs != null)
      for (int i = 0, n = this.attrs.size(); i < n; i++)
        methodVisitor.visitAttribute((Attribute)this.attrs.get(i));  
    if (this.instructions.size() > 0) {
      methodVisitor.visitCode();
      if (this.tryCatchBlocks != null)
        for (int i = 0, n = this.tryCatchBlocks.size(); i < n; i++) {
          ((TryCatchBlockNode)this.tryCatchBlocks.get(i)).updateIndex(i);
          ((TryCatchBlockNode)this.tryCatchBlocks.get(i)).accept(methodVisitor);
        }  
      this.instructions.accept(methodVisitor);
      if (this.localVariables != null)
        for (int i = 0, n = this.localVariables.size(); i < n; i++)
          ((LocalVariableNode)this.localVariables.get(i)).accept(methodVisitor);  
      if (this.visibleLocalVariableAnnotations != null)
        for (int i = 0, n = this.visibleLocalVariableAnnotations.size(); i < n; i++)
          ((LocalVariableAnnotationNode)this.visibleLocalVariableAnnotations.get(i)).accept(methodVisitor, true);  
      if (this.invisibleLocalVariableAnnotations != null)
        for (int i = 0, n = this.invisibleLocalVariableAnnotations.size(); i < n; i++)
          ((LocalVariableAnnotationNode)this.invisibleLocalVariableAnnotations.get(i)).accept(methodVisitor, false);  
      methodVisitor.visitMaxs(this.maxStack, this.maxLocals);
      this.visited = true;
    } 
    methodVisitor.visitEnd();
  }
}
