package net.optifine.expr;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TestExpressions {
    public static void main(final String[] args) throws Exception {
        final ExpressionParser expressionparser = new ExpressionParser(null);

        while (true) {
            try {
                final InputStreamReader inputstreamreader = new InputStreamReader(System.in);
                final BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
                final String s = bufferedreader.readLine();

                if (s.length() <= 0) {
                    return;
                }

                final IExpression iexpression = expressionparser.parse(s);

                if (iexpression instanceof IExpressionFloat) {
                    final IExpressionFloat iexpressionfloat = (IExpressionFloat) iexpression;
                    final float f = iexpressionfloat.eval();
                    System.out.println("" + f);
                }

                if (iexpression instanceof IExpressionBool) {
                    final IExpressionBool iexpressionbool = (IExpressionBool) iexpression;
                    final boolean flag = iexpressionbool.eval();
                    System.out.println("" + flag);
                }
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
