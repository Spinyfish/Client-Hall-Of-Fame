package net.optifine.expr;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class TokenParser {
    public static Token[] parse(final String str) throws IOException, ParseException {
        final Reader reader = new StringReader(str);
        final PushbackReader pushbackreader = new PushbackReader(reader);
        final List<Token> list = new ArrayList();

        while (true) {
            final int i = pushbackreader.read();

            if (i < 0) {
                final Token[] atoken = list.toArray(new Token[list.size()]);
                return atoken;
            }

            final char c0 = (char) i;

            if (!Character.isWhitespace(c0)) {
                final TokenType tokentype = TokenType.getTypeByFirstChar(c0);

                if (tokentype == null) {
                    throw new ParseException("Invalid character: '" + c0 + "', in: " + str);
                }

                final Token token = readToken(c0, tokentype, pushbackreader);
                list.add(token);
            }
        }
    }

    private static Token readToken(final char chFirst, final TokenType type, final PushbackReader pr) throws IOException {
        final StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(chFirst);

        while (true) {
            final int i = pr.read();

            if (i < 0) {
                break;
            }

            final char c0 = (char) i;

            if (!type.hasCharNext(c0)) {
                pr.unread(c0);
                break;
            }

            stringbuffer.append(c0);
        }

        return new Token(type, stringbuffer.toString());
    }
}
