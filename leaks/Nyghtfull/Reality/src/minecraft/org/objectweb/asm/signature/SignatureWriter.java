package org.objectweb.asm.signature;

public class SignatureWriter extends SignatureVisitor {
  private final StringBuilder stringBuilder = new StringBuilder();
  
  private boolean hasFormals;
  
  private boolean hasParameters;
  
  private int argumentStack;
  
  public SignatureWriter() { super(458752); }
  
  public void visitFormalTypeParameter(String name) {
    if (!this.hasFormals) {
      this.hasFormals = true;
      this.stringBuilder.append('<');
    } 
    this.stringBuilder.append(name);
    this.stringBuilder.append(':');
  }
  
  public SignatureVisitor visitClassBound() { return this; }
  
  public SignatureVisitor visitInterfaceBound() {
    this.stringBuilder.append(':');
    return this;
  }
  
  public SignatureVisitor visitSuperclass() {
    endFormals();
    return this;
  }
  
  public SignatureVisitor visitInterface() { return this; }
  
  public SignatureVisitor visitParameterType() {
    endFormals();
    if (!this.hasParameters) {
      this.hasParameters = true;
      this.stringBuilder.append('(');
    } 
    return this;
  }
  
  public SignatureVisitor visitReturnType() {
    endFormals();
    if (!this.hasParameters)
      this.stringBuilder.append('('); 
    this.stringBuilder.append(')');
    return this;
  }
  
  public SignatureVisitor visitExceptionType() {
    this.stringBuilder.append('^');
    return this;
  }
  
  public void visitBaseType(char descriptor) { this.stringBuilder.append(descriptor); }
  
  public void visitTypeVariable(String name) {
    this.stringBuilder.append('T');
    this.stringBuilder.append(name);
    this.stringBuilder.append(';');
  }
  
  public SignatureVisitor visitArrayType() {
    this.stringBuilder.append('[');
    return this;
  }
  
  public void visitClassType(String name) {
    this.stringBuilder.append('L');
    this.stringBuilder.append(name);
    this.argumentStack *= 2;
  }
  
  public void visitInnerClassType(String name) {
    endArguments();
    this.stringBuilder.append('.');
    this.stringBuilder.append(name);
    this.argumentStack *= 2;
  }
  
  public void visitTypeArgument() {
    if (this.argumentStack % 2 == 0) {
      this.argumentStack |= 0x1;
      this.stringBuilder.append('<');
    } 
    this.stringBuilder.append('*');
  }
  
  public SignatureVisitor visitTypeArgument(char wildcard) {
    if (this.argumentStack % 2 == 0) {
      this.argumentStack |= 0x1;
      this.stringBuilder.append('<');
    } 
    if (wildcard != '=')
      this.stringBuilder.append(wildcard); 
    return this;
  }
  
  public void visitEnd() {
    endArguments();
    this.stringBuilder.append(';');
  }
  
  public String toString() { return this.stringBuilder.toString(); }
  
  private void endFormals() {
    if (this.hasFormals) {
      this.hasFormals = false;
      this.stringBuilder.append('>');
    } 
  }
  
  private void endArguments() {
    if (this.argumentStack % 2 == 1)
      this.stringBuilder.append('>'); 
    this.argumentStack /= 2;
  }
}
