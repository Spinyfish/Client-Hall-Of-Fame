package org.objectweb.asm;

import org.objectweb.asm.ByteVector;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Symbol;
import org.objectweb.asm.SymbolTable;
import org.objectweb.asm.Type;

class Frame {
  static final int SAME_FRAME = 0;
  static final int SAME_LOCALS_1_STACK_ITEM_FRAME = 64;
  static final int RESERVED = 128;
  static final int SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED = 247;
  static final int CHOP_FRAME = 248;
  static final int SAME_FRAME_EXTENDED = 251;
  static final int APPEND_FRAME = 252;
  static final int FULL_FRAME = 255;
  static final int ITEM_TOP = 0;
  static final int ITEM_INTEGER = 1;
  static final int ITEM_FLOAT = 2;
  static final int ITEM_DOUBLE = 3;
  static final int ITEM_LONG = 4;
  static final int ITEM_NULL = 5;
  static final int ITEM_UNINITIALIZED_THIS = 6;
  static final int ITEM_OBJECT = 7;
  static final int ITEM_UNINITIALIZED = 8;
  private static final int ITEM_ASM_BOOLEAN = 9;
  private static final int ITEM_ASM_BYTE = 10;
  private static final int ITEM_ASM_CHAR = 11;
  private static final int ITEM_ASM_SHORT = 12;
  private static final int DIM_MASK = -268435456;
  private static final int KIND_MASK = 251658240;
  private static final int FLAGS_MASK = 15728640;
  private static final int VALUE_MASK = 1048575;
  private static final int DIM_SHIFT = 28;
  private static final int ARRAY_OF = 268435456;
  private static final int ELEMENT_OF = -268435456;
  private static final int CONSTANT_KIND = 16777216;
  private static final int REFERENCE_KIND = 33554432;
  private static final int UNINITIALIZED_KIND = 50331648;
  private static final int LOCAL_KIND = 67108864;
  private static final int STACK_KIND = 83886080;
  private static final int TOP_IF_LONG_OR_DOUBLE_FLAG = 1048576;
  private static final int TOP = 16777216;
  private static final int BOOLEAN = 16777225;
  private static final int BYTE = 16777226;
  private static final int CHAR = 16777227;
  private static final int SHORT = 16777228;
  private static final int INTEGER = 16777217;
  private static final int FLOAT = 16777218;
  private static final int LONG = 16777220;
  private static final int DOUBLE = 16777219;
  private static final int NULL = 16777221;
  private static final int UNINITIALIZED_THIS = 16777222;
  Label owner;
  private int[] inputLocals;
  private int[] inputStack;
  private int[] outputLocals;
  private int[] outputStack;
  private short outputStackStart;
  private short outputStackTop;
  private int initializationCount;
  private int[] initializations;

  Frame(Label owner) {
    this.owner = owner;
  }

  final void copyFrom(Frame frame) {
    this.inputLocals = frame.inputLocals;
    this.inputStack = frame.inputStack;
    this.outputStackStart = 0;
    this.outputLocals = frame.outputLocals;
    this.outputStack = frame.outputStack;
    this.outputStackTop = frame.outputStackTop;
    this.initializationCount = frame.initializationCount;
    this.initializations = frame.initializations;
  }

  static int getAbstractTypeFromApiFormat(SymbolTable symbolTable, Object type) {
    if(type instanceof Integer) {
      return 16777216 | ((Integer)type).intValue();
    } else if(type instanceof String) {
      String descriptor = Type.getObjectType((String)type).getDescriptor();
      return getAbstractTypeFromDescriptor(symbolTable, descriptor, 0);
    } else {
      return 50331648 | symbolTable.addUninitializedType("", ((Label)type).bytecodeOffset);
    }
  }

  static int getAbstractTypeFromInternalName(SymbolTable symbolTable, String internalName) {
    return 33554432 | symbolTable.addType(internalName);
  }

  private static int getAbstractTypeFromDescriptor(SymbolTable symbolTable, String buffer, int offset) {
    String internalName;
    switch(buffer.charAt(offset)) {
      case 'B':
      case 'C':
      case 'I':
      case 'S':
      case 'Z':
        return 16777217;
      case 'D':
        return 16777219;
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
      case 'W':
      case 'X':
      case 'Y':
      default:
        throw new IllegalArgumentException();
      case 'F':
        return 16777218;
      case 'J':
        return 16777220;
      case 'L':
        internalName = buffer.substring(offset + 1, buffer.length() - 1);
        return 33554432 | symbolTable.addType(internalName);
      case 'V':
        return 0;
      case '[':
        int elementDescriptorOffset;
        for(elementDescriptorOffset = offset + 1; buffer.charAt(elementDescriptorOffset) == 91; ++elementDescriptorOffset) {
          ;
        }

        int typeValue;
        switch(buffer.charAt(elementDescriptorOffset)) {
          case 'B':
            typeValue = 16777226;
            break;
          case 'C':
            typeValue = 16777227;
            break;
          case 'D':
            typeValue = 16777219;
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
            throw new IllegalArgumentException();
          case 'F':
            typeValue = 16777218;
            break;
          case 'I':
            typeValue = 16777217;
            break;
          case 'J':
            typeValue = 16777220;
            break;
          case 'L':
            internalName = buffer.substring(elementDescriptorOffset + 1, buffer.length() - 1);
            typeValue = 33554432 | symbolTable.addType(internalName);
            break;
          case 'S':
            typeValue = 16777228;
            break;
          case 'Z':
            typeValue = 16777225;
        }

        return elementDescriptorOffset - offset << 28 | typeValue;
    }
  }

  final void setInputFrameFromDescriptor(SymbolTable symbolTable, int access, String descriptor, int maxLocals) {
    this.inputLocals = new int[maxLocals];
    this.inputStack = new int[0];
    int inputLocalIndex = 0;
    if((access & 8) == 0) {
      if((access & 262144) == 0) {
        this.inputLocals[inputLocalIndex++] = 33554432 | symbolTable.addType(symbolTable.getClassName());
      } else {
        this.inputLocals[inputLocalIndex++] = 16777222;
      }
    }

    Type[] var6 = Type.getArgumentTypes(descriptor);
    int var7 = var6.length;

    for(int var8 = 0; var8 < var7; ++var8) {
      Type argumentType = var6[var8];
      int abstractType = getAbstractTypeFromDescriptor(symbolTable, argumentType.getDescriptor(), 0);
      this.inputLocals[inputLocalIndex++] = abstractType;
      if(abstractType == 16777220 || abstractType == 16777219) {
        this.inputLocals[inputLocalIndex++] = 16777216;
      }
    }

    while(inputLocalIndex < maxLocals) {
      this.inputLocals[inputLocalIndex++] = 16777216;
    }

  }

  final void setInputFrameFromApiFormat(SymbolTable symbolTable, int numLocal, Object[] local, int numStack, Object[] stack) {
    int inputLocalIndex = 0;

    int numStackTop;
    for(numStackTop = 0; numStackTop < numLocal; ++numStackTop) {
      this.inputLocals[inputLocalIndex++] = getAbstractTypeFromApiFormat(symbolTable, local[numStackTop]);
      if(local[numStackTop] == Opcodes.LONG || local[numStackTop] == Opcodes.DOUBLE) {
        this.inputLocals[inputLocalIndex++] = 16777216;
      }
    }

    while(inputLocalIndex < this.inputLocals.length) {
      this.inputLocals[inputLocalIndex++] = 16777216;
    }

    numStackTop = 0;

    int inputStackIndex;
    for(inputStackIndex = 0; inputStackIndex < numStack; ++inputStackIndex) {
      if(stack[inputStackIndex] == Opcodes.LONG || stack[inputStackIndex] == Opcodes.DOUBLE) {
        ++numStackTop;
      }
    }

    this.inputStack = new int[numStack + numStackTop];
    inputStackIndex = 0;

    for(int i = 0; i < numStack; ++i) {
      this.inputStack[inputStackIndex++] = getAbstractTypeFromApiFormat(symbolTable, stack[i]);
      if(stack[i] == Opcodes.LONG || stack[i] == Opcodes.DOUBLE) {
        this.inputStack[inputStackIndex++] = 16777216;
      }
    }

    this.outputStackTop = 0;
    this.initializationCount = 0;
  }

  final int getInputStackSize() {
    return this.inputStack.length;
  }

  private int getLocal(int localIndex) {
    if(this.outputLocals != null && localIndex < this.outputLocals.length) {
      int abstractType = this.outputLocals[localIndex];
      if(abstractType == 0) {
        abstractType = this.outputLocals[localIndex] = 67108864 | localIndex;
      }

      return abstractType;
    } else {
      return 67108864 | localIndex;
    }
  }

  private void setLocal(int localIndex, int abstractType) {
    if(this.outputLocals == null) {
      this.outputLocals = new int[10];
    }

    int outputLocalsLength = this.outputLocals.length;
    if(localIndex >= outputLocalsLength) {
      int[] newOutputLocals = new int[Math.max(localIndex + 1, 2 * outputLocalsLength)];
      System.arraycopy(this.outputLocals, 0, newOutputLocals, 0, outputLocalsLength);
      this.outputLocals = newOutputLocals;
    }

    this.outputLocals[localIndex] = abstractType;
  }

  private void push(int abstractType) {
    if(this.outputStack == null) {
      this.outputStack = new int[10];
    }

    int outputStackLength = this.outputStack.length;
    if(this.outputStackTop >= outputStackLength) {
      int[] outputStackSize = new int[Math.max(this.outputStackTop + 1, 2 * outputStackLength)];
      System.arraycopy(this.outputStack, 0, outputStackSize, 0, outputStackLength);
      this.outputStack = outputStackSize;
    }

    this.outputStack[this.outputStackTop++] = abstractType;
    short var4 = (short)(this.outputStackStart + this.outputStackTop);
    if(var4 > this.owner.outputStackMax) {
      this.owner.outputStackMax = var4;
    }

  }

  private void push(SymbolTable symbolTable, String descriptor) {
    int typeDescriptorOffset = descriptor.charAt(0) == 40?descriptor.indexOf(41) + 1:0;
    int abstractType = getAbstractTypeFromDescriptor(symbolTable, descriptor, typeDescriptorOffset);
    if(abstractType != 0) {
      this.push(abstractType);
      if(abstractType == 16777220 || abstractType == 16777219) {
        this.push(16777216);
      }
    }

  }

  private int pop() {
    return this.outputStackTop > 0?this.outputStack[--this.outputStackTop]:83886080 | -(--this.outputStackStart);
  }

  private void pop(int elements) {
    if(this.outputStackTop >= elements) {
      this.outputStackTop = (short)(this.outputStackTop - elements);
    } else {
      this.outputStackStart = (short)(this.outputStackStart - (elements - this.outputStackTop));
      this.outputStackTop = 0;
    }

  }

  private void pop(String descriptor) {
    char firstDescriptorChar = descriptor.charAt(0);
    if(firstDescriptorChar == 40) {
      this.pop((Type.getArgumentsAndReturnSizes(descriptor) >> 2) - 1);
    } else if(firstDescriptorChar != 74 && firstDescriptorChar != 68) {
      this.pop(1);
    } else {
      this.pop(2);
    }

  }

  private void addInitializedType(int abstractType) {
    if(this.initializations == null) {
      this.initializations = new int[2];
    }

    int initializationsLength = this.initializations.length;
    if(this.initializationCount >= initializationsLength) {
      int[] newInitializations = new int[Math.max(this.initializationCount + 1, 2 * initializationsLength)];
      System.arraycopy(this.initializations, 0, newInitializations, 0, initializationsLength);
      this.initializations = newInitializations;
    }

    this.initializations[this.initializationCount++] = abstractType;
  }

  private int getInitializedType(SymbolTable symbolTable, int abstractType) {
    if(abstractType == 16777222 || (abstractType & -16777216) == 50331648) {
      for(int i = 0; i < this.initializationCount; ++i) {
        int initializedType = this.initializations[i];
        int dim = initializedType & -268435456;
        int kind = initializedType & 251658240;
        int value = initializedType & 1048575;
        if(kind == 67108864) {
          initializedType = dim + this.inputLocals[value];
        } else if(kind == 83886080) {
          initializedType = dim + this.inputStack[this.inputStack.length - value];
        }

        if(abstractType == initializedType) {
          if(abstractType == 16777222) {
            return 33554432 | symbolTable.addType(symbolTable.getClassName());
          }

          return 33554432 | symbolTable.addType(symbolTable.getType(abstractType & 1048575).value);
        }
      }
    }

    return abstractType;
  }

  void execute(int opcode, int arg, Symbol argSymbol, SymbolTable symbolTable) {
    int abstractType1;
    int abstractType2;
    int abstractType3;
    int arrayElementType1;
    switch(opcode) {
      case 0:
      case 116:
      case 117:
      case 118:
      case 119:
      case 145:
      case 146:
      case 147:
      case 167:
      case 177:
        break;
      case 1:
        this.push(16777221);
        break;
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 16:
      case 17:
      case 21:
        this.push(16777217);
        break;
      case 9:
      case 10:
      case 22:
        this.push(16777220);
        this.push(16777216);
        break;
      case 11:
      case 12:
      case 13:
      case 23:
        this.push(16777218);
        break;
      case 14:
      case 15:
      case 24:
        this.push(16777219);
        this.push(16777216);
        break;
      case 18:
        switch(argSymbol.tag) {
          case 3:
            this.push(16777217);
            return;
          case 4:
            this.push(16777218);
            return;
          case 5:
            this.push(16777220);
            this.push(16777216);
            return;
          case 6:
            this.push(16777219);
            this.push(16777216);
            return;
          case 7:
            this.push(33554432 | symbolTable.addType("java/lang/Class"));
            return;
          case 8:
            this.push(33554432 | symbolTable.addType("java/lang/String"));
            return;
          case 9:
          case 10:
          case 11:
          case 12:
          case 13:
          case 14:
          default:
            throw new AssertionError();
          case 15:
            this.push(33554432 | symbolTable.addType("java/lang/invoke/MethodHandle"));
            return;
          case 16:
            this.push(33554432 | symbolTable.addType("java/lang/invoke/MethodType"));
            return;
          case 17:
            this.push(symbolTable, argSymbol.value);
            return;
        }
      case 19:
      case 20:
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
      case 196:
      default:
        throw new IllegalArgumentException();
      case 25:
        this.push(this.getLocal(arg));
        break;
      case 46:
      case 51:
      case 52:
      case 53:
      case 96:
      case 100:
      case 104:
      case 108:
      case 112:
      case 120:
      case 122:
      case 124:
      case 126:
      case 128:
      case 130:
      case 136:
      case 142:
      case 149:
      case 150:
        this.pop(2);
        this.push(16777217);
        break;
      case 47:
      case 143:
        this.pop(2);
        this.push(16777220);
        this.push(16777216);
        break;
      case 48:
      case 98:
      case 102:
      case 106:
      case 110:
      case 114:
      case 137:
      case 144:
        this.pop(2);
        this.push(16777218);
        break;
      case 49:
      case 138:
        this.pop(2);
        this.push(16777219);
        this.push(16777216);
        break;
      case 50:
        this.pop(1);
        abstractType1 = this.pop();
        this.push(abstractType1 == 16777221?abstractType1:-268435456 + abstractType1);
        break;
      case 54:
      case 56:
      case 58:
        abstractType1 = this.pop();
        this.setLocal(arg, abstractType1);
        if(arg > 0) {
          arrayElementType1 = this.getLocal(arg - 1);
          if(arrayElementType1 != 16777220 && arrayElementType1 != 16777219) {
            if((arrayElementType1 & 251658240) == 67108864 || (arrayElementType1 & 251658240) == 83886080) {
              this.setLocal(arg - 1, arrayElementType1 | 1048576);
            }
          } else {
            this.setLocal(arg - 1, 16777216);
          }
        }
        break;
      case 55:
      case 57:
        this.pop(1);
        abstractType1 = this.pop();
        this.setLocal(arg, abstractType1);
        this.setLocal(arg + 1, 16777216);
        if(arg > 0) {
          arrayElementType1 = this.getLocal(arg - 1);
          if(arrayElementType1 != 16777220 && arrayElementType1 != 16777219) {
            if((arrayElementType1 & 251658240) == 67108864 || (arrayElementType1 & 251658240) == 83886080) {
              this.setLocal(arg - 1, arrayElementType1 | 1048576);
            }
          } else {
            this.setLocal(arg - 1, 16777216);
          }
        }
        break;
      case 79:
      case 81:
      case 83:
      case 84:
      case 85:
      case 86:
        this.pop(3);
        break;
      case 80:
      case 82:
        this.pop(4);
        break;
      case 87:
      case 153:
      case 154:
      case 155:
      case 156:
      case 157:
      case 158:
      case 170:
      case 171:
      case 172:
      case 174:
      case 176:
      case 191:
      case 194:
      case 195:
      case 198:
      case 199:
        this.pop(1);
        break;
      case 88:
      case 159:
      case 160:
      case 161:
      case 162:
      case 163:
      case 164:
      case 165:
      case 166:
      case 173:
      case 175:
        this.pop(2);
        break;
      case 89:
        abstractType1 = this.pop();
        this.push(abstractType1);
        this.push(abstractType1);
        break;
      case 90:
        abstractType1 = this.pop();
        abstractType2 = this.pop();
        this.push(abstractType1);
        this.push(abstractType2);
        this.push(abstractType1);
        break;
      case 91:
        abstractType1 = this.pop();
        abstractType2 = this.pop();
        abstractType3 = this.pop();
        this.push(abstractType1);
        this.push(abstractType3);
        this.push(abstractType2);
        this.push(abstractType1);
        break;
      case 92:
        abstractType1 = this.pop();
        abstractType2 = this.pop();
        this.push(abstractType2);
        this.push(abstractType1);
        this.push(abstractType2);
        this.push(abstractType1);
        break;
      case 93:
        abstractType1 = this.pop();
        abstractType2 = this.pop();
        abstractType3 = this.pop();
        this.push(abstractType2);
        this.push(abstractType1);
        this.push(abstractType3);
        this.push(abstractType2);
        this.push(abstractType1);
        break;
      case 94:
        abstractType1 = this.pop();
        abstractType2 = this.pop();
        abstractType3 = this.pop();
        int abstractType4 = this.pop();
        this.push(abstractType2);
        this.push(abstractType1);
        this.push(abstractType4);
        this.push(abstractType3);
        this.push(abstractType2);
        this.push(abstractType1);
        break;
      case 95:
        abstractType1 = this.pop();
        abstractType2 = this.pop();
        this.push(abstractType1);
        this.push(abstractType2);
        break;
      case 97:
      case 101:
      case 105:
      case 109:
      case 113:
      case 127:
      case 129:
      case 131:
        this.pop(4);
        this.push(16777220);
        this.push(16777216);
        break;
      case 99:
      case 103:
      case 107:
      case 111:
      case 115:
        this.pop(4);
        this.push(16777219);
        this.push(16777216);
        break;
      case 121:
      case 123:
      case 125:
        this.pop(3);
        this.push(16777220);
        this.push(16777216);
        break;
      case 132:
        this.setLocal(arg, 16777217);
        break;
      case 133:
      case 140:
        this.pop(1);
        this.push(16777220);
        this.push(16777216);
        break;
      case 134:
        this.pop(1);
        this.push(16777218);
        break;
      case 135:
      case 141:
        this.pop(1);
        this.push(16777219);
        this.push(16777216);
        break;
      case 139:
      case 190:
      case 193:
        this.pop(1);
        this.push(16777217);
        break;
      case 148:
      case 151:
      case 152:
        this.pop(4);
        this.push(16777217);
        break;
      case 168:
      case 169:
        throw new IllegalArgumentException("JSR/RET are not supported with computeFrames option");
      case 178:
        this.push(symbolTable, argSymbol.value);
        break;
      case 179:
        this.pop(argSymbol.value);
        break;
      case 180:
        this.pop(1);
        this.push(symbolTable, argSymbol.value);
        break;
      case 181:
        this.pop(argSymbol.value);
        this.pop();
        break;
      case 182:
      case 183:
      case 184:
      case 185:
        this.pop(argSymbol.value);
        if(opcode != 184) {
          abstractType1 = this.pop();
          if(opcode == 183 && argSymbol.name.charAt(0) == 60) {
            this.addInitializedType(abstractType1);
          }
        }

        this.push(symbolTable, argSymbol.value);
        break;
      case 186:
        this.pop(argSymbol.value);
        this.push(symbolTable, argSymbol.value);
        break;
      case 187:
        this.push(50331648 | symbolTable.addUninitializedType(argSymbol.value, arg));
        break;
      case 188:
        this.pop();
        switch(arg) {
          case 4:
            this.push(285212681);
            return;
          case 5:
            this.push(285212683);
            return;
          case 6:
            this.push(285212674);
            return;
          case 7:
            this.push(285212675);
            return;
          case 8:
            this.push(285212682);
            return;
          case 9:
            this.push(285212684);
            return;
          case 10:
            this.push(285212673);
            return;
          case 11:
            this.push(285212676);
            return;
          default:
            throw new IllegalArgumentException();
        }
      case 189:
        String arrayElementType = argSymbol.value;
        this.pop();
        if(arrayElementType.charAt(0) == 91) {
          this.push(symbolTable, '[' + arrayElementType);
        } else {
          this.push(301989888 | symbolTable.addType(arrayElementType));
        }
        break;
      case 192:
        String castType = argSymbol.value;
        this.pop();
        if(castType.charAt(0) == 91) {
          this.push(symbolTable, castType);
        } else {
          this.push(33554432 | symbolTable.addType(castType));
        }
        break;
      case 197:
        this.pop(arg);
        this.push(symbolTable, argSymbol.value);
    }

  }

  final boolean merge(SymbolTable symbolTable, Frame dstFrame, int catchTypeIndex) {
    boolean frameChanged = false;
    int numLocal = this.inputLocals.length;
    int numStack = this.inputStack.length;
    if(dstFrame.inputLocals == null) {
      dstFrame.inputLocals = new int[numLocal];
      frameChanged = true;
    }

    int numInputStack;
    int i;
    int concreteOutputType;
    int abstractOutputType;
    int dim;
    for(numInputStack = 0; numInputStack < numLocal; ++numInputStack) {
      if(this.outputLocals != null && numInputStack < this.outputLocals.length) {
        concreteOutputType = this.outputLocals[numInputStack];
        if(concreteOutputType == 0) {
          i = this.inputLocals[numInputStack];
        } else {
          abstractOutputType = concreteOutputType & -268435456;
          dim = concreteOutputType & 251658240;
          if(dim == 67108864) {
            i = abstractOutputType + this.inputLocals[concreteOutputType & 1048575];
            if((concreteOutputType & 1048576) != 0 && (i == 16777220 || i == 16777219)) {
              i = 16777216;
            }
          } else if(dim == 83886080) {
            i = abstractOutputType + this.inputStack[numStack - (concreteOutputType & 1048575)];
            if((concreteOutputType & 1048576) != 0 && (i == 16777220 || i == 16777219)) {
              i = 16777216;
            }
          } else {
            i = concreteOutputType;
          }
        }
      } else {
        i = this.inputLocals[numInputStack];
      }

      if(this.initializations != null) {
        i = this.getInitializedType(symbolTable, i);
      }

      frameChanged |= merge(symbolTable, i, dstFrame.inputLocals, numInputStack);
    }

    if(catchTypeIndex > 0) {
      for(numInputStack = 0; numInputStack < numLocal; ++numInputStack) {
        frameChanged |= merge(symbolTable, this.inputLocals[numInputStack], dstFrame.inputLocals, numInputStack);
      }

      if(dstFrame.inputStack == null) {
        dstFrame.inputStack = new int[1];
        frameChanged = true;
      }

      frameChanged |= merge(symbolTable, catchTypeIndex, dstFrame.inputStack, 0);
      return frameChanged;
    } else {
      numInputStack = this.inputStack.length + this.outputStackStart;
      if(dstFrame.inputStack == null) {
        dstFrame.inputStack = new int[numInputStack + this.outputStackTop];
        frameChanged = true;
      }

      for(i = 0; i < numInputStack; ++i) {
        concreteOutputType = this.inputStack[i];
        if(this.initializations != null) {
          concreteOutputType = this.getInitializedType(symbolTable, concreteOutputType);
        }

        frameChanged |= merge(symbolTable, concreteOutputType, dstFrame.inputStack, i);
      }

      for(i = 0; i < this.outputStackTop; ++i) {
        abstractOutputType = this.outputStack[i];
        dim = abstractOutputType & -268435456;
        int kind = abstractOutputType & 251658240;
        if(kind == 67108864) {
          concreteOutputType = dim + this.inputLocals[abstractOutputType & 1048575];
          if((abstractOutputType & 1048576) != 0 && (concreteOutputType == 16777220 || concreteOutputType == 16777219)) {
            concreteOutputType = 16777216;
          }
        } else if(kind == 83886080) {
          concreteOutputType = dim + this.inputStack[numStack - (abstractOutputType & 1048575)];
          if((abstractOutputType & 1048576) != 0 && (concreteOutputType == 16777220 || concreteOutputType == 16777219)) {
            concreteOutputType = 16777216;
          }
        } else {
          concreteOutputType = abstractOutputType;
        }

        if(this.initializations != null) {
          concreteOutputType = this.getInitializedType(symbolTable, concreteOutputType);
        }

        frameChanged |= merge(symbolTable, concreteOutputType, dstFrame.inputStack, numInputStack + i);
      }

      return frameChanged;
    }
  }

  private static boolean merge(SymbolTable symbolTable, int sourceType, int[] dstTypes, int dstIndex) {
    int dstType = dstTypes[dstIndex];
    if(dstType == sourceType) {
      return false;
    } else {
      int srcType = sourceType;
      if((sourceType & 268435455) == 16777221) {
        if(dstType == 16777221) {
          return false;
        }

        srcType = 16777221;
      }

      if(dstType == 0) {
        dstTypes[dstIndex] = srcType;
        return true;
      } else {
        int mergedType;
        if((dstType & -268435456) == 0 && (dstType & 251658240) != 33554432) {
          if(dstType == 16777221) {
            mergedType = (srcType & -268435456) == 0 && (srcType & 251658240) != 33554432?16777216:srcType;
          } else {
            mergedType = 16777216;
          }
        } else {
          if(srcType == 16777221) {
            return false;
          }

          int srcDim;
          if((srcType & -16777216) == (dstType & -16777216)) {
            if((dstType & 251658240) == 33554432) {
              mergedType = srcType & -268435456 | 33554432 | symbolTable.addMergedType(srcType & 1048575, dstType & 1048575);
            } else {
              srcDim = -268435456 + (srcType & -268435456);
              mergedType = srcDim | 33554432 | symbolTable.addType("java/lang/Object");
            }
          } else if((srcType & -268435456) == 0 && (srcType & 251658240) != 33554432) {
            mergedType = 16777216;
          } else {
            srcDim = srcType & -268435456;
            if(srcDim != 0 && (srcType & 251658240) != 33554432) {
              srcDim += -268435456;
            }

            int dstDim = dstType & -268435456;
            if(dstDim != 0 && (dstType & 251658240) != 33554432) {
              dstDim += -268435456;
            }

            mergedType = Math.min(srcDim, dstDim) | 33554432 | symbolTable.addType("java/lang/Object");
          }
        }

        if(mergedType != dstType) {
          dstTypes[dstIndex] = mergedType;
          return true;
        } else {
          return false;
        }
      }
    }
  }

  final void accept(MethodWriter methodWriter) {
    int[] localTypes = this.inputLocals;
    int numLocal = 0;
    int numTrailingTop = 0;
    int i = 0;

    while(i < localTypes.length) {
      int stackTypes = localTypes[i];
      i += stackTypes != 16777220 && stackTypes != 16777219?1:2;
      if(stackTypes == 16777216) {
        ++numTrailingTop;
      } else {
        numLocal += numTrailingTop + 1;
        numTrailingTop = 0;
      }
    }

    int[] var10 = this.inputStack;
    int numStack = 0;

    int frameIndex;
    for(i = 0; i < var10.length; ++numStack) {
      frameIndex = var10[i];
      i += frameIndex != 16777220 && frameIndex != 16777219?1:2;
    }

    frameIndex = methodWriter.visitFrameStart(this.owner.bytecodeOffset, numLocal, numStack);
    i = 0;

    int stackType;
    while(numLocal-- > 0) {
      stackType = localTypes[i];
      i += stackType != 16777220 && stackType != 16777219?1:2;
      methodWriter.visitAbstractType(frameIndex++, stackType);
    }

    i = 0;

    while(numStack-- > 0) {
      stackType = var10[i];
      i += stackType != 16777220 && stackType != 16777219?1:2;
      methodWriter.visitAbstractType(frameIndex++, stackType);
    }

    methodWriter.visitFrameEnd();
  }

  static void putAbstractType(SymbolTable symbolTable, int abstractType, ByteVector output) {
    int arrayDimensions = (abstractType & -268435456) >> 28;
    if(arrayDimensions == 0) {
      int typeDescriptor = abstractType & 1048575;
      switch(abstractType & 251658240) {
        case 16777216:
          output.putByte(typeDescriptor);
          break;
        case 33554432:
          output.putByte(7).putShort(symbolTable.addConstantClass(symbolTable.getType(typeDescriptor).value).index);
          break;
        case 50331648:
          output.putByte(8).putShort((int)symbolTable.getType(typeDescriptor).data);
          break;
        default:
          throw new AssertionError();
      }
    } else {
      StringBuilder var5 = new StringBuilder();

      while(arrayDimensions-- > 0) {
        var5.append('[');
      }

      if((abstractType & 251658240) == 33554432) {
        var5.append('L').append(symbolTable.getType(abstractType & 1048575).value).append(';');
      } else {
        switch(abstractType & 1048575) {
          case 1:
            var5.append('I');
            break;
          case 2:
            var5.append('F');
            break;
          case 3:
            var5.append('D');
            break;
          case 4:
            var5.append('J');
            break;
          case 5:
          case 6:
          case 7:
          case 8:
          default:
            throw new AssertionError();
          case 9:
            var5.append('Z');
            break;
          case 10:
            var5.append('B');
            break;
          case 11:
            var5.append('C');
            break;
          case 12:
            var5.append('S');
        }
      }

      output.putByte(7).putShort(symbolTable.addConstantClass(var5.toString()).index);
    }

  }
}
