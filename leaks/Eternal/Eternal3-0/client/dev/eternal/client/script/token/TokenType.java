package dev.eternal.client.script.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenType {

  DEFINE_INT("int"),
  EQUALS("="),
  NUMBER(""),
  IDENTIFIER(""), //variable, eg: var x = 42; x is the Identifier
  END_LINE(";"),
  ADDITION("+");

  private final String token;

}