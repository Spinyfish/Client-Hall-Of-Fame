package org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.TypePath;

public class ClassNode extends ClassVisitor {
  public int version;
  
  public int access;
  
  public String name;
  
  public String signature;
  
  public String superName;
  
  public List<String> interfaces;
  
  public String sourceFile;
  
  public String sourceDebug;
  
  public ModuleNode module;
  
  public String outerClass;
  
  public String outerMethod;
  
  public String outerMethodDesc;
  
  public List<AnnotationNode> visibleAnnotations;
  
  public List<AnnotationNode> invisibleAnnotations;
  
  public List<TypeAnnotationNode> visibleTypeAnnotations;
  
  public List<TypeAnnotationNode> invisibleTypeAnnotations;
  
  public List<Attribute> attrs;
  
  public List<InnerClassNode> innerClasses;
  
  public String nestHostClass;
  
  public List<String> nestMembers;
  
  public List<FieldNode> fields;
  
  public List<MethodNode> methods;
  
  public ClassNode() {
    this(458752);
    if (getClass() != ClassNode.class)
      throw new IllegalStateException(); 
  }
  
  public ClassNode(int api) {
    super(api);
    this.interfaces = new ArrayList();
    this.innerClasses = new ArrayList();
    this.fields = new ArrayList();
    this.methods = new ArrayList();
  }
  
  public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
    this.version = version;
    this.access = access;
    this.name = name;
    this.signature = signature;
    this.superName = superName;
    this.interfaces = Util.asArrayList(interfaces);
  }
  
  public void visitSource(String file, String debug) {
    this.sourceFile = file;
    this.sourceDebug = debug;
  }
  
  public ModuleVisitor visitModule(String name, int access, String version) {
    this.module = new ModuleNode(name, access, version);
    return this.module;
  }
  
  public void visitNestHost(String nestHost) { this.nestHostClass = nestHost; }
  
  public void visitOuterClass(String owner, String name, String descriptor) {
    this.outerClass = owner;
    this.outerMethod = name;
    this.outerMethodDesc = descriptor;
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
  
  public void visitAttribute(Attribute attribute) {
    if (this.attrs == null)
      this.attrs = new ArrayList(1); 
    this.attrs.add(attribute);
  }
  
  public void visitNestMember(String nestMember) {
    if (this.nestMembers == null)
      this.nestMembers = new ArrayList(); 
    this.nestMembers.add(nestMember);
  }
  
  public void visitInnerClass(String name, String outerName, String innerName, int access) {
    InnerClassNode innerClass = new InnerClassNode(name, outerName, innerName, access);
    this.innerClasses.add(innerClass);
  }
  
  public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
    FieldNode field = new FieldNode(access, name, descriptor, signature, value);
    this.fields.add(field);
    return field;
  }
  
  public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
    MethodNode method = new MethodNode(access, name, descriptor, signature, exceptions);
    this.methods.add(method);
    return method;
  }
  
  public void visitEnd() {}
  
  public void check(int api) {
    if (api < 458752 && (this.nestHostClass != null || this.nestMembers != null))
      throw new UnsupportedClassVersionException(); 
    if (api < 393216 && this.module != null)
      throw new UnsupportedClassVersionException(); 
    if (api < 327680) {
      if (this.visibleTypeAnnotations != null && !this.visibleTypeAnnotations.isEmpty())
        throw new UnsupportedClassVersionException(); 
      if (this.invisibleTypeAnnotations != null && !this.invisibleTypeAnnotations.isEmpty())
        throw new UnsupportedClassVersionException(); 
    } 
    if (this.visibleAnnotations != null)
      for (int i = this.visibleAnnotations.size() - 1; i >= 0; i--)
        ((AnnotationNode)this.visibleAnnotations.get(i)).check(api);  
    if (this.invisibleAnnotations != null)
      for (int i = this.invisibleAnnotations.size() - 1; i >= 0; i--)
        ((AnnotationNode)this.invisibleAnnotations.get(i)).check(api);  
    if (this.visibleTypeAnnotations != null)
      for (int i = this.visibleTypeAnnotations.size() - 1; i >= 0; i--)
        ((TypeAnnotationNode)this.visibleTypeAnnotations.get(i)).check(api);  
    if (this.invisibleTypeAnnotations != null)
      for (int i = this.invisibleTypeAnnotations.size() - 1; i >= 0; i--)
        ((TypeAnnotationNode)this.invisibleTypeAnnotations.get(i)).check(api);  
    for (int i = this.fields.size() - 1; i >= 0; i--)
      ((FieldNode)this.fields.get(i)).check(api); 
    for (int i = this.methods.size() - 1; i >= 0; i--)
      ((MethodNode)this.methods.get(i)).check(api); 
  }
  
  public void accept(ClassVisitor classVisitor) {
    String[] interfacesArray = new String[this.interfaces.size()];
    this.interfaces.toArray(interfacesArray);
    classVisitor.visit(this.version, this.access, this.name, this.signature, this.superName, interfacesArray);
    if (this.sourceFile != null || this.sourceDebug != null)
      classVisitor.visitSource(this.sourceFile, this.sourceDebug); 
    if (this.module != null)
      this.module.accept(classVisitor); 
    if (this.nestHostClass != null)
      classVisitor.visitNestHost(this.nestHostClass); 
    if (this.outerClass != null)
      classVisitor.visitOuterClass(this.outerClass, this.outerMethod, this.outerMethodDesc); 
    if (this.visibleAnnotations != null)
      for (int i = 0, n = this.visibleAnnotations.size(); i < n; i++) {
        AnnotationNode annotation = (AnnotationNode)this.visibleAnnotations.get(i);
        annotation.accept(classVisitor.visitAnnotation(annotation.desc, true));
      }  
    if (this.invisibleAnnotations != null)
      for (int i = 0, n = this.invisibleAnnotations.size(); i < n; i++) {
        AnnotationNode annotation = (AnnotationNode)this.invisibleAnnotations.get(i);
        annotation.accept(classVisitor.visitAnnotation(annotation.desc, false));
      }  
    if (this.visibleTypeAnnotations != null)
      for (int i = 0, n = this.visibleTypeAnnotations.size(); i < n; i++) {
        TypeAnnotationNode typeAnnotation = (TypeAnnotationNode)this.visibleTypeAnnotations.get(i);
        typeAnnotation.accept(classVisitor
            .visitTypeAnnotation(typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, true));
      }  
    if (this.invisibleTypeAnnotations != null)
      for (int i = 0, n = this.invisibleTypeAnnotations.size(); i < n; i++) {
        TypeAnnotationNode typeAnnotation = (TypeAnnotationNode)this.invisibleTypeAnnotations.get(i);
        typeAnnotation.accept(classVisitor
            .visitTypeAnnotation(typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, false));
      }  
    if (this.attrs != null)
      for (int i = 0, n = this.attrs.size(); i < n; i++)
        classVisitor.visitAttribute((Attribute)this.attrs.get(i));  
    if (this.nestMembers != null)
      for (int i = 0, n = this.nestMembers.size(); i < n; i++)
        classVisitor.visitNestMember((String)this.nestMembers.get(i));  
    for (int i = 0, n = this.innerClasses.size(); i < n; i++)
      ((InnerClassNode)this.innerClasses.get(i)).accept(classVisitor); 
    for (int i = 0, n = this.fields.size(); i < n; i++)
      ((FieldNode)this.fields.get(i)).accept(classVisitor); 
    for (int i = 0, n = this.methods.size(); i < n; i++)
      ((MethodNode)this.methods.get(i)).accept(classVisitor); 
    classVisitor.visitEnd();
  }
}
