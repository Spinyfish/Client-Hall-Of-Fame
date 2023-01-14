package net.optifine.expr;

public enum TokenType {
   IDENTIFIER("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_:."),
   NUMBER("0123456789", "0123456789."),
   OPERATOR("+-*/%!&|<>=", "&|="),
   COMMA(","),
   BRACKET_OPEN("("),
   BRACKET_CLOSE(")");

   private final String charsFirst;
   private final String charsNext;
   public static final TokenType[] VALUES = values();

   private TokenType(String charsFirst) {
      this(charsFirst, "");
   }

   private TokenType(String charsFirst, String charsNext) {
      this.charsFirst = charsFirst;
      this.charsNext = charsNext;
   }

   public String getCharsFirst() {
      return this.charsFirst;
   }

   public static TokenType getTypeByFirstChar(char ch) {
      for(TokenType tokentype : VALUES) {
         if (tokentype.getCharsFirst().indexOf(ch) >= 0) {
            return tokentype;
         }
      }

      return null;
   }

   public boolean hasCharNext(char ch) {
      return this.charsNext.indexOf(ch) >= 0;
   }
}
