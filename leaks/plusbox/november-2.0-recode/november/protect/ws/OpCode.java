/* November.lol © 2023 */
package lol.november.protect.ws;

import lombok.Getter;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Getter
public enum OpCode {
  AUTHENTICATE(0x00),
  CHAT(0x01),
  FEATURE_REQUEST(0x02);

  private final int op;

  OpCode(int op) {
    this.op = op;
  }

  public static OpCode of(int op) {
    for (OpCode opCode : values()) {
      if (opCode.getOp() == op) return opCode;
    }
    return null;
  }
}
