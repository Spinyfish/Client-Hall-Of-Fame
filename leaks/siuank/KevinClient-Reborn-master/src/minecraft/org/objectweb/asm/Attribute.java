package org.objectweb.asm;

public class Attribute {
  public final String type;
  
  private byte[] content;
  
  Attribute nextAttribute;
  
  protected Attribute(String type) { this.type = type; }
  
  public boolean isUnknown() { return true; }
  
  public boolean isCodeAttribute() { return false; }
  
  protected Label[] getLabels() { return new Label[0]; }
  
  protected Attribute read(ClassReader classReader, int offset, int length, char[] charBuffer, int codeAttributeOffset, Label[] labels) {
    Attribute attribute = new Attribute(this.type);
    attribute.content = new byte[length];
    System.arraycopy(classReader.b, offset, attribute.content, 0, length);
    return attribute;
  }
  
  protected ByteVector write(ClassWriter classWriter, byte[] code, int codeLength, int maxStack, int maxLocals) { return new ByteVector(this.content); }
  
  final int getAttributeCount() {
    int count = 0;
    Attribute attribute = this;
    while (attribute != null) {
      count++;
      attribute = attribute.nextAttribute;
    } 
    return count;
  }
  
  final int computeAttributesSize(SymbolTable symbolTable) {
    byte[] code = null;
    int codeLength = 0;
    int maxStack = -1;
    int maxLocals = -1;
    return computeAttributesSize(symbolTable, code, 0, -1, -1);
  }
  
  final int computeAttributesSize(SymbolTable symbolTable, byte[] code, int codeLength, int maxStack, int maxLocals) {
    ClassWriter classWriter = symbolTable.classWriter;
    int size = 0;
    Attribute attribute = this;
    while (attribute != null) {
      symbolTable.addConstantUtf8(attribute.type);
      size += 6 + (attribute.write(classWriter, code, codeLength, maxStack, maxLocals)).length;
      attribute = attribute.nextAttribute;
    } 
    return size;
  }
  
  final void putAttributes(SymbolTable symbolTable, ByteVector output) {
    byte[] code = null;
    int codeLength = 0;
    int maxStack = -1;
    int maxLocals = -1;
    putAttributes(symbolTable, code, 0, -1, -1, output);
  }
  
  final void putAttributes(SymbolTable symbolTable, byte[] code, int codeLength, int maxStack, int maxLocals, ByteVector output) {
    ClassWriter classWriter = symbolTable.classWriter;
    Attribute attribute = this;
    while (attribute != null) {
      ByteVector attributeContent = attribute.write(classWriter, code, codeLength, maxStack, maxLocals);
      output.putShort(symbolTable.addConstantUtf8(attribute.type)).putInt(attributeContent.length);
      output.putByteArray(attributeContent.data, 0, attributeContent.length);
      attribute = attribute.nextAttribute;
    } 
  }
  
  static final class Set {
    private static final int SIZE_INCREMENT = 6;
    
    private int size;
    
    private Attribute[] data = new Attribute[6];
    
    void addAttributes(Attribute attributeList) {
      Attribute attribute = attributeList;
      while (attribute != null) {
        if (!contains(attribute))
          add(attribute); 
        attribute = attribute.nextAttribute;
      } 
    }
    
    Attribute[] toArray() {
      Attribute[] result = new Attribute[this.size];
      System.arraycopy(this.data, 0, result, 0, this.size);
      return result;
    }
    
    private boolean contains(Attribute attribute) {
      for (int i = 0; i < this.size; i++) {
        if ((this.data[i]).type.equals(attribute.type))
          return true; 
      } 
      return false;
    }
    
    private void add(Attribute attribute) {
      if (this.size >= this.data.length) {
        Attribute[] newData = new Attribute[this.data.length + 6];
        System.arraycopy(this.data, 0, newData, 0, this.size);
        this.data = newData;
      } 
      this.data[this.size++] = attribute;
    }
  }
}
