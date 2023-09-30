package org.objectweb.asm;

final class AnnotationWriter extends AnnotationVisitor {
  private final SymbolTable symbolTable;
  
  private final boolean useNamedValues;
  
  private final ByteVector annotation;
  
  private final int numElementValuePairsOffset;
  
  private int numElementValuePairs;
  
  private final AnnotationWriter previousAnnotation;
  
  private AnnotationWriter nextAnnotation;
  
  AnnotationWriter(SymbolTable symbolTable, boolean useNamedValues, ByteVector annotation, AnnotationWriter previousAnnotation) {
    super(458752);
    this.symbolTable = symbolTable;
    this.useNamedValues = useNamedValues;
    this.annotation = annotation;
    this.numElementValuePairsOffset = (annotation.length == 0) ? -1 : (annotation.length - 2);
    this.previousAnnotation = previousAnnotation;
    if (previousAnnotation != null)
      previousAnnotation.nextAnnotation = this; 
  }
  
  AnnotationWriter(SymbolTable symbolTable, ByteVector annotation, AnnotationWriter previousAnnotation) { this(symbolTable, true, annotation, previousAnnotation); }
  
  public void visit(String name, Object value) {
    this.numElementValuePairs++;
    if (this.useNamedValues)
      this.annotation.putShort(this.symbolTable.addConstantUtf8(name)); 
    if (value instanceof String) {
      this.annotation.put12(115, this.symbolTable.addConstantUtf8((String)value));
    } else if (value instanceof Byte) {
      this.annotation.put12(66, (this.symbolTable.addConstantInteger(((Byte)value).byteValue())).index);
    } else if (value instanceof Boolean) {
      int booleanValue = ((Boolean)value).booleanValue() ? 1 : 0;
      this.annotation.put12(90, (this.symbolTable.addConstantInteger(booleanValue)).index);
    } else if (value instanceof Character) {
      this.annotation.put12(67, (this.symbolTable.addConstantInteger(((Character)value).charValue())).index);
    } else if (value instanceof Short) {
      this.annotation.put12(83, (this.symbolTable.addConstantInteger(((Short)value).shortValue())).index);
    } else if (value instanceof Type) {
      this.annotation.put12(99, this.symbolTable.addConstantUtf8(((Type)value).getDescriptor()));
    } else if (value instanceof byte[]) {
      byte[] byteArray = (byte[])value;
      this.annotation.put12(91, byteArray.length);
      for (byte byteValue : byteArray)
        this.annotation.put12(66, (this.symbolTable.addConstantInteger(byteValue)).index); 
    } else if (value instanceof boolean[]) {
      boolean[] booleanArray = (boolean[])value;
      this.annotation.put12(91, booleanArray.length);
      for (boolean booleanValue : booleanArray)
        this.annotation.put12(90, (this.symbolTable.addConstantInteger(booleanValue ? 1 : 0)).index); 
    } else if (value instanceof short[]) {
      short[] shortArray = (short[])value;
      this.annotation.put12(91, shortArray.length);
      for (short shortValue : shortArray)
        this.annotation.put12(83, (this.symbolTable.addConstantInteger(shortValue)).index); 
    } else if (value instanceof char[]) {
      char[] charArray = (char[])value;
      this.annotation.put12(91, charArray.length);
      for (char charValue : charArray)
        this.annotation.put12(67, (this.symbolTable.addConstantInteger(charValue)).index); 
    } else if (value instanceof int[]) {
      int[] intArray = (int[])value;
      this.annotation.put12(91, intArray.length);
      for (int intValue : intArray)
        this.annotation.put12(73, (this.symbolTable.addConstantInteger(intValue)).index); 
    } else if (value instanceof long[]) {
      long[] longArray = (long[])value;
      this.annotation.put12(91, longArray.length);
      for (long longValue : longArray)
        this.annotation.put12(74, (this.symbolTable.addConstantLong(longValue)).index); 
    } else if (value instanceof float[]) {
      float[] floatArray = (float[])value;
      this.annotation.put12(91, floatArray.length);
      for (float floatValue : floatArray)
        this.annotation.put12(70, (this.symbolTable.addConstantFloat(floatValue)).index); 
    } else if (value instanceof double[]) {
      double[] doubleArray = (double[])value;
      this.annotation.put12(91, doubleArray.length);
      for (double doubleValue : doubleArray)
        this.annotation.put12(68, (this.symbolTable.addConstantDouble(doubleValue)).index); 
    } else {
      Symbol symbol = this.symbolTable.addConstant(value);
      this.annotation.put12(".s.IFJDCS".charAt(symbol.tag), symbol.index);
    } 
  }
  
  public void visitEnum(String name, String descriptor, String value) {
    this.numElementValuePairs++;
    if (this.useNamedValues)
      this.annotation.putShort(this.symbolTable.addConstantUtf8(name)); 
    this.annotation
      .put12(101, this.symbolTable.addConstantUtf8(descriptor))
      .putShort(this.symbolTable.addConstantUtf8(value));
  }
  
  public AnnotationVisitor visitAnnotation(String name, String descriptor) {
    this.numElementValuePairs++;
    if (this.useNamedValues)
      this.annotation.putShort(this.symbolTable.addConstantUtf8(name)); 
    this.annotation.put12(64, this.symbolTable.addConstantUtf8(descriptor)).putShort(0);
    return new AnnotationWriter(this.symbolTable, this.annotation, null);
  }
  
  public AnnotationVisitor visitArray(String name) {
    this.numElementValuePairs++;
    if (this.useNamedValues)
      this.annotation.putShort(this.symbolTable.addConstantUtf8(name)); 
    this.annotation.put12(91, 0);
    return new AnnotationWriter(this.symbolTable, false, this.annotation, null);
  }
  
  public void visitEnd() {
    if (this.numElementValuePairsOffset != -1) {
      byte[] data = this.annotation.data;
      data[this.numElementValuePairsOffset] = (byte)(this.numElementValuePairs >>> 8);
      data[this.numElementValuePairsOffset + 1] = (byte)this.numElementValuePairs;
    } 
  }
  
  int computeAnnotationsSize(String attributeName) {
    if (attributeName != null)
      this.symbolTable.addConstantUtf8(attributeName); 
    int attributeSize = 8;
    AnnotationWriter annotationWriter = this;
    while (annotationWriter != null) {
      attributeSize += annotationWriter.annotation.length;
      annotationWriter = annotationWriter.previousAnnotation;
    } 
    return attributeSize;
  }
  
  void putAnnotations(int attributeNameIndex, ByteVector output) {
    int attributeLength = 2;
    int numAnnotations = 0;
    AnnotationWriter annotationWriter = this;
    AnnotationWriter firstAnnotation = null;
    while (annotationWriter != null) {
      annotationWriter.visitEnd();
      attributeLength += annotationWriter.annotation.length;
      numAnnotations++;
      firstAnnotation = annotationWriter;
      annotationWriter = annotationWriter.previousAnnotation;
    } 
    output.putShort(attributeNameIndex);
    output.putInt(attributeLength);
    output.putShort(numAnnotations);
    annotationWriter = firstAnnotation;
    while (annotationWriter != null) {
      output.putByteArray(annotationWriter.annotation.data, 0, annotationWriter.annotation.length);
      annotationWriter = annotationWriter.nextAnnotation;
    } 
  }
  
  static int computeParameterAnnotationsSize(String attributeName, AnnotationWriter[] annotationWriters, int annotableParameterCount) {
    int attributeSize = 7 + 2 * annotableParameterCount;
    for (int i = 0; i < annotableParameterCount; i++) {
      AnnotationWriter annotationWriter = annotationWriters[i];
      attributeSize += ((annotationWriter == null) ? 0 : (annotationWriter
        .computeAnnotationsSize(attributeName) - 8));
    } 
    return attributeSize;
  }
  
  static void putParameterAnnotations(int attributeNameIndex, AnnotationWriter[] annotationWriters, int annotableParameterCount, ByteVector output) {
    int attributeLength = 1 + 2 * annotableParameterCount;
    for (int i = 0; i < annotableParameterCount; i++) {
      AnnotationWriter annotationWriter = annotationWriters[i];
      attributeLength += ((annotationWriter == null) ? 0 : (annotationWriter
        .computeAnnotationsSize(null) - 8));
    } 
    output.putShort(attributeNameIndex);
    output.putInt(attributeLength);
    output.putByte(annotableParameterCount);
    for (int i = 0; i < annotableParameterCount; i++) {
      AnnotationWriter annotationWriter = annotationWriters[i];
      AnnotationWriter firstAnnotation = null;
      int numAnnotations = 0;
      while (annotationWriter != null) {
        annotationWriter.visitEnd();
        numAnnotations++;
        firstAnnotation = annotationWriter;
        annotationWriter = annotationWriter.previousAnnotation;
      } 
      output.putShort(numAnnotations);
      annotationWriter = firstAnnotation;
      while (annotationWriter != null) {
        output.putByteArray(annotationWriter.annotation.data, 0, annotationWriter.annotation.length);
        annotationWriter = annotationWriter.nextAnnotation;
      } 
    } 
  }
}
