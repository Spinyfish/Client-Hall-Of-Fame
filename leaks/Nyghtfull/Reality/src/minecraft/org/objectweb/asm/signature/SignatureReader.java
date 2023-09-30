package org.objectweb.asm.signature;

public class SignatureReader {
  private final String signatureValue;
  
  public SignatureReader(String signature) { this.signatureValue = signature; }
  
  public void accept(SignatureVisitor signatureVistor) {
    int offset;
    String signature = this.signatureValue;
    int length = signature.length();
    if (signature.charAt(0) == '<') {
      char currentChar;
      offset = 2;
      do {
        int classBoundStartOffset = signature.indexOf(':', offset);
        signatureVistor.visitFormalTypeParameter(signature
            .substring(offset - 1, classBoundStartOffset));
        offset = classBoundStartOffset + 1;
        currentChar = signature.charAt(offset);
        if (currentChar == 'L' || currentChar == '[' || currentChar == 'T')
          offset = parseType(signature, offset, signatureVistor.visitClassBound()); 
        while ((currentChar = signature.charAt(offset++)) == ':')
          offset = parseType(signature, offset, signatureVistor.visitInterfaceBound()); 
      } while (currentChar != '>');
    } else {
      offset = 0;
    } 
    if (signature.charAt(offset) == '(') {
      offset++;
      while (signature.charAt(offset) != ')')
        offset = parseType(signature, offset, signatureVistor.visitParameterType()); 
      offset = parseType(signature, offset + 1, signatureVistor.visitReturnType());
      while (offset < length)
        offset = parseType(signature, offset + 1, signatureVistor.visitExceptionType()); 
    } else {
      offset = parseType(signature, offset, signatureVistor.visitSuperclass());
      while (offset < length)
        offset = parseType(signature, offset, signatureVistor.visitInterface()); 
    } 
  }
  
  public void acceptType(SignatureVisitor signatureVisitor) { parseType(this.signatureValue, 0, signatureVisitor); }
  
  private static int parseType(String signature, int startOffset, SignatureVisitor signatureVisitor) {
    boolean inner, visited;
    int start, endOffset, offset = startOffset;
    char currentChar = signature.charAt(offset++);
    switch (currentChar) {
      case 'B':
      case 'C':
      case 'D':
      case 'F':
      case 'I':
      case 'J':
      case 'S':
      case 'V':
      case 'Z':
        signatureVisitor.visitBaseType(currentChar);
        return offset;
      case '[':
        return parseType(signature, offset, signatureVisitor.visitArrayType());
      case 'T':
        endOffset = signature.indexOf(';', offset);
        signatureVisitor.visitTypeVariable(signature.substring(offset, endOffset));
        return endOffset + 1;
      case 'L':
        start = offset;
        visited = false;
        inner = false;
        while (true) {
          currentChar = signature.charAt(offset++);
          if (currentChar == '.' || currentChar == ';') {
            if (!visited) {
              String name = signature.substring(start, offset - 1);
              if (inner) {
                signatureVisitor.visitInnerClassType(name);
              } else {
                signatureVisitor.visitClassType(name);
              } 
            } 
            if (currentChar == ';') {
              signatureVisitor.visitEnd();
              break;
            } 
            start = offset;
            visited = false;
            inner = true;
            continue;
          } 
          if (currentChar == '<') {
            String name = signature.substring(start, offset - 1);
            if (inner) {
              signatureVisitor.visitInnerClassType(name);
            } else {
              signatureVisitor.visitClassType(name);
            } 
            visited = true;
            while ((currentChar = signature.charAt(offset)) != '>') {
              switch (currentChar) {
                case '*':
                  offset++;
                  signatureVisitor.visitTypeArgument();
                  continue;
                case '+':
                case '-':
                  offset = parseType(signature, offset + 1, signatureVisitor
                      .visitTypeArgument(currentChar));
                  continue;
              } 
              offset = parseType(signature, offset, signatureVisitor.visitTypeArgument('='));
            } 
          } 
        } 
        return offset;
    } 
    throw new IllegalArgumentException();
  }
}
