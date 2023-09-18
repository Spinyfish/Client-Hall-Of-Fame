package org.objectweb.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.AnnotationWriter;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ByteVector;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassTooLargeException;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.FieldWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodTooLargeException;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.MethodWriter;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.ModuleWriter;
import org.objectweb.asm.Symbol;
import org.objectweb.asm.SymbolTable;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;
import org.objectweb.asm.Attribute.Set;

public class ClassWriter extends ClassVisitor {
  public static final int COMPUTE_MAXS = 1;
  public static final int COMPUTE_FRAMES = 2;
  private int version;
  private final SymbolTable symbolTable;
  private int accessFlags;
  private int thisClass;
  private int superClass;
  private int interfaceCount;
  private int[] interfaces;
  private FieldWriter firstField;
  private FieldWriter lastField;
  private MethodWriter firstMethod;
  private MethodWriter lastMethod;
  private int numberOfInnerClasses;
  private ByteVector innerClasses;
  private int enclosingClassIndex;
  private int enclosingMethodIndex;
  private int signatureIndex;
  private int sourceFileIndex;
  private ByteVector debugExtension;
  private AnnotationWriter lastRuntimeVisibleAnnotation;
  private AnnotationWriter lastRuntimeInvisibleAnnotation;
  private AnnotationWriter lastRuntimeVisibleTypeAnnotation;
  private AnnotationWriter lastRuntimeInvisibleTypeAnnotation;
  private ModuleWriter moduleWriter;
  private int nestHostClassIndex;
  private int numberOfNestMemberClasses;
  private ByteVector nestMemberClasses;
  private Attribute firstAttribute;
  private int compute;

  public ClassWriter(int flags) {
    this((ClassReader)null, flags);
  }

  public ClassWriter(ClassReader classReader, int flags) {
    super(458752);
    this.symbolTable = classReader == null?new SymbolTable(this):new SymbolTable(this, classReader);
    if((flags & 2) != 0) {
      this.compute = 4;
    } else if((flags & 1) != 0) {
      this.compute = 1;
    } else {
      this.compute = 0;
    }

  }

  public final void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
    this.version = version;
    this.accessFlags = access;
    this.thisClass = this.symbolTable.setMajorVersionAndClassName(version & '\uffff', name);
    if(signature != null) {
      this.signatureIndex = this.symbolTable.addConstantUtf8(signature);
    }

    this.superClass = superName == null?0:this.symbolTable.addConstantClass(superName).index;
    if(interfaces != null && interfaces.length > 0) {
      this.interfaceCount = interfaces.length;
      this.interfaces = new int[this.interfaceCount];

      for(int i = 0; i < this.interfaceCount; ++i) {
        this.interfaces[i] = this.symbolTable.addConstantClass(interfaces[i]).index;
      }
    }

    if(this.compute == 1 && (version & '\uffff') >= 51) {
      this.compute = 2;
    }

  }

  public final void visitSource(String file, String debug) {
    if(file != null) {
      this.sourceFileIndex = this.symbolTable.addConstantUtf8(file);
    }

    if(debug != null) {
      this.debugExtension = (new ByteVector()).encodeUtf8(debug, 0, Integer.MAX_VALUE);
    }

  }

  public final ModuleVisitor visitModule(String name, int access, String version) {
    return this.moduleWriter = new ModuleWriter(this.symbolTable, this.symbolTable.addConstantModule(name).index, access, version == null?0:this.symbolTable.addConstantUtf8(version));
  }

  public void visitNestHost(String nestHost) {
    this.nestHostClassIndex = this.symbolTable.addConstantClass(nestHost).index;
  }

  public final void visitOuterClass(String owner, String name, String descriptor) {
    this.enclosingClassIndex = this.symbolTable.addConstantClass(owner).index;
    if(name != null && descriptor != null) {
      this.enclosingMethodIndex = this.symbolTable.addConstantNameAndType(name, descriptor);
    }

  }

  public final AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
    ByteVector annotation = new ByteVector();
    annotation.putShort(this.symbolTable.addConstantUtf8(descriptor)).putShort(0);
    return visible?(this.lastRuntimeVisibleAnnotation = new AnnotationWriter(this.symbolTable, annotation, this.lastRuntimeVisibleAnnotation)):(this.lastRuntimeInvisibleAnnotation = new AnnotationWriter(this.symbolTable, annotation, this.lastRuntimeInvisibleAnnotation));
  }

  public final AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
    ByteVector typeAnnotation = new ByteVector();
    TypeReference.putTarget(typeRef, typeAnnotation);
    TypePath.put(typePath, typeAnnotation);
    typeAnnotation.putShort(this.symbolTable.addConstantUtf8(descriptor)).putShort(0);
    return visible?(this.lastRuntimeVisibleTypeAnnotation = new AnnotationWriter(this.symbolTable, typeAnnotation, this.lastRuntimeVisibleTypeAnnotation)):(this.lastRuntimeInvisibleTypeAnnotation = new AnnotationWriter(this.symbolTable, typeAnnotation, this.lastRuntimeInvisibleTypeAnnotation));
  }

  public final void visitAttribute(Attribute attribute) {
    attribute.nextAttribute = this.firstAttribute;
    this.firstAttribute = attribute;
  }

  public void visitNestMember(String nestMember) {
    if(this.nestMemberClasses == null) {
      this.nestMemberClasses = new ByteVector();
    }

    ++this.numberOfNestMemberClasses;
    this.nestMemberClasses.putShort(this.symbolTable.addConstantClass(nestMember).index);
  }

  public final void visitInnerClass(String name, String outerName, String innerName, int access) {
    if(this.innerClasses == null) {
      this.innerClasses = new ByteVector();
    }

    Symbol nameSymbol = this.symbolTable.addConstantClass(name);
    if(nameSymbol.info == 0) {
      ++this.numberOfInnerClasses;
      this.innerClasses.putShort(nameSymbol.index);
      this.innerClasses.putShort(outerName == null?0:this.symbolTable.addConstantClass(outerName).index);
      this.innerClasses.putShort(innerName == null?0:this.symbolTable.addConstantUtf8(innerName));
      this.innerClasses.putShort(access);
      nameSymbol.info = this.numberOfInnerClasses;
    }

  }

  public final FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
    FieldWriter fieldWriter = new FieldWriter(this.symbolTable, access, name, descriptor, signature, value);
    if(this.firstField == null) {
      this.firstField = fieldWriter;
    } else {
      this.lastField.fv = fieldWriter;
    }

    return this.lastField = fieldWriter;
  }

  public final MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
    MethodWriter methodWriter = new MethodWriter(this.symbolTable, access, name, descriptor, signature, exceptions, this.compute);
    if(this.firstMethod == null) {
      this.firstMethod = methodWriter;
    } else {
      this.lastMethod.mv = methodWriter;
    }

    return this.lastMethod = methodWriter;
  }

  public final void visitEnd() {
  }

  public byte[] toByteArray() throws ClassTooLargeException, MethodTooLargeException {
    int size = 24 + 2 * this.interfaceCount;
    int fieldsCount = 0;

    FieldWriter fieldWriter;
    for(fieldWriter = this.firstField; fieldWriter != null; fieldWriter = (FieldWriter)fieldWriter.fv) {
      ++fieldsCount;
      size += fieldWriter.computeFieldInfoSize();
    }

    int methodsCount = 0;

    MethodWriter methodWriter;
    for(methodWriter = this.firstMethod; methodWriter != null; methodWriter = (MethodWriter)methodWriter.mv) {
      ++methodsCount;
      size += methodWriter.computeMethodInfoSize();
    }

    int attributesCount = 0;
    if(this.innerClasses != null) {
      ++attributesCount;
      size += 8 + this.innerClasses.length;
      this.symbolTable.addConstantUtf8("InnerClasses");
    }

    if(this.enclosingClassIndex != 0) {
      ++attributesCount;
      size += 10;
      this.symbolTable.addConstantUtf8("EnclosingMethod");
    }

    if((this.accessFlags & 4096) != 0 && (this.version & '\uffff') < 49) {
      ++attributesCount;
      size += 6;
      this.symbolTable.addConstantUtf8("Synthetic");
    }

    if(this.signatureIndex != 0) {
      ++attributesCount;
      size += 8;
      this.symbolTable.addConstantUtf8("Signature");
    }

    if(this.sourceFileIndex != 0) {
      ++attributesCount;
      size += 8;
      this.symbolTable.addConstantUtf8("SourceFile");
    }

    if(this.debugExtension != null) {
      ++attributesCount;
      size += 6 + this.debugExtension.length;
      this.symbolTable.addConstantUtf8("SourceDebugExtension");
    }

    if((this.accessFlags & 131072) != 0) {
      ++attributesCount;
      size += 6;
      this.symbolTable.addConstantUtf8("Deprecated");
    }

    if(this.lastRuntimeVisibleAnnotation != null) {
      ++attributesCount;
      size += this.lastRuntimeVisibleAnnotation.computeAnnotationsSize("RuntimeVisibleAnnotations");
    }

    if(this.lastRuntimeInvisibleAnnotation != null) {
      ++attributesCount;
      size += this.lastRuntimeInvisibleAnnotation.computeAnnotationsSize("RuntimeInvisibleAnnotations");
    }

    if(this.lastRuntimeVisibleTypeAnnotation != null) {
      ++attributesCount;
      size += this.lastRuntimeVisibleTypeAnnotation.computeAnnotationsSize("RuntimeVisibleTypeAnnotations");
    }

    if(this.lastRuntimeInvisibleTypeAnnotation != null) {
      ++attributesCount;
      size += this.lastRuntimeInvisibleTypeAnnotation.computeAnnotationsSize("RuntimeInvisibleTypeAnnotations");
    }

    if(this.symbolTable.computeBootstrapMethodsSize() > 0) {
      ++attributesCount;
      size += this.symbolTable.computeBootstrapMethodsSize();
    }

    if(this.moduleWriter != null) {
      attributesCount += this.moduleWriter.getAttributeCount();
      size += this.moduleWriter.computeAttributesSize();
    }

    if(this.nestHostClassIndex != 0) {
      ++attributesCount;
      size += 8;
      this.symbolTable.addConstantUtf8("NestHost");
    }

    if(this.nestMemberClasses != null) {
      ++attributesCount;
      size += 8 + this.nestMemberClasses.length;
      this.symbolTable.addConstantUtf8("NestMembers");
    }

    if(this.firstAttribute != null) {
      attributesCount += this.firstAttribute.getAttributeCount();
      size += this.firstAttribute.computeAttributesSize(this.symbolTable);
    }

    size += this.symbolTable.getConstantPoolLength();
    int constantPoolCount = this.symbolTable.getConstantPoolCount();
    if(constantPoolCount > '\uffff') {
      throw new ClassTooLargeException(this.symbolTable.getClassName(), constantPoolCount);
    } else {
      ByteVector result = new ByteVector(size);
      result.putInt(-889275714).putInt(this.version);
      this.symbolTable.putConstantPool(result);
      int mask = (this.version & '\uffff') < 49?4096:0;
      result.putShort(this.accessFlags & ~mask).putShort(this.thisClass).putShort(this.superClass);
      result.putShort(this.interfaceCount);

      for(int hasFrames = 0; hasFrames < this.interfaceCount; ++hasFrames) {
        result.putShort(this.interfaces[hasFrames]);
      }

      result.putShort(fieldsCount);

      for(fieldWriter = this.firstField; fieldWriter != null; fieldWriter = (FieldWriter)fieldWriter.fv) {
        fieldWriter.putFieldInfo(result);
      }

      result.putShort(methodsCount);
      boolean var13 = false;
      boolean hasAsmInstructions = false;

      for(methodWriter = this.firstMethod; methodWriter != null; methodWriter = (MethodWriter)methodWriter.mv) {
        var13 |= methodWriter.hasFrames();
        hasAsmInstructions |= methodWriter.hasAsmInstructions();
        methodWriter.putMethodInfo(result);
      }

      result.putShort(attributesCount);
      if(this.innerClasses != null) {
        result.putShort(this.symbolTable.addConstantUtf8("InnerClasses")).putInt(this.innerClasses.length + 2).putShort(this.numberOfInnerClasses).putByteArray(this.innerClasses.data, 0, this.innerClasses.length);
      }

      if(this.enclosingClassIndex != 0) {
        result.putShort(this.symbolTable.addConstantUtf8("EnclosingMethod")).putInt(4).putShort(this.enclosingClassIndex).putShort(this.enclosingMethodIndex);
      }

      if((this.accessFlags & 4096) != 0 && (this.version & '\uffff') < 49) {
        result.putShort(this.symbolTable.addConstantUtf8("Synthetic")).putInt(0);
      }

      if(this.signatureIndex != 0) {
        result.putShort(this.symbolTable.addConstantUtf8("Signature")).putInt(2).putShort(this.signatureIndex);
      }

      if(this.sourceFileIndex != 0) {
        result.putShort(this.symbolTable.addConstantUtf8("SourceFile")).putInt(2).putShort(this.sourceFileIndex);
      }

      if(this.debugExtension != null) {
        int length = this.debugExtension.length;
        result.putShort(this.symbolTable.addConstantUtf8("SourceDebugExtension")).putInt(length).putByteArray(this.debugExtension.data, 0, length);
      }

      if((this.accessFlags & 131072) != 0) {
        result.putShort(this.symbolTable.addConstantUtf8("Deprecated")).putInt(0);
      }

      if(this.lastRuntimeVisibleAnnotation != null) {
        this.lastRuntimeVisibleAnnotation.putAnnotations(this.symbolTable.addConstantUtf8("RuntimeVisibleAnnotations"), result);
      }

      if(this.lastRuntimeInvisibleAnnotation != null) {
        this.lastRuntimeInvisibleAnnotation.putAnnotations(this.symbolTable.addConstantUtf8("RuntimeInvisibleAnnotations"), result);
      }

      if(this.lastRuntimeVisibleTypeAnnotation != null) {
        this.lastRuntimeVisibleTypeAnnotation.putAnnotations(this.symbolTable.addConstantUtf8("RuntimeVisibleTypeAnnotations"), result);
      }

      if(this.lastRuntimeInvisibleTypeAnnotation != null) {
        this.lastRuntimeInvisibleTypeAnnotation.putAnnotations(this.symbolTable.addConstantUtf8("RuntimeInvisibleTypeAnnotations"), result);
      }

      this.symbolTable.putBootstrapMethods(result);
      if(this.moduleWriter != null) {
        this.moduleWriter.putAttributes(result);
      }

      if(this.nestHostClassIndex != 0) {
        result.putShort(this.symbolTable.addConstantUtf8("NestHost")).putInt(2).putShort(this.nestHostClassIndex);
      }

      if(this.nestMemberClasses != null) {
        result.putShort(this.symbolTable.addConstantUtf8("NestMembers")).putInt(this.nestMemberClasses.length + 2).putShort(this.numberOfNestMemberClasses).putByteArray(this.nestMemberClasses.data, 0, this.nestMemberClasses.length);
      }

      if(this.firstAttribute != null) {
        this.firstAttribute.putAttributes(this.symbolTable, result);
      }

      return hasAsmInstructions?this.replaceAsmInstructions(result.data, var13):result.data;
    }
  }

  private byte[] replaceAsmInstructions(byte[] classFile, boolean hasFrames) {
    Attribute[] attributes = this.getAttributePrototypes();
    this.firstField = null;
    this.lastField = null;
    this.firstMethod = null;
    this.lastMethod = null;
    this.lastRuntimeVisibleAnnotation = null;
    this.lastRuntimeInvisibleAnnotation = null;
    this.lastRuntimeVisibleTypeAnnotation = null;
    this.lastRuntimeInvisibleTypeAnnotation = null;
    this.moduleWriter = null;
    this.nestHostClassIndex = 0;
    this.numberOfNestMemberClasses = 0;
    this.nestMemberClasses = null;
    this.firstAttribute = null;
    this.compute = hasFrames?3:0;
    (new ClassReader(classFile, 0, false)).accept(this, attributes, (hasFrames?8:0) | 256);
    return this.toByteArray();
  }

  private Attribute[] getAttributePrototypes() {
    Set attributePrototypes = new Set();
    attributePrototypes.addAttributes(this.firstAttribute);

    for(FieldWriter fieldWriter = this.firstField; fieldWriter != null; fieldWriter = (FieldWriter)fieldWriter.fv) {
      fieldWriter.collectAttributePrototypes(attributePrototypes);
    }

    for(MethodWriter methodWriter = this.firstMethod; methodWriter != null; methodWriter = (MethodWriter)methodWriter.mv) {
      methodWriter.collectAttributePrototypes(attributePrototypes);
    }

    return attributePrototypes.toArray();
  }

  public int newConst(Object value) {
    return this.symbolTable.addConstant(value).index;
  }

  public int newUTF8(String value) {
    return this.symbolTable.addConstantUtf8(value);
  }

  public int newClass(String value) {
    return this.symbolTable.addConstantClass(value).index;
  }

  public int newMethodType(String methodDescriptor) {
    return this.symbolTable.addConstantMethodType(methodDescriptor).index;
  }

  public int newModule(String moduleName) {
    return this.symbolTable.addConstantModule(moduleName).index;
  }

  public int newPackage(String packageName) {
    return this.symbolTable.addConstantPackage(packageName).index;
  }

  @Deprecated
  public int newHandle(int tag, String owner, String name, String descriptor) {
    return this.newHandle(tag, owner, name, descriptor, tag == 9);
  }

  public int newHandle(int tag, String owner, String name, String descriptor, boolean isInterface) {
    return this.symbolTable.addConstantMethodHandle(tag, owner, name, descriptor, isInterface).index;
  }

  public int newConstantDynamic(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
    return this.symbolTable.addConstantDynamic(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments).index;
  }

  public int newInvokeDynamic(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
    return this.symbolTable.addConstantInvokeDynamic(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments).index;
  }

  public int newField(String owner, String name, String descriptor) {
    return this.symbolTable.addConstantFieldref(owner, name, descriptor).index;
  }

  public int newMethod(String owner, String name, String descriptor, boolean isInterface) {
    return this.symbolTable.addConstantMethodref(owner, name, descriptor, isInterface).index;
  }

  public int newNameType(String name, String descriptor) {
    return this.symbolTable.addConstantNameAndType(name, descriptor);
  }

  protected String getCommonSuperClass(String type1, String type2) {
    ClassLoader classLoader = this.getClassLoader();

    Class class1;
    try {
      class1 = Class.forName(type1.replace('/', '.'), false, classLoader);
    } catch (ClassNotFoundException var8) {
      throw new TypeNotPresentException(type1, var8);
    }

    Class class2;
    try {
      class2 = Class.forName(type2.replace('/', '.'), false, classLoader);
    } catch (ClassNotFoundException var7) {
      throw new TypeNotPresentException(type2, var7);
    }

    if(class1.isAssignableFrom(class2)) {
      return type1;
    } else if(class2.isAssignableFrom(class1)) {
      return type2;
    } else if(!class1.isInterface() && !class2.isInterface()) {
      do {
        class1 = class1.getSuperclass();
      } while(!class1.isAssignableFrom(class2));

      return class1.getName().replace('.', '/');
    } else {
      return "java/lang/Object";
    }
  }

  protected ClassLoader getClassLoader() {
    return this.getClass().getClassLoader();
  }
}
