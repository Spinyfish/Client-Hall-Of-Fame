package dev.eternal.client.script;

import dev.eternal.client.script.token.Token;
import dev.eternal.client.script.token.TokenType;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class ScriptParser {

  private static boolean isToken(char c) {
    return isToken(String.valueOf(c));
  }

  private static TokenType getTokenType(char c) {
    return getTokenType(String.valueOf(c));
  }

  private static boolean isToken(String c) {
    return Arrays.stream(TokenType.values())
        .map(TokenType::token)
        .filter(s -> s.length() == c.length())
        .anyMatch(character -> character.equals(c));
  }

  private static TokenType getTokenType(String c) {
    return Arrays.stream(TokenType.values())
        .filter(tokenType -> tokenType.token().length() == c.length())
        .filter(tokenType -> tokenType.token().equals(c))
        .findFirst().orElseThrow();
  }

  private static boolean isAlphabetical(String s) {
    return !s.toLowerCase().equals(s.toUpperCase());
  }

  private static boolean isNumber(String s) {
    for(int i = 0; i < 9; i++) {
      if(s.equals(String.valueOf(i))) return true;
    }
    return false;
  }

  private static boolean valid(char c) {
    return c != ' ' && c != '\n';
  }

  public static List<Token> parseSource(String source) {
    final List<Token> tokens = new ArrayList<>();
    final Queue<Character> sourceCode = new ConcurrentLinkedQueue<>();

    for(char c : source.toCharArray()) {
      if(valid(c)) sourceCode.add(c);
    }

    loop:
    while(!sourceCode.isEmpty()) {
      char currentChar = sourceCode.poll();
      if(isToken(currentChar)) {
        tokens.add(new Token(String.valueOf(currentChar), getTokenType(currentChar)));
      } else {
        //multi-char token
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(currentChar);
        if(isNumber(String.valueOf(currentChar))) {
          while (!sourceCode.isEmpty() && isNumber(String.valueOf(sourceCode.peek()))) {
            currentChar = sourceCode.poll();
            stringBuilder.append(currentChar);
          }
          tokens.add(new Token(stringBuilder.toString(), TokenType.NUMBER));
        } else {
          while (!sourceCode.isEmpty() && !isToken(sourceCode.peek())) {
            currentChar = sourceCode.poll();
            stringBuilder.append(currentChar);
            if(isToken(stringBuilder.toString())) {
              tokens.add(new Token(stringBuilder.toString(), getTokenType(stringBuilder.toString())));
              continue loop;
            }
          }
          tokens.add(new Token(stringBuilder.toString(), TokenType.IDENTIFIER));
        }
      }
    }

    return tokens;
  }

  @Test
  public void test() {
    List<Token> tokens = parseSource("""
      int x = 17;
      int mynamejeff = 52;
      int math = x + mynamejeff;
      """);
    System.out.println(tokens.stream().map(Token::type).toList());
  }

}