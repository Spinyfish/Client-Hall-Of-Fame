package org.objectweb.asm;

final class CurrentFrame extends Frame {
  CurrentFrame(Label owner) { super(owner); }
  
  void execute(int opcode, int arg, Symbol symbolArg, SymbolTable symbolTable) {
    super.execute(opcode, arg, symbolArg, symbolTable);
    Frame successor = new Frame(null);
    merge(symbolTable, successor, 0);
    copyFrom(successor);
  }
}
