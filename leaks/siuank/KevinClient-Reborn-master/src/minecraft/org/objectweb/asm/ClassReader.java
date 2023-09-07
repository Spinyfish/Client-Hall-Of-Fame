package org.objectweb.asm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Context;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.MethodWriter;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

public class ClassReader {
  public static final int SKIP_CODE = 1;
  public static final int SKIP_DEBUG = 2;
  public static final int SKIP_FRAMES = 4;
  public static final int EXPAND_FRAMES = 8;
  static final int EXPAND_ASM_INSNS = 256;
  private static final int INPUT_STREAM_DATA_CHUNK_SIZE = 4096;
  public final byte[] b;
  private final int[] cpInfoOffsets;
  private final String[] constantUtf8Values;
  private final ConstantDynamic[] constantDynamicValues;
  private final int[] bootstrapMethodOffsets;
  private final int maxStringLength;
  public final int header;

  public ClassReader(byte[] classFile) {
    this(classFile, 0, classFile.length);
  }

  public ClassReader(byte[] classFileBuffer, int classFileOffset, int classFileLength) {
    this(classFileBuffer, classFileOffset, true);
  }

  ClassReader(byte[] classFileBuffer, int classFileOffset, boolean checkClassVersion) {
    this.b = classFileBuffer;
    if(checkClassVersion && this.readShort(classFileOffset + 6) > 56) {
      throw new IllegalArgumentException("Unsupported class file major version " + this.readShort(classFileOffset + 6));
    } else {
      int constantPoolCount = this.readUnsignedShort(classFileOffset + 8);
      this.cpInfoOffsets = new int[constantPoolCount];
      this.constantUtf8Values = new String[constantPoolCount];
      int currentCpInfoIndex = 1;
      int currentCpInfoOffset = classFileOffset + 10;
      int currentMaxStringLength = 0;
      boolean hasConstantDynamic = false;

      boolean hasConstantInvokeDynamic;
      int cpInfoSize;
      for(hasConstantInvokeDynamic = false; currentCpInfoIndex < constantPoolCount; currentCpInfoOffset += cpInfoSize) {
        this.cpInfoOffsets[currentCpInfoIndex++] = currentCpInfoOffset + 1;
        switch(classFileBuffer[currentCpInfoOffset]) {
          case 1:
            cpInfoSize = 3 + this.readUnsignedShort(currentCpInfoOffset + 1);
            if(cpInfoSize > currentMaxStringLength) {
              currentMaxStringLength = cpInfoSize;
            }
            break;
          case 2:
          case 13:
          case 14:
          default:
            throw new IllegalArgumentException();
          case 3:
          case 4:
          case 9:
          case 10:
          case 11:
          case 12:
            cpInfoSize = 5;
            break;
          case 5:
          case 6:
            cpInfoSize = 9;
            ++currentCpInfoIndex;
            break;
          case 7:
          case 8:
          case 16:
          case 19:
          case 20:
            cpInfoSize = 3;
            break;
          case 15:
            cpInfoSize = 4;
            break;
          case 17:
            cpInfoSize = 5;
            hasConstantDynamic = true;
            break;
          case 18:
            cpInfoSize = 5;
            hasConstantInvokeDynamic = true;
        }
      }

      this.maxStringLength = currentMaxStringLength;
      this.header = currentCpInfoOffset;
      this.constantDynamicValues = hasConstantDynamic?new ConstantDynamic[constantPoolCount]:null;
      this.bootstrapMethodOffsets = hasConstantDynamic | hasConstantInvokeDynamic?this.readBootstrapMethodsAttribute(currentMaxStringLength):null;
    }
  }

  public ClassReader(InputStream inputStream) throws IOException {
    this(readStream(inputStream, false));
  }

  public ClassReader(String className) throws IOException {
    this(readStream(ClassLoader.getSystemResourceAsStream(className.replace('.', '/') + ".class"), true));
  }

  private static byte[] readStream(InputStream inputStream, boolean close) throws IOException {
    if(inputStream == null) {
      throw new IOException("Class not found");
    } else {
      try {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = new byte[4096];

        int bytesRead;
        while((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
          outputStream.write(data, 0, bytesRead);
        }

        outputStream.flush();
        byte[] var5 = outputStream.toByteArray();
        return var5;
      } finally {
        if(close) {
          inputStream.close();
        }

      }
    }
  }

  public int getAccess() {
    return this.readUnsignedShort(this.header);
  }

  public String getClassName() {
    return this.readClass(this.header + 2, new char[this.maxStringLength]);
  }

  public String getSuperName() {
    return this.readClass(this.header + 4, new char[this.maxStringLength]);
  }

  public String[] getInterfaces() {
    int currentOffset = this.header + 6;
    int interfacesCount = this.readUnsignedShort(currentOffset);
    String[] interfaces = new String[interfacesCount];
    if(interfacesCount > 0) {
      char[] charBuffer = new char[this.maxStringLength];

      for(int i = 0; i < interfacesCount; ++i) {
        currentOffset += 2;
        interfaces[i] = this.readClass(currentOffset, charBuffer);
      }
    }

    return interfaces;
  }

  public void accept(ClassVisitor classVisitor, int parsingOptions) {
    this.accept(classVisitor, new Attribute[0], parsingOptions);
  }

  public void accept(ClassVisitor classVisitor, Attribute[] attributePrototypes, int parsingOptions) {
    Context context = new Context();
    context.attributePrototypes = attributePrototypes;
    context.parsingOptions = parsingOptions;
    context.charBuffer = new char[this.maxStringLength];
    char[] charBuffer = context.charBuffer;
    int currentOffset = this.header;
    int accessFlags = this.readUnsignedShort(currentOffset);
    String thisClass = this.readClass(currentOffset + 2, charBuffer);
    String superClass = this.readClass(currentOffset + 4, charBuffer);
    String[] interfaces = new String[this.readUnsignedShort(currentOffset + 6)];
    currentOffset += 8;

    int innerClassesOffset;
    for(innerClassesOffset = 0; innerClassesOffset < interfaces.length; ++innerClassesOffset) {
      interfaces[innerClassesOffset] = this.readClass(currentOffset, charBuffer);
      currentOffset += 2;
    }

    innerClassesOffset = 0;
    int enclosingMethodOffset = 0;
    String signature = null;
    String sourceFile = null;
    String sourceDebugExtension = null;
    int runtimeVisibleAnnotationsOffset = 0;
    int runtimeInvisibleAnnotationsOffset = 0;
    int runtimeVisibleTypeAnnotationsOffset = 0;
    int runtimeInvisibleTypeAnnotationsOffset = 0;
    int moduleOffset = 0;
    int modulePackagesOffset = 0;
    String moduleMainClass = null;
    String nestHostClass = null;
    int nestMembersOffset = 0;
    Attribute attributes = null;
    int currentAttributeOffset = this.getFirstAttributeOffset();

    int fieldsCount;
    for(fieldsCount = this.readUnsignedShort(currentAttributeOffset - 2); fieldsCount > 0; --fieldsCount) {
      String methodsCount = this.readUTF8(currentAttributeOffset, charBuffer);
      int annotationDescriptor = this.readInt(currentAttributeOffset + 2);
      currentAttributeOffset += 6;
      if("SourceFile".equals(methodsCount)) {
        sourceFile = this.readUTF8(currentAttributeOffset, charBuffer);
      } else if("InnerClasses".equals(methodsCount)) {
        innerClassesOffset = currentAttributeOffset;
      } else if("EnclosingMethod".equals(methodsCount)) {
        enclosingMethodOffset = currentAttributeOffset;
      } else if("NestHost".equals(methodsCount)) {
        nestHostClass = this.readClass(currentAttributeOffset, charBuffer);
      } else if("NestMembers".equals(methodsCount)) {
        nestMembersOffset = currentAttributeOffset;
      } else if("Signature".equals(methodsCount)) {
        signature = this.readUTF8(currentAttributeOffset, charBuffer);
      } else if("RuntimeVisibleAnnotations".equals(methodsCount)) {
        runtimeVisibleAnnotationsOffset = currentAttributeOffset;
      } else if("RuntimeVisibleTypeAnnotations".equals(methodsCount)) {
        runtimeVisibleTypeAnnotationsOffset = currentAttributeOffset;
      } else if("Deprecated".equals(methodsCount)) {
        accessFlags |= 131072;
      } else if("Synthetic".equals(methodsCount)) {
        accessFlags |= 4096;
      } else if("SourceDebugExtension".equals(methodsCount)) {
        sourceDebugExtension = this.readUtf(currentAttributeOffset, annotationDescriptor, new char[annotationDescriptor]);
      } else if("RuntimeInvisibleAnnotations".equals(methodsCount)) {
        runtimeInvisibleAnnotationsOffset = currentAttributeOffset;
      } else if("RuntimeInvisibleTypeAnnotations".equals(methodsCount)) {
        runtimeInvisibleTypeAnnotationsOffset = currentAttributeOffset;
      } else if("Module".equals(methodsCount)) {
        moduleOffset = currentAttributeOffset;
      } else if("ModuleMainClass".equals(methodsCount)) {
        moduleMainClass = this.readClass(currentAttributeOffset, charBuffer);
      } else if("ModulePackages".equals(methodsCount)) {
        modulePackagesOffset = currentAttributeOffset;
      } else if(!"BootstrapMethods".equals(methodsCount)) {
        Attribute type = this.readAttribute(attributePrototypes, methodsCount, currentAttributeOffset, annotationDescriptor, charBuffer, -1, (Label[])null);
        type.nextAttribute = attributes;
        attributes = type;
      }

      currentAttributeOffset += annotationDescriptor;
    }

    classVisitor.visit(this.readInt(this.cpInfoOffsets[1] - 7), accessFlags, thisClass, signature, superClass, interfaces);
    if((parsingOptions & 2) == 0 && (sourceFile != null || sourceDebugExtension != null)) {
      classVisitor.visitSource(sourceFile, sourceDebugExtension);
    }

    if(moduleOffset != 0) {
      this.readModuleAttributes(classVisitor, context, moduleOffset, modulePackagesOffset, moduleMainClass);
    }

    if(nestHostClass != null) {
      classVisitor.visitNestHost(nestHostClass);
    }

    int var32;
    String var33;
    if(enclosingMethodOffset != 0) {
      String var31 = this.readClass(enclosingMethodOffset, charBuffer);
      var32 = this.readUnsignedShort(enclosingMethodOffset + 2);
      var33 = var32 == 0?null:this.readUTF8(this.cpInfoOffsets[var32], charBuffer);
      String var34 = var32 == 0?null:this.readUTF8(this.cpInfoOffsets[var32] + 2, charBuffer);
      classVisitor.visitOuterClass(var31, var33, var34);
    }

    if(runtimeVisibleAnnotationsOffset != 0) {
      fieldsCount = this.readUnsignedShort(runtimeVisibleAnnotationsOffset);

      for(var32 = runtimeVisibleAnnotationsOffset + 2; fieldsCount-- > 0; var32 = this.readElementValues(classVisitor.visitAnnotation(var33, true), var32, true, charBuffer)) {
        var33 = this.readUTF8(var32, charBuffer);
        var32 += 2;
      }
    }

    if(runtimeInvisibleAnnotationsOffset != 0) {
      fieldsCount = this.readUnsignedShort(runtimeInvisibleAnnotationsOffset);

      for(var32 = runtimeInvisibleAnnotationsOffset + 2; fieldsCount-- > 0; var32 = this.readElementValues(classVisitor.visitAnnotation(var33, false), var32, true, charBuffer)) {
        var33 = this.readUTF8(var32, charBuffer);
        var32 += 2;
      }
    }

    if(runtimeVisibleTypeAnnotationsOffset != 0) {
      fieldsCount = this.readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);

      for(var32 = runtimeVisibleTypeAnnotationsOffset + 2; fieldsCount-- > 0; var32 = this.readElementValues(classVisitor.visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, var33, true), var32, true, charBuffer)) {
        var32 = this.readTypeAnnotationTarget(context, var32);
        var33 = this.readUTF8(var32, charBuffer);
        var32 += 2;
      }
    }

    if(runtimeInvisibleTypeAnnotationsOffset != 0) {
      fieldsCount = this.readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);

      for(var32 = runtimeInvisibleTypeAnnotationsOffset + 2; fieldsCount-- > 0; var32 = this.readElementValues(classVisitor.visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, var33, false), var32, true, charBuffer)) {
        var32 = this.readTypeAnnotationTarget(context, var32);
        var33 = this.readUTF8(var32, charBuffer);
        var32 += 2;
      }
    }

    while(attributes != null) {
      Attribute var35 = attributes.nextAttribute;
      attributes.nextAttribute = null;
      classVisitor.visitAttribute(attributes);
      attributes = var35;
    }

    if(nestMembersOffset != 0) {
      fieldsCount = this.readUnsignedShort(nestMembersOffset);

      for(var32 = nestMembersOffset + 2; fieldsCount-- > 0; var32 += 2) {
        classVisitor.visitNestMember(this.readClass(var32, charBuffer));
      }
    }

    if(innerClassesOffset != 0) {
      fieldsCount = this.readUnsignedShort(innerClassesOffset);

      for(var32 = innerClassesOffset + 2; fieldsCount-- > 0; var32 += 8) {
        classVisitor.visitInnerClass(this.readClass(var32, charBuffer), this.readClass(var32 + 2, charBuffer), this.readUTF8(var32 + 4, charBuffer), this.readUnsignedShort(var32 + 6));
      }
    }

    fieldsCount = this.readUnsignedShort(currentOffset);

    for(currentOffset += 2; fieldsCount-- > 0; currentOffset = this.readField(classVisitor, context, currentOffset)) {
      ;
    }

    var32 = this.readUnsignedShort(currentOffset);

    for(currentOffset += 2; var32-- > 0; currentOffset = this.readMethod(classVisitor, context, currentOffset)) {
      ;
    }

    classVisitor.visitEnd();
  }

  private void readModuleAttributes(ClassVisitor classVisitor, Context context, int moduleOffset, int modulePackagesOffset, String moduleMainClass) {
    char[] buffer = context.charBuffer;
    String moduleName = this.readModule(moduleOffset, buffer);
    int moduleFlags = this.readUnsignedShort(moduleOffset + 2);
    String moduleVersion = this.readUTF8(moduleOffset + 4, buffer);
    int currentOffset = moduleOffset + 6;
    ModuleVisitor moduleVisitor = classVisitor.visitModule(moduleName, moduleFlags, moduleVersion);
    if(moduleVisitor != null) {
      if(moduleMainClass != null) {
        moduleVisitor.visitMainClass(moduleMainClass);
      }

      int requiresCount;
      int exportsCount;
      if(modulePackagesOffset != 0) {
        requiresCount = this.readUnsignedShort(modulePackagesOffset);

        for(exportsCount = modulePackagesOffset + 2; requiresCount-- > 0; exportsCount += 2) {
          moduleVisitor.visitPackage(this.readPackage(exportsCount, buffer));
        }
      }

      requiresCount = this.readUnsignedShort(currentOffset);
      currentOffset += 2;

      int opensCount;
      String usesCount;
      while(requiresCount-- > 0) {
        String var21 = this.readModule(currentOffset, buffer);
        opensCount = this.readUnsignedShort(currentOffset + 2);
        usesCount = this.readUTF8(currentOffset + 4, buffer);
        currentOffset += 6;
        moduleVisitor.visitRequire(var21, opensCount, usesCount);
      }

      exportsCount = this.readUnsignedShort(currentOffset);

      int providesCount;
      String[] provides;
      int providesWithCount;
      String var22;
      int var23;
      for(currentOffset += 2; exportsCount-- > 0; moduleVisitor.visitExport(var22, var23, provides)) {
        var22 = this.readPackage(currentOffset, buffer);
        var23 = this.readUnsignedShort(currentOffset + 2);
        providesCount = this.readUnsignedShort(currentOffset + 4);
        currentOffset += 6;
        provides = null;
        if(providesCount != 0) {
          provides = new String[providesCount];

          for(providesWithCount = 0; providesWithCount < providesCount; ++providesWithCount) {
            provides[providesWithCount] = this.readModule(currentOffset, buffer);
            currentOffset += 2;
          }
        }
      }

      opensCount = this.readUnsignedShort(currentOffset);

      String[] var26;
      for(currentOffset += 2; opensCount-- > 0; moduleVisitor.visitOpen(usesCount, providesCount, var26)) {
        usesCount = this.readPackage(currentOffset, buffer);
        providesCount = this.readUnsignedShort(currentOffset + 2);
        int var24 = this.readUnsignedShort(currentOffset + 4);
        currentOffset += 6;
        var26 = null;
        if(var24 != 0) {
          var26 = new String[var24];

          for(int providesWith = 0; providesWith < var24; ++providesWith) {
            var26[providesWith] = this.readModule(currentOffset, buffer);
            currentOffset += 2;
          }
        }
      }

      var23 = this.readUnsignedShort(currentOffset);

      for(currentOffset += 2; var23-- > 0; currentOffset += 2) {
        moduleVisitor.visitUse(this.readClass(currentOffset, buffer));
      }

      providesCount = this.readUnsignedShort(currentOffset);
      currentOffset += 2;

      while(providesCount-- > 0) {
        String var25 = this.readClass(currentOffset, buffer);
        providesWithCount = this.readUnsignedShort(currentOffset + 2);
        currentOffset += 4;
        String[] var27 = new String[providesWithCount];

        for(int i = 0; i < providesWithCount; ++i) {
          var27[i] = this.readClass(currentOffset, buffer);
          currentOffset += 2;
        }

        moduleVisitor.visitProvide(var25, var27);
      }

      moduleVisitor.visitEnd();
    }
  }

  private int readField(ClassVisitor classVisitor, Context context, int fieldInfoOffset) {
    char[] charBuffer = context.charBuffer;
    int accessFlags = this.readUnsignedShort(fieldInfoOffset);
    String name = this.readUTF8(fieldInfoOffset + 2, charBuffer);
    String descriptor = this.readUTF8(fieldInfoOffset + 4, charBuffer);
    int currentOffset = fieldInfoOffset + 6;
    Object constantValue = null;
    String signature = null;
    int runtimeVisibleAnnotationsOffset = 0;
    int runtimeInvisibleAnnotationsOffset = 0;
    int runtimeVisibleTypeAnnotationsOffset = 0;
    int runtimeInvisibleTypeAnnotationsOffset = 0;
    Attribute attributes = null;
    int attributesCount = this.readUnsignedShort(currentOffset);

    int nextAttribute;
    int currentAnnotationOffset;
    for(currentOffset += 2; attributesCount-- > 0; currentOffset += nextAttribute) {
      String fieldVisitor = this.readUTF8(currentOffset, charBuffer);
      nextAttribute = this.readInt(currentOffset + 2);
      currentOffset += 6;
      if("ConstantValue".equals(fieldVisitor)) {
        currentAnnotationOffset = this.readUnsignedShort(currentOffset);
        constantValue = currentAnnotationOffset == 0?null:this.readConst(currentAnnotationOffset, charBuffer);
      } else if("Signature".equals(fieldVisitor)) {
        signature = this.readUTF8(currentOffset, charBuffer);
      } else if("Deprecated".equals(fieldVisitor)) {
        accessFlags |= 131072;
      } else if("Synthetic".equals(fieldVisitor)) {
        accessFlags |= 4096;
      } else if("RuntimeVisibleAnnotations".equals(fieldVisitor)) {
        runtimeVisibleAnnotationsOffset = currentOffset;
      } else if("RuntimeVisibleTypeAnnotations".equals(fieldVisitor)) {
        runtimeVisibleTypeAnnotationsOffset = currentOffset;
      } else if("RuntimeInvisibleAnnotations".equals(fieldVisitor)) {
        runtimeInvisibleAnnotationsOffset = currentOffset;
      } else if("RuntimeInvisibleTypeAnnotations".equals(fieldVisitor)) {
        runtimeInvisibleTypeAnnotationsOffset = currentOffset;
      } else {
        Attribute var22 = this.readAttribute(context.attributePrototypes, fieldVisitor, currentOffset, nextAttribute, charBuffer, -1, (Label[])null);
        var22.nextAttribute = attributes;
        attributes = var22;
      }
    }

    FieldVisitor var21 = classVisitor.visitField(accessFlags, name, descriptor, signature, constantValue);
    if(var21 == null) {
      return currentOffset;
    } else {
      String annotationDescriptor;
      if(runtimeVisibleAnnotationsOffset != 0) {
        nextAttribute = this.readUnsignedShort(runtimeVisibleAnnotationsOffset);

        for(currentAnnotationOffset = runtimeVisibleAnnotationsOffset + 2; nextAttribute-- > 0; currentAnnotationOffset = this.readElementValues(var21.visitAnnotation(annotationDescriptor, true), currentAnnotationOffset, true, charBuffer)) {
          annotationDescriptor = this.readUTF8(currentAnnotationOffset, charBuffer);
          currentAnnotationOffset += 2;
        }
      }

      if(runtimeInvisibleAnnotationsOffset != 0) {
        nextAttribute = this.readUnsignedShort(runtimeInvisibleAnnotationsOffset);

        for(currentAnnotationOffset = runtimeInvisibleAnnotationsOffset + 2; nextAttribute-- > 0; currentAnnotationOffset = this.readElementValues(var21.visitAnnotation(annotationDescriptor, false), currentAnnotationOffset, true, charBuffer)) {
          annotationDescriptor = this.readUTF8(currentAnnotationOffset, charBuffer);
          currentAnnotationOffset += 2;
        }
      }

      if(runtimeVisibleTypeAnnotationsOffset != 0) {
        nextAttribute = this.readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);

        for(currentAnnotationOffset = runtimeVisibleTypeAnnotationsOffset + 2; nextAttribute-- > 0; currentAnnotationOffset = this.readElementValues(var21.visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, true), currentAnnotationOffset, true, charBuffer)) {
          currentAnnotationOffset = this.readTypeAnnotationTarget(context, currentAnnotationOffset);
          annotationDescriptor = this.readUTF8(currentAnnotationOffset, charBuffer);
          currentAnnotationOffset += 2;
        }
      }

      if(runtimeInvisibleTypeAnnotationsOffset != 0) {
        nextAttribute = this.readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);

        for(currentAnnotationOffset = runtimeInvisibleTypeAnnotationsOffset + 2; nextAttribute-- > 0; currentAnnotationOffset = this.readElementValues(var21.visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, false), currentAnnotationOffset, true, charBuffer)) {
          currentAnnotationOffset = this.readTypeAnnotationTarget(context, currentAnnotationOffset);
          annotationDescriptor = this.readUTF8(currentAnnotationOffset, charBuffer);
          currentAnnotationOffset += 2;
        }
      }

      while(attributes != null) {
        Attribute var23 = attributes.nextAttribute;
        attributes.nextAttribute = null;
        var21.visitAttribute(attributes);
        attributes = var23;
      }

      var21.visitEnd();
      return currentOffset;
    }
  }

  private int readMethod(ClassVisitor classVisitor, Context context, int methodInfoOffset) {
    char[] charBuffer = context.charBuffer;
    context.currentMethodAccessFlags = this.readUnsignedShort(methodInfoOffset);
    context.currentMethodName = this.readUTF8(methodInfoOffset + 2, charBuffer);
    context.currentMethodDescriptor = this.readUTF8(methodInfoOffset + 4, charBuffer);
    int currentOffset = methodInfoOffset + 6;
    int codeOffset = 0;
    int exceptionsOffset = 0;
    String[] exceptions = null;
    boolean synthetic = false;
    int signatureIndex = 0;
    int runtimeVisibleAnnotationsOffset = 0;
    int runtimeInvisibleAnnotationsOffset = 0;
    int runtimeVisibleParameterAnnotationsOffset = 0;
    int runtimeInvisibleParameterAnnotationsOffset = 0;
    int runtimeVisibleTypeAnnotationsOffset = 0;
    int runtimeInvisibleTypeAnnotationsOffset = 0;
    int annotationDefaultOffset = 0;
    int methodParametersOffset = 0;
    Attribute attributes = null;
    int attributesCount = this.readUnsignedShort(currentOffset);

    int nextAttribute;
    int var27;
    for(currentOffset += 2; attributesCount-- > 0; currentOffset += nextAttribute) {
      String methodVisitor = this.readUTF8(currentOffset, charBuffer);
      nextAttribute = this.readInt(currentOffset + 2);
      currentOffset += 6;
      if("Code".equals(methodVisitor)) {
        if((context.parsingOptions & 1) == 0) {
          codeOffset = currentOffset;
        }
      } else if("Exceptions".equals(methodVisitor)) {
        exceptionsOffset = currentOffset;
        exceptions = new String[this.readUnsignedShort(currentOffset)];
        var27 = currentOffset + 2;

        for(int annotationDescriptor = 0; annotationDescriptor < exceptions.length; ++annotationDescriptor) {
          exceptions[annotationDescriptor] = this.readClass(var27, charBuffer);
          var27 += 2;
        }
      } else if("Signature".equals(methodVisitor)) {
        signatureIndex = this.readUnsignedShort(currentOffset);
      } else if("Deprecated".equals(methodVisitor)) {
        context.currentMethodAccessFlags |= 131072;
      } else if("RuntimeVisibleAnnotations".equals(methodVisitor)) {
        runtimeVisibleAnnotationsOffset = currentOffset;
      } else if("RuntimeVisibleTypeAnnotations".equals(methodVisitor)) {
        runtimeVisibleTypeAnnotationsOffset = currentOffset;
      } else if("AnnotationDefault".equals(methodVisitor)) {
        annotationDefaultOffset = currentOffset;
      } else if("Synthetic".equals(methodVisitor)) {
        synthetic = true;
        context.currentMethodAccessFlags |= 4096;
      } else if("RuntimeInvisibleAnnotations".equals(methodVisitor)) {
        runtimeInvisibleAnnotationsOffset = currentOffset;
      } else if("RuntimeInvisibleTypeAnnotations".equals(methodVisitor)) {
        runtimeInvisibleTypeAnnotationsOffset = currentOffset;
      } else if("RuntimeVisibleParameterAnnotations".equals(methodVisitor)) {
        runtimeVisibleParameterAnnotationsOffset = currentOffset;
      } else if("RuntimeInvisibleParameterAnnotations".equals(methodVisitor)) {
        runtimeInvisibleParameterAnnotationsOffset = currentOffset;
      } else if("MethodParameters".equals(methodVisitor)) {
        methodParametersOffset = currentOffset;
      } else {
        Attribute currentAnnotationOffset = this.readAttribute(context.attributePrototypes, methodVisitor, currentOffset, nextAttribute, charBuffer, -1, (Label[])null);
        currentAnnotationOffset.nextAttribute = attributes;
        attributes = currentAnnotationOffset;
      }
    }

    MethodVisitor var25 = classVisitor.visitMethod(context.currentMethodAccessFlags, context.currentMethodName, context.currentMethodDescriptor, signatureIndex == 0?null:this.readUtf(signatureIndex, charBuffer), exceptions);
    if(var25 == null) {
      return currentOffset;
    } else {
      if(var25 instanceof MethodWriter) {
        MethodWriter var26 = (MethodWriter)var25;
        if(var26.canCopyMethodAttributes(this, methodInfoOffset, currentOffset - methodInfoOffset, synthetic, (context.currentMethodAccessFlags & 131072) != 0, this.readUnsignedShort(methodInfoOffset + 4), signatureIndex, exceptionsOffset)) {
          return currentOffset;
        }
      }

      if(methodParametersOffset != 0) {
        nextAttribute = this.readByte(methodParametersOffset);

        for(var27 = methodParametersOffset + 1; nextAttribute-- > 0; var27 += 4) {
          var25.visitParameter(this.readUTF8(var27, charBuffer), this.readUnsignedShort(var27 + 2));
        }
      }

      if(annotationDefaultOffset != 0) {
        AnnotationVisitor var28 = var25.visitAnnotationDefault();
        this.readElementValue(var28, annotationDefaultOffset, (String)null, charBuffer);
        if(var28 != null) {
          var28.visitEnd();
        }
      }

      String var29;
      if(runtimeVisibleAnnotationsOffset != 0) {
        nextAttribute = this.readUnsignedShort(runtimeVisibleAnnotationsOffset);

        for(var27 = runtimeVisibleAnnotationsOffset + 2; nextAttribute-- > 0; var27 = this.readElementValues(var25.visitAnnotation(var29, true), var27, true, charBuffer)) {
          var29 = this.readUTF8(var27, charBuffer);
          var27 += 2;
        }
      }

      if(runtimeInvisibleAnnotationsOffset != 0) {
        nextAttribute = this.readUnsignedShort(runtimeInvisibleAnnotationsOffset);

        for(var27 = runtimeInvisibleAnnotationsOffset + 2; nextAttribute-- > 0; var27 = this.readElementValues(var25.visitAnnotation(var29, false), var27, true, charBuffer)) {
          var29 = this.readUTF8(var27, charBuffer);
          var27 += 2;
        }
      }

      if(runtimeVisibleTypeAnnotationsOffset != 0) {
        nextAttribute = this.readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);

        for(var27 = runtimeVisibleTypeAnnotationsOffset + 2; nextAttribute-- > 0; var27 = this.readElementValues(var25.visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, var29, true), var27, true, charBuffer)) {
          var27 = this.readTypeAnnotationTarget(context, var27);
          var29 = this.readUTF8(var27, charBuffer);
          var27 += 2;
        }
      }

      if(runtimeInvisibleTypeAnnotationsOffset != 0) {
        nextAttribute = this.readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);

        for(var27 = runtimeInvisibleTypeAnnotationsOffset + 2; nextAttribute-- > 0; var27 = this.readElementValues(var25.visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, var29, false), var27, true, charBuffer)) {
          var27 = this.readTypeAnnotationTarget(context, var27);
          var29 = this.readUTF8(var27, charBuffer);
          var27 += 2;
        }
      }

      if(runtimeVisibleParameterAnnotationsOffset != 0) {
        this.readParameterAnnotations(var25, context, runtimeVisibleParameterAnnotationsOffset, true);
      }

      if(runtimeInvisibleParameterAnnotationsOffset != 0) {
        this.readParameterAnnotations(var25, context, runtimeInvisibleParameterAnnotationsOffset, false);
      }

      while(attributes != null) {
        Attribute var30 = attributes.nextAttribute;
        attributes.nextAttribute = null;
        var25.visitAttribute(attributes);
        attributes = var30;
      }

      if(codeOffset != 0) {
        var25.visitCode();
        this.readCode(var25, context, codeOffset);
      }

      var25.visitEnd();
      return currentOffset;
    }
  }

  private void readCode(MethodVisitor methodVisitor, Context context, int codeOffset) {
    byte[] classFileBuffer = this.b;
    char[] charBuffer = context.charBuffer;
    int maxStack = this.readUnsignedShort(codeOffset);
    int maxLocals = this.readUnsignedShort(codeOffset + 2);
    int codeLength = this.readInt(codeOffset + 4);
    int currentOffset = codeOffset + 8;
    int bytecodeStartOffset = currentOffset;
    int bytecodeEndOffset = currentOffset + codeLength;
    Label[] labels = context.currentMethodLabels = new Label[codeLength + 1];

    int exceptionTableLength;
    int stackMapFrameOffset;
    int stackMapTableEndOffset;
    label419:
    while(currentOffset < bytecodeEndOffset) {
      exceptionTableLength = currentOffset - bytecodeStartOffset;
      stackMapFrameOffset = classFileBuffer[currentOffset] & 255;
      switch(stackMapFrameOffset) {
        case 0:
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
        case 8:
        case 9:
        case 10:
        case 11:
        case 12:
        case 13:
        case 14:
        case 15:
        case 26:
        case 27:
        case 28:
        case 29:
        case 30:
        case 31:
        case 32:
        case 33:
        case 34:
        case 35:
        case 36:
        case 37:
        case 38:
        case 39:
        case 40:
        case 41:
        case 42:
        case 43:
        case 44:
        case 45:
        case 46:
        case 47:
        case 48:
        case 49:
        case 50:
        case 51:
        case 52:
        case 53:
        case 59:
        case 60:
        case 61:
        case 62:
        case 63:
        case 64:
        case 65:
        case 66:
        case 67:
        case 68:
        case 69:
        case 70:
        case 71:
        case 72:
        case 73:
        case 74:
        case 75:
        case 76:
        case 77:
        case 78:
        case 79:
        case 80:
        case 81:
        case 82:
        case 83:
        case 84:
        case 85:
        case 86:
        case 87:
        case 88:
        case 89:
        case 90:
        case 91:
        case 92:
        case 93:
        case 94:
        case 95:
        case 96:
        case 97:
        case 98:
        case 99:
        case 100:
        case 101:
        case 102:
        case 103:
        case 104:
        case 105:
        case 106:
        case 107:
        case 108:
        case 109:
        case 110:
        case 111:
        case 112:
        case 113:
        case 114:
        case 115:
        case 116:
        case 117:
        case 118:
        case 119:
        case 120:
        case 121:
        case 122:
        case 123:
        case 124:
        case 125:
        case 126:
        case 127:
        case 128:
        case 129:
        case 130:
        case 131:
        case 133:
        case 134:
        case 135:
        case 136:
        case 137:
        case 138:
        case 139:
        case 140:
        case 141:
        case 142:
        case 143:
        case 144:
        case 145:
        case 146:
        case 147:
        case 148:
        case 149:
        case 150:
        case 151:
        case 152:
        case 172:
        case 173:
        case 174:
        case 175:
        case 176:
        case 177:
        case 190:
        case 191:
        case 194:
        case 195:
          ++currentOffset;
          break;
        case 16:
        case 18:
        case 21:
        case 22:
        case 23:
        case 24:
        case 25:
        case 54:
        case 55:
        case 56:
        case 57:
        case 58:
        case 169:
        case 188:
          currentOffset += 2;
          break;
        case 17:
        case 19:
        case 20:
        case 132:
        case 178:
        case 179:
        case 180:
        case 181:
        case 182:
        case 183:
        case 184:
        case 187:
        case 189:
        case 192:
        case 193:
          currentOffset += 3;
          break;
        case 153:
        case 154:
        case 155:
        case 156:
        case 157:
        case 158:
        case 159:
        case 160:
        case 161:
        case 162:
        case 163:
        case 164:
        case 165:
        case 166:
        case 167:
        case 168:
        case 198:
        case 199:
          this.createLabel(exceptionTableLength + this.readShort(currentOffset + 1), labels);
          currentOffset += 3;
          break;
        case 170:
          currentOffset += 4 - (exceptionTableLength & 3);
          this.createLabel(exceptionTableLength + this.readInt(currentOffset), labels);
          stackMapTableEndOffset = this.readInt(currentOffset + 8) - this.readInt(currentOffset + 4) + 1;
          currentOffset += 12;

          while(true) {
            if(stackMapTableEndOffset-- <= 0) {
              continue label419;
            }

            this.createLabel(exceptionTableLength + this.readInt(currentOffset), labels);
            currentOffset += 4;
          }
        case 171:
          currentOffset += 4 - (exceptionTableLength & 3);
          this.createLabel(exceptionTableLength + this.readInt(currentOffset), labels);
          int compressedFrames = this.readInt(currentOffset + 4);
          currentOffset += 8;

          while(true) {
            if(compressedFrames-- <= 0) {
              continue label419;
            }

            this.createLabel(exceptionTableLength + this.readInt(currentOffset + 4), labels);
            currentOffset += 8;
          }
        case 185:
        case 186:
          currentOffset += 5;
          break;
        case 196:
          switch(classFileBuffer[currentOffset + 1] & 255) {
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 169:
              currentOffset += 4;
              continue;
            case 132:
              currentOffset += 6;
              continue;
            default:
              throw new IllegalArgumentException();
          }
        case 197:
          currentOffset += 4;
          break;
        case 200:
        case 201:
        case 220:
          this.createLabel(exceptionTableLength + this.readInt(currentOffset + 1), labels);
          currentOffset += 5;
          break;
        case 202:
        case 203:
        case 204:
        case 205:
        case 206:
        case 207:
        case 208:
        case 209:
        case 210:
        case 211:
        case 212:
        case 213:
        case 214:
        case 215:
        case 216:
        case 217:
        case 218:
        case 219:
          this.createLabel(exceptionTableLength + this.readUnsignedShort(currentOffset + 1), labels);
          currentOffset += 3;
          break;
        default:
          throw new IllegalArgumentException();
      }
    }

    exceptionTableLength = this.readUnsignedShort(currentOffset);
    currentOffset += 2;

    while(exceptionTableLength-- > 0) {
      Label var41 = this.createLabel(this.readUnsignedShort(currentOffset), labels);
      Label var42 = this.createLabel(this.readUnsignedShort(currentOffset + 2), labels);
      Label var43 = this.createLabel(this.readUnsignedShort(currentOffset + 4), labels);
      String localVariableTableOffset = this.readUTF8(this.cpInfoOffsets[this.readUnsignedShort(currentOffset + 6)], charBuffer);
      currentOffset += 8;
      methodVisitor.visitTryCatchBlock(var41, var42, var43, localVariableTableOffset);
    }

    stackMapFrameOffset = 0;
    stackMapTableEndOffset = 0;
    boolean var44 = true;
    int var45 = 0;
    int localVariableTypeTableOffset = 0;
    int[] visibleTypeAnnotationOffsets = null;
    int[] invisibleTypeAnnotationOffsets = null;
    Attribute attributes = null;
    int attributesCount = this.readUnsignedShort(currentOffset);

    int currentVisibleTypeAnnotationIndex;
    int currentInvisibleTypeAnnotationIndex;
    int currentInvisibleTypeAnnotationBytecodeOffset;
    int var47;
    for(currentOffset += 2; attributesCount-- > 0; currentOffset += currentVisibleTypeAnnotationIndex) {
      String expandFrames = this.readUTF8(currentOffset, charBuffer);
      currentVisibleTypeAnnotationIndex = this.readInt(currentOffset + 2);
      currentOffset += 6;
      int insertFrame;
      if("LocalVariableTable".equals(expandFrames)) {
        if((context.parsingOptions & 2) == 0) {
          var45 = currentOffset;
          currentInvisibleTypeAnnotationIndex = this.readUnsignedShort(currentOffset);

          for(var47 = currentOffset + 2; currentInvisibleTypeAnnotationIndex-- > 0; var47 += 10) {
            currentInvisibleTypeAnnotationBytecodeOffset = this.readUnsignedShort(var47);
            this.createDebugLabel(currentInvisibleTypeAnnotationBytecodeOffset, labels);
            insertFrame = this.readUnsignedShort(var47 + 2);
            this.createDebugLabel(currentInvisibleTypeAnnotationBytecodeOffset + insertFrame, labels);
          }
        }
      } else if("LocalVariableTypeTable".equals(expandFrames)) {
        localVariableTypeTableOffset = currentOffset;
      } else if("LineNumberTable".equals(expandFrames)) {
        if((context.parsingOptions & 2) == 0) {
          currentInvisibleTypeAnnotationIndex = this.readUnsignedShort(currentOffset);
          var47 = currentOffset + 2;

          while(currentInvisibleTypeAnnotationIndex-- > 0) {
            currentInvisibleTypeAnnotationBytecodeOffset = this.readUnsignedShort(var47);
            insertFrame = this.readUnsignedShort(var47 + 2);
            var47 += 4;
            this.createDebugLabel(currentInvisibleTypeAnnotationBytecodeOffset, labels);
            labels[currentInvisibleTypeAnnotationBytecodeOffset].addLineNumber(insertFrame);
          }
        }
      } else if("RuntimeVisibleTypeAnnotations".equals(expandFrames)) {
        visibleTypeAnnotationOffsets = this.readTypeAnnotations(methodVisitor, context, currentOffset, true);
      } else if("RuntimeInvisibleTypeAnnotations".equals(expandFrames)) {
        invisibleTypeAnnotationOffsets = this.readTypeAnnotations(methodVisitor, context, currentOffset, false);
      } else if("StackMapTable".equals(expandFrames)) {
        if((context.parsingOptions & 4) == 0) {
          stackMapFrameOffset = currentOffset + 2;
          stackMapTableEndOffset = currentOffset + currentVisibleTypeAnnotationIndex;
        }
      } else if("StackMap".equals(expandFrames)) {
        if((context.parsingOptions & 4) == 0) {
          stackMapFrameOffset = currentOffset + 2;
          stackMapTableEndOffset = currentOffset + currentVisibleTypeAnnotationIndex;
          var44 = false;
        }
      } else {
        Attribute currentVisibleTypeAnnotationBytecodeOffset = this.readAttribute(context.attributePrototypes, expandFrames, currentOffset, currentVisibleTypeAnnotationIndex, charBuffer, codeOffset, labels);
        currentVisibleTypeAnnotationBytecodeOffset.nextAttribute = attributes;
        attributes = currentVisibleTypeAnnotationBytecodeOffset;
      }
    }

    boolean var46 = (context.parsingOptions & 8) != 0;
    if(stackMapFrameOffset != 0) {
      context.currentFrameOffset = -1;
      context.currentFrameType = 0;
      context.currentFrameLocalCount = 0;
      context.currentFrameLocalCountDelta = 0;
      context.currentFrameLocalTypes = new Object[maxLocals];
      context.currentFrameStackCount = 0;
      context.currentFrameStackTypes = new Object[maxStack];
      if(var46) {
        this.computeImplicitFrame(context);
      }

      for(currentVisibleTypeAnnotationIndex = stackMapFrameOffset; currentVisibleTypeAnnotationIndex < stackMapTableEndOffset - 2; ++currentVisibleTypeAnnotationIndex) {
        if(classFileBuffer[currentVisibleTypeAnnotationIndex] == 8) {
          var47 = this.readUnsignedShort(currentVisibleTypeAnnotationIndex + 1);
          if(var47 >= 0 && var47 < codeLength && (classFileBuffer[bytecodeStartOffset + var47] & 255) == 187) {
            this.createLabel(var47, labels);
          }
        }
      }
    }

    if(var46 && (context.parsingOptions & 256) != 0) {
      methodVisitor.visitFrame(-1, maxLocals, (Object[])null, 0, (Object[])null);
    }

    currentVisibleTypeAnnotationIndex = 0;
    var47 = this.getTypeAnnotationBytecodeOffset(visibleTypeAnnotationOffsets, 0);
    currentInvisibleTypeAnnotationIndex = 0;
    currentInvisibleTypeAnnotationBytecodeOffset = this.getTypeAnnotationBytecodeOffset(invisibleTypeAnnotationOffsets, 0);
    boolean var48 = false;
    int wideJumpOpcodeDelta = (context.parsingOptions & 256) == 0?33:0;
    currentOffset = bytecodeStartOffset;

    int startPc;
    String annotationDescriptor;
    int var52;
    int var53;
    String var55;
    String var59;
    while(currentOffset < bytecodeEndOffset) {
      int nextAttribute = currentOffset - bytecodeStartOffset;
      Label localVariableTableLength = labels[nextAttribute];
      if(localVariableTableLength != null) {
        localVariableTableLength.accept(methodVisitor, (context.parsingOptions & 2) == 0);
      }

      while(stackMapFrameOffset != 0 && (context.currentFrameOffset == nextAttribute || context.currentFrameOffset == -1)) {
        if(context.currentFrameOffset != -1) {
          if(var44 && !var46) {
            methodVisitor.visitFrame(context.currentFrameType, context.currentFrameLocalCountDelta, context.currentFrameLocalTypes, context.currentFrameStackCount, context.currentFrameStackTypes);
          } else {
            methodVisitor.visitFrame(-1, context.currentFrameLocalCount, context.currentFrameLocalTypes, context.currentFrameStackCount, context.currentFrameStackTypes);
          }

          var48 = false;
        }

        if(stackMapFrameOffset < stackMapTableEndOffset) {
          stackMapFrameOffset = this.readStackMapFrame(stackMapFrameOffset, var44, var46, context);
        } else {
          stackMapFrameOffset = 0;
        }
      }

      if(var48) {
        if((context.parsingOptions & 8) != 0) {
          methodVisitor.visitFrame(256, 0, (Object[])null, 0, (Object[])null);
        }

        var48 = false;
      }

      startPc = classFileBuffer[currentOffset] & 255;
      Label typeAnnotationOffset;
      String index;
      int signature;
      Label[] var57;
      switch(startPc) {
        case 0:
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
        case 8:
        case 9:
        case 10:
        case 11:
        case 12:
        case 13:
        case 14:
        case 15:
        case 46:
        case 47:
        case 48:
        case 49:
        case 50:
        case 51:
        case 52:
        case 53:
        case 79:
        case 80:
        case 81:
        case 82:
        case 83:
        case 84:
        case 85:
        case 86:
        case 87:
        case 88:
        case 89:
        case 90:
        case 91:
        case 92:
        case 93:
        case 94:
        case 95:
        case 96:
        case 97:
        case 98:
        case 99:
        case 100:
        case 101:
        case 102:
        case 103:
        case 104:
        case 105:
        case 106:
        case 107:
        case 108:
        case 109:
        case 110:
        case 111:
        case 112:
        case 113:
        case 114:
        case 115:
        case 116:
        case 117:
        case 118:
        case 119:
        case 120:
        case 121:
        case 122:
        case 123:
        case 124:
        case 125:
        case 126:
        case 127:
        case 128:
        case 129:
        case 130:
        case 131:
        case 133:
        case 134:
        case 135:
        case 136:
        case 137:
        case 138:
        case 139:
        case 140:
        case 141:
        case 142:
        case 143:
        case 144:
        case 145:
        case 146:
        case 147:
        case 148:
        case 149:
        case 150:
        case 151:
        case 152:
        case 172:
        case 173:
        case 174:
        case 175:
        case 176:
        case 177:
        case 190:
        case 191:
        case 194:
        case 195:
          methodVisitor.visitInsn(startPc);
          ++currentOffset;
          break;
        case 16:
        case 188:
          methodVisitor.visitIntInsn(startPc, classFileBuffer[currentOffset + 1]);
          currentOffset += 2;
          break;
        case 17:
          methodVisitor.visitIntInsn(startPc, this.readShort(currentOffset + 1));
          currentOffset += 3;
          break;
        case 18:
          methodVisitor.visitLdcInsn(this.readConst(classFileBuffer[currentOffset + 1] & 255, charBuffer));
          currentOffset += 2;
          break;
        case 19:
        case 20:
          methodVisitor.visitLdcInsn(this.readConst(this.readUnsignedShort(currentOffset + 1), charBuffer));
          currentOffset += 3;
          break;
        case 21:
        case 22:
        case 23:
        case 24:
        case 25:
        case 54:
        case 55:
        case 56:
        case 57:
        case 58:
        case 169:
          methodVisitor.visitVarInsn(startPc, classFileBuffer[currentOffset + 1] & 255);
          currentOffset += 2;
          break;
        case 26:
        case 27:
        case 28:
        case 29:
        case 30:
        case 31:
        case 32:
        case 33:
        case 34:
        case 35:
        case 36:
        case 37:
        case 38:
        case 39:
        case 40:
        case 41:
        case 42:
        case 43:
        case 44:
        case 45:
          startPc -= 26;
          methodVisitor.visitVarInsn(21 + (startPc >> 2), startPc & 3);
          ++currentOffset;
          break;
        case 59:
        case 60:
        case 61:
        case 62:
        case 63:
        case 64:
        case 65:
        case 66:
        case 67:
        case 68:
        case 69:
        case 70:
        case 71:
        case 72:
        case 73:
        case 74:
        case 75:
        case 76:
        case 77:
        case 78:
          startPc -= 59;
          methodVisitor.visitVarInsn(54 + (startPc >> 2), startPc & 3);
          ++currentOffset;
          break;
        case 132:
          methodVisitor.visitIincInsn(classFileBuffer[currentOffset + 1] & 255, classFileBuffer[currentOffset + 2]);
          currentOffset += 3;
          break;
        case 153:
        case 154:
        case 155:
        case 156:
        case 157:
        case 158:
        case 159:
        case 160:
        case 161:
        case 162:
        case 163:
        case 164:
        case 165:
        case 166:
        case 167:
        case 168:
        case 198:
        case 199:
          methodVisitor.visitJumpInsn(startPc, labels[nextAttribute + this.readShort(currentOffset + 1)]);
          currentOffset += 3;
          break;
        case 170:
          currentOffset += 4 - (nextAttribute & 3);
          typeAnnotationOffset = labels[nextAttribute + this.readInt(currentOffset)];
          var53 = this.readInt(currentOffset + 4);
          int var56 = this.readInt(currentOffset + 8);
          currentOffset += 12;
          var57 = new Label[var56 - var53 + 1];

          for(signature = 0; signature < var57.length; ++signature) {
            var57[signature] = labels[nextAttribute + this.readInt(currentOffset)];
            currentOffset += 4;
          }

          methodVisitor.visitTableSwitchInsn(var53, var56, typeAnnotationOffset, var57);
          break;
        case 171:
          currentOffset += 4 - (nextAttribute & 3);
          typeAnnotationOffset = labels[nextAttribute + this.readInt(currentOffset)];
          var53 = this.readInt(currentOffset + 4);
          currentOffset += 8;
          int[] var54 = new int[var53];
          var57 = new Label[var53];

          for(signature = 0; signature < var53; ++signature) {
            var54[signature] = this.readInt(currentOffset);
            var57[signature] = labels[nextAttribute + this.readInt(currentOffset + 4)];
            currentOffset += 8;
          }

          methodVisitor.visitLookupSwitchInsn(typeAnnotationOffset, var54, var57);
          break;
        case 178:
        case 179:
        case 180:
        case 181:
        case 182:
        case 183:
        case 184:
        case 185:
          var52 = this.cpInfoOffsets[this.readUnsignedShort(currentOffset + 1)];
          var53 = this.cpInfoOffsets[this.readUnsignedShort(var52 + 2)];
          annotationDescriptor = this.readClass(var52, charBuffer);
          index = this.readUTF8(var53, charBuffer);
          var59 = this.readUTF8(var53 + 2, charBuffer);
          if(startPc < 182) {
            methodVisitor.visitFieldInsn(startPc, annotationDescriptor, index, var59);
          } else {
            boolean var60 = classFileBuffer[var52 - 1] == 11;
            methodVisitor.visitMethodInsn(startPc, annotationDescriptor, index, var59, var60);
          }

          if(startPc == 185) {
            currentOffset += 5;
          } else {
            currentOffset += 3;
          }
          break;
        case 186:
          var52 = this.cpInfoOffsets[this.readUnsignedShort(currentOffset + 1)];
          var53 = this.cpInfoOffsets[this.readUnsignedShort(var52 + 2)];
          annotationDescriptor = this.readUTF8(var53, charBuffer);
          index = this.readUTF8(var53 + 2, charBuffer);
          signature = this.bootstrapMethodOffsets[this.readUnsignedShort(var52)];
          Handle i = (Handle)this.readConst(this.readUnsignedShort(signature), charBuffer);
          Object[] bootstrapMethodArguments = new Object[this.readUnsignedShort(signature + 2)];
          signature += 4;

          for(int i1 = 0; i1 < bootstrapMethodArguments.length; ++i1) {
            bootstrapMethodArguments[i1] = this.readConst(this.readUnsignedShort(signature), charBuffer);
            signature += 2;
          }

          methodVisitor.visitInvokeDynamicInsn(annotationDescriptor, index, i, bootstrapMethodArguments);
          currentOffset += 5;
          break;
        case 187:
        case 189:
        case 192:
        case 193:
          methodVisitor.visitTypeInsn(startPc, this.readClass(currentOffset + 1, charBuffer));
          currentOffset += 3;
          break;
        case 196:
          startPc = classFileBuffer[currentOffset + 1] & 255;
          if(startPc == 132) {
            methodVisitor.visitIincInsn(this.readUnsignedShort(currentOffset + 2), this.readShort(currentOffset + 4));
            currentOffset += 6;
          } else {
            methodVisitor.visitVarInsn(startPc, this.readUnsignedShort(currentOffset + 2));
            currentOffset += 4;
          }
          break;
        case 197:
          methodVisitor.visitMultiANewArrayInsn(this.readClass(currentOffset + 1, charBuffer), classFileBuffer[currentOffset + 3] & 255);
          currentOffset += 4;
          break;
        case 200:
        case 201:
          methodVisitor.visitJumpInsn(startPc - wideJumpOpcodeDelta, labels[nextAttribute + this.readInt(currentOffset + 1)]);
          currentOffset += 5;
          break;
        case 202:
        case 203:
        case 204:
        case 205:
        case 206:
        case 207:
        case 208:
        case 209:
        case 210:
        case 211:
        case 212:
        case 213:
        case 214:
        case 215:
        case 216:
        case 217:
        case 218:
        case 219:
          startPc = startPc < 218?startPc - 49:startPc - 20;
          typeAnnotationOffset = labels[nextAttribute + this.readUnsignedShort(currentOffset + 1)];
          if(startPc != 167 && startPc != 168) {
            startPc = startPc < 167?(startPc + 1 ^ 1) - 1:startPc ^ 1;
            Label targetType = this.createLabel(nextAttribute + 3, labels);
            methodVisitor.visitJumpInsn(startPc, targetType);
            methodVisitor.visitJumpInsn(200, typeAnnotationOffset);
            var48 = true;
          } else {
            methodVisitor.visitJumpInsn(startPc + 33, typeAnnotationOffset);
          }

          currentOffset += 3;
          break;
        case 220:
          methodVisitor.visitJumpInsn(200, labels[nextAttribute + this.readInt(currentOffset + 1)]);
          var48 = true;
          currentOffset += 5;
          break;
        default:
          throw new AssertionError();
      }

      while(visibleTypeAnnotationOffsets != null && currentVisibleTypeAnnotationIndex < visibleTypeAnnotationOffsets.length && var47 <= nextAttribute) {
        if(var47 == nextAttribute) {
          var52 = this.readTypeAnnotationTarget(context, visibleTypeAnnotationOffsets[currentVisibleTypeAnnotationIndex]);
          var55 = this.readUTF8(var52, charBuffer);
          var52 += 2;
          this.readElementValues(methodVisitor.visitInsnAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, var55, true), var52, true, charBuffer);
        }

        ++currentVisibleTypeAnnotationIndex;
        var47 = this.getTypeAnnotationBytecodeOffset(visibleTypeAnnotationOffsets, currentVisibleTypeAnnotationIndex);
      }

      while(invisibleTypeAnnotationOffsets != null && currentInvisibleTypeAnnotationIndex < invisibleTypeAnnotationOffsets.length && currentInvisibleTypeAnnotationBytecodeOffset <= nextAttribute) {
        if(currentInvisibleTypeAnnotationBytecodeOffset == nextAttribute) {
          var52 = this.readTypeAnnotationTarget(context, invisibleTypeAnnotationOffsets[currentInvisibleTypeAnnotationIndex]);
          var55 = this.readUTF8(var52, charBuffer);
          var52 += 2;
          this.readElementValues(methodVisitor.visitInsnAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, var55, false), var52, true, charBuffer);
        }

        ++currentInvisibleTypeAnnotationIndex;
        currentInvisibleTypeAnnotationBytecodeOffset = this.getTypeAnnotationBytecodeOffset(invisibleTypeAnnotationOffsets, currentInvisibleTypeAnnotationIndex);
      }
    }

    if(labels[codeLength] != null) {
      methodVisitor.visitLabel(labels[codeLength]);
    }

    int[] var49;
    int var50;
    if(var45 != 0 && (context.parsingOptions & 2) == 0) {
      var49 = null;
      if(localVariableTypeTableOffset != 0) {
        var49 = new int[this.readUnsignedShort(localVariableTypeTableOffset) * 3];
        currentOffset = localVariableTypeTableOffset + 2;

        for(var50 = var49.length; var50 > 0; currentOffset += 10) {
          --var50;
          var49[var50] = currentOffset + 6;
          --var50;
          var49[var50] = this.readUnsignedShort(currentOffset + 8);
          --var50;
          var49[var50] = this.readUnsignedShort(currentOffset);
        }
      }

      var50 = this.readUnsignedShort(var45);

      int var58;
      for(currentOffset = var45 + 2; var50-- > 0; methodVisitor.visitLocalVariable(var55, annotationDescriptor, var59, labels[startPc], labels[startPc + var52], var58)) {
        startPc = this.readUnsignedShort(currentOffset);
        var52 = this.readUnsignedShort(currentOffset + 2);
        var55 = this.readUTF8(currentOffset + 4, charBuffer);
        annotationDescriptor = this.readUTF8(currentOffset + 6, charBuffer);
        var58 = this.readUnsignedShort(currentOffset + 8);
        currentOffset += 10;
        var59 = null;
        if(var49 != null) {
          for(int var61 = 0; var61 < var49.length; var61 += 3) {
            if(var49[var61] == startPc && var49[var61 + 1] == var58) {
              var59 = this.readUTF8(var49[var61 + 2], charBuffer);
              break;
            }
          }
        }
      }
    }

    if(visibleTypeAnnotationOffsets != null) {
      var49 = visibleTypeAnnotationOffsets;
      var50 = visibleTypeAnnotationOffsets.length;

      for(startPc = 0; startPc < var50; ++startPc) {
        var52 = var49[startPc];
        var53 = this.readByte(var52);
        if(var53 == 64 || var53 == 65) {
          currentOffset = this.readTypeAnnotationTarget(context, var52);
          annotationDescriptor = this.readUTF8(currentOffset, charBuffer);
          currentOffset += 2;
          this.readElementValues(methodVisitor.visitLocalVariableAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, context.currentLocalVariableAnnotationRangeStarts, context.currentLocalVariableAnnotationRangeEnds, context.currentLocalVariableAnnotationRangeIndices, annotationDescriptor, true), currentOffset, true, charBuffer);
        }
      }
    }

    if(invisibleTypeAnnotationOffsets != null) {
      var49 = invisibleTypeAnnotationOffsets;
      var50 = invisibleTypeAnnotationOffsets.length;

      for(startPc = 0; startPc < var50; ++startPc) {
        var52 = var49[startPc];
        var53 = this.readByte(var52);
        if(var53 == 64 || var53 == 65) {
          currentOffset = this.readTypeAnnotationTarget(context, var52);
          annotationDescriptor = this.readUTF8(currentOffset, charBuffer);
          currentOffset += 2;
          this.readElementValues(methodVisitor.visitLocalVariableAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, context.currentLocalVariableAnnotationRangeStarts, context.currentLocalVariableAnnotationRangeEnds, context.currentLocalVariableAnnotationRangeIndices, annotationDescriptor, false), currentOffset, true, charBuffer);
        }
      }
    }

    while(attributes != null) {
      Attribute var51 = attributes.nextAttribute;
      attributes.nextAttribute = null;
      methodVisitor.visitAttribute(attributes);
      attributes = var51;
    }

    methodVisitor.visitMaxs(maxStack, maxLocals);
  }

  protected Label readLabel(int bytecodeOffset, Label[] labels) {
    if(labels[bytecodeOffset] == null) {
      labels[bytecodeOffset] = new Label();
    }

    return labels[bytecodeOffset];
  }

  private Label createLabel(int bytecodeOffset, Label[] labels) {
    Label label = this.readLabel(bytecodeOffset, labels);
    label.flags = (short)(label.flags & -2);
    return label;
  }

  private void createDebugLabel(int bytecodeOffset, Label[] labels) {
    if(labels[bytecodeOffset] == null) {
      Label var10000 = this.readLabel(bytecodeOffset, labels);
      var10000.flags = (short)(var10000.flags | 1);
    }

  }

  private int[] readTypeAnnotations(MethodVisitor methodVisitor, Context context, int runtimeTypeAnnotationsOffset, boolean visible) {
    char[] charBuffer = context.charBuffer;
    int[] typeAnnotationsOffsets = new int[this.readUnsignedShort(runtimeTypeAnnotationsOffset)];
    int currentOffset = runtimeTypeAnnotationsOffset + 2;

    for(int i = 0; i < typeAnnotationsOffsets.length; ++i) {
      int targetType;
      int pathLength;
      typeAnnotationsOffsets[i] = currentOffset;
      targetType = this.readInt(currentOffset);
      label32:
      switch(targetType >>> 24) {
        case 0:
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
        case 8:
        case 9:
        case 10:
        case 11:
        case 12:
        case 13:
        case 14:
        case 15:
        case 19:
        case 20:
        case 21:
        case 22:
        case 24:
        case 25:
        case 26:
        case 27:
        case 28:
        case 29:
        case 30:
        case 31:
        case 32:
        case 33:
        case 34:
        case 35:
        case 36:
        case 37:
        case 38:
        case 39:
        case 40:
        case 41:
        case 42:
        case 43:
        case 44:
        case 45:
        case 46:
        case 47:
        case 48:
        case 49:
        case 50:
        case 51:
        case 52:
        case 53:
        case 54:
        case 55:
        case 56:
        case 57:
        case 58:
        case 59:
        case 60:
        case 61:
        case 62:
        case 63:
        default:
          throw new IllegalArgumentException();
        case 16:
        case 17:
        case 18:
        case 23:
        case 66:
        case 67:
        case 68:
        case 69:
        case 70:
          currentOffset += 3;
          break;
        case 64:
        case 65:
          pathLength = this.readUnsignedShort(currentOffset + 1);
          currentOffset += 3;

          while(true) {
            if(pathLength-- <= 0) {
              break label32;
            }

            int path = this.readUnsignedShort(currentOffset);
            int annotationDescriptor = this.readUnsignedShort(currentOffset + 2);
            currentOffset += 6;
            this.createLabel(path, context.currentMethodLabels);
            this.createLabel(path + annotationDescriptor, context.currentMethodLabels);
          }
        case 71:
        case 72:
        case 73:
        case 74:
        case 75:
          currentOffset += 4;
      }

      pathLength = this.readByte(currentOffset);
      if(targetType >>> 24 == 66) {
        TypePath var13 = pathLength == 0?null:new TypePath(this.b, currentOffset);
        currentOffset += 1 + 2 * pathLength;
        String var14 = this.readUTF8(currentOffset, charBuffer);
        currentOffset += 2;
        currentOffset = this.readElementValues(methodVisitor.visitTryCatchAnnotation(targetType & -256, var13, var14, visible), currentOffset, true, charBuffer);
      } else {
        currentOffset += 3 + 2 * pathLength;
        currentOffset = this.readElementValues((AnnotationVisitor)null, currentOffset, true, charBuffer);
      }
    }

    return typeAnnotationsOffsets;
  }

  private int getTypeAnnotationBytecodeOffset(int[] typeAnnotationOffsets, int typeAnnotationIndex) {
    return typeAnnotationOffsets != null && typeAnnotationIndex < typeAnnotationOffsets.length && this.readByte(typeAnnotationOffsets[typeAnnotationIndex]) >= 67?this.readUnsignedShort(typeAnnotationOffsets[typeAnnotationIndex] + 1):-1;
  }

  private int readTypeAnnotationTarget(Context context, int typeAnnotationOffset) {
    int currentOffset;
    int targetType;
    int pathLength;
    targetType = this.readInt(typeAnnotationOffset);
    label26:
    switch(targetType >>> 24) {
      case 0:
      case 1:
      case 22:
        targetType &= -65536;
        currentOffset = typeAnnotationOffset + 2;
        break;
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
      case 14:
      case 15:
      case 24:
      case 25:
      case 26:
      case 27:
      case 28:
      case 29:
      case 30:
      case 31:
      case 32:
      case 33:
      case 34:
      case 35:
      case 36:
      case 37:
      case 38:
      case 39:
      case 40:
      case 41:
      case 42:
      case 43:
      case 44:
      case 45:
      case 46:
      case 47:
      case 48:
      case 49:
      case 50:
      case 51:
      case 52:
      case 53:
      case 54:
      case 55:
      case 56:
      case 57:
      case 58:
      case 59:
      case 60:
      case 61:
      case 62:
      case 63:
      default:
        throw new IllegalArgumentException();
      case 16:
      case 17:
      case 18:
      case 23:
      case 66:
        targetType &= -256;
        currentOffset = typeAnnotationOffset + 3;
        break;
      case 19:
      case 20:
      case 21:
        targetType &= -16777216;
        currentOffset = typeAnnotationOffset + 1;
        break;
      case 64:
      case 65:
        targetType &= -16777216;
        pathLength = this.readUnsignedShort(typeAnnotationOffset + 1);
        currentOffset = typeAnnotationOffset + 3;
        context.currentLocalVariableAnnotationRangeStarts = new Label[pathLength];
        context.currentLocalVariableAnnotationRangeEnds = new Label[pathLength];
        context.currentLocalVariableAnnotationRangeIndices = new int[pathLength];
        int i = 0;

        while(true) {
          if(i >= pathLength) {
            break label26;
          }

          int startPc = this.readUnsignedShort(currentOffset);
          int length = this.readUnsignedShort(currentOffset + 2);
          int index = this.readUnsignedShort(currentOffset + 4);
          currentOffset += 6;
          context.currentLocalVariableAnnotationRangeStarts[i] = this.createLabel(startPc, context.currentMethodLabels);
          context.currentLocalVariableAnnotationRangeEnds[i] = this.createLabel(startPc + length, context.currentMethodLabels);
          context.currentLocalVariableAnnotationRangeIndices[i] = index;
          ++i;
        }
      case 67:
      case 68:
      case 69:
      case 70:
        targetType &= -16777216;
        currentOffset = typeAnnotationOffset + 3;
        break;
      case 71:
      case 72:
      case 73:
      case 74:
      case 75:
        targetType &= -16776961;
        currentOffset = typeAnnotationOffset + 4;
    }

    context.currentTypeAnnotationTarget = targetType;
    pathLength = this.readByte(currentOffset);
    context.currentTypeAnnotationTargetPath = pathLength == 0?null:new TypePath(this.b, currentOffset);
    return currentOffset + 1 + 2 * pathLength;
  }

  private void readParameterAnnotations(MethodVisitor methodVisitor, Context context, int runtimeParameterAnnotationsOffset, boolean visible) {
    int currentOffset = runtimeParameterAnnotationsOffset + 1;
    int numParameters = this.b[runtimeParameterAnnotationsOffset] & 255;
    methodVisitor.visitAnnotableParameterCount(numParameters, visible);
    char[] charBuffer = context.charBuffer;

    for(int i = 0; i < numParameters; ++i) {
      int numAnnotations = this.readUnsignedShort(currentOffset);

      String annotationDescriptor;
      for(currentOffset += 2; numAnnotations-- > 0; currentOffset = this.readElementValues(methodVisitor.visitParameterAnnotation(i, annotationDescriptor, visible), currentOffset, true, charBuffer)) {
        annotationDescriptor = this.readUTF8(currentOffset, charBuffer);
        currentOffset += 2;
      }
    }

  }

  private int readElementValues(AnnotationVisitor annotationVisitor, int annotationOffset, boolean named, char[] charBuffer) {
    int numElementValuePairs = this.readUnsignedShort(annotationOffset);
    int currentOffset = annotationOffset + 2;
    if(named) {
      while(numElementValuePairs-- > 0) {
        String elementName = this.readUTF8(currentOffset, charBuffer);
        currentOffset = this.readElementValue(annotationVisitor, currentOffset + 2, elementName, charBuffer);
      }
    } else {
      while(numElementValuePairs-- > 0) {
        currentOffset = this.readElementValue(annotationVisitor, currentOffset, (String)null, charBuffer);
      }
    }

    if(annotationVisitor != null) {
      annotationVisitor.visitEnd();
    }

    return currentOffset;
  }

  private int readElementValue(AnnotationVisitor annotationVisitor, int elementValueOffset, String elementName, char[] charBuffer) {
    if(annotationVisitor == null) {
      switch(this.b[elementValueOffset] & 255) {
        case 64:
          return this.readElementValues((AnnotationVisitor)null, elementValueOffset + 3, true, charBuffer);
        case 91:
          return this.readElementValues((AnnotationVisitor)null, elementValueOffset + 1, false, charBuffer);
        case 101:
          return elementValueOffset + 5;
        default:
          return elementValueOffset + 3;
      }
    } else {
      int currentOffset = elementValueOffset + 1;
      switch(this.b[elementValueOffset] & 255) {
        case 64:
          currentOffset = this.readElementValues(annotationVisitor.visitAnnotation(elementName, this.readUTF8(currentOffset, charBuffer)), currentOffset + 2, true, charBuffer);
          break;
        case 65:
        case 69:
        case 71:
        case 72:
        case 75:
        case 76:
        case 77:
        case 78:
        case 79:
        case 80:
        case 81:
        case 82:
        case 84:
        case 85:
        case 86:
        case 87:
        case 88:
        case 89:
        case 92:
        case 93:
        case 94:
        case 95:
        case 96:
        case 97:
        case 98:
        case 100:
        case 102:
        case 103:
        case 104:
        case 105:
        case 106:
        case 107:
        case 108:
        case 109:
        case 110:
        case 111:
        case 112:
        case 113:
        case 114:
        default:
          throw new IllegalArgumentException();
        case 66:
          annotationVisitor.visit(elementName, Byte.valueOf((byte)this.readInt(this.cpInfoOffsets[this.readUnsignedShort(currentOffset)])));
          currentOffset += 2;
          break;
        case 67:
          annotationVisitor.visit(elementName, Character.valueOf((char)this.readInt(this.cpInfoOffsets[this.readUnsignedShort(currentOffset)])));
          currentOffset += 2;
          break;
        case 68:
        case 70:
        case 73:
        case 74:
          annotationVisitor.visit(elementName, this.readConst(this.readUnsignedShort(currentOffset), charBuffer));
          currentOffset += 2;
          break;
        case 83:
          annotationVisitor.visit(elementName, Short.valueOf((short)this.readInt(this.cpInfoOffsets[this.readUnsignedShort(currentOffset)])));
          currentOffset += 2;
          break;
        case 90:
          annotationVisitor.visit(elementName, this.readInt(this.cpInfoOffsets[this.readUnsignedShort(currentOffset)]) == 0?Boolean.FALSE:Boolean.TRUE);
          currentOffset += 2;
          break;
        case 91:
          int numValues = this.readUnsignedShort(currentOffset);
          currentOffset += 2;
          if(numValues == 0) {
            return this.readElementValues(annotationVisitor.visitArray(elementName), currentOffset - 2, false, charBuffer);
          }

          switch(this.b[currentOffset] & 255) {
            case 66:
              byte[] byteValues = new byte[numValues];

              for(int var16 = 0; var16 < numValues; ++var16) {
                byteValues[var16] = (byte)this.readInt(this.cpInfoOffsets[this.readUnsignedShort(currentOffset + 1)]);
                currentOffset += 3;
              }

              annotationVisitor.visit(elementName, byteValues);
              return currentOffset;
            case 67:
              char[] var18 = new char[numValues];

              for(int var19 = 0; var19 < numValues; ++var19) {
                var18[var19] = (char)this.readInt(this.cpInfoOffsets[this.readUnsignedShort(currentOffset + 1)]);
                currentOffset += 3;
              }

              annotationVisitor.visit(elementName, var18);
              return currentOffset;
            case 68:
              double[] var22 = new double[numValues];

              for(int i = 0; i < numValues; ++i) {
                var22[i] = Double.longBitsToDouble(this.readLong(this.cpInfoOffsets[this.readUnsignedShort(currentOffset + 1)]));
                currentOffset += 3;
              }

              annotationVisitor.visit(elementName, var22);
              return currentOffset;
            case 69:
            case 71:
            case 72:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            default:
              currentOffset = this.readElementValues(annotationVisitor.visitArray(elementName), currentOffset - 2, false, charBuffer);
              return currentOffset;
            case 70:
              float[] var21 = new float[numValues];

              for(int doubleValues = 0; doubleValues < numValues; ++doubleValues) {
                var21[doubleValues] = Float.intBitsToFloat(this.readInt(this.cpInfoOffsets[this.readUnsignedShort(currentOffset + 1)]));
                currentOffset += 3;
              }

              annotationVisitor.visit(elementName, var21);
              return currentOffset;
            case 73:
              int[] intValues = new int[numValues];

              for(int var20 = 0; var20 < numValues; ++var20) {
                intValues[var20] = this.readInt(this.cpInfoOffsets[this.readUnsignedShort(currentOffset + 1)]);
                currentOffset += 3;
              }

              annotationVisitor.visit(elementName, intValues);
              return currentOffset;
            case 74:
              long[] longValues = new long[numValues];

              for(int floatValues = 0; floatValues < numValues; ++floatValues) {
                longValues[floatValues] = this.readLong(this.cpInfoOffsets[this.readUnsignedShort(currentOffset + 1)]);
                currentOffset += 3;
              }

              annotationVisitor.visit(elementName, longValues);
              return currentOffset;
            case 83:
              short[] var17 = new short[numValues];

              for(int charValues = 0; charValues < numValues; ++charValues) {
                var17[charValues] = (short)this.readInt(this.cpInfoOffsets[this.readUnsignedShort(currentOffset + 1)]);
                currentOffset += 3;
              }

              annotationVisitor.visit(elementName, var17);
              return currentOffset;
            case 90:
              boolean[] booleanValues = new boolean[numValues];

              for(int shortValues = 0; shortValues < numValues; ++shortValues) {
                booleanValues[shortValues] = this.readInt(this.cpInfoOffsets[this.readUnsignedShort(currentOffset + 1)]) != 0;
                currentOffset += 3;
              }

              annotationVisitor.visit(elementName, booleanValues);
              return currentOffset;
          }
        case 99:
          annotationVisitor.visit(elementName, Type.getType(this.readUTF8(currentOffset, charBuffer)));
          currentOffset += 2;
          break;
        case 101:
          annotationVisitor.visitEnum(elementName, this.readUTF8(currentOffset, charBuffer), this.readUTF8(currentOffset + 2, charBuffer));
          currentOffset += 4;
          break;
        case 115:
          annotationVisitor.visit(elementName, this.readUTF8(currentOffset, charBuffer));
          currentOffset += 2;
      }

      return currentOffset;
    }
  }

  private void computeImplicitFrame(Context context) {
    String methodDescriptor = context.currentMethodDescriptor;
    Object[] locals = context.currentFrameLocalTypes;
    int numLocal = 0;
    if((context.currentMethodAccessFlags & 8) == 0) {
      if("<init>".equals(context.currentMethodName)) {
        locals[numLocal++] = Opcodes.UNINITIALIZED_THIS;
      } else {
        locals[numLocal++] = this.readClass(this.header + 2, context.charBuffer);
      }
    }

    int currentMethodDescritorOffset = 1;

    while(true) {
      int currentArgumentDescriptorStartOffset = currentMethodDescritorOffset;
      switch(methodDescriptor.charAt(currentMethodDescritorOffset++)) {
        case 'B':
        case 'C':
        case 'I':
        case 'S':
        case 'Z':
          locals[numLocal++] = Opcodes.INTEGER;
          break;
        case 'D':
          locals[numLocal++] = Opcodes.DOUBLE;
          break;
        case 'E':
        case 'G':
        case 'H':
        case 'K':
        case 'M':
        case 'N':
        case 'O':
        case 'P':
        case 'Q':
        case 'R':
        case 'T':
        case 'U':
        case 'V':
        case 'W':
        case 'X':
        case 'Y':
        default:
          context.currentFrameLocalCount = numLocal;
          return;
        case 'F':
          locals[numLocal++] = Opcodes.FLOAT;
          break;
        case 'J':
          locals[numLocal++] = Opcodes.LONG;
          break;
        case 'L':
          while(methodDescriptor.charAt(currentMethodDescritorOffset) != 59) {
            ++currentMethodDescritorOffset;
          }

          locals[numLocal++] = methodDescriptor.substring(currentArgumentDescriptorStartOffset + 1, currentMethodDescritorOffset++);
          break;
        case '[':
          while(methodDescriptor.charAt(currentMethodDescritorOffset) == 91) {
            ++currentMethodDescritorOffset;
          }

          if(methodDescriptor.charAt(currentMethodDescritorOffset) == 76) {
            ++currentMethodDescritorOffset;

            while(methodDescriptor.charAt(currentMethodDescritorOffset) != 59) {
              ++currentMethodDescritorOffset;
            }
          }

          int var10001 = numLocal++;
          ++currentMethodDescritorOffset;
          locals[var10001] = methodDescriptor.substring(currentArgumentDescriptorStartOffset, currentMethodDescritorOffset);
      }
    }
  }

  private int readStackMapFrame(int stackMapFrameOffset, boolean compressed, boolean expand, Context context) {
    int currentOffset = stackMapFrameOffset;
    char[] charBuffer = context.charBuffer;
    Label[] labels = context.currentMethodLabels;
    int frameType;
    if(compressed) {
      currentOffset = stackMapFrameOffset + 1;
      frameType = this.b[stackMapFrameOffset] & 255;
    } else {
      frameType = 255;
      context.currentFrameOffset = -1;
    }

    context.currentFrameLocalCountDelta = 0;
    int offsetDelta;
    if(frameType < 64) {
      offsetDelta = frameType;
      context.currentFrameType = 3;
      context.currentFrameStackCount = 0;
    } else if(frameType < 128) {
      offsetDelta = frameType - 64;
      currentOffset = this.readVerificationTypeInfo(currentOffset, context.currentFrameStackTypes, 0, charBuffer, labels);
      context.currentFrameType = 4;
      context.currentFrameStackCount = 1;
    } else {
      if(frameType < 247) {
        throw new IllegalArgumentException();
      }

      offsetDelta = this.readUnsignedShort(currentOffset);
      currentOffset += 2;
      if(frameType == 247) {
        currentOffset = this.readVerificationTypeInfo(currentOffset, context.currentFrameStackTypes, 0, charBuffer, labels);
        context.currentFrameType = 4;
        context.currentFrameStackCount = 1;
      } else if(frameType >= 248 && frameType < 251) {
        context.currentFrameType = 2;
        context.currentFrameLocalCountDelta = 251 - frameType;
        context.currentFrameLocalCount -= context.currentFrameLocalCountDelta;
        context.currentFrameStackCount = 0;
      } else if(frameType == 251) {
        context.currentFrameType = 3;
        context.currentFrameStackCount = 0;
      } else {
        int numberOfLocals;
        int numberOfStackItems;
        if(frameType < 255) {
          numberOfLocals = expand?context.currentFrameLocalCount:0;

          for(numberOfStackItems = frameType - 251; numberOfStackItems > 0; --numberOfStackItems) {
            currentOffset = this.readVerificationTypeInfo(currentOffset, context.currentFrameLocalTypes, numberOfLocals++, charBuffer, labels);
          }

          context.currentFrameType = 1;
          context.currentFrameLocalCountDelta = frameType - 251;
          context.currentFrameLocalCount += context.currentFrameLocalCountDelta;
          context.currentFrameStackCount = 0;
        } else {
          numberOfLocals = this.readUnsignedShort(currentOffset);
          currentOffset += 2;
          context.currentFrameType = 0;
          context.currentFrameLocalCountDelta = numberOfLocals;
          context.currentFrameLocalCount = numberOfLocals;

          for(numberOfStackItems = 0; numberOfStackItems < numberOfLocals; ++numberOfStackItems) {
            currentOffset = this.readVerificationTypeInfo(currentOffset, context.currentFrameLocalTypes, numberOfStackItems, charBuffer, labels);
          }

          numberOfStackItems = this.readUnsignedShort(currentOffset);
          currentOffset += 2;
          context.currentFrameStackCount = numberOfStackItems;

          for(int stack = 0; stack < numberOfStackItems; ++stack) {
            currentOffset = this.readVerificationTypeInfo(currentOffset, context.currentFrameStackTypes, stack, charBuffer, labels);
          }
        }
      }
    }

    context.currentFrameOffset += offsetDelta + 1;
    this.createLabel(context.currentFrameOffset, labels);
    return currentOffset;
  }

  private int readVerificationTypeInfo(int verificationTypeInfoOffset, Object[] frame, int index, char[] charBuffer, Label[] labels) {
    int currentOffset = verificationTypeInfoOffset + 1;
    int tag = this.b[verificationTypeInfoOffset] & 255;
    switch(tag) {
      case 0:
        frame[index] = Opcodes.TOP;
        break;
      case 1:
        frame[index] = Opcodes.INTEGER;
        break;
      case 2:
        frame[index] = Opcodes.FLOAT;
        break;
      case 3:
        frame[index] = Opcodes.DOUBLE;
        break;
      case 4:
        frame[index] = Opcodes.LONG;
        break;
      case 5:
        frame[index] = Opcodes.NULL;
        break;
      case 6:
        frame[index] = Opcodes.UNINITIALIZED_THIS;
        break;
      case 7:
        frame[index] = this.readClass(currentOffset, charBuffer);
        currentOffset += 2;
        break;
      case 8:
        frame[index] = this.createLabel(this.readUnsignedShort(currentOffset), labels);
        currentOffset += 2;
        break;
      default:
        throw new IllegalArgumentException();
    }

    return currentOffset;
  }

  final int getFirstAttributeOffset() {
    int currentOffset = this.header + 8 + this.readUnsignedShort(this.header + 6) * 2;
    int fieldsCount = this.readUnsignedShort(currentOffset);
    currentOffset += 2;

    int methodsCount;
    while(fieldsCount-- > 0) {
      methodsCount = this.readUnsignedShort(currentOffset + 6);

      for(currentOffset += 8; methodsCount-- > 0; currentOffset += 6 + this.readInt(currentOffset + 2)) {
        ;
      }
    }

    methodsCount = this.readUnsignedShort(currentOffset);
    currentOffset += 2;

    while(methodsCount-- > 0) {
      int attributesCount = this.readUnsignedShort(currentOffset + 6);

      for(currentOffset += 8; attributesCount-- > 0; currentOffset += 6 + this.readInt(currentOffset + 2)) {
        ;
      }
    }

    return currentOffset + 2;
  }

  private int[] readBootstrapMethodsAttribute(int maxStringLength) {
    char[] charBuffer = new char[maxStringLength];
    int currentAttributeOffset = this.getFirstAttributeOffset();
    Object currentBootstrapMethodOffsets = null;

    for(int i = this.readUnsignedShort(currentAttributeOffset - 2); i > 0; --i) {
      String attributeName = this.readUTF8(currentAttributeOffset, charBuffer);
      int attributeLength = this.readInt(currentAttributeOffset + 2);
      currentAttributeOffset += 6;
      if("BootstrapMethods".equals(attributeName)) {
        int[] var10 = new int[this.readUnsignedShort(currentAttributeOffset)];
        int currentBootstrapMethodOffset = currentAttributeOffset + 2;

        for(int j = 0; j < var10.length; ++j) {
          var10[j] = currentBootstrapMethodOffset;
          currentBootstrapMethodOffset += 4 + this.readUnsignedShort(currentBootstrapMethodOffset + 2) * 2;
        }

        return var10;
      }

      currentAttributeOffset += attributeLength;
    }

    return null;
  }

  private Attribute readAttribute(Attribute[] attributePrototypes, String type, int offset, int length, char[] charBuffer, int codeAttributeOffset, Label[] labels) {
    Attribute[] var8 = attributePrototypes;
    int var9 = attributePrototypes.length;

    for(int var10 = 0; var10 < var9; ++var10) {
      Attribute attributePrototype = var8[var10];
      if(attributePrototype.type.equals(type)) {
        return attributePrototype.read(this, offset, length, charBuffer, codeAttributeOffset, labels);
      }
    }

    return (new Attribute(type)).read(this, offset, length, (char[])null, -1, (Label[])null);
  }

  public int getItemCount() {
    return this.cpInfoOffsets.length;
  }

  public int getItem(int constantPoolEntryIndex) {
    return this.cpInfoOffsets[constantPoolEntryIndex];
  }

  public int getMaxStringLength() {
    return this.maxStringLength;
  }

  public int readByte(int offset) {
    return this.b[offset] & 255;
  }

  public int readUnsignedShort(int offset) {
    byte[] classFileBuffer = this.b;
    return (classFileBuffer[offset] & 255) << 8 | classFileBuffer[offset + 1] & 255;
  }

  public short readShort(int offset) {
    byte[] classFileBuffer = this.b;
    return (short)((classFileBuffer[offset] & 255) << 8 | classFileBuffer[offset + 1] & 255);
  }

  public int readInt(int offset) {
    byte[] classFileBuffer = this.b;
    return (classFileBuffer[offset] & 255) << 24 | (classFileBuffer[offset + 1] & 255) << 16 | (classFileBuffer[offset + 2] & 255) << 8 | classFileBuffer[offset + 3] & 255;
  }

  public long readLong(int offset) {
    long l1 = (long)this.readInt(offset);
    long l0 = (long)this.readInt(offset + 4) & 4294967295L;
    return l1 << 32 | l0;
  }

  public String readUTF8(int offset, char[] charBuffer) {
    int constantPoolEntryIndex = this.readUnsignedShort(offset);
    return offset != 0 && constantPoolEntryIndex != 0?this.readUtf(constantPoolEntryIndex, charBuffer):null;
  }

  final String readUtf(int constantPoolEntryIndex, char[] charBuffer) {
    String value = this.constantUtf8Values[constantPoolEntryIndex];
    if(value != null) {
      return value;
    } else {
      int cpInfoOffset = this.cpInfoOffsets[constantPoolEntryIndex];
      return this.constantUtf8Values[constantPoolEntryIndex] = this.readUtf(cpInfoOffset + 2, this.readUnsignedShort(cpInfoOffset), charBuffer);
    }
  }

  private String readUtf(int utfOffset, int utfLength, char[] charBuffer) {
    int currentOffset = utfOffset;
    int endOffset = utfOffset + utfLength;
    int strLength = 0;
    byte[] classFileBuffer = this.b;

    while(currentOffset < endOffset) {
      byte currentByte = classFileBuffer[currentOffset++];
      if((currentByte & 128) == 0) {
        charBuffer[strLength++] = (char)(currentByte & 127);
      } else if((currentByte & 224) == 192) {
        charBuffer[strLength++] = (char)(((currentByte & 31) << 6) + (classFileBuffer[currentOffset++] & 63));
      } else {
        charBuffer[strLength++] = (char)(((currentByte & 15) << 12) + ((classFileBuffer[currentOffset++] & 63) << 6) + (classFileBuffer[currentOffset++] & 63));
      }
    }

    return new String(charBuffer, 0, strLength);
  }

  private String readStringish(int offset, char[] charBuffer) {
    return this.readUTF8(this.cpInfoOffsets[this.readUnsignedShort(offset)], charBuffer);
  }

  public String readClass(int offset, char[] charBuffer) {
    return this.readStringish(offset, charBuffer);
  }

  public String readModule(int offset, char[] charBuffer) {
    return this.readStringish(offset, charBuffer);
  }

  public String readPackage(int offset, char[] charBuffer) {
    return this.readStringish(offset, charBuffer);
  }

  private ConstantDynamic readConstantDynamic(int constantPoolEntryIndex, char[] charBuffer) {
    ConstantDynamic constantDynamic = this.constantDynamicValues[constantPoolEntryIndex];
    if(constantDynamic != null) {
      return constantDynamic;
    } else {
      int cpInfoOffset = this.cpInfoOffsets[constantPoolEntryIndex];
      int nameAndTypeCpInfoOffset = this.cpInfoOffsets[this.readUnsignedShort(cpInfoOffset + 2)];
      String name = this.readUTF8(nameAndTypeCpInfoOffset, charBuffer);
      String descriptor = this.readUTF8(nameAndTypeCpInfoOffset + 2, charBuffer);
      int bootstrapMethodOffset = this.bootstrapMethodOffsets[this.readUnsignedShort(cpInfoOffset)];
      Handle handle = (Handle)this.readConst(this.readUnsignedShort(bootstrapMethodOffset), charBuffer);
      Object[] bootstrapMethodArguments = new Object[this.readUnsignedShort(bootstrapMethodOffset + 2)];
      bootstrapMethodOffset += 4;

      for(int i = 0; i < bootstrapMethodArguments.length; ++i) {
        bootstrapMethodArguments[i] = this.readConst(this.readUnsignedShort(bootstrapMethodOffset), charBuffer);
        bootstrapMethodOffset += 2;
      }

      return this.constantDynamicValues[constantPoolEntryIndex] = new ConstantDynamic(name, descriptor, handle, bootstrapMethodArguments);
    }
  }

  public Object readConst(int constantPoolEntryIndex, char[] charBuffer) {
    int cpInfoOffset = this.cpInfoOffsets[constantPoolEntryIndex];
    switch(this.b[cpInfoOffset - 1]) {
      case 3:
        return Integer.valueOf(this.readInt(cpInfoOffset));
      case 4:
        return Float.valueOf(Float.intBitsToFloat(this.readInt(cpInfoOffset)));
      case 5:
        return Long.valueOf(this.readLong(cpInfoOffset));
      case 6:
        return Double.valueOf(Double.longBitsToDouble(this.readLong(cpInfoOffset)));
      case 7:
        return Type.getObjectType(this.readUTF8(cpInfoOffset, charBuffer));
      case 8:
        return this.readUTF8(cpInfoOffset, charBuffer);
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
      case 14:
      default:
        throw new IllegalArgumentException();
      case 15:
        int referenceKind = this.readByte(cpInfoOffset);
        int referenceCpInfoOffset = this.cpInfoOffsets[this.readUnsignedShort(cpInfoOffset + 1)];
        int nameAndTypeCpInfoOffset = this.cpInfoOffsets[this.readUnsignedShort(referenceCpInfoOffset + 2)];
        String owner = this.readClass(referenceCpInfoOffset, charBuffer);
        String name = this.readUTF8(nameAndTypeCpInfoOffset, charBuffer);
        String descriptor = this.readUTF8(nameAndTypeCpInfoOffset + 2, charBuffer);
        boolean isInterface = this.b[referenceCpInfoOffset - 1] == 11;
        return new Handle(referenceKind, owner, name, descriptor, isInterface);
      case 16:
        return Type.getMethodType(this.readUTF8(cpInfoOffset, charBuffer));
      case 17:
        return this.readConstantDynamic(constantPoolEntryIndex, charBuffer);
    }
  }
}
